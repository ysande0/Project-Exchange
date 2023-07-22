<?php
date_default_timezone_set("America/New_York");
// For both requests and responses

$path = 'C:/Web/Project/TheExchange Project/';
$private_crypt_key_path = $path . "keys/cryp_key.txt";

require $path . 'vendor/autoload.php';
require_once 'FirebaseDB.php';
require_once 'Point.php';
require_once 'MessageEntry.php';
require_once 'TransactionRequest.php';
require_once 'TransactionResponse.php';
require_once 'Map.php';
require_once 'User.php';
require_once 'Game.php';



use Kreait\Firebase\Factory;
use Kreait\Firebase\ServiceAccount;
use Kreait\Firebase\Messaging\CloudMessage;
use Kreait\Firebase\Messaging\MulticastSendReport;
use Kreait\Firebase\Messaging\Notification;
use Defuse\Crypto\Key;
use Defuse\Crypto\Crypto;

$service_account = ServiceAccount::fromJsonFile($path . 'secret/exchange-project-30ec1-4c320432756f.json');

$firebase = (new Factory())->withServiceAccount($service_account)->create();


$input = file_get_contents("php://input");
$input = json_decode($input, true);

$crypt_key = file_get_contents($private_crypt_key_path);

/*
 * Transaction method:
 * Request - 100
 * Response - 101
 * Cancel - 102
 * Complete - 104
 * Retry - 103
 * Query - 105
 * Query Recipient Software - 106
 * 
 * 
 * Transaction response: 
 * Accept - 1
 * Decline - 0 (Reject the request)
 * Reject - 2 (Reject the map) 
 * 
 * */
$headline_game = null;
$transaction_request = null;
$transaction_response = null;
$user_id = null;
$user_token = null;
$message_entry = new MessageEntry();

$from_uid;
$to_uid;
$sender_want_arr = array();
$recipient_have_arr = array();

$target_destination;
$transaction_response;
$transaction_id;
$conversation_id;

$rating_score;

$map = null;

if($input['messaging_operation'] === 100){
    
    $headline_game = new Game();
    $transaction_request = new TransactionRequest();
    
    $transaction_request->from_first_name = $input['from_first_name'];
    $transaction_request->from_id = $input['from'];
    $transaction_request->to_id = $input['to'];
    $transaction_request->to_first_name = $input['to_first_name'];
    $transaction_request->to_id_rating = $input['rating'];
    $transaction_request->time = $input['time'];
    $transaction_request->date = $input['date'];
    $transaction_request->transaction_id = $input['transaction_id'];
    $transaction_request->conversation_id = $input['conversation_id']; 
    
    /*
    $transaction_request->software_want_array = $input['want'];
    $transaction_request->software_have_array = $input['have'];
*/

    
    /*
    $headline_game->title = $input['headline_title'];
    $headline_game->platform = $input['headline_platform'];
    $headline_game->file_path = $input['headline_software_image_url'];
    */
    
    
}
else if($input['messaging_operation'] === 101){
    
    $transaction_response = new TransactionResponse();
    
    $transaction_response->from_uid = $input['from_uid'];
    $transaction_response->to_uid = $input['to_uid'];
    $transaction_response->response = $input['transaction_response'];
    $transaction_response->transaction_id = $input['transaction_id'];
    $transaction_response->conversation_id = $input['conversation_id'];
    
    

}
else if($input['messaging_operation'] === 102){
    
    $transaction_response = new TransactionResponse();
    
    $transaction_response->from_first_name = $input['from_first_name'];
    $transaction_response->from_uid = $input['from_uid'];
    $transaction_response->to_first_name = $input['to_first_name'];
    $transaction_response->to_uid = $input['to_uid'];
    $transaction_response->transaction_id = $input['transaction_id'];
    $transaction_response->conversation_id = $input['conversation_id'];
    
    
}
else if($input['messaging_operation'] === 104){
    
    $transaction_response = new TransactionResponse();
    
    $transaction_response->from_uid = $input['from_uid'];
    $user_id = $input['id'];
    $user_token = $input['access_token'];
    $transaction_response->transaction_id = $input['transaction_id'];
    $transaction_response->response = $input['transaction_response'];
    
}
else if($input['messaging_operation'] === 103){
    
    $transaction_response = new TransactionResponse();
    $transaction_response->from_uid = $input['from'];
    $transaction_response->transaction_id = $input['transaction_id'];
    
    
}
else if($input['messaging_operation'] === 105){
    
    $transaction_request = new TransactionRequest();
    $transaction_request->from_id = $input['id'];
    $transaction_request->conversation_id = $input['conversation_id'];
    $transaction_request->transaction_id = $input['transaction_id'];
    
}
else if($input['messaging_operation'] === 106){
 
    $transaction_request = new TransactionRequest();
    $transaction_request->to_id = $input['to_id'];
    
}


require 'DatabaseLoginInfo.php';
require 'FirebaseMessage.php';

//$user_token = Crypto::decrypt($user_token, Key::loadFromAsciiSafeString($crypt_key));

try{
    
    $pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
   
    /*
    $token = new Token($pdo);
    $token->set_user_id($user_id);
    $token->set_token($user_token);
    $session_timeout = $token->validate();
    
    if($session_timeout){
        
        echo json_encode(array("session_timeout" => true));
        return;
    }
    */
   
        
    if($input['messaging_operation'] === 100){
          // Request
        
        // Check if from_uid is blocked by to_uid; If they are then return.
        
        $sql = "SELECT blocked_user_id FROM blocked_users WHERE user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$transaction_request->to_id]);
        $blocked_user_id = $pdo_statement->fetch(PDO::FETCH_ASSOC);
        
        if($transaction_request->from_id === $blocked_user_id['blocked_users_id']){
            echo $transaction_request->from_id . " has been blocked by " . $transaction_request->to_id;
            return;
        }
        
 
        // Change $transaction_request->software_have_array to $input
        /*
        for($i = 0; $i < sizeof($input['have']); $i++){
        
            $arr_have = array();
            $arr_have = $input['have'][$i];
            $game = new Game();
            $game->title = $arr_have['title'];
            $game->platform = $arr_have['platform'];
            $game->file_path = $arr_have['software_image_url'];
            
            $sql = "SELECT software_inventory.software_image_url, users.first_name, users.profile_image_url FROM software_inventory INNER JOIN users ON users.user_id = software_inventory.user_id 
          AND software_inventory.title = ? AND software_inventory.platform = ?"; 
            $pdo_statement = $pdo->prepare($sql);
            $pdo_statement->execute([$game->title, $game->platform]);
            $url_result = $pdo_statement->fetch(PDO::FETCH_ASSOC);
            
            $game->file_path = $url_result['software_image_url'];
            $transaction_request->from_profile_image_url = $url_result['profile_image_url'];
            
            $arr_have = array("title" => $game->title, "platform" => $game->platform, "software_image_url" => $game->file_path);
            $input['have'][$i] = $arr_have;
            
        }
        */
        for($i = 0; $i < sizeof($input['have']); $i++){
         
            $arr_have = $input['have'][$i];
            $game = new Game();
            $game->title = $arr_have['title'];
            $game->platform = $arr_have['platform'];
            $game->publisher = $arr_have['publisher'];
            $game->developer = $arr_have['developer'];
            $game->upc = $arr_have['upc'];
            $game->file_path = $arr_have['software_image_url'];
            
            // delete the previous
            if(is_file($game->file_path))
                unlink($game->file_path);
            
            $sql = "DELETE FROM software_inventory WHERE user_id = ? AND title = ? AND publisher = ? AND developer = ? AND platform = ?  AND upc = ? AND software_image_url = ? ";
            $pdo_statement = $pdo->prepare($sql);
            $pdo_statement->execute([$transaction_request->from_id, $game->title, $game->publisher, $game->developer, $game->platform, $game->upc, $game->file_path]);
 
            array_push($transaction_request->software_have_array, $game);
            
        }
        
        
        for($i = 0; $i < sizeof($input['want']); $i++){
            
            $arr_want = $input['want'][$i];
            $game = new Game();
            $game->title = $arr_want['title'];
            $game->platform = $arr_want['platform'];
            $game->publisher = $arr_want['publisher'];
            $game->developer = $arr_want['developer'];
            $game->upc = $arr_want['upc'];
            $game->file_path = $arr_want['software_image_url'];
            
            if(!is_file($game->file_path)){
                
                // An error occurred.
            }
                
            
            $source_file = basename($game->file_path);
            $source_path = $game->file_path;
            
            
            $destination_path = "http://192.168.1.242:80/Project/TheExchange%20Project/Images/" . $transaction_request->from_uid . "/software_images" . "/";
            copy($source_path, $destination_path);
            
            $game->file_path = $destination_path . $source_file;
            
            $sql = "INSERT INTO software_inventory (user_id, title, publisher, developer, platform, upc, software_image_url, created_at, last_modified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)  ";
            $pdo_statement = $pdo->prepare($sql);
            $pdo_statement->execute([$transaction_request->from_id, $game->title, $game->publisher, $game->developer, $game->platform, $game->upc, $game->file_path, time(), time()]);
            
            
            array_push($transaction_request->software_want_array, $game);
         
        }
        
      
   
        $firebase_database = new FirebaseDB($firebase, "Transactions");
 
         $firebase_database->insert_transaction($transaction_request->from_id, 
        [$transaction_request->conversation_id =>
        [$transaction_request->transaction_id => 
        ["want" => $transaction_request->software_want_array, "have" => $transaction_request->software_have_array,
        "time" => $transaction_request->time, "date" => $transaction_request->date, "first_name" => $transaction_request->to_first_name, "recipient_id" => $transaction_request->to_id, "rating" => $transaction_request->to_id_rating, 
        ]]]); 
        
        /*
        $sql = "UPDATE user_ratings SET aggregate_score = aggregate_score + ? , number_transactions = number_transactions + 1,
        rating = aggregate_score / number_transactions WHERE user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$transaction_request->to_id_rating, $transaction_request->to_id]);
        */
        echo json_encode(array("transaction_complete" => true));
        
    }
    else if($input['messaging_operation'] === 101){
        // Response 
        
        $firebase_database = new FirebaseDB($firebase, "Transactions");
        
        if($transaction_response->response === 0){
             // Decline 
           
            $sql = "DELETE FROM transaction_table WHERE transaction_id = ?";
            $pdo_statement = $pdo->prepare($sql);
            $pdo_statement->execute([$transaction_response->transaction_id]);
            
            $firebase_database->delete_transaction("Transactions/" . $transaction_response->transaction_id);
            
        }else if($transaction_response->response === 1){
            // Accept 
            
            $sql = "UPDATE transaction_table SET complete = 0 WHERE transaction_id = ?";
            $pdo_statement = $pdo->prepare($sql);
            $pdo_statement->execute([$transaction_response->transaction_id]);
            
            $sql = "SELECT first_name, fcm_token FROM users WHERE unique_id = ?";
            $pdo_statement = $pdo->prepare($sql);
            $pdo_statement->execute([$transaction_response->to_uid]);
            $query = $pdo_statement->fetchAll(PDO::FETCH_ASSOC);
            
            $recipient_user = $query[0];
            $transaction_response->to_first_name = $recipient_user['first_name'];

            $messaging = $firebase->getMessaging();
            // send firebase notification to other user, informing them the transaction has been canceled.
            $message = CloudMessage::withTarget('token', $recipient_user['fcm_token'])
            ->withNotification(Notification::create('title', $recipient_user['first_name']))
            ->withData(["transaction_status" => "2", "from_first_name" => $transaction_response->from_first_name, "from_uid" => $transaction_response->from_uid, "to_first_name" => $transaction_response->to_first_name, "to_uid" => $transaction_response->to_uid,
                "conversation_id" => $transaction_response->conversation_id, "transaction_id" => $transaction_response->transaction_id, "headline_title" => $input['title'], "headline_platform" => $input['platform'], "profile_image_url" => $input['profile_image_url'], "headline_software_image_url" => $input['software_image_url']]);
            
            try{
                
                $messaging->validate($message);
                
            }catch(\Kreait\Firebase\Exception\Messaging\InvalidMessage $error){
                print_r($error->errors());
            }
            
            $messaging->send($message);
            
            // Send firebase notifcation 
            echo json_encode(array("TRANSACTION_RESPONSE_101_OK" => true, "conversation_id" => $transaction_response->conversation_id, "transaction_id" => $transaction_response->transaction_id, "isActive" => 2 ));
        }
        else if($transaction_response->response === 2){
            // reject offer (map location)
            
            $firebase_database->delete_transaction("Transactions/" . $transaction_response->transaction_id . "/maps");
            echo "Success reject";
        }
        
    }
    else if($input['messaging_operation'] === 102){
        // Cancel
        
        // NOTE: deduct points 
        $sql = "SELECT user_id FROM users WHERE unique_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$transaction_response->from_uid]);
        $query = $pdo_statement->fetchAll(PDO::FETCH_ASSOC);
        
        $user_id = $query[0];
        
        $sql = "SELECT first_name, fcm_token FROM users WHERE unique_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$transaction_response->to_uid]);
        $query = $pdo_statement->fetchAll(PDO::FETCH_ASSOC);
        
        $recipient_user = $query[0];
        
        $sql = "UPDATE user_ratings SET aggregate_score = aggregate_score - 1 , number_transactions = number_transactions + 1,
        rating = aggregate_score / number_transactions WHERE user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$user_id['user_id']]);
        
        
        // Remove transaction 
        $sql = "UPDATE transaction_table SET complete = -2 WHERE transaction_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$transaction_response->transaction_id]);
        
        // send firebase notification to other user, informing them the transaction has been canceled.
        
        $messaging = $firebase->getMessaging();
        
        $message = CloudMessage::withTarget('token', $recipient_user['fcm_token'])
        ->withNotification(Notification::create('title', $recipient_user['first_name']))
        ->withData(["transaction_status" => "-2", "from_first_name" => $transaction_response->from_first_name, "from_uid" => $transaction_response->from_uid, "to_first_name" => $transaction_response->to_first_name, "to_uid" => $transaction_response->to_uid,
            "headline_title" => $input['title'], "headline_platform" => $input['platform'], "headline_software_image_url" => $input['software_image_url'], "profile_image_url" => $input['profile_image_url'],"conversation_id" => $transaction_response->conversation_id, "transaction_id" => $transaction_response->transaction_id]);

        try{
            
            $messaging->validate($message);
            
        }catch(\Kreait\Firebase\Exception\Messaging\InvalidMessage $error){
            
            print_r($error->errors());
            
        }
        
        $messaging->send($message);
        
        
        echo json_encode(array("TRANSACTION_RESPONSE_102_OK" => true, "conversation_id" => $transaction_response->conversation_id, "transaction_id" => $transaction_response->transaction_id, "isActive" => -2));
    }
    else if($input['messaging_operation'] === 104){
        // Confirm 
        
        $firebase_database = new FirebaseDB($firebase, "Transactions");
        $firebase_database->update_transaction("Transactions/" . $transaction_response->transaction_id . "/received", [$transaction_response->from_uid => $transaction_response->response]);
        
        /*
        if($firebase_database->getNumChild("Transactions/" . $transaction_response->transaction_id . "/received") < 2)
            return;
        */
         
        $sql = "UPDATE transaction_table SET complete = 1 WHERE transaction_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$transaction_response->transaction_id]);
     
        echo json_encode(array("TRANSACTION_RESPONSE_103_OK" => true, "conversation_id" => $transaction_response->conversation_id, "transaction_id" => $transaction_response->transaction_id, "isActive" => 3));
     
    }
    else if($input['messaging_operation'] === 103){
        
        $firebase_database = new FirebaseDB($firebase, "Transactions");
        $firebase_database->update_transaction("Transactions/" . $transaction_response->transaction_id . "/maps", [$transaction_response->from_uid => ["map_url" => $transaction_response->map_url, "map_street_address" => $transaction_response->map_street_address]]);
        echo "Success Retry";
    }
    else if($input['messaging_operation'] === 105){

        $firebase_database = new FirebaseDB($firebase, "Transactions");
        $entry = $firebase_database->query_transaction($transaction_request->transaction_id, $transaction_request->from_uid);
        
        /*
        if(is_array($entry['have'])){
            
            echo json_encode(array("array" => "entry is an array"));
            
            if(is_iterable($entry['have']))
                echo json_encode(array("array" => "entry is iterable"));
            else 
                echo json_encode(array("array" => "entry is not iterable"));
            
            
        }
        else 
            echo  json_encode(array("array" => "entry is not an array"));
        return;
   */
        if($entry === 0)
            echo json_encode(array("TRANSACTION_OPERATION_105_ERROR" => true));
        else{

            $have_array = array();
            $have_array = $entry['have'];
            
            $json_have = array();
            for($i = 0; $i < count($have_array); $i++){
                
                $current_entry = array();
                $current_entry = $entry['have'][$i];
                $current_entry['title'];
                $current_entry['platform'];
                $current_entry['software_image_url'];
                
                array_push($json_have, array('title' => $current_entry['title'], 'platform' => $current_entry['platform'], 'software_image_url' => $current_entry['software_image_url']));
                
               
            }
            
            $want_array = array();
            $want_array =  $entry['want'];
           
            $json_want = array();
            for($i = 0; $i < count($want_array); $i++){
                
                $current_entry = array();
                $current_entry = $entry['want'][$i];
                
                $current_entry['title'];
                $current_entry['platform'];
                $current_entry['software_image_url'];
                
                array_push($json_want, array('title' => $current_entry['title'], 'platform' => $current_entry['platform'], 'software_image_url' => $current_entry['software_image_url']));

            }
            
            $json_transaction_query = array("have" => $json_have, "want" => $json_want);
             echo json_encode($json_transaction_query);
/*
            $food = array("Apple" => array("Green Apple's", "Red Apple's"), "Grape's" => 4, "Orange's" => 6);
            print_r($food);
            echo json_encode(array("Number of Foods" => count($food['Apple']), $food));
 */
     
        }
        
    }
    else if($input['messaging_operation'] === 106){
       
        $recipient_software_library = array();
        
        /*
        $sql = "SELECT user_id FROM users WHERE unique_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$transaction_request->to_id]);
        $query = $pdo_statement->fetchAll(PDO::FETCH_ASSOC);
        
        $recipient_id = $query[0];
        */
        
        $sql = "SELECT title, platform, publisher, developer, upc, software_image_url FROM software_inventory WHERE user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$transaction_request->to_id]);
        $query = $pdo_statement->fetchAll(PDO::FETCH_ASSOC);
        
        for($i = 0; $i < sizeof($query) ; $i++){
            
            $game = new Game();
            $game->title = $query[$i]['title'];
            $game->platform = $query[$i]['platform'];
            $game->publisher = $query[$i]['publisher'];
            $game->developer = $query[$i]['developer'];
            $game->upc = $query[$i]['upc'];
            $game->file_path = $query[$i]['software_image_url'];
            
            $arr = array("title" => $game->title, "platform" => $game->platform, "publisher" => $game->publisher, "developer" => $game->developer, "upc" => $game->upc, "software_image_url" => $game->file_path);
            array_push($recipient_software_library, $arr);
            
        }
        
        echo json_encode(array("TRANSACTION_RESPONSE_106_OK" => true, "recipient_library" => $recipient_software_library));
    }
       
    
    
}catch(PDOException $pdo_error){
    
    echo $pdo_error->getMessage();

}

?>