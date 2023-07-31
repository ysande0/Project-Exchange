<?php

use Kreait\Firebase\Messaging\CloudMessage;
use Kreait\Firebase\Factory;
use Kreait\Firebase\ServiceAccount;
use Kreait\Firebase\Messaging\Notification;
use Kreait\Firebase;

class Backoff{
    
    private $MAX_MS;
    private $MAX_MS_RANDOM;
    private $MIN_MS_RANDOM;
    private $BASE;
    
    private $is_message_sent;
    private $number_tries; //
    private $time_to_wait_ms;
    private $maximum_backoff_time_ms;
    
    private $error_message;
    private $clients;
    private $firebase_messaging;
    private $message;
    private $conversation_id;
    private $message_id;
    private $token;
    private $sender_token;
    
    private $from_user_id;
    private $is_sender;
    
    function __construct($firebase_messaging, $message, $token, $error_message, $clients){
        
        $this->number_tries = 0;
        $this->time_to_wait_ms = 1;
        $this->maximum_backoff_time_ms = 64000;
        
    
        $this->MAX_MS_RANDOM = 1000;
        $this->MIN_MS_RANDOM = 1;
        $this->BASE = 2;
        
        $this->is_message_sent = false;
        $this->clients = $clients;
        $this->error_message = $error_message;
        $this->firebase_messaging = $firebase_messaging;
        $this->message = $message;
        $this->token = $token;
        
    }
    
    private function calculate_wait_time($exp){
        
        $random_ms = mt_rand($this->MIN_MS_RANDOM, $this->MAX_MS_RANDOM);
        return pow($this->BASE, $exp) + $random_ms;
    }
    
    private function convert_ms_to_seconds($ms){
        
        return ($ms / $this->MAX_MS_RANDOM);
    }
    
    private function reset(){
        
        $this->number_tries = 0;
        $this->time_to_wait_ms = 1;
        $this->maximum_backoff_time_ms = 64000;
        
 
        $this->MAX_MS_RANDOM = 1000;
        $this->MIN_MS_RANDOM = 1;
        $this->BASE = 2;
        
    }
    
    private function send(){
        
        try{
            
            $this->firebase_messaging->validate($this->message);
     
            echo "FCM: Message validated" . "\n";
            
        }catch(\Kreait\Firebase\Exception\Messaging\InvalidMessage $error){
            
            $error_array = $error->errors();
            $error_description = $error_array['error'];
            $error_code = $error_description['code'];
            
            
            echo "2) FCM: Message validation error " . "\n";
               $this->is_message_sent = false;
            switch($error_code){
                
                case 429:
                    echo "Backoff: 429 " . "\n";
                    return true;
                    
                case 503:
                    echo "Backoff: 503 " . "\n";
                    return true;
                    
                case 500:
                    echo "Backoff: 500 " . "\n";
                    return true;
                    
            }
        }
        
        echo "2) FCM sent!" . "\n";
        $this->firebase_messaging->send($this->message);
       
        $this->is_message_sent = true;
        return false;
    }
    
    public function retry_transmission(){
        
        $counter = 1;
        while($this->time_to_wait_ms <= $this->maximum_backoff_time_ms){
            
            $this->time_to_wait_ms += $this->calculate_wait_time($this->number_tries);
            
            echo $counter . ") ".  $this->time_to_wait_ms . " ms" . "\n";
            $counter++;
            sleep( $this->convert_ms_to_seconds($this->time_to_wait_ms) );
            
            $is_transmission_error = $this->send();
            $this->number_tries++;
            
            if(!$is_transmission_error)
                break;
                
        }
        
       
        if($this->is_sender){
            echo " [Prevent resend to sender] " . "\n";
            return;
        }
        
        if($this->is_message_sent === false){
            
           
            if(array_key_exists($this->from_user_id, $this->clients)){
                
                echo $this->from_user_id. " is currently available  (2)" . "\n";
   
                $client = $this->clients[$this->from_user_id];
                
                if($client === null)
                    echo "Client is null" . "\n";
                else 
                    echo "Client is not null" . "\n";     
                    
                $client->send($this->error_message);
                 return;             
            }else{
                
                     echo "Entered FCM Zone" . "\n";
                     $error_message_fcm = CloudMessage::withTarget('token', $this->sender_token)
                     ->withData(["message_error" => true , 'conversation_id' => $this->conversation_id,
                         'message_id' => $this->message_id]);
                     
                     try{
                         
                         $this->firebase_messaging->validate($error_message_fcm);
                         
                         
                     }catch(\Kreait\Firebase\Exception\Messaging\InvalidMessage $error){
                         
                         $error_array = $error->errors();
                         $error_description = $error_array['error'];
                         $error_code = $error_description['code'];
                         
                         
                         switch($error_code){
                             
                             case 429:
                                 echo "Message is not sent. 429 Retry" . "\n";
                                 $this->reset();
                                 $this->is_sender = true;
                                 $this->retry_transmission();
                                 break;
                                 
                             case 503:
                                 echo "Message is not sent. 503 Retry" . "\n";
                                 $this->reset();
                                 $this->is_sender = true;
                                 $this->retry_transmission();
                                 break;
                                 
                             case 500:
                                 echo "Message is not sent. 500 Retry" . "\n";
                                 $this->reset();
                                 $this->is_sender = true;
                                 $this->retry_transmission();
                                 break;
                                 
                         }
                         
                         return;
                     }
                     
                     echo "Message was not sent. Sending error message to sender" . "\n";
                     $this->firebase_messaging->send($error_message_fcm);
                     return;
                  
                }
              
            }
                  
            $this->reset();
        }


    public function set_from_id($from_id){
        
       $this->from_user_id = $from_id; 
       
    }
    
    public function set_conversation_id($conversation_id){
        
        $this->conversation_id = $conversation_id;
    }
    
    public function set_message_id($message_id){
        
        $this->message_id = $message_id;
    }
    
    public function set_from_token($token){
        
        $this->sender_token = $token;
    }
    
    public function is_sender_alerted($is_sender){
        
        $this->is_sender = $is_sender;
    }
}
?>
