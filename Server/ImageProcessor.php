<?php
$path = '/var/www/exchange_project/';
require_once $path . '/vendor/autoload.php';
require 'DatabaseLoginInfo.php';
require 'ImageResizer.php';
use Aws\S3\S3Client;

$client = null;

try{

    $pdo = new PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $image_resizer = new ImageResizer();
    $query = $image_resizer->select_image($pdo);

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
    for($i = 0; $i < count($query); $i++){
    
   
        $input = $query[$i];
        $url = $input['image_file_path'];
        $image_encoded = $client->getObject(array(
            
            'Bucket' => $bucket,
            'Key' => $url
                 
        ));
    
        generate_image($client, base64_encode($image_encoded['Body']), $input['image_name_full'], $input['image_name_thumbnail'], $input['dpi']);

        $is_processed = 1;

        $image_resizer->update_is_processed($pdo, $input['id'], $is_processed);
        
            
    }

}catch(PDOException $pdo_error){
    
    echo json_encode(array("database_connection_error" => "error: Database Connection"));
    return;
    
}


function generate_image($client, $image_encoded_full, $image_name_full, $image_name_thumbnail, $dpi){

    $xxxhdpi_path = "img/xxxhdpi/";
    $xxhdpi_path = "img/xxhdpi/";
    $xhdpi_path = "img/xhdpi/";
    $hdpi_path = "img/hdpi/";
    $mdpi_path = "img/mdpi/";
    $ldpi_path = "img/ldpi/";
    
    
    $quality_full = 96;
    $quality_thumbnail = 75;

    if($dpi <= 120){
        
        // [LDPI]
        
        $imagick =  new Imagick();
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $imagick->setImageCompression(Imagick::COMPRESSION_JPEG);
        $imagick->setImageCompressionQuality((int) $quality_full);
        
        // xxxhdpi
        $image_width =  $imagick->getimagewidth() * (16 / 3);
        $image_height =  $imagick->getimageheight() * (16 / 3);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxxhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 400;
        $image_height = 400;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
         
         $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxxhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $imagick->clear();
        
     
        // xxhdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (12 / 3);
        $image_height =  $imagick->getimageheight() * (12 / 3);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 300;
        $image_height = 300;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $imagick->clear();
        
        // xhdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (8 / 3);
        $image_height =  $imagick->getimageheight() * (8 / 3);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        
        $image_width = 200;
        $image_height = 200;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $imagick->clear();
        
        // hdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (6 / 3);
        $image_height =  $imagick->getimageheight() * (6 / 3);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $hdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        
        $image_width = 150;
        $image_height = 150;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $hdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        // mdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (4 / 3);
        $image_height =  $imagick->getimageheight() * (4 / 3);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);

        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $mdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        
        $image_width = 100;
        $image_height = 100;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $mdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        
        
        
    }else if($dpi > 120 && $dpi <= 160){
        
        // [MDPI]
        
        $imagick =  new Imagick();
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $imagick->setImageCompression(Imagick::COMPRESSION_JPEG);
        $imagick->setImageCompressionQuality((int) $quality_full);
        
        // xxxhdpi
        
        $image_width =  $imagick->getimagewidth() * (16 / 4);
        $image_height =  $imagick->getimageheight() * (16 / 4);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxxhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 400;
        $image_height = 400;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxxhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $imagick->clear();
        
        // xxhdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (12 / 4);
        $image_height =  $imagick->getimageheight() * (12 / 4);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 300;
        $image_height = 300;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        // xhdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (8 / 4);
        $image_height =  $imagick->getimageheight() * (8 / 4);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 200;
        $image_height = 200;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        // hdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (6 / 4);
        $image_height =  $imagick->getimageheight() * (6 / 4);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $hdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 150;
        $image_height = 150;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $hdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        
        // ldpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (3 / 4);
        $image_height =  $imagick->getimageheight() * (3 / 4);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $ldpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 75;
        $image_height = 75;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $ldpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        
    }
    else if($dpi > 160 &&  $dpi <= 240){
        
        // [HDPI]
        
        $imagick =  new Imagick();
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $imagick->setImageCompression(Imagick::COMPRESSION_JPEG);
        $imagick->setImageCompressionQuality((int) $quality_full);
        
        // xxxhdpi
        
        $image_width =  $imagick->getimagewidth() * (16 / 6);
        $image_height =  $imagick->getimageheight() * (16 / 6);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxxhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 400;
        $image_height = 400;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxxhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        // xxhdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (12 /6);
        $image_height =  $imagick->getimageheight() * (12 / 6);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 300;
        $image_height = 300;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        // xhdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (8 / 6);
        $image_height =  $imagick->getimageheight() * (8 / 6);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 200;
        $image_height = 200;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        
        // mdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (4 / 6);
        $image_height =  $imagick->getimageheight() * (4 / 6);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $mdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 100;
        $image_height = 100;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $mdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        // ldpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (3 / 6);
        $image_height =  $imagick->getimageheight() * (3 / 6);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $ldpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 75;
        $image_height = 75;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $ldpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
    }
    else if($dpi > 240 && $dpi <= 320){
        
        // [XHDPI]
        
        $imagick =  new Imagick();
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $imagick->setImageCompression(Imagick::COMPRESSION_JPEG);
        $imagick->setImageCompressionQuality((int) $quality_full);
        
        // xxxhdpi
        
        $image_width =  $imagick->getimagewidth() * (16 / 8);
        $image_height =  $imagick->getimageheight() * (16 / 8);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxxhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 400;
        $image_height = 400;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxxhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        // xxhdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (12 / 8);
        $image_height =  $imagick->getimageheight() * (12 / 8);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 300;
        $image_height = 300;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        
        // hdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (6 / 8);
        $image_height =  $imagick->getimageheight() * (6 / 8);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $hdpi_path . $image_name_full,
            'Body' => base64_encode($imagick->getimageblob()),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 150;
        $image_height = 150;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $hdpi_path . $image_name_thumbnail,
            'Body' => base64_encode($imagick->getimageblob()),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        // mdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (4 / 8);
        $image_height =  $imagick->getimageheight() * (4 / 8);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $mdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 100;
        $image_height = 100;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $mdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        // ldpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (3 / 8);
        $image_height =  $imagick->getimageheight() * (3 / 8);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $ldpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 75;
        $image_height = 75;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $ldpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
    }
    else if($dpi > 320 && $dpi <= 480){
        
        // [XXHDPI]
        echo_memory_usage();
    
        $imagick =  new Imagick();
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $imagick->setImageCompression(Imagick::COMPRESSION_JPEG);
    
        
        // xxxhdpi
        $local_file_path = "/var/www/exchange_project/img/xxxhdpi/";
        $image_width  =  $imagick->getimagewidth() * (16 / 12);
        $image_height =  $imagick->getimageheight() * (16 / 12);

        $imagick->resizeImage($image_width, $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxxhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
  
        $image_width = 400;
        $image_height = 400;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxxhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));

        $imagick->clear();

        // xhdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (8 / 12);
        $image_height =  $imagick->getimageheight() * (8 / 12);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        echo "#3a: executing... \n";
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));

        $image_width = 200;
        $image_height = 200;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);

        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
     
        $imagick->clear();
   
        // hdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (6 / 12);
        $image_height =  $imagick->getimageheight() * (6 / 12);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $hdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 150;
        $image_height = 150;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);

        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $hdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $imagick->clear();
        
        // mdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (4 / 12);
        $image_height =  $imagick->getimageheight() * (4 / 12);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
       
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $mdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
 
        $image_width = 100;
        $image_height = 100;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $mdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $imagick->clear();
   
        // ldpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (3 / 12);
        $image_height =  $imagick->getimageheight() * (3 / 12);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
       
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $ldpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
 
        $image_width = 75;
        $image_height = 75;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $ldpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $imagick->clear();

    }
    else if($dpi > 480 && $dpi <= 640){
        
        // [XXXHDPI]
        
        $imagick =  new Imagick();
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $imagick->setImageCompression(Imagick::COMPRESSION_JPEG);
        $imagick->setImageCompressionQuality((int) $quality_full);
        
        
        // xxhdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (12 / 16);
        $image_height =  $imagick->getimageheight() * (12 / 16);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 300;
        $image_height = 300;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xxhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        // xhdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (8 / 16);
        $image_height =  $imagick->getimageheight() * (8 / 16);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xhdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 200;
        $image_height = 200;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $xhdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        // hdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (6 / 16);
        $image_height =  $imagick->getimageheight() * (6 / 16);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $hdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 150;
        $image_height = 150;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $hdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        // mdpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (4 / 16);
        $image_height =  $imagick->getimageheight() * (4 / 16);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $mdpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 100;
        $image_height = 100;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $mdpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
        // ldpi
        $imagick->readImageBlob(base64_decode($image_encoded_full));
        $image_width =  $imagick->getimagewidth() * (3 / 16);
        $image_height =  $imagick->getimageheight() * (3 / 16);
        $imagick->resizeImage($image_width , $image_height, Imagick::FILTER_LANCZOS, 1);
        $result_image_full = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $ldpi_path . $image_name_full,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        
        $image_width = 75;
        $image_height = 75;
        $imagick->setImageCompressionQuality((int) $quality_thumbnail);
        $imagick->thumbnailImage($image_width, $image_height , false, false);
        $result_image_thumbnail = $client->putObject( array(
            
            'Bucket' => 'exchangeproject',
            'Key' => $ldpi_path . $image_name_thumbnail,
            'Body' => $imagick->getimageblob(),
            'ACL' => 'public-read',
            'ContentType' => 'image/jpeg'
        ));
        $imagick->clear();
        
    }
}

function echo_memory_usage() {
    
    $mem_usage = memory_get_usage();
    $mem_peak = memory_get_peak_usage();
    
} 

?>
