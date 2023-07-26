<?php

require 'DatabaseLoginInfo.php';

$input = file_get_contents("php://input");
$input = json_decode($input, true);

$user_uid = $input['uid'];
$user_id = $input['id'];
$profile_image = $input['image_encoded'];
$profile_image_name = $input['image_name'];


if(empty($user_uid) || empty($profile_image) || empty($profile_image_name))
    echo json_encode(array("image_upload_error" => true));


$profile_image_path =  "C:/Web/Project/TheExchange Project/Images/". $user_uid . "/" . "profile_image";
$profile_image_files = glob($profile_image_path . "/*");

foreach ($profile_image_files as $file){
    
    if(is_file($file))
        unlink($file);
    
}


$profile_image_path = "C:/Web/Project/TheExchange Project/Images/" . $user_uid. "/" . "profile_image" . "/" . $profile_image_name;

file_put_contents($profile_image_path, base64_decode($profile_image));

$profile_image_path = "http://192.168.1.242:80/Project/TheExchange%20Project/Images/" . $user_uid . "/" . "profile_image" . "/" . $profile_image_name;
try{
    
    $pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $token = new Token($pdo);
    $token->set_user_id($user_id);
    $token->set_token($user_token);
    $session_timeout = $token->validate();
    
    if($session_timeout){
        
        echo json_encode(array("session_timeout" => true));
        return;
    }
    
    $sql = "UPDATE users SET profile_image_url = ? WHERE unique_id = ? ";
    $pdo_statement = $pdo->prepare($sql);
    $pdo_statement->execute([$profile_image_path, $user_uid]);
    
    echo json_encode(array("image_upload_error" => false));
    return;
}catch (PDOException $pdo_error){
   
    $pdo_error->getMessage(); 
    echo json_encode(array("image_upload_error" => true));
    return;
}



?>