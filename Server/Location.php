<?php

$path = 'C:/Web/Project/TheExchange Project/';
$private_crypt_key_path = $path . "keys/cryp_key.txt";

require_once 'DatabaseLoginInfo.php';
require_once 'Point.php';
require $path . 'vendor/autoload.php';
require 'Token.php';

$input = file_get_contents("php://input");
$input = json_decode($input, true);


$crypt_key = file_get_contents($private_crypt_key_path);

$user_id = $input['id'];
$user_token = $input['access_token'];
$point = new Point($input['latitude'], $input['longitude']);

//$user_token = Crypto::decrypt($user_token, Key::loadFromAsciiSafeString($crypt_key));

if($point->latitude === null || $point->longitude === null || $user_id === null){
    echo json_encode(array("location_error_input" => true));
    return;
}


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

        $sql = "UPDATE locations SET position = POINT(?, ?) WHERE user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $is_location_updated = $pdo_statement->execute([$point->latitude, $point->longitude, $user_id]);
        
        if($is_location_updated){
            
            echo json_encode(array("location_error_2" => false));
            return;
            
        }
        else if(!$is_location_updated){
        
            echo json_encode(array("location_error_2" => true));
            return;
            
        }
        
        
      
        

    
}catch(PDOException $pdo_error){
    
    echo $pdo_error->getMessage();
    
}

$pdo = null;

?>