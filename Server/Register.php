<?php
date_default_timezone_set("America/New_York");
/* IMPORTANT NOTE 
 * 
 * 1) Email verification mechanism 
 * 
 * */


/* Error List:
 * 
 * register_error_100 -> Data Entry
 * register_error_101 -> Name identification
 * register_error_102 -> Email
 * register_error_103 -> Password
 * 
 * */

// IMPORTANT NOTE: Add mechanism that verifies email is real before inserting data to database.
// Send user an email verifying they wanted to create an account. Disregard
// Make sure each email is unique 

$path = '/var/www/exchange_project/';
//$path = 'C:/Web/Project/TheExchange Project/';
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

/*
if(preg_match_all("/[\d\W]/", $registeration_information['first_name'])
    || preg_match_all("/[\d\W]/", $registeration_information['last_name'])){
    
        header('Content-type: application/json; charset=utf-8');
       echo json_encode(array("register_error_101" => "First or last name has inappropriate symbols"));
       return;
}

if(preg_match_all("/[\s]/", $registeration_information['email'])){
    
    header('Content-type: application/json; charset=utf-8');
    echo json_encode(array("register_error_102" => "Email has inappropriate symbols"));
    return;
}
*/

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


/*
use Kreait\Firebase\Factory;
use Kreait\Firebase\ServiceAccount;
use Kreait\Firebase\Auth;



$service_account = ServiceAccount::fromJsonFile($path . 'secret/exchange-project-30ec1-4c320432756f.json');

$firebase = (new Factory())->withServiceAccount($service_account)->create();
$account = new Account($firebase, $user);
$account->create_account();
*/
//print_r("--> UID: " . $user->uid . " Time:  " . date('h:m:s', time()) . " \n");

if(empty($user->uid)){
    
    echo json_encode(array("register_error_102" => "register_error_102"));
    return;
}

/*
if(!($user->password)){
    
    echo json_encode(array("register_error_103" => "Authentication issue occurred"));
    return;
}
*/
//$user->access_token = $account->get_access_token();

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
       
    /*
    $token = new Token($pdo);
    $token->create_token_table_entry();
    */

    $sql = "INSERT INTO locations (position) VALUES (POINT(?, ?))";
    $pdo_statement = $pdo->prepare($sql);
    $operation_status = $pdo_statement->execute([0, 0]);
    
    if(!$operation_status){
        
        header('Content-type: application/json; charset=utf-8');
        echo json_encode(array("register_error_104" => "register_error_104"));
        return;
    }
    
    
 /*
    $sql = "INSERT INTO user_ratings (aggregate_score, number_transactions, rating) VALUES (0, 0, 0)";
    $pdo_statement = $pdo->prepare($sql);
    $operation_status = $pdo_statement->execute();
        
    if(!$operation_status){
        
        header('Content-type: application/json; charset=utf-8');
        echo json_encode(array("account_created" => false));
        return;
    }

*/

}catch (PDOException $pdo_error){
    
    echo $pdo_error->getMessage();
    $pdo = null;
    header('Content-type: application/json; charset=utf-8');
    echo json_encode(array("register_error_105" => "register_error_105"));
    return;
    
}

$pdo = null;

/*
$email_sent = true;
$email_sent = $account->sendEmailConfirmation();
*/

//exec("php AccountCreation.php {$user->uid} {$user->email} {$user->password} {$user->first_name} {$user->last_name} > /dev/null &");

header('Content-type: application/json; charset=utf-8');
echo json_encode(array("account_created" => true, "email" => $user->email, "password" => $user->password, "first_name" => $user->first_name,
 "last_name" => $user->last_name, "uid" => $user->uid));

?>