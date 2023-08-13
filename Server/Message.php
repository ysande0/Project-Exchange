<?php

$path = 'C:/Web/Project/TheExchange Project/';
$private_crypt_key_path = $path . "keys/cryp_key.txt";

// Determine if message is from registered user.
$message_input = file_get_contents("php://input");
$message_input = json_decode($message_input, true);


require $path . 'vendor/autoload.php';
require 'FirebaseMessage.php';
require 'FirebaseDB.php';
require 'MessageEntry.php';


$crypt_key = file_get_contents($private_crypt_key_path);

use Kreait\Firebase\Factory;
use Kreait\Firebase\ServiceAccount;
use Kreait\Firebase\Messaging\CloudMessage;
use Kreait\Firebase;


$service_account = ServiceAccount::fromJsonFile($path . 'secret/exchange-project-30ec1-4c320432756f.json');
$firebase = (new Factory())->withServiceAccount($service_account)->create();
$firebase_message = new FirebaseMessage($firebase);
$firebase_database = new FirebaseDB($firebase, "Conversations");

$message_entry = new MessageEntry();
$title = null;
$platform = null;
$transaction_id = null;
$user_id = null;
$user_token = null;
    
// Write name, message contents, date, and time to Real Time database
$from_recipient = $message_input['from'];
$to_recipient = $message_input['to'];

  
$messaging_operation = $from_recipient['messaging_operation']; 
if($messaging_operation === 100){
    

    $message_entry->conversation_id = $from_recipient['conversation_id'];
    $message_entry->from_uid = $from_recipient['uid'];
    $message_entry->to_uid = $to_recipient['uid'];
    $message_entry->first_name = $from_recipient['name'];
    $user_token = $from_recipient['access_token'];
    $user_id = $from_recipient['user_id'];
    $message_entry->message = $from_recipient['message'];
    $message_entry->time = $from_recipient['time'];
    $message_entry->date = $from_recipient['date'];
    $message_entry->profile_image_url = $from_recipient['profile_image_url'];
    $message_entry->software_image_url = $from_recipient['software_image_url'];
    $title = $from_recipient['title'];
    $platform = $from_recipient['platform'];
    $transaction_id = $from_recipient['transaction_id'];

    
    if($message_entry->conversation_id === 0)
        $message_entry->conversation_id = $firebase_database->insert_message($message_entry->conversation_id, $message_entry);
    else
        $firebase_database->insert_message($message_entry->conversation_id, $message_entry);
    
}
else if($messaging_operation === 101){
    
    $user_id = $from_recipient['id'];
    $user_token = $from_recipient['access_token'];
    $message_entry->from_id = $from_recipient['id'];
    $message_entry->to_id = $to_recipient['id'];
      
}

require 'DatabaseLoginInfo.php';

$device_token = null;
try{
    
    $pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
  
    if($messaging_operation === 100){
        
        $sql = "SELECT blocked_user_id FROM blocked_users WHERE user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$message_entry->to_id]);
        $blocked_user_id = $pdo_statement->fetch(PDO::FETCH_ASSOC);
        
        if($message_entry->from_id === $blocked_user_id['blocked_user_id']){
            echo $message_entry->from_id . " has been blocked by " . $message_entry->to_id;
            return;
        }
        
        
        $sql = "SELECT fcm_token FROM users WHERE user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$message_entry->to_id]);
        $device_token = $pdo_statement->fetch(PDO::FETCH_ASSOC);
        
        
    }
    else if($messaging_operation === 101){
        
        $sql = "SELECT COUNT(1) AS blocked_entry FROM blocked_users WHERE user_id = ? AND blocked_user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$message_entry->from_id, $message_entry->to_id]);
        $entry = $pdo_statement->fetch(PDO::FETCH_ASSOC);
        
        
        if($entry['blocked_entry'] == 1){
            echo "User already blocked";
            return;
        }
        
        $sql = "INSERT INTO blocked_users (user_id, blocked_user_id) VALUES (?, ?)";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$message_entry->from_id, $message_entry->to_id]);
        
        
    }
    
    
}catch(PDOException $pdo_error){
    
    echo $pdo_error->getMessage();
}

if($device_token === false){
    
    echo "Error! User does not exist";
    return;
    
}

/**
 * NOTE: Send with newly generated conversation_id
 **/


if($messaging_operation === 100){

      $token = $device_token['fcm_token'];
    $messaging = $firebase->getMessaging();
    

    $message = CloudMessage::withTarget('token', $device_token['fcm_token'])
    ->withData(["messaging" => true ,'headline_title' => $title, "headline_platform" => $platform, "transaction_id" => $transaction_id, 'conversation_id' => $message_entry->conversation_id, 'first_name' => $message_entry->first_name, 'from_uid' => $message_entry->from_uid,
        'message' => $message_entry->message, 'time' => $message_entry->time, 'date' => $message_entry->date, 'profile_image_url' => $message_entry->profile_image_url, 'headline_software_image_url' => $message_entry->software_image_url]);
    
      try{
           
          $messaging->validate($message);
          
      }catch(\Kreait\Firebase\Exception\Messaging\InvalidMessage $error){
          print_r($error->errors());
      }
      
      $messaging->send($message);
      
    
    echo json_encode(array("conversation_id" => $message_entry->conversation_id, "MESSAGE_ERROR" => false));

}
else if($messaging_operation === 101){
    
    echo json_encode(array("MESSAGE_OPERATION_101" => true));
}

?>
