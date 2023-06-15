<?php
$path = '/var/www/exchange_project/';
//$path = 'C:/Web/Project/TheExchange Project/';
//$private_crypt_key_path = $path . "keys/cryp_key.txt";

// Determine if message is from registered user.
$message_input = file_get_contents("php://input");
$message_input = json_decode($message_input, true);

require 'DatabaseLoginInfo.php';
require 'MessageEntry.php';

$ops = null;
$message_entry = new MessageEntry();
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    
    $ops = (int) $_GET['ops'];
    $message_entry->from_id = (int) $_GET['from_id'];
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    $ops = $message_input['ops'];
}



if($ops === 1){
    
    // Insert Blocked User
    $message_entry->from_id = $message_input['from_id'];
    $message_entry->to_id = $message_input['to_id'];
}
else if($ops === 2){
    
    // Delete Blocked User 
    $message_entry->from_id = $message_input['from_id'];
    $message_entry->to_id = $message_input['to_id'];
    
}

try{
    
    $pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    if($ops === 1){
        
        $sql = "SELECT COUNT(1) AS blocked_entry FROM blocked_users WHERE user_id = ? AND blocked_user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$message_entry->from_id, $message_entry->to_id]);
        $entry = $pdo_statement->fetch(PDO::FETCH_ASSOC);
        
        
        if($entry['blocked_entry'] == 1){
            echo json_encode(array("user_already_blocked" => true));
            return;
        }
        
        $sql = "INSERT INTO blocked_users (user_id, blocked_user_id) VALUES (?, ?)";
        $pdo_statement = $pdo->prepare($sql);
        $is_executed = $pdo_statement->execute([$message_entry->from_id, $message_entry->to_id]);
        
        if(!$is_executed){
            
            $pdo = null;
            header('Content-type: application/json; charset=utf-8');
            echo json_encode(array("block_user_error_100" => "error! could not block user"));
            return;
            
        }
        
         header('Content-type: application/json; charset=utf-8');
         echo json_encode(array("user_is_blocked" => "user is blocked"));
         return;
    }
    else if($ops === 2){
        
        $sql = "DELETE FROM blocked_users WHERE user_id = ? AND blocked_user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $is_executed = $pdo_statement->execute([$message_entry->from_id, $message_entry->to_id]);
        
        if(!$is_executed){
            
            header('Content-type: application/json; charset=utf-8');
            echo json_encode(array("unblocked_error" => "user could not be unblocked"));
            return;
            
        }
        
        header('Content-type: application/json; charset=utf-8');
        echo json_encode(array("user_is_unblocked" => "user is unblocked"));
        return;
        
    }
    else if($ops === 3){
        
        $sql = "SELECT users.user_id, users.first_name, users.profile_image_name_thumbnail FROM users INNER JOIN blocked_users ON users.user_id = blocked_users.blocked_user_id WHERE blocked_users.user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$message_entry->from_id]);
        $query = $pdo_statement->fetchAll(PDO::FETCH_ASSOC);
            
        header('Content-type: application/json; charset=utf-8'); 
        echo json_encode($query);
        return;
    }
    
   
    
}catch(PDOException $pdo_error){
    
    echo $pdo_error->getMessage();
}




?>