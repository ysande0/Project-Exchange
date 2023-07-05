<?php

/*
 * 1 - Insert
 * 2 - Delete
 * 3 - Update
 *
 * 100 - Hardware
 * 101 - Software
 *
 * */

$path = '/var/www/exchange_project/';
//$path = 'C:/Web/Project/TheExchange Project/';
//$private_crypt_key_path = $path . "keys/cryp_key.txt";

$input = file_get_contents("php://input");
$input = json_decode($input, true);

require $path . 'vendor/autoload.php';
require 'DatabaseLoginInfo.php';
require 'ImageResizer.php';
require 'Game.php';
require 'Hardware.php';
require 'Token.php';
require 'Inventory.php';

use Aws\S3\S3Client;
use Aws\S3\Exception\S3Exception;


//$crypt_key = file_get_contents($private_crypt_key_path);
$headers = apache_request_headers();
$pdo = null;
$hardware = null;
$inventory_category = null;
$inventory_operation = null;
$user_uid = null;
$user_id = null;
$user_token = null;
$dpi = null;

//$user_token = Crypto::decrypt($user_token, Key::loadFromAsciiSafeString($crypt_key));

$user_token = $headers['Authorization'];
if ($_SERVER['REQUEST_METHOD'] === 'GET') {

    $inventory_category = (int) $_GET['category'];
    $inventory_operation = (int) $_GET['ops'];
    $user_id = $_GET['id'];
  //  $user_token = $_GET['access_token'];
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
             
             $inventory_category = $input['category'];
             $inventory_operation = $input['ops'];
             $user_uid = $input['uid'];
             $user_id = $input['id'];
    //         $user_token = $input['access_token'];
}


try{
    
    $pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
/*
    $token = new Token($pdo);
    $token->set_user_id($user_id);
    $token->set_token($user_token);
    $session_timeout = $token->is_token_expired();
    */
    if($session_timeout){
        
        echo json_encode(array("session_timeout" => true));
        $pdo = null;
        return;
    }

    
}catch(PDOException $pdo_error){
    
    if($inventory_category === 101 && $inventory_operation === 3){
        echo json_encode(array(array("database_connection_error" => "error: Database Connection")));
        $pdo = null;
        return;
    }
    
    echo json_encode(array("database_connection_error" => "error: Database Connection"));
    $pdo = null;
    return;
    
}


$hardware_platforms  = array();
 if($inventory_category === 100){
     
        
         $hardware = new Hardware();
         $hardware->manufacturer = $input['manufacturer'];
         $hardware->platform = $input['platform'];
         

        
}
else if($inventory_category === 101){
    
   
            $game = new Game();
            // Rename manufacturer to Game Publisher. Add Game Developer (Studio)
        if($inventory_operation === 1){
          // Insert
          
            $game->publisher = $input['publisher'];
            $game->developer = $input['developer'];
            $game->title = $input['title'];
            $game->platform = $input['platform'];
            $game->upc = $input['upc'];
            $game->user_description = $input['user_description']; 
            $game->game_image_encoded_thumbnail = $input['encoded_bitmap_thumbnail'];
            $game->game_image_encoded_full = $input['encoded_bitmap_full'];
            $game->image_name_thumbnail = $input['image_name_thumbnail'];
            $game->image_name_full = $input['image_name_full'];
            $game->software_uid = $input['software_uid'];
            $dpi = $input['dpi'];
         
        }
        else if($inventory_operation === 2){

            // Delete
  
              
               $game->software_uid = $input['software_uid'];
               $game->software_image_thumbnail_url = $input['software_image_thumbnail_url'];
               $game->software_image_full_url = $input['software_image_full_url'];
            
        }
        else if($inventory_operation === 3){
            
       
            
        }
        else if($inventory_operation === 4){
            
            // Update  
            
            $game->title = $input['title'];
            $game->publisher = $input['publisher'];
            $game->developer = $input['developer'];
            $game->platform = $input['platform'];
            $game->upc = $input['upc'];
            $game->user_description = $input['user_description'];
            
            if(isset($input['encoded_bitmap_thumbnail']))
                $game->game_image_encoded_thumbnail = $input['encoded_bitmap_thumbnail'];
            
            if(isset($input['encoded_bitmap_full']))
                $game->game_image_encoded_full = $input['encoded_bitmap_full'];
           
            
            $game->image_name_thumbnail = $input['software_image_name_thumbnail'];
            $game->image_name_full = $input['software_image_name_full'];
            $game->software_uid = $input['software_uid'];
            $dpi = $input['dpi'];
        }
        
    }
    

try{
    
    
    $operation_status = null;
    $inventory = new Inventory();  
    
    if($inventory_category === 100){
     // Hardware    

        
        if($inventory_operation === 1){
            
          //  print_r(sizeof($hardware_platforms));
            
             $operation_status = $inventory->insert_hardware($pdo, $user_id, $hardware);
          
             if($operation_status === false){
               echo json_encode(array("transaction_hardware_error_100_1" => "error: could not save hardware")) ;
               $pdo = null;
               return;
             }
                
  
            
            echo json_encode(array("transaction_hardware_100_1" => true));    
            $pdo = null;
            return;
        }
        else if($inventory_operation === 2){
            
            
                    $operation_status = $inventory->delete_hardware($pdo, $user_id, $hardware);
                    
                    if($operation_status === false){
                        echo json_encode(array("transaction_hardware_error_100_2" => "error: could not delete hardware"));
                        $pdo = null;
                        return;
                    }
                    
        
            
            echo json_encode(array("transaction_hardware_100_2" => true));
            $pdo = null;
            return;
        }
        else if($inventory_operation === 3){
           
            $user_platforms = array();
            $query = $inventory->select_hardware($pdo, $user_id);
            
            if($operation_status === false){
                
                echo json_encode(array("transaction_hardware_error_100_3" => "error: could not retrieve hardware data"));
                $pdo = null;
                return;
            }
            
            for($i = 0; $i < sizeof($query); $i++){
                
                $arr = array();
                $arr = $query[$i];
                
                $platform = new Hardware();
                $platform->manufacturer = $arr['manufacturer'];
                $platform->platform = $arr['platform'];
               
                $platform_information = array("manufacturer" => $platform->manufacturer, "platform" => $platform->platform);
                
                array_push($user_platforms, $platform_information);
                
            }
            
            echo json_encode($user_platforms);
            $pdo = null;
            return;
            
        }

    }
    
    else if($inventory_category === 101){
        

        $inventory = new Inventory();
        $image_resizer = new ImageResizer();

        $client = new S3Client([
            'version' => 'latest',
            'region'  => 'us-east-1',
            'endpoint' => 'https://nyc3.digitaloceanspaces.com',
            'credentials' => [
                'key'    => '6522JNRTCYS4ALSTHO2P',
                'secret' => 'dfOq3ks6RKmfXZEWmbl56vUECzw7IJoF38A+GcL0pvg',
            ],
        ]);
        
        $bucket = 'exchangeproject';
        
        if($inventory_operation === 1){
            // Insert
/*
            $game->software_image_thumbnail_url = "http://192.168.1.242:80/Project/TheExchange%20Project/img/" . $game->image_name_thumbnail;
            $game->software_image_full_url = "http://192.168.1.242:80/Project/TheExchange%20Project/img/" . $game->image_name_full;
         */              

                    $image_file_path = null;
                    /*
                    $xxxhdpi_path = "C:/Web/Project/TheExchange Project/img/xxxhdpi/";
                    $xxhdpi_path = "C:/Web/Project/TheExchange Project/img/xxhdpi/";
                    $xhdpi_path = "C:/Web/Project/TheExchange Project/img/xhdpi/";
                    $hdpi_path = "C:/Web/Project/TheExchange Project/img/hdpi/";
                    $mdpi_path = "C:/Web/Project/TheExchange Project/img/mdpi/";
                    $ldpi_path = "C:/Web/Project/TheExchange Project/img/ldpi/";
                   */
                    
                    $xxxhdpi_path = "img/xxxhdpi/";
                    $xxhdpi_path = "img/xxhdpi/";
                    $xhdpi_path = "img/xhdpi/";
                    $hdpi_path = "img/hdpi/";
                    $mdpi_path = "img/mdpi/";
                    $ldpi_path = "img/ldpi/";
        
                    if($dpi <= 120){
                        
                        // [LDPI]
                        /*
                        file_put_contents($ldpi_path . $game->image_name_full, base64_decode($game->game_image_encoded_full));
                        file_put_contents($ldpi_path . $game->image_name_thumbnail, base64_decode($game->game_image_encoded_thumbnail));
                       */
                        $image_file_path = $ldpi_path . $game->image_name_full;
                        $result_image_full = $client->putObject( array(
                            
                            'Bucket' => 'exchangeproject',
                            'Key' => $ldpi_path. $game->image_name_full,
                            'Body' => base64_decode($game->game_image_encoded_full),
                            'ACL' => 'public-read',
                            'ContentType' => 'image/jpeg'
                        ));
                          
                        $result_image_thumbnail = $client->putObject( array(
                            
                            'Bucket' => 'exchangeproject',
                            'Key' => $ldpi_path . $game->image_name_thumbnail,
                            'Body' => base64_decode($game->game_image_encoded_thumbnail),
                            'ACL' => 'public-read',
                            'ContentType' => 'image/jpeg'
                        ));
                        
                        
                        
                    }else if($dpi > 120 && $dpi <= 160){
                        
                        // [MDPI]
                        /*
                        file_put_contents($mdpi_path . $game->image_name_full, base64_decode($game->game_image_encoded_full));
                        file_put_contents($mdpi_path . $game->image_name_thumbnail, base64_decode($game->game_image_encoded_thumbnail));
        */
                        $image_file_path = $mdpi_path . $game->image_name_full;
                     
                        $result_image_full = $client->putObject( array(
                            
                            'Bucket' => 'exchangeproject',
                            'Key' => $mdpi_path . $game->image_name_full,
                            'Body' => base64_decode($game->game_image_encoded_full),
                            'ACL' => 'public-read',
                            'ContentType' => 'image/jpeg'
                        ));
                        
                        $result_image_thumbnail = $client->putObject( array(
                            
                            'Bucket' => 'exchangeproject',
                            'Key' => $mdpi_path . $game->image_name_thumbnail,
                            'Body' => base64_decode($game->game_image_encoded_thumbnail),
                            'ACL' => 'public-read',
                            'ContentType' => 'image/jpeg'
                        ));
                    }
                    else if($dpi > 160 &&  $dpi <= 240){
                        
                        // [HDPI]
                        /*
                        file_put_contents($hdpi_path . $game->image_name_full, base64_decode($game->game_image_encoded_full));
                        file_put_contents($hdpi_path . $game->image_name_thumbnail, base64_decode($game->game_image_encoded_thumbnail));
                  */
                        $image_file_path = $hdpi_path . $game->image_name_full;
                       
                        $result_image_full = $client->putObject( array(
                            
                            'Bucket' => 'exchangeproject',
                            'Key' => $hdpi_path . $game->image_name_full,
                            'Body' => base64_decode($game->game_image_encoded_full),
                            'ACL' => 'public-read',
                            'ContentType' => 'image/jpeg'
                        ));
                        
                        $result_image_thumbnail = $client->putObject( array(
                            
                            'Bucket' => 'exchangeproject',
                            'Key' => $hdpi_path.  $game->image_name_thumbnail,
                            'Body' => base64_decode($game->game_image_encoded_thumbnail),
                            'ACL' => 'public-read',
                            'ContentType' => 'image/jpeg'
                        ));
                       
                    }
                    else if($dpi > 240 && $dpi <= 320){
                        
                        // [XHDPI]
                      /* 
                        file_put_contents($xhdpi_path . $game->image_name_full, base64_decode($game->game_image_encoded_full));
                        file_put_contents($xhdpi_path . $game->image_name_thumbnail, base64_decode($game->game_image_encoded_thumbnail));
                        */
                        $image_file_path = $xhdpi_path . $game->image_name_full;
                      
                        $result_image_full = $client->putObject( array(
                            
                            'Bucket' => 'exchangeproject',
                            'Key' => $xhdpi_path . $game->image_name_full,
                            'Body' => base64_decode($game->game_image_encoded_full),
                            'ACL' => 'public-read',
                            'ContentType' => 'image/jpeg'
                        ));
                        
                        $result_image_thumbnail = $client->putObject( array(
                            
                            'Bucket' => 'exchangeproject',
                            'Key' => $xhdpi_path . $game->image_name_thumbnail,
                            'Body' => base64_decode($game->game_image_encoded_thumbnail),
                            'ACL' => 'public-read',
                            'ContentType' => 'image/jpeg'
                        ));
                        
                    }
                    else if($dpi > 320 && $dpi <= 480){
                        
                        
                        // [XXHDPI]
                        /*
                        file_put_contents($xxhdpi_path . $game->image_name_full, base64_decode($game->game_image_encoded_full));
                        file_put_contents($xxhdpi_path . $game->image_name_thumbnail, base64_decode($game->game_image_encoded_thumbnail));
                        */
                        $image_file_path = $xxhdpi_path . $game->image_name_full;
                        $result_image_full = $client->putObject( array(
                            
                            'Bucket' => 'exchangeproject',
                            'Key' => $xxhdpi_path . $game->image_name_full,
                            'Body' => base64_decode($game->game_image_encoded_full),
                            'ACL' => 'public-read',
                            'ContentType' => 'image/jpeg'
                        ));
                        
                        $result_image_thumbnail = $client->putObject( array(
                            
                            'Bucket' => 'exchangeproject',
                            'Key' => $xxhdpi_path. $game->image_name_thumbnail,
                            'Body' => base64_decode($game->game_image_encoded_thumbnail),
                            'ACL' => 'public-read',
                            'ContentType' => 'image/jpeg'
                        ));
                        
                    }
                    else if($dpi > 480 && $dpi <= 640){
                        
                        // [XXXHDPI]
                        /*
                        file_put_contents($xxxhdpi_path . $game->image_name_full, base64_decode($game->game_image_encoded_full));
                        file_put_contents($xxxhdpi_path . $game->image_name_thumbnail, base64_decode($game->game_image_encoded_thumbnail));
               */
                        $image_file_path = $xxxhdpi_path . $game->image_name_full;
                        $result_image_full = $client->putObject( array(
                            
                            'Bucket' => 'exchangeproject',
                            'Key' => $xxxhdpi_path . $game->image_name_full,
                            'Body' => base64_decode($game->game_image_encoded_full),
                            'ACL' => 'public-read',
                            'ContentType' => 'image/jpeg'
                        ));
                        
                        $result_image_thumbnail = $client->putObject( array(
                            
                            'Bucket' => 'exchangeproject',
                            'Key' => $xxxhdpi_path . $game->image_name_thumbnail,
                            'Body' => base64_decode($game->game_image_encoded_thumbnail),
                            'ACL' => 'public-read',
                            'ContentType' => 'image/jpeg'
                        ));
                    }
                  

            $operation_software_status = $inventory->insert_software($pdo, $user_id, $game);
            $operation_resizer_status  = $image_resizer->insert_image($pdo, $image_file_path, $game->image_name_full, $game->image_name_thumbnail, $dpi);
            
            if($operation_software_status === false){
                
                echo json_encode(array("transaction_software_error_101_1" => "error: could not save software : software"));
                $pdo = null;
                return;
                
            }
            
            if($operation_status === false || $operation_resizer_status === false){
                
                echo json_encode(array("transaction_software_error_101_1" => "error: could not save software : image_resizer"));
                $pdo = null;
                return;
            }
            
         
            
          /*
            $game->software_image_thumbnail_url = "C:/Web/Project/TheExchange Project/img/" . $game->image_name_thumbnail;
            $game->software_image_full_url = "C:/Web/Project/TheExchange Project/img/" . $game->image_name_full;
     
            // If the function below does not work. Try using a relative path starting from TheExchange Project 
            file_put_contents($game->software_image_thumbnail_url, base64_decode($game->game_image_encoded_thumbnail));
            file_put_contents($game->software_image_full_url, base64_decode($game->game_image_encoded_full));
            */
            echo json_encode(array("transaction_software_101_1" => true, 
                "software_image_name_thumbnail" => $game->image_name_thumbnail,
                "software_image_name_full" => $game->image_name_full));
    
    
            $pdo = null;
            return;
            
        }
        else if($inventory_operation === 2){
            // Delete

            $game->image_name_thumbnail = basename($game->software_image_thumbnail_url);
            $game->image_name_full = basename($game->software_image_full_url);
            
        //    echo "Game Name Full: " . $game->image_name_full . " " . " Game Name Thumbnail: " . $game->image_name_thumbnail;

            $operation_software_status = $inventory->delete_software($pdo, $user_id, $game);
        
            if($operation_software_status === false){
                echo json_encode(array("transaction_software_error_101_2" => "error: could not delete software"));
                $pdo = null;
                return;
            }
    
            // [LDPI]
            /*
            $game->software_image_thumbnail_url = "C:/Web/Project/TheExchange Project/img/ldpi/" . $game->image_name_thumbnail;
            $game->software_image_full_url = "C:/Web/Project/TheExchange Project/img/ldpi/" . $game->image_name_full;
*/
            $game->software_image_thumbnail_url = "img/ldpi/" . $game->image_name_thumbnail;
            $game->software_image_full_url = "img/ldpi/" . $game->image_name_full;
            
            $result = $client->deleteObject(array(
                'Bucket' => $bucket,
                'Key'    => $game->software_image_thumbnail_url,
            ));
            
            $result = $client->deleteObject(array(
                'Bucket' => $bucket,
                'Key'    => $game->software_image_full_url,
            ));
                
              // [MDPI]
                /*
                 $game->software_image_thumbnail_url = "C:/Web/Project/TheExchange Project/img/mdpi/" . $game->image_name_thumbnail;
                 $game->software_image_full_url = "C:/Web/Project/TheExchange Project/img/mdpi/" . $game->image_name_full;
                
                 */
                
              $game->software_image_thumbnail_url = "img/mdpi/" . $game->image_name_thumbnail;
              $game->software_image_full_url = "img/mdpi/" . $game->image_name_full;
                /*
                if(is_file($game->software_image_thumbnail_url))
                    unlink($game->software_image_thumbnail_url);
                    
                if(is_file($game->software_image_full_url))
                    unlink($game->software_image_full_url);
                   */
                $result = $client->deleteObject(array(
                    'Bucket' => $bucket,
                    'Key'    => $game->software_image_thumbnail_url,
                ));
                
                $result = $client->deleteObject(array(
                    'Bucket' => $bucket,
                    'Key'    => $game->software_image_full_url,
                ));
                
            // [HDPI]
             /*
                     $game->software_image_thumbnail_url = "C:/Web/Project/TheExchange Project/img/hdpi/" . $game->image_name_thumbnail;
                     $game->software_image_full_url = "C:/Web/Project/TheExchange Project/img/hdpi/" . $game->image_name_full;
                */
                    
              $game->software_image_thumbnail_url = "img/hdpi/" . $game->image_name_thumbnail;
              $game->software_image_full_url = "img/hdpi/" . $game->image_name_full;
                 
              $result = $client->deleteObject(array(
                  'Bucket' => $bucket,
                  'Key'    => $game->software_image_thumbnail_url,
              ));
              
              $result = $client->deleteObject(array(
                  'Bucket' => $bucket,
                  'Key'    => $game->software_image_full_url,
              ));
              
              /*
              if(is_file($game->software_image_thumbnail_url))
                        unlink($game->software_image_thumbnail_url);
                        
              if(is_file($game->software_image_full_url))
                        unlink($game->software_image_full_url);
                     */       
            // [XHDPI]
            /*
                         $game->software_image_thumbnail_url = "C:/Web/Project/TheExchange Project/img/xhdpi/" . $game->image_name_thumbnail;
                         $game->software_image_full_url = "C:/Web/Project/TheExchange Project/img/xhdpi/" . $game->image_name_full;
            */
               $game->software_image_thumbnail_url = "img/xhdpi/" . $game->image_name_thumbnail;
               $game->software_image_full_url = "img/xhdpi/" . $game->image_name_full;
                        /*
               if(is_file($game->software_image_thumbnail_url))
                 unlink($game->software_image_thumbnail_url);
                            
               if(is_file($game->software_image_full_url))
                 unlink($game->software_image_full_url);
                 */
               
               $result = $client->deleteObject(array(
                   'Bucket' => $bucket,
                   'Key'    => $game->software_image_thumbnail_url,
               ));
               
               $result = $client->deleteObject(array(
                   'Bucket' => $bucket,
                   'Key'    => $game->software_image_full_url,
               ));
               
            // [XXHDPI]
              /*
                  $game->software_image_thumbnail_url = "C:/Web/Project/TheExchange Project/img/xxhdpi/" . $game->image_name_thumbnail;
                  $game->software_image_full_url = "C:/Web/Project/TheExchange Project/img/xxhdpi/" . $game->image_name_full;
              */
              $game->software_image_thumbnail_url = "img/xxhdpi/" . $game->image_name_thumbnail;
              $game->software_image_full_url = "img/xxhdpi/" . $game->image_name_full;
                                /*
              if(is_file($game->software_image_thumbnail_url))
                 unlink($game->software_image_thumbnail_url);
                                    
              if(is_file($game->software_image_full_url))
                 unlink($game->software_image_full_url);
                 */
              $result = $client->deleteObject(array(
                  'Bucket' => $bucket,
                  'Key'    => $game->software_image_thumbnail_url,
              ));
              
              $result = $client->deleteObject(array(
                  'Bucket' => $bucket,
                  'Key'    => $game->software_image_full_url,
              ));
            // [XXXHDPI]
             /*
                  $game->software_image_thumbnail_url = "C:/Web/Project/TheExchange Project/img/xxxhdpi/" . $game->image_name_thumbnail;
                  $game->software_image_full_url = "C:/Web/Project/TheExchange Project/img/xxxhdpi/" . $game->image_name_full;
             */
             $game->software_image_thumbnail_url = "img/xxxhdpi/" . $game->image_name_thumbnail;
             $game->software_image_full_url = "img/xxxhdpi/" . $game->image_name_full;
                                /*        
             if(is_file($game->software_image_thumbnail_url))
               unlink($game->software_image_thumbnail_url);
                                            
              if(is_file($game->software_image_full_url))
                unlink($game->software_image_full_url);
                    */
              
              $result = $client->deleteObject(array(
                  'Bucket' => $bucket,
                  'Key'    => $game->software_image_thumbnail_url,
              ));
              
              $result = $client->deleteObject(array(
                  'Bucket' => $bucket,
                  'Key'    => $game->software_image_full_url,
              ));
                 
             
              $image_resizer->delete_image($pdo, $game->image_name_full, $game->image_name_thumbnail);
                echo json_encode(array("transaction_software_101_2" => true));
                $pdo = null;
                return;
  
        }
        else if($inventory_operation === 3){
            // Select
             $user_software = array();
            $query = $inventory->select_software($pdo, $user_id);
           
            
            if($operation_status === false){
                echo json_encode(array(array("transaction_software_error_101_3" => "error: query failure"))) ;
                $pdo = null;
                return;
            }
            
            for($i = 0; $i < sizeof($query); $i++){
                
                $arr = array();
                $arr = $query[$i];
                
                $software = new Game();
                $software->title = $arr['title'];
                $software->publisher = $arr['publisher'];
                $software->developer = $arr['developer'];
                $software->platform = $arr['platform'];
                $software->upc = $arr['upc'];
                $software->user_description = $arr['user_description'];
                $software->created_at = $arr['created_at'];
                $software->last_modified = $arr['last_modified'];
                $software->image_name_thumbnail = $arr['software_image_name_thumbnail'];
                $software->image_name_full = $arr['software_image_name_full'];
                $software->software_uid = $arr['software_uid'];
                
                $software_information = array("title" => $software->title, "publisher" => $software->publisher, "developer" => $software->developer,
                    "platform" => $software->platform, "software_image_name_thumbnail" => $software->image_name_thumbnail, "software_image_name_full" => $software->image_name_full, 
                    "upc" => $software->upc, "user_description" => $software->user_description,
                    "software_uid" => $software->software_uid ,"created_at" => $software->created_at, "last_modified" => $software->last_modified);
                
                array_push($user_software, $software_information);
            
            }
                
                echo json_encode($user_software);
                //echo json_encode(array("transaction_software_101_3" => "[Games]"));
                $pdo = null;
                return;
   
        }
        else if($inventory_operation === 4){
            // Update
            
          
            $operation_status = $inventory->update_software($pdo,  $game);
            
            
            if(isset($game->game_image_encoded_full)){
                
                $image_file_path = null;
                
                /*
                $xxxhdpi_path = "C:/Web/Project/TheExchange Project/img/xxxhdpi/";
                $xxhdpi_path = "C:/Web/Project/TheExchange Project/img/xxhdpi/";
                $xhdpi_path = "C:/Web/Project/TheExchange Project/img/xhdpi/";
                $hdpi_path = "C:/Web/Project/TheExchange Project/img/hdpi/";
                $mdpi_path = "C:/Web/Project/TheExchange Project/img/mdpi/";
                $ldpi_path = "C:/Web/Project/TheExchange Project/img/ldpi/";
                */
        
                $xxxhdpi_path = "img/xxxhdpi/";
                $xxhdpi_path = "img/xxhdpi/";
                $xhdpi_path = "img/xhdpi/";
                $hdpi_path = "img/hdpi/";
                $mdpi_path = "img/mdpi/";
                $ldpi_path = "img/ldpi/";
                
                
                if($dpi <= 120){
                    
                    // [LDPI]
                    /*
                    file_put_contents($ldpi_path . $game->image_name_full, base64_decode($game->game_image_encoded_full));
                    file_put_contents($ldpi_path . $game->image_name_thumbnail, base64_decode($game->game_image_encoded_thumbnail));
                    */
                    $result_image_full = $client->putObject( array(
                        
                        'Bucket' => 'exchangeproject',
                        'Key' => $ldpi_path. $game->image_name_full,
                        'Body' => base64_decode($game->game_image_encoded_full),
                        'ACL' => 'public-read',
                        'ContentType' => 'image/jpeg'
                    ));
                    
                    $result_image_thumbnail = $client->putObject( array(
                        
                        'Bucket' => 'exchangeproject',
                        'Key' => $ldpi_path . $game->image_name_thumbnail,
                        'Body' => base64_decode($game->game_image_encoded_thumbnail),
                        'ACL' => 'public-read',
                        'ContentType' => 'image/jpeg'
                    ));
                    
                    $image_file_path = $ldpi_path . $game->image_name_full;
                    
                }else if($dpi > 120 && $dpi <= 160){
                    
                    // [MDPI]
                    /*
                    file_put_contents($mdpi_path . $game->image_name_full, base64_decode($game->game_image_encoded_full));
                    file_put_contents($mdpi_path . $game->image_name_thumbnail, base64_decode($game->game_image_encoded_thumbnail));
                    */
                    $result_image_full = $client->putObject( array(
                        
                        'Bucket' => 'exchangeproject',
                        'Key' => $mdpi_path. $game->image_name_full,
                        'Body' => base64_decode($game->game_image_encoded_full),
                        'ACL' => 'public-read',
                        'ContentType' => 'image/jpeg'
                    ));
                    
                    $result_image_thumbnail = $client->putObject( array(
                        
                        'Bucket' => 'exchangeproject',
                        'Key' => $mdpi_path . $game->image_name_thumbnail,
                        'Body' => base64_decode($game->game_image_encoded_thumbnail),
                        'ACL' => 'public-read',
                        'ContentType' => 'image/jpeg'
                    ));
                    
                    $image_file_path = $mdpi_path . $game->image_name_full;
                }
                else if($dpi > 160 &&  $dpi <= 240){
                    
                    // [HDPI]
                    /*
                    file_put_contents($hdpi_path . $game->image_name_full, base64_decode($game->game_image_encoded_full));
                    file_put_contents($hdpi_path . $game->image_name_thumbnail, base64_decode($game->game_image_encoded_thumbnail));
                    */
                    
                    $result_image_full = $client->putObject( array(
                        
                        'Bucket' => 'exchangeproject',
                        'Key' => $hdpi_path. $game->image_name_full,
                        'Body' => base64_decode($game->game_image_encoded_full),
                        'ACL' => 'public-read',
                        'ContentType' => 'image/jpeg'
                    ));
                    
                    $result_image_thumbnail = $client->putObject( array(
                        
                        'Bucket' => 'exchangeproject',
                        'Key' => $hdpi_path . $game->image_name_thumbnail,
                        'Body' => base64_decode($game->game_image_encoded_thumbnail),
                        'ACL' => 'public-read',
                        'ContentType' => 'image/jpeg'
                    ));
                    
                    $image_file_path = $hdpi_path . $game->image_name_full;
                    
                }
                else if($dpi > 240 && $dpi <= 320){
                    
                    // [XHDPI]
                    /*
                    file_put_contents($xhdpi_path . $game->image_name_full, base64_decode($game->game_image_encoded_full));
                    file_put_contents($xhdpi_path . $game->image_name_thumbnail, base64_decode($game->game_image_encoded_thumbnail));
                    */
                    $result_image_full = $client->putObject( array(
                        
                        'Bucket' => 'exchangeproject',
                        'Key' => $xhdpi_path. $game->image_name_full,
                        'Body' => base64_decode($game->game_image_encoded_full),
                        'ACL' => 'public-read',
                        'ContentType' => 'image/jpeg'
                    ));
                    
                    $result_image_thumbnail = $client->putObject( array(
                        
                        'Bucket' => 'exchangeproject',
                        'Key' => $xhdpi_path . $game->image_name_thumbnail,
                        'Body' => base64_decode($game->game_image_encoded_thumbnail),
                        'ACL' => 'public-read',
                        'ContentType' => 'image/jpeg'
                    ));
                    $image_file_path = $xhdpi_path . $game->image_name_full;
                    
                }
                else if($dpi > 320 && $dpi <= 480){
                    
                    
                    // [XXHDPI]
                    /*
                    file_put_contents($xxhdpi_path . $game->image_name_full, base64_decode($game->game_image_encoded_full));
                    file_put_contents($xxhdpi_path . $game->image_name_thumbnail, base64_decode($game->game_image_encoded_thumbnail));
                    */
                    $result_image_full = $client->putObject( array(
                        
                        'Bucket' => 'exchangeproject',
                        'Key' => $xxhdpi_path. $game->image_name_full,
                        'Body' => base64_decode($game->game_image_encoded_full),
                        'ACL' => 'public-read',
                        'ContentType' => 'image/jpeg'
                    ));
                    
                    $result_image_thumbnail = $client->putObject( array(
                        
                        'Bucket' => 'exchangeproject',
                        'Key' => $xxhdpi_path . $game->image_name_thumbnail,
                        'Body' => base64_decode($game->game_image_encoded_thumbnail),
                        'ACL' => 'public-read',
                        'ContentType' => 'image/jpeg'
                    ));
                    
                    $image_file_path = $xxhdpi_path . $game->image_name_full;
                }
                else if($dpi > 480 && $dpi <= 640){
                    
                    // [XXXHDPI]
                    /*
                    file_put_contents($xxxhdpi_path . $game->image_name_full, base64_decode($game->game_image_encoded_full));
                    file_put_contents($xxxhdpi_path . $game->image_name_thumbnail, base64_decode($game->game_image_encoded_thumbnail));
                    */
                    $result_image_full = $client->putObject( array(
                        
                        'Bucket' => 'exchangeproject',
                        'Key' => $xxxhdpi_path. $game->image_name_full,
                        'Body' => base64_decode($game->game_image_encoded_full),
                        'ACL' => 'public-read',
                        'ContentType' => 'image/jpeg'
                    ));
                    
                    $result_image_thumbnail = $client->putObject( array(
                        
                        'Bucket' => 'exchangeproject',
                        'Key' => $xxxhdpi_path . $game->image_name_thumbnail,
                        'Body' => base64_decode($game->game_image_encoded_thumbnail),
                        'ACL' => 'public-read',
                        'ContentType' => 'image/jpeg'
                    ));
                    
                    $image_file_path = $xxxhdpi_path . $game->image_name_full;
                }
                
      
                $image_resizer->insert_image($pdo, $image_file_path, $game->image_name_full, $game->image_name_thumbnail, $dpi);
                
                    
            }
            
            
            echo json_encode(array("transaction_software_101_4" => true));
            $pdo = null;
            return;
            
        }
        
    }
    
    
    
}catch (PDOException $pdo_error){
    
   // echo $pdo_error->getMessage();
    echo json_encode(array("inventory_management_error" => "error 100: inventory could not be accessed"));
    $pdo = null;
    return;
    
}
?>