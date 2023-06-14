<?php



$path = 'C:/Web/Project/TheExchange Project/';
require $path . 'vendor/autoload.php';
require_once 'Account.php';
require_once 'User.php';

$user = new User();
$user->uid = $argv[1];
$user->email = $argv[2];
$user->password = $argv[3];
$user->first_name = $argv[4];
$user->last_name = $argv[5];

use Kreait\Firebase\Factory;
use Kreait\Firebase\ServiceAccount;
use Kreait\Firebase\Auth;

$service_account = ServiceAccount::fromJsonFile($path . 'secret/exchange-project-30ec1-4c320432756f.json');

$firebase = (new Factory())->withServiceAccount($service_account)->create();
$account = new Account($firebase, $user);
$account->create_account();

$email_sent = true;
$email_sent = $account->sendEmailConfirmation();


?>