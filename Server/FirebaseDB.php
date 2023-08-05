<?php

use Kreait\Firebase\Exception\InvalidArgumentException;

class FirebaseDB{
    
    
    
    private $firebase_database;
    private $database_name;
    public function __construct($firebase, $database_name){
        
        $this->firebase_database = $firebase->getDatabase();
        $this->database_name = $database_name;
    }
    
    public function Insert($data){
        
        if(!isset($data)){
            echo "Error! no data available";
            return false;
        }

        $entry = [
            
            "name" => $data->first_name,
            "uid" => $data->last_name,
            "message"=> $data->email,
            "date" => $data->password,
            "time" => $data->fcm_token
            
        ]; 
        $this->firebase_database->getReference($this->database_name .'/'. $data->uid)->update($entry);
            
        return true;
    }
    
    public function insert_message($conversation_id, $message_entry){
    
        $number_message_entry = $this->firebase_database->getReference($this->database_name . '/' . $conversation_id)->getSnapshot()->numChildren();
        
        if((int)$number_message_entry < 1){
            
            $entry = [1 => ["first_name" => $message_entry->first_name, "uid" => $message_entry->from_uid, "message" => $message_entry->message, "time" => $message_entry->time, "date" => $message_entry->date]];

            $this->firebase_database->getReference($this->database_name . '/' . $conversation_id)->update($entry);
           
        }
        else {
            
            $entry = [$number_message_entry => ["first_name" => $message_entry->first_name, "uid" => $message_entry->from_uid, "message" => $message_entry->message, "time" => $message_entry->time, "date" => $message_entry->date]];
            
            try{

               
                $this->firebase_database->getReference($this->database_name . '/' . $conversation_id)->update($entry);
            }catch(InvalidArgumentException $firebase_error){
                
                echo "This conversation does not exist";
                echo $firebase_error->getMessage();
                return -1;
            }
            
        }
    
        return $conversation_id;
        
    }
    
    
    public function insert_transaction($user_id, $entry){
        
        try{

            $this->firebase_database->getReference($this->database_name . '/' . $user_id)->update($entry);
        }catch(InvalidArgumentException $firebase_error){
            
            echo "This transaction does not exist";
            echo $firebase_error->getMessage();
            return -1;
        }
       
    }
    
    public function query_transaction($transaction_id, $uid){
        
        if($transaction_id === 0){
            
            return $transaction_id;
        }
        
        
        return $this->firebase_database->getReference($this->database_name .'/'. $transaction_id . '/' . 'trade' . '/' . $uid)->getSnapshot()->getValue();
    }
    
    public function update_transaction($path, $entry){
        
        if(empty($path) || $path === null){
            return;
        }
        
        
        try{
            $this->firebase_database->getReference($path)->update($entry);
        }catch(InvalidArgumentException $firebase_error){
            
            echo "This transaction does not exist";
            echo $firebase_error->getMessage();
            return -1;
        }
        
    }
    
    public function getNumChild($path){
        
        if(empty($path) || $path === null){
            return;
        }
        
        $numChild = 0;
        try{
           $numChild =  $this->firebase_database->getReference($path)->getSnapshot()->numChildren();
        }catch(InvalidArgumentException $firebase_error){
            
            echo "This transaction does not exist";
            echo $firebase_error->getMessage();
            return -1;
        }
        
        return $numChild;
    }
    
    public function delete_transaction($path){
        
        if(empty($path) || $path === null){
            
            echo "Following transaction does not exist";
            return;
        }
        
        try{
            $this->firebase_database->getReference($path)->remove();
        }catch(InvalidArgumentException $firebase_error){
            
            echo "This transaction does not exist";
            echo $firebase_error->getMessage();
            return -1;
        }
        
    }
    
    public function Query($key, $data){
        
        if(!isset($data)){
            return;
        }
        
        return $this->firebase_database->getReference()->getChild($this->database_name . '/' . $data->uid . '/' . $key)->getValue();
    }
    
}

?>
