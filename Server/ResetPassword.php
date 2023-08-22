<?php
date_default_timezone_set("America/New_York");
$path = '/var/www/exchange_project/';
require $path . 'vendor/autoload.php';
require_once 'User.php';
require_once 'FirebaseDB.php';
require_once 'Account.php';
require_once 'DatabaseLoginInfo.php';

$input = file_get_contents("php://input");
$input = json_decode($input, true);

if(empty($input['email'])){
    
    json_encode(array("forgot_password_error_100" => true));
    return;
}

if(empty($input['password'])){
    
    json_encode(array("forgot_password_error_101" => true));
    return;
}


if(strlen($input['password']) < 4 ){
    
    json_encode(array("forgot_password_error_102" => true));
    return;
}




$user = new User();
$user->email = $input['email'];
$user->password = password_hash($input['password'], PASSWORD_DEFAULT);

// Make sure the length is at 
try{
    
    $pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    

    $sql = "UPDATE users SET password = ? WHERE email = ?";
    $pdo_statement = $pdo->prepare($sql);
    $pdo_statement->execute([$user->password, $user->email]);

    
    echo json_encode(array("reset_password" => true));
    
    
}catch(PDOException $pdo_error){
    
    echo $pdo_error->getMessage();
}


?>
