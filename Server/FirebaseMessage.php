<?php

use Kreait\Firebase\Messaging\CloudMessage;

class FirebaseMessage{
    
    private $firebase;
    private $device_token;
    private $title;
    private $display_name; 
    private $notification;
    
    public function __construct($firebase){
     
        $this->firebase = $firebase->getMessaging();
        
    }
    
    public function topic($title){
        
        if(!isset($title)){
            
            echo "Unnamed message";
            return;
        }
        
        $this->title = $title;
    }
    
    public function recipient($device_token, $diplay_name){
        
        if(!isset($device_token)){
            
            echo 'No token given';
            return; 
        }
         
        if(!isset($display_name)){
            
            echo 'No name given';
            return;
        }
        
        $this->device_token = $device_token;
        $this->display_name = $diplay_name;
        
    }
    
    public function compose($messsage){
        
        $this->notification = CloudMessage::fromArray([
            
            'token' => $this->device_token,
            'title' => $this->title,
            'message' => $messsage
            
        ]);
        
    }
    
    public function send(){
        
        if(!isset($notification)){
            
            echo "Error! ";
            return;
        }
        
       $this->firebase->send($notification);
        
    }
    
}


?>