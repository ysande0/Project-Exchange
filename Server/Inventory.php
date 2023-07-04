<?php
date_default_timezone_set("America/New_York");
class Inventory{
    
    public function __construct(){
        
        
    }
   
    public function insert_hardware($pdo, $user_id, $hardware){
        
        $sql = "INSERT INTO hardware_inventory (user_id, manufacturer, platform) VALUES (?, ?, ?);";
        $pdo_statement = $pdo->prepare($sql); 
        $operation_status = $pdo_statement->execute([$user_id, $hardware->manufacturer, $hardware->platform]);
        
        return $operation_status;
    }

    public function delete_hardware($pdo, $user_id, $hardware){
        
        $sql = "DELETE FROM hardware_inventory WHERE user_id = ? AND manufacturer = ? AND platform = ?";
        $pdo_statement = $pdo->prepare($sql);
        $operation_status = $pdo_statement->execute([$user_id,  $hardware->manufacturer, $hardware->platform]);
        
        return $operation_status;
               
    }
    
    public function select_hardware($pdo, $user_id){
        
        $sql = "SELECT manufacturer, platform FROM hardware_inventory WHERE user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$user_id]);
        $query = $pdo_statement->fetchAll(PDO::FETCH_ASSOC);
        
        return $query;
    }
    
    public function insert_software($pdo, $user_id, $game){
        
        $sql = "INSERT INTO software_inventory (user_id, title, publisher, developer, platform, upc, software_image_name_thumbnail, software_image_name_full, user_description, created_at, last_modified, software_uid ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now(), ?);";
        $pdo_statement = $pdo->prepare($sql);

  
        $operation_status = $pdo_statement->execute([$user_id, $game->title, $game->publisher, $game->developer, $game->platform,
            $game->upc, $game->image_name_thumbnail, $game->image_name_full, $game->user_description, $game->software_uid]);
   
       
        return $operation_status;
        
    }
    
    public function delete_software($pdo, $user_id, $game){
        
        $sql = "DELETE FROM software_inventory WHERE software_uid = ?";
        $pdo_statement = $pdo->prepare($sql);
        $operation_status = $pdo_statement->execute([$game->software_uid]);
        
        return $operation_status;
        
    }
    
    public function update_software($pdo, $game){
        
        
        $sql = "UPDATE software_inventory SET title = ? , publisher = ? , developer = ?  , platform = ? , upc = ? ,  user_description = ? , last_modified = now() WHERE software_uid = ? ";
        $pdo_statement = $pdo->prepare($sql);
        $operation_status = $pdo_statement->execute([$game->title, $game->publisher, $game->developer, $game->platform, $game->upc, $game->user_description, $game->software_uid]);
        
        return $operation_status;
        
    }
    
    public function select_software($pdo, $user_id){
        
        $sql = "SELECT title, publisher, developer, platform, upc, software_image_name_thumbnail, software_image_name_full, user_description, software_uid, created_at, last_modified FROM software_inventory WHERE user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$user_id]);
        $query = $pdo_statement->fetchAll(PDO::FETCH_ASSOC);
        
        
        return $query;
        
    }
    
}


?>