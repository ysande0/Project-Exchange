<?php

$path = '/var/www/exchange_project/';
//$path = 'C:/Web/Project/TheExchange Project/';
//$private_crypt_key_path = $path . "keys/cryp_key.txt";

require $path . 'vendor/autoload.php';
require 'DatabaseLoginInfo.php';
require 'ImageResizer.php';
require 'Token.php';
require 'User.php';
use Aws\S3\S3Client;

$client = null;

$input = file_get_contents("php://input");
$input = json_decode($input, true);

$pdo = null;
$user = new User();
//$crypt_key = file_get_contents($private_crypt_key_path);
$headers = apache_request_headers();

$category = $input['category'];
$first_name = null;
$last_name = null;

$profile_image_path_thumbnail = null;
$profile_image_path_full = null;


$profile_image_encode_thumbnail = null;
$profile_image_name_thumbnail = null;
$profile_image_encode_full = null;
$profile_image_name_full = null;
$dpi = null;
$user->id = $input['id'];
//$user_token = $headers['Authorization'];
$user->access_token = $headers['Authorization'];


try{
    
    $pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    
}catch(PDOException $pdo_error){
    
    echo json_encode(array("settings_upload_error" => "error: settings could not update"));
    $pdo = null;
    return;
    
}


if($category === 100){

/*
    $token = new Token($pdo);
    $token->set_user_id($user->id);
    $token->set_token($user->access_token);
    $session_timeout = $token->is_token_expired();
    */
    if($session_timeout){
        
        echo json_encode(array("session_timeout" => true));
        $pdo = null;
        return;
    }

    
    $user->first_name = $input['first_name'];
    $user->last_name = $input['last_name'];
    $dpi = $input['dpi'];

    $user->uid = $input['uid'];
    /*
    if(isset($input['image_encoded_thumbnail']) || isset($input['image_name_thumbnail']) || isset($input['image_encoded_full']) || isset($input['image_name_full'])){
        
            $profile_image_encode_thumbnail = $input['image_encoded_thumbnail'];
            $profile_image_name_thumbnail = $input['image_name_thumbnail'];
            
            $profile_image_encode_full = $input['image_encoded_full'];
            $profile_image_name_full = $input['image_name_full'];
            
    }
   
    
    if(empty($first_name) || empty($last_name) || empty($user_id) || empty($user_uid)){
        echo json_encode(array("settings_upload_error" => true));
        return;
    }

  if(!(empty($profile_image_encode_thumbnail)) || !(empty($profile_image_name_thumbnail))){
      

    $profile_image_path_thumbnail =  "C:/Web/Project/TheExchange Project/img/". $profile_image_name_thumbnail;
    $profile_image_files = glob($profile_image_path_thumbnail);
    
    foreach ($profile_image_files as $file){
        
        if(is_file($file))
            unlink($file);
        
    }
    
        $profile_image_path_thumbnail = "C:/Web/Project/TheExchange Project/img/". $profile_image_name_thumbnail;
        file_put_contents($profile_image_path_thumbnail, base64_decode($profile_image_encode_thumbnail));
        $profile_image_path_thumbnail = "http://192.168.1.242:80/Project/TheExchange%20Project/img/" . $profile_image_name_thumbnail;
  
    
  }
  */
  
      
     if(isset($input['image_encoded_full']) || isset($input['image_name_full'])){
            


            $user->profile_image_encode_full = $input['image_encoded_full'];
            $user->profile_image_name_full = $input['image_name_full'];
           // $user->profile_image_name_thumbnail = $input['image_name_thumbnail'];
            
            
            
     }
     
     if(isset($input['image_encoded_thumbnail']) || isset($input['image_name_thumbnail'])){
         
        
         $user->profile_image_encode_thumbnail = $input['image_encoded_thumbnail'];
         $user->profile_image_name_thumbnail = $input['image_name_thumbnail'];
         
     }
      
     if(empty($user->first_name) || empty($user->last_name) || empty($user->id) || empty($user->uid)){
   
         echo json_encode(array("settings_upload_error" => true));
         return;
     }
        
  if(!(empty($user->profile_image_encode_full)) || !(empty($user->profile_image_name_full))){
      
      
          $image_resizer = new ImageResizer();
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
          
       
          $client = new S3Client([
              'version' => 'latest',
              'region'  => 'us-east-1',
              'endpoint' => 'https://nyc3.digitaloceanspaces.com',
              'credentials' => [
                  'key'    => '6522JNRTCYS4ALSTHO2P',
                  'secret' => 'dfOq3ks6RKmfXZEWmbl56vUECzw7IJoF38A+GcL0pvg',
              ],
          ]);
          
         
          if($dpi <= 120){
              
              // [LDPI]
              /*
              $profile_image_files = glob($ldpi_path . $user->profile_image_name_thumbnail);
              foreach ($profile_image_files as $file){
                  
                  if(is_file($file))
                      unlink($file);
                  
                      
              }
              
              $profile_image_files = glob($ldpi_path . $user->profile_image_name_full);
              foreach ($profile_image_files as $file){
                  
                  if(is_file($file)){
                     
                      unlink($file);
                  }
                      
              }
              
              file_put_contents($ldpi_path . $user->profile_image_name_full, base64_decode($user->profile_image_encode_full));
              file_put_contents($ldpi_path . $user->profile_image_name_thumbnail, base64_decode($user->profile_image_encode_thumbnail));
              */
                  
                  $result_image_full = $client->putObject( array(
                      
                      'Bucket' => 'exchangeproject',
                      'Key' => $ldpi_path. $user->profile_image_name_full,
                      'Body' => base64_decode($user->profile_image_encode_full),
                      'ACL' => 'public-read',
                      'ContentType' => 'image/jpeg'
                  ));
                  
                  $result_image_thumbnail = $client->putObject( array(
                      
                      'Bucket' => 'exchangeproject',
                      'Key' => $ldpi_path. $user->profile_image_name_thumbnail,
                      'Body' => base64_decode($user->profile_image_encode_thumbnail),
                      'ACL' => 'public-read',
                      'ContentType' => 'image/jpeg'
                  ));
                  
              $image_file_path = $ldpi_path . $user->profile_image_name_full;
              
          }else if($dpi > 120 && $dpi <= 160){
              
              // [MDPI]
              /*
              $profile_image_files = glob($mdpi_path . $user->profile_image_name_thumbnail);
              foreach ($profile_image_files as $file){
                  
                  if(is_file($file))
                      unlink($file);
                  
                      
              }
              
              $profile_image_files = glob($mdpi_path . $user->profile_image_name_full);
              foreach ($profile_image_files as $file){
                  
                  if(is_file($file)){

                      unlink($file);
                  }
                      
              }
              
              file_put_contents($mdpi_path . $user->profile_image_name_full, base64_decode($user->profile_image_encode_full));
              file_put_contents($mdpi_path . $user->profile_image_name_thumbnail, base64_decode($user->profile_image_encode_thumbnail));
              */
                  $result_image_full = $client->putObject( array(
                      
                      'Bucket' => 'exchangeproject',
                      'Key' => $mdpi_path. $user->profile_image_name_full,
                      'Body' => base64_decode($user->profile_image_encode_full),
                      'ACL' => 'public-read',
                      'ContentType' => 'image/jpeg'
                  ));
                  
                  $result_image_thumbnail = $client->putObject( array(
                      
                      'Bucket' => 'exchangeproject',
                      'Key' => $mdpi_path. $user->profile_image_name_thumbnail,
                      'Body' => base64_decode($user->profile_image_encode_thumbnail),
                      'ACL' => 'public-read',
                      'ContentType' => 'image/jpeg'
                  ));
                  
              $image_file_path = $mdpi_path . $user->profile_image_name_full;
          }
          else if($dpi > 160 &&  $dpi <= 240){
              
              // [HDPI]
          /*
              $profile_image_files = glob($hdpi_path . $user->profile_image_name_thumbnail);
              foreach ($profile_image_files as $file){
                  
                  if(is_file($file))
                      unlink($file);
                  
                      
              }
              
              $profile_image_files = glob($hdpi_path . $user->profile_image_name_full);
              foreach ($profile_image_files as $file){
                  
                  if(is_file($file)){
                   
                      unlink($file);
                  }
                      
              }
              
              file_put_contents($hdpi_path . $user->profile_image_name_full, base64_decode($user->profile_image_encode_full));
              file_put_contents($hdpi_path . $user->profile_image_name_thumbnail, base64_decode($user->profile_image_encode_thumbnail));
              */
                  $result_image_full = $client->putObject( array(
                      
                      'Bucket' => 'exchangeproject',
                      'Key' => $hdpi_path. $user->profile_image_name_full,
                      'Body' => base64_decode($user->profile_image_encode_full),
                      'ACL' => 'public-read',
                      'ContentType' => 'image/jpeg'
                  ));
                  
                  $result_image_thumbnail = $client->putObject( array(
                      
                      'Bucket' => 'exchangeproject',
                      'Key' => $hdpi_path. $user->profile_image_name_thumbnail,
                      'Body' => base64_decode($user->profile_image_encode_thumbnail),
                      'ACL' => 'public-read',
                      'ContentType' => 'image/jpeg'
                  ));
                  
              $image_file_path = $hdpi_path . $user->profile_image_name_full;
          }
          else if($dpi > 240 && $dpi <= 320){
              
              // [XHDPI]
           /*
              $profile_image_files = glob($xhdpi_path . $user->profile_image_name_thumbnail);
              foreach ($profile_image_files as $file){
                  
                  if(is_file($file))
                      unlink($file);
                  
                      
              }
              
              $profile_image_files = glob($xhdpi_path . $user->profile_image_name_full);
              foreach ($profile_image_files as $file){
                  
                  if(is_file($file)){
                      unlink($file);
                  }
                      
              }
              
              file_put_contents($xhdpi_path . $user->profile_image_name_full, base64_decode($user->profile_image_encode_full));
              file_put_contents($xhdpi_path . $user->profile_image_name_thumbnail, base64_decode($user->profile_image_encode_thumbnail));
              */
                  $result_image_full = $client->putObject( array(
                      
                      'Bucket' => 'exchangeproject',
                      'Key' => $xhdpi_path. $user->profile_image_name_full,
                      'Body' => base64_decode($user->profile_image_encode_full),
                      'ACL' => 'public-read',
                      'ContentType' => 'image/jpeg'
                  ));
                  
                  $result_image_thumbnail = $client->putObject( array(
                      
                      'Bucket' => 'exchangeproject',
                      'Key' => $xhdpi_path. $user->profile_image_name_thumbnail,
                      'Body' => base64_decode($user->profile_image_encode_thumbnail),
                      'ACL' => 'public-read',
                      'ContentType' => 'image/jpeg'
                  ));
                  
              $image_file_path = $xhdpi_path . $user->profile_image_name_full;
          }
          else if($dpi > 320 && $dpi <= 480){
              
              
              // [XXHDPI]
 /*
              $profile_image_files = glob($xxhdpi_path . $user->profile_image_name_thumbnail);
              foreach ($profile_image_files as $file){
                  
                  if(is_file($file))
                      unlink($file);
                  
                      
              }
              
              $profile_image_files = glob($xxhdpi_path . $user->profile_image_name_full);
              foreach ($profile_image_files as $file){
                  
                  if(is_file($file)){
                    
                      unlink($file);
                  }
                      
              }
              
              file_put_contents($xxhdpi_path . $user->profile_image_name_full, base64_decode($user->profile_image_encode_full));
              file_put_contents($xxhdpi_path . $user->profile_image_name_thumbnail, base64_decode($user->profile_image_encode_thumbnail));
              */
                  
                  $result_image_full = $client->putObject( array(
                      
                      'Bucket' => 'exchangeproject',
                      'Key' => $xxhdpi_path. $user->profile_image_name_full,
                      'Body' => base64_decode($user->profile_image_encode_full),
                      'ACL' => 'public-read',
                      'ContentType' => 'image/jpeg'
                  ));
                  
                  $result_image_thumbnail = $client->putObject( array(
                      
                      'Bucket' => 'exchangeproject',
                      'Key' => $xxhdpi_path. $user->profile_image_name_thumbnail,
                      'Body' => base64_decode($user->profile_image_encode_thumbnail),
                      'ACL' => 'public-read',
                      'ContentType' => 'image/jpeg'
                  ));
                  
                 
              $image_file_path = $xxhdpi_path . $user->profile_image_name_full;
          }
          else if($dpi > 480 && $dpi <= 640){
              
              // [XXXHDPI]
              /*
              $profile_image_files = glob($xxxhdpi_path . $user->profile_image_name_thumbnail);
              foreach ($profile_image_files as $file){
                  
                  if(is_file($file))
                      unlink($file);
                      
              }
              
              $profile_image_files = glob($xxxhdpi_path . $user->profile_image_name_full);
              foreach ($profile_image_files as $file){
                  
                  if(is_file($file)){
                      
                      unlink($file);
                  }
                      
              }
              
              file_put_contents($xxxhdpi_path . $user->profile_image_name_full, base64_decode($user->profile_image_encode_full));
              file_put_contents($xxxhdpi_path . $user->profile_image_name_thumbnail, base64_decode($user->profile_image_encode_thumbnail));
      */
                  $result_image_full = $client->putObject( array(
                      
                      'Bucket' => 'exchangeproject',
                      'Key' => $xxxhdpi_path. $user->profile_image_name_full,
                      'Body' => base64_decode($user->profile_image_encode_full),
                      'ACL' => 'public-read',
                      'ContentType' => 'image/jpeg'
                  ));
                  
                  $result_image_thumbnail = $client->putObject( array(
                      
                      'Bucket' => 'exchangeproject',
                      'Key' => $xxxhdpi_path. $user->profile_image_name_thumbnail,
                      'Body' => base64_decode($user->profile_image_encode_thumbnail),
                      'ACL' => 'public-read',
                      'ContentType' => 'image/jpeg'
                  ));
                  
              $image_file_path = $xxxhdpi_path . $user->profile_image_name_full;
          }
          
          $image_resizer->insert_image($pdo, $image_file_path, $user->profile_image_name_full, $user->profile_image_name_thumbnail, $dpi);
      
  }
  
}
else if($category === 101){
    
    $user->password = password_hash($input['password'], PASSWORD_DEFAULT);
    $user->id = $input['id'];
    $user->uid = $input['uid'];
}


//$user_token = Crypto::decrypt($user_token, Key::loadFromAsciiSafeString($crypt_key));

try{
    
    if($category === 100){
        
        if(!empty($user->profile_image_name_thumbnail) && !empty($user->profile_image_name_full)){
            
            $sql = "UPDATE users SET first_name = ?, last_name = ?, profile_image_name_thumbnail = ?, profile_image_name_full = ?, last_modified = now() WHERE user_id = ? ";
            $pdo_statement = $pdo->prepare($sql);
            $pdo_statement->execute([$user->first_name, $user->last_name, $user->profile_image_name_thumbnail, $user->profile_image_name_full, $user->id]);
          
            echo json_encode(array("settings_uploaded" => "settings uploaded", 
                "profile_image_name_thumbnail" => $user->profile_image_name_thumbnail, "profile_image_name_full" => $user->profile_image_name_full));
        }
        else if(empty($user->profile_image_name_full) || empty($user->profile_image_name_thumbnail)){
            
            $sql = "UPDATE users SET first_name = ?, last_name = ?, last_modified = now() WHERE user_id = ? ";
            $pdo_statement = $pdo->prepare($sql);
            $pdo_statement->execute([$user->first_name, $user->last_name, $user->id]);
            
            echo json_encode(array("settings_uploaded" => "settings uploaded"));
        }
            
        $pdo = null;     
        return;
    }
    else if($category === 101){
        
        $sql = "UPDATE users SET password = ?, last_modified = now() WHERE user_id = ? ";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$user->password, $user->id]);
        
        echo json_encode(array("settings_uploaded" => "settings uploaded"));
        $pdo = null;
        return;
        
    }
    
    
}catch (PDOException $pdo_error){
   
    //$pdo_error->getMessage(); 
    echo json_encode(array("settings_upload_error" => "error 100: settings could not update"));
    $pdo = null;
    return;
}



?>