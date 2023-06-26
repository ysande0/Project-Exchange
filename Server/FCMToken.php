<?php

$input = file_get_contents("php://input");
$input = json_decode($input, true);

$path = 'C:/Web/Project/TheExchange Project/';
require $path . 'vendor/autoload.php';
require 'DatabaseLoginInfo.php';

$from_uid = $input['uid'];
$fcm_token = $input['fcm_token'];

try{
    
    $pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    
    $sql = "UPDATE users SET fcm_token = ? WHERE unique_id = ?";
    $pdo_statement = $pdo->prepare($sql);
    $pdo_statement->execute([$fcm_token, $from_uid]);
    
    echo json_encode(array("is_fcm_token_generated" => true));
    
    
}catch(PDOException $pdo_error){
   
    echo $pdo_error->getMessage();
   
}



?>