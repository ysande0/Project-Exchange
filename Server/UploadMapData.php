<?php

$input = file_get_contents("php://input");
$input = json_decode($input, true);


$user_id = $input['uid'];
$file_map = $input['file_map']; 
$image_name = $input['image_name'];

// Check if user_id and or file_map are empty

if(empty($user_id) || empty($file_map) || empty($image_name)){
    
    return json_encode(array("map_error" => "An error occurred"));
}


$file_path = "http://192.168.1.242:80/Project/TheExchange%20Project/Images/" . $user_uid . "/map_images" . "/" . $image_name;
move_uploaded_file(base64_decode($file_map), $file_path);


echo json_encode(array("map_url" => $file_path));




?>