<?php
date_default_timezone_set("America/New_York");
/*
 * Google recommends using Firebase Client SDK for log in and out 
 * 
 * */

$path = '/var/www/exchange_project/';
//$path = 'C:/Web/Project/TheExchange Project/';

require $path . 'vendor/autoload.php';
require 'User.php';
require 'Game.php';
require 'Token.php';


$login_information = file_get_contents("php://input");
$login_information = json_decode($login_information, true);

/*
 * login_error_100 - data fields are empty
 * login_error_101 - Email spacing issues
 * login_error_102 - Could not sign in account or verify email
 * login_error_103 - Server could not deliver data because it did not meet a requirement 
 * login_error_104 - Password is wrong
 * 
 * */


if(!isset($login_information['email']) || !isset($login_information['password'])){
    header('Content-type: application/json; charset=utf-8');
    echo json_encode(array("login_error_100" => "Error! Data field(s) are empty"));
    return;
    
}


if(!isset($login_information['fcm_token'])){
    
    header('Content-type: application/json; charset=utf-8');
    echo json_encode(array("login_error_101" => "Error! token was not generated. please try again later"));
    return;
}

if(!isset($login_information['is_email_verified'])){
    
    header('Content-type: application/json; charset=utf-8');
    echo json_encode(array("login_error_106" => "unverified account. please verify your email"));
    return;
    
}



$user = new User();
$user->email = $login_information['email'];
$user->password = $login_information['password'];
$user->fcm_token = $login_information['fcm_token'];
$user->is_email_verified = $login_information['is_email_verified'];


if($user->is_email_verified === false){
    
    header('Content-type: application/json; charset=utf-8');
    $json_response = json_encode(['login_error_102' => "unverified account. please verify your email"]);
    echo $json_response;
    return;
}


require_once 'DatabaseLoginInfo.php';

$pdo = null;
$user_hardware = array();
$user_software = array();
$has_hardware = false;
$has_software = false;

try{
    
    $pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    

    $sql = "SELECT user_id, first_name, last_name, password, fcm_token, unique_id, active, profile_image_name_thumbnail, profile_image_name_full FROM users WHERE email = ?";
    $pdo_statement = $pdo->prepare($sql);
    $pdo_statement->execute([$user->email]);
    $query = $pdo_statement->fetchAll(PDO::FETCH_ASSOC);
   
    if(empty($query)){
        
        $pdo = null;
        header('Content-type: application/json; charset=utf-8');
        echo json_encode(array("login_error_105" => "invalid credentials"));
        return;
    }
   
    $query_results = $query[0];
   

    if( !(password_verify($user->password, $query_results['password']))){
        
        $pdo = null;
        header('Content-type: application/json; charset=utf-8');
        echo json_encode(array("login_error_104" => "invalid credentials"));
        return;
    }

    $user->id =  $query_results['user_id'];
    $user->uid = $query_results['unique_id'];
    $user->first_name = $query_results['first_name'];
    $user->last_name = $query_results['last_name'];
    $user->is_active =  (int) $query_results['active'];
    ///$user->is_active =  1;
   // $user->is_active =  0;
    $user->profile_image_name_thumbnail = $query_results['profile_image_name_thumbnail'];
    $user->profile_image_name_full = $query_results['profile_image_name_full'];
 
    $token = new Token($pdo);
    $token->set_user_id($user->id);
    $user->access_token = $token->refresh_token();
    
    if($user->is_email_verified === true && $user->is_active === 1){

        
        // Experienced user signed in
   
            $sql = "UPDATE users SET fcm_token = ?, is_online = 1, last_modified = now(), last_login = now() WHERE email = ?";
            $pdo_statement = $pdo->prepare($sql);
            $pdo_statement->execute([$user->fcm_token, $user->email]);
            
            $sql = "SELECT manufacturer, platform FROM hardware_inventory WHERE user_id = ?";
            $pdo_statement = $pdo->prepare($sql);
            $pdo_statement->execute([$user->id]);
            $query_hardware = $pdo_statement->fetchAll(PDO::FETCH_ASSOC);
            $user_hardware = $query_hardware;
            
            
            $sql = "SELECT title, publisher, developer, platform, upc, software_image_name_thumbnail, software_image_name_full, user_description, software_uid FROM software_inventory WHERE user_id = ?";
            $pdo_statement = $pdo->prepare($sql);
            $pdo_statement->execute([$user->id]);
            $query_software = $pdo_statement->fetchAll(PDO::FETCH_ASSOC);
            
            for($i = 0; $i < sizeof($query_software); $i++){
                
                $game = new Game();
                $game->title = $query_software[$i]['title'];
                $game->publisher = $query_software[$i]['publisher'];
                $game->developer = $query_software[$i]['developer'];
                $game->platform = $query_software[$i]['platform'];
                $game->upc = $query_software[$i]['upc'];
                $game->image_name_thumbnail = $query_software[$i]['software_image_name_thumbnail'];
                $game->image_name_full = $query_software[$i]['software_image_name_full'];
                $game->user_description = $query_software[$i]['user_description'];
                $game->software_uid = $query_software[$i]['software_uid'];
                
                $item_arr = array("title" => $game->title, "publisher" => $game->publisher, "developer" => $game->developer, "platform" => $game->platform,
                    "upc" => $game->upc, "software_image_name_thumbnail" => $game->image_name_thumbnail, "software_image_name_full" => $game->image_name_full, 
                "user_description" => $game->user_description, "software_uid" => $game->software_uid);
                array_push($user_software, $item_arr);
            }
        
        
        // Encrypt Access token 
       // $user->access_token = Crypto::encrypt($user->access_token, Key::loadFromAsciiSafeString($crypt_key));
        //print_r("Experienced Access Token: " . $user->access_token);
        
        // NOTE: Hash password
        $json_response = null;
        
        header('Content-type: application/json; charset=utf-8');
        $json_response = json_encode(['Experienced' => true, "login_status" => true, 'user_id' => $user->id, 'first_name' => $user->first_name, 'last_name' => $user->last_name, 'email' => $user->email,
            'uid' => $user->uid , "fcm_token" => $user->fcm_token , 'access_token' => $user->access_token, "profile_image_name_thumbnail" => $user->profile_image_name_thumbnail, "profile_image_name_full" => $user->profile_image_name_full, 
            'user_hardware' => $user_hardware, 'user_software' => $user_software]);

        echo $json_response;
        $pdo = null;
        return;
    }
    else if($user->is_email_verified === true && $user->is_active === 0){
           
        // Beginner user signed in

        $sql = "UPDATE users SET active = 1, is_online = 1, last_modified = now(), last_login = now() WHERE user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$user->id]);

        /*
        $software_image_path = "C:/Web/Project/TheExchange Project/Images/" . $user->uid . "/software_images";
        $profile_image_path = "C:/Web/Project/TheExchange Project/Images/" . $user->uid . "/profile_image";

        
        if(!file_exists($software_image_path)){
            mkdir($software_image_path, 0777, true);
        }
        
        if(!file_exists($profile_image_path)){
            mkdir($profile_image_path, 0777, true);
        }
       */
        
      //  $user->access_token = Crypto::encrypt($user->access_token, Key::loadFromAsciiSafeString($crypt_key));
     //   print_r("Beginner Access Token: " . $user->access_token . "\n");

        // NOTE: Hash password
        // NOTE: Encrypt Auth Token
        header('Content-type: application/json; charset=utf-8');
        $json_response = json_encode(['Beginner' => true, "login_status" => true, 'user_id' => $user->id,'first_name' => $user->first_name, 'last_name' => $user->last_name, 'email' => $user->email,
            'uid' => $user->uid, "fcm_token" => $user->fcm_token ,"profile_image_name_thumbnail" => $user->profile_image_name_thumbnail, 
            "profile_image_name_full" => $user->profile_image_name_full, 'access_token' => $user->access_token]);


        echo $json_response;
        $pdo = null;
        return;
    }
       
}catch(PDOException $pdo_error){
    
   
    echo $pdo_error->getMessage();
}

header('Content-type: application/json; charset=utf-8');
echo json_encode(array("login_error_103" => "access denied"));
$pdo = null;
return;

?>