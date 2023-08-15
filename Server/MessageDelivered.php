<?php
$path = '/var/www/exchange_project/';
$private_crypt_key_path = $path . "keys/cryp_key.txt";

require_once 'DatabaseLoginInfo.php';
require $path . 'MessageEntry.php';
require $path . 'Backoff.php';
require $path . 'vendor/autoload.php';

use Kreait\Firebase\Factory;
use Kreait\Firebase\ServiceAccount;
use Kreait\Firebase\Messaging\AndroidConfig;
use Kreait\Firebase\Messaging\CloudMessage;

$input = file_get_contents("php://input");
$input = json_decode($input, true);

$message_entry = new MessageEntry();
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    $message_entry->conversation_id = $input['conversation_id'];
    $message_entry->message_id = $input['message_id'];
    $message_entry->sender_fcm_token = $input['fcm_token'];
    $message_entry->to_id = $input['to_id'];
    $message_entry->message = $input['message_content'];
    
    
}

$service_account = ServiceAccount::fromJsonFile($path . 'secret/exchange-project-30ec1-4c320432756f.json');
$firebase = (new Factory())->withServiceAccount($service_account)->create();


try{
    
    $pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $sql = "SELECT fcm_token FROM users WHERE user_id = ? ";
    $pdo_statement = $pdo->prepare($sql);
    $pdo_statement->execute([$message_entry->to_id]);
    $token = $pdo_statement->fetch(\PDO::FETCH_ASSOC);
    
    $message_entry->recipient_fcm_token = $token['fcm_token'];
    
}catch(PDOException $pdo_error){
    
    echo $pdo_error->getMessage();
}

$firebase_messaging = $firebase->getMessaging();
$config = AndroidConfig::fromArray([
    
    'priority' => 'high'
    
]);
$message = CloudMessage::withTarget('token',  $message_entry->recipient_fcm_token)
->withAndroidConfig($config)
->withData(["message_received" => true, "conversation_id" => $message_entry->conversation_id,
    "message_id" => $message_entry->message_id, "message_content" => $message_entry->message]);

try{
    
    $firebase_messaging->validate($message);
    
    
}catch(\Kreait\Firebase\Exception\Messaging\InvalidMessage $error){
    
    print_r($error->errors());
    
    $error_array = $error->errors();
    $error_description = $error_array['error'];
    $error_code = $error_description['code'];
    
    
    $error_message = json_encode(array("messaging_error" => true , 'conversation_id' => $message_entry->conversation_id,
        "message_id" => $message_entry->message_id, "message_content" => $message_entry->message));
    
    switch($error_code){
        
        case 400:
            // Send Message to sender that message could not send
            $this->message_transmission_failure($error_message, $message_entry->sender_fcm_token);   
            break;
            
        case 404:
            // Send Message to sender that message could not send
            $this->message_transmission_failure($error_message, $message_entry->sender_fcm_token);
            break;
            
        case 403:
            // Send Message to sender that message could not send
            $this->message_transmission_failure($error_message, $message_entry->sender_fcm_token);
            break;
            
        case 429:
            $backoff = new Backoff($firebase_messaging, $message, $message_entry->recipient_fcm_token, $error_message, null);
            $backoff->set_from_id(null);
            $backoff->set_from_token($message_entry->sender_fcm_token);
            $backoff->retry_transmission();
            break;
            
        case 503:
            $backoff = new Backoff($firebase_messaging, $message, $message_entry->recipient_fcm_token);
            $backoff->set_from_id($message_entry->sender_fcm_token);
            $backoff->retry_transmission();
            break;
            
        case 500:
            $backoff = new Backoff($firebase_messaging, $message, $message_entry->recipient_fcm_token);
            $backoff->set_from_id($message_entry->sender_fcm_token);
            $backoff->retry_transmission();
            break;
            
    }
    
    return;
}

$firebase_messaging->send($message);

echo json_encode(array("message_delivered" => true));

?>
