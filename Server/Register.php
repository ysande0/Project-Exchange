<?php
date_default_timezone_set("America/New_York");

$path = '/var/www/exchange_project/';
require $path . 'vendor/autoload.php';
require 'Token.php';

$registeration_information = file_get_contents("php://input");
$registeration_information = json_decode($registeration_information, true);


if(!isset($registeration_information['first_name']) || !isset($registeration_information['last_name']) 
    || !isset($registeration_information['email']) || !isset($registeration_information['password'])){
       
        header('Content-type: application/json; charset=utf-8');
        echo json_encode(array("register_error_100" => "error! data field(s) are empty"));
         return;
}


if(!isset($registeration_information['fcm_token'])){
    
    header('Content-type: application/json; charset=utf-8');
    echo json_encode(array("register_error_101" => "register_error_101"));
    return;
}

require_once 'User.php';
require_once 'DatabaseLoginInfo.php';

$user = new User();
$user->first_name = $registeration_information['first_name'];
$user->last_name = $registeration_information['last_name'];
$user->date_of_birth_ms = $registeration_information['date_of_birth_ms'];
$user->email = $registeration_information['email'];
$user->password = password_hash($registeration_information['password'], PASSWORD_DEFAULT);
$user->fcm_token = $registeration_information['fcm_token'];
$user->uid = $registeration_information['uid'];

if(empty($user->uid)){
    
    echo json_encode(array("register_error_102" => "register_error_102"));
    return;
}

$pdo = null;
try{
    
    $pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);


    $sql = "INSERT INTO users (first_name, last_name, date_of_birth, email, password, unique_id, fcm_token) 
VALUES(?, ?, ?, ?, ?, ?, ?)";
    $pdo_statement = $pdo->prepare($sql);
    $operation_status = $pdo_statement->execute([$user->first_name, $user->last_name, $user->date_of_birth_ms, $user->email, $user->password,
        $user->uid, $user->fcm_token]);
    
    if(!$operation_status){
        
        header('Content-type: application/json; charset=utf-8');
        echo json_encode(array("register_error_103" => "register_error_103"));
        return;
    }
    

    $sql = "INSERT INTO locations (position) VALUES (POINT(?, ?))";
    $pdo_statement = $pdo->prepare($sql);
    $operation_status = $pdo_statement->execute([0, 0]);
    
    if(!$operation_status){
        
        header('Content-type: application/json; charset=utf-8');
        echo json_encode(array("register_error_104" => "register_error_104"));
        return;
    }


}catch (PDOException $pdo_error){
    
    echo $pdo_error->getMessage();
    $pdo = null;
    header('Content-type: application/json; charset=utf-8');
    echo json_encode(array("register_error_105" => "register_error_105"));
    return;
    
}

$pdo = null;

header('Content-type: application/json; charset=utf-8');
echo json_encode(array("account_created" => true, "email" => $user->email, "password" => $user->password, "first_name" => $user->first_name,
 "last_name" => $user->last_name, "uid" => $user->uid));

?>
