<?php
$path = '/var/www/exchange_project/';
$private_crypt_key_path = $path . "keys/cryp_key.txt";

require_once 'DatabaseLoginInfo.php';
require_once 'BoundingBox.php';
require $path . 'vendor/autoload.php';
require 'Token.php';

$input = file_get_contents("php://input");
$input = json_decode($input, true);

$pdo = null;
$headers = apache_request_headers();


if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    
    $user_id = $_GET['id'];

$user_latitude  = $_GET['latitude'];
$user_longitude = $_GET['longitude'];
$miles = $_GET['radius'];
    
}


if ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    
    $user_id = $input['id'];
    
    $user_latitude  = $input['latitude'];
    $user_longitude = $input['longitude'];
    
}

$query;
try{
    

    $pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    
    if($_SERVER['REQUEST_METHOD'] === 'PUT'){
        

        $sql = "UPDATE locations SET position = (POINT(? , ?)) WHERE user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $is_executed = $pdo_statement->execute([$user_latitude, $user_longitude, $user_id]);

        
        if(!$is_executed){
            
            $pdo = null;
            header('Content-type: application/json; charset=utf-8');
            echo json_encode(array(array("home_error_100" => "error: Location Failure")));
            $pdo = null;
            return;
        }
        
        header('Content-type: application/json; charset=utf-8');
        echo json_encode(array("location_updated" => true));
        $pdo = null;
        return;
        
    }
 
    if($_SERVER['REQUEST_METHOD'] === 'GET'){
        
        $bounding_box = new BoundingBox();
        $bounding_box->radius = $miles;
        
        $bounding_box->latitude1 =   $user_latitude - ($bounding_box->radius / 69);
        $bounding_box->latitude2 =   $user_latitude + ($bounding_box->radius / 69);
        
        $bounding_box->longitude1 =  $user_longitude - $bounding_box->radius / abs(cos(deg2rad($user_latitude)) * 69);
        $bounding_box->longitude2 =  $user_longitude + $bounding_box->radius / abs(cos(deg2rad($user_latitude)) * 69);
        
        $sql = "SELECT available_results.user_id,
    available_results.first_name, available_results.fcm_token, available_results.title AS title, available_results.platform,
    available_results.publisher, available_results.developer, available_results.upc,
    available_results.software_image_name_thumbnail, available_results.software_image_name_full, available_results.user_description,
    available_results.profile_image_name_thumbnail, available_results.profile_image_name_full, available_results.cdist FROM (
            
    SELECT users.user_id, users.first_name, software_inventory.title, software_inventory.platform, software_inventory.publisher, software_inventory.developer, software_inventory.upc,  software_inventory.software_image_name_thumbnail, software_inventory.software_image_name_full, software_inventory.user_description, users.profile_image_name_thumbnail, users.profile_image_name_full, users.fcm_token, (ST_Distance_Sphere(POINT(ST_Y(locations.position), ST_X(locations.position)), POINT(?, ?))) * 0.000621371192 AS cdist
    FROM (users INNER JOIN software_inventory ON users.user_id = software_inventory.user_id AND NOT users.user_id = ?) INNER JOIN locations ON users.user_id = locations.user_id INNER JOIN hardware_inventory ON software_inventory.user_id = hardware_inventory.user_id AND hardware_inventory.platform = software_inventory.platform
    WHERE ST_Intersects(locations.position, ST_PolygonFromText('Polygon(( " . $bounding_box->latitude1 . "   " . $bounding_box->longitude1 . " , " . $bounding_box->latitude1 . " " . $bounding_box->longitude2 . " , " . $bounding_box->latitude2 . "  " . $bounding_box->longitude2 . " , " . $bounding_box->latitude2 . "  " . $bounding_box->longitude1 . " ,  " .$bounding_box->latitude1 . " " . $bounding_box->longitude1 . "))')) HAVING cdist <= ?
        
        
    ) AS available_results INNER JOIN hardware_inventory ON available_results.platform = hardware_inventory.platform AND hardware_inventory.user_id = ? GROUP BY available_results.user_id";
    
        $pdo_statement = $pdo->prepare($sql);
        $is_executed = $pdo_statement->execute([$user_longitude, $user_latitude, $user_id, $bounding_box->radius, $user_id]);
        
        if(!$is_executed){
            
            $pdo = null;
            header('Content-type: application/json; charset=utf-8');
            echo json_encode(array(array("home_error_101" => "error: Query Failure")));
            return;
        }
        
        $query = $pdo_statement->fetchAll(PDO::FETCH_ASSOC);

      echo json_encode($query);
     
    }

}catch(PDOException $pdo_error){
    
    print_r($pdo_error->getMessage());
    echo json_encode(array(array("home_error_102" => "error: Database Connection")));
    $pdo = null;
    return;
    
}



?>
