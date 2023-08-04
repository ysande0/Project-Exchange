<?php

  
use function GuzzleHttp\Promise\queue;
  
$request_information = file_get_contents("php://input");
$request_information = json_decode($request_information, true);

require_once 'Game.php';

$have_set = array();
$want_set = array();

$have_set_arr = $request_information['Have'];
$want_set_arr = $request_information['Want'];

$user_latitude = $request_information['latitude'];   // Decimal degrees 
$user_longitude = $request_information['longitude']; // Decimal degrees 
$miles = $request_information['miles'];

require_once 'BoundingBox.php';

$bounding_box = new BoundingBox();
$bounding_box->radius = $miles;

$bounding_box->latitude1 =   $user_latitude - ($bounding_box->radius / 69);
$bounding_box->latitude2 =   $user_latitude + ($bounding_box->radius / 69);

$bounding_box->longitude1 =  $user_longitude - $bounding_box->radius / abs(cos(deg2rad($user_latitude)) * 69);
$bounding_box->longitude2 =  $user_longitude + $bounding_box->radius / abs(cos(deg2rad($user_latitude)) * 69);

for($i = 0; $i < sizeof($have_set_arr); $i++){
    
    $game = new Game();
    $arr = $have_set_arr[$i];
   
    $game->title = $arr['title'];
    $game->platform = $arr['platform'];
    $game->upc = $arr['upc'];
    
    array_push($have_set, $game);
}

for($i = 0; $i < sizeof($want_set_arr); $i++){
    
    $game = new Game();
    $arr = $want_set_arr[$i];
    
    $game->title = $arr['title'];
    $game->platform = $arr['platform'];
    $game->upc = $arr['upc'];
    
    array_push($want_set, $game);
    
}


require_once 'Product.php';


$cartesian_product = array();

for($i = 0; $i < sizeof($have_set); $i++){
    
    for($j = 0; $j < sizeof($want_set); $j++){
        
        $product = new Product();
        $product->have = $have_set[$i];
        $product->want = $want_set[$j];
        
        array_push($cartesian_product, $product);
    }
    
}

$query_arr = array();

require_once 'FirebaseDB.php';
require_once 'User.php';
require_once 'DatabaseLoginInfo.php';

$path = 'C:/Web/Project/TheExchange Project/';
require_once $path . 'vendor/autoload.php';

use Kreait\Firebase\Factory;
use Kreait\Firebase\ServiceAccount;
use Kreait\Firebase\Messaging\CloudMessage;

$service_account = ServiceAccount::fromJsonFile($path . 'secret/exchange-project-30ec1-4c320432756f.json');

$firebase = (new Factory())->withServiceAccount($service_account)->create();

$exchange_response = array();

$pdo = null;

try{
    
$pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

foreach($cartesian_product as $product){

    $sql = "SELECT users.first_name, users.last_name, software_inventory.title, software_inventory.manufacturer, software_inventory.platform, software_inventory.upc, users.fcm_token, users.unique_id , (ST_Distance_Sphere(POINT(ST_Y(locations.position), ST_X(locations.position)), POINT( ?, ?))) * 0.00062137 AS cdist 
FROM ((software_inventory INNER JOIN requesteditems ON software_inventory.user_id = requesteditems.user_id AND software_inventory.title = ? AND requesteditems.title = ? AND requesteditems.platform = ?) 
INNER JOIN users ON software_inventory.user_id = users.user_id) INNER JOIN hardwareinventory ON hardwareinventory.platform = software_inventory.platform AND software_inventory.title = ?
INNER JOIN locations ON users.user_id = locations.user_id
WHERE ST_Intersects(locations.position, PolygonFromText('Polygon(( " . $bounding_box->latitude1 . "   " . $bounding_box->longitude1 . " , " . $bounding_box->latitude1 . " " . $bounding_box->longitude2 . " , " . $bounding_box->latitude2 . "  " . $bounding_box->longitude2 . " , " . $bounding_box->latitude2 . "  " . $bounding_box->longitude1 . " ,  " .$bounding_box->latitude1 . " " . $bounding_box->longitude1 . "))'))
HAVING cdist <= '$bounding_box->radius' ";


    $pdo_statement = $pdo->prepare($sql);
    $pdo_statement->execute([$user_longitude, $user_latitude, $product->want->title, $product->have->title, $product->have->platform, $product->want->title]);
    $query = $pdo_statement->fetchAll(PDO::FETCH_ASSOC);


if(!$query)
    continue;

    for($i = 0; $i < sizeof($query); $i++){
  
        $arr = $query[$i];
        $user = new User();
        $user->first_name = $arr['first_name'];
        $user->last_name = $arr['last_name'];
        $user->fcm_token = $arr['fcm_token'];
        $user->uid = $arr['unique_id'];
        
        $game = new Game();
        $game->title = $arr['title'];
        $game->platform = $arr['platform'];

        $response['name'] = $user->first_name . ' ' . $user->last_name;
        $response['uid'] = $user->uid;
        $response['title'] = $game->title;
        $response['platform'] = $game->platform;
        
        array_push($exchange_response, $response);
        }

    }

}catch(PDOException $pdo_error){
    
    echo $pdo_error->getMessage();
}

if(sizeof($exchange_response) === 0){
    return;
}


$exchange_response = json_encode($exchange_response);
echo $exchange_response;


?>
