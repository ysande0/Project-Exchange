<?php
date_default_timezone_set("America/New_York");
$path = '/var/www/exchange_project/';
//$path = 'C:/Web/Project/TheExchange Project/';

require_once 'DatabaseLoginInfo.php';
require_once 'User.php';

$logout_information = file_get_contents("php://input");
$logout_information = json_decode($logout_information, true);

if(!isset($logout_information['email']) || !isset($logout_information['user_id'])){
    
    header('Content-type: application/json; charset=utf-8');
    echo json_encode(array("logout_error_100" => "logout_error_100"));
    return;
    
}

$user = new User();
$user->email = $logout_information['email'];
$user->id = $logout_information['user_id'];

try{
    
    $pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    
    // reset users token time back to zero for each column
    /*
    $sql = "UPDATE tokens SET token = 0, token_issue_time = 0, token_start_valid_time = 0, token_end_valid_time = 0 WHERE user_id = ?";
    $pdo_statement = $pdo->prepare($sql);
    $is_executed = $pdo_statement->execute([$user->id]);

    if(!$is_executed){
        
        $pdo = null;
        header('Content-type: application/json; charset=utf-8');
        echo json_encode(array("logout_error_101" => true));
        return;
        
    }
        */
    
    $sql = "UPDATE users SET is_online = 0, last_modified = now(), last_logout = now() WHERE email = ? AND user_id = ?";
    $pdo_statement = $pdo->prepare($sql);
    $is_executed = $pdo_statement->execute([$user->email, $user->id]);
    
    if(!$is_executed){
        
        $pdo = null;
        header('Content-type: application/json; charset=utf-8');
        echo json_encode(array("logout_error_101" => "logout_error_101"));
        return;
        
    }
    
    header('Content-type: application/json; charset=utf-8');
    echo json_encode(array("logout_100" => true));
    return;
    
}catch(PDOException $pdo_error){
    
    $pdo_error->getMessage();
    $pdo = null;
    header('Content-type: application/json; charset=utf-8');
    echo json_encode(array("logout_error_103" => "logout_error_103"));
    return;
}


?>