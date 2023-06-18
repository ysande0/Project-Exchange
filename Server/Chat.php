<?php

namespace MyApp;

$path = 'C:/Web/Project/TheExchange Project/';
require $path . 'DatabaseLoginInfo.php';
require $path . 'MessageEntry.php';
require $path . 'FirebaseMessage.php';
require $path . 'Game.php';
require $path . 'TransactionRequest.php';
require $path . 'TransactionResponse.php';

use Ratchet\MessageComponentInterface;
use Ratchet\ConnectionInterface;
use \MessageEntry;
use \Game;
use \TransactionRequest;
use \TransactionResponse;
use \FirebaseDB;
use \FirebaseMessage;
use Kreait\Firebase\Factory;
use Kreait\Firebase\ServiceAccount;
use Kreait\Firebase\Messaging\CloudMessage;
use Kreait\Firebase;



class Chat implements MessageComponentInterface {
    
    private $clients;
    private $message_entry;
    private $service_account;
    private $firebase;
    private $firebase_message;
    private $firebase_database;
    private $path;
    private $headline_game;
    private $transaction_request;
    private $transaction_response;
    private $user_id;
    
    public function __construct(){
        
        $this->path = 'C:/Web/Project/TheExchange Project/';
        $this->clients = array();
        $this->message_entry = new MessageEntry();
        $this->service_account = ServiceAccount::fromJsonFile($this->path . 'secret/exchange-project-30ec1-4c320432756f.json');
        $this->firebase = (new Factory())->withServiceAccount($this->service_account)->create();
        $this->firebase_message = new FirebaseMessage($this->firebase);
        $this->firebase_database = new FirebaseDB($this->firebase, "Conversations");
        $this->headline_game = new Game();
        $this->transaction_request = new TransactionRequest();
        $this->transaction_response = new TransactionResponse();
        
    }
    
    public function onOpen(ConnectionInterface $conn) {
        
        $query_array = array();
        $query_string = $conn->httpRequest->getUri()->getQuery();
        parse_str($query_string, $query_array);
        $this->user_id  = $query_array['id'];
        
        
        $this->clients[$this->user_id] = $conn;
        
        echo "Server: Connection Opened "  . $this->user_id . "\n";
    }
    
    /*
     * 100 - Messaging
     * 101 - Block user  <--- Will Handle in Message.php
     * 102 - Transaction Request
     * 103 - Transaction Response
     * 104 - Transaction Completion  <--- Will Handle in TransactionModule
     * 105 - Transaction Cancellation
     *
     * */
    
    
    public function onMessage(ConnectionInterface $from, $msg) {
        
        echo " Server: onMessage \n ";
        
        $message_input = json_decode($msg, true);
        
        $messaging_operation = $message_input['messaging_operation'];
        echo "Operation: " . $messaging_operation . "\n";
        $database_host = '127.0.0.1'; // Local Machine
        $database_name = 'exchange';
        $database_username = 'root';
        $database_password = '';
        
        
        try{
            
            $pdo = new \PDO("mysql:host=$database_host;dbname=$database_name", $database_username, $database_password);
            $pdo->setAttribute(\PDO::ATTR_ERRMODE, \PDO::ERRMODE_EXCEPTION);
            
            if($messaging_operation === 100){
                
                echo "*** Messaging in progress \n";
                $from_recipient = $message_input['from'];
                $to_recipient = $message_input['to'];
                $this->message_entry->conversation_id = $from_recipient['conversation_id'];
                $this->message_entry->from_id = $from_recipient['id'];
                $this->message_entry->to_id = $to_recipient['id'];
                $this->message_entry->first_name = $from_recipient['name'];
                $this->message_entry->message = $from_recipient['message'];
                $this->message_entry->time = $from_recipient['time'];
                $this->message_entry->date = $from_recipient['date'];
                $this->message_entry->profile_image_url = $from_recipient['profile_image_url'];
                
                
                
                $this->send_message($pdo);
            }
            
            
        }catch(\PDOException $pdo_error ){
            
            $pdo_error->getMessage();
        }
        
        echo " [onMessage] Exit \n";
        
        /*
         foreach ($this->clients as $client){
         
         if($client != $from){
         
         $client->send($msg);
         
         
         }
         
         }
         
         // Lookup UID
         
         $client = $this->clients[$this->USER_ID];
         $client->send($msg);
         */
    }
    
    public function onClose(ConnectionInterface $conn) {
        echo " Server: Connection Closed \n" ;
    }
    
    public function onError(ConnectionInterface $conn, \Exception $e) {
        
        // Check if user is logged in. If the user is logged in send them a notification
        
        echo " Server Error: " . $e->getMessage();
        
    }
    
    private function send_message($pdo){
        
        echo " Executing... \n";
        
        /*
         $sql = "SELECT blocked_user_id FROM blocked_users WHERE user_id = ?";
         $pdo_statement = $pdo->prepare($sql);
         $pdo_statement->execute([$this->message_entry->to_id]);
         $blocked_user_id = $pdo_statement->fetch(\PDO::FETCH_ASSOC);
         
         if($this->message_entry->from_id === $blocked_user_id['blocked_users_id']){
         echo $this->message_entry->from_id . " has been blocked by " . $this->message_entry->to_id;
         return;
         }
         else
         echo " User not blocked \n";
         */
         $message_response = json_encode(array("messaging" => true , 'conversation_id' => $this->message_entry->conversation_id ,'first_name' => $this->message_entry->first_name, 'from_id' => $this->message_entry->from_id,
             'message' => $this->message_entry->message, 'profile_image_url' => $this->message_entry->profile_image_url));
         
         
         if(array_key_exists($this->message_entry->to_id, $this->clients)){
             
             echo $this->message_entry->to_id . " is currently available";
             $client = $this->clients[$this->message_entry->to_id];
             $client->send($message_response);
             
             echo " Message Sent to " . $this->message_entry->to_id  ."\n";
             
         }else{
             
             echo $this->message_entry->to_id . " is currently unavailable";
             
             $sql = "SELECT fcm_token FROM users WHERE user_id = ?";
             $pdo_statement = $pdo->prepare($sql);
             $pdo_statement->execute([$this->message_entry->to_id]);
             $token = $pdo_statement->fetch(\PDO::FETCH_ASSOC);
             
             $this->firebase_messaging = $this->firebase->getMessaging();
             $this->message_entry->fcm_token = $token['fcm_token'];
             
             
             
             $message = CloudMessage::withTarget('token',  $this->message_entry->fcm_token)
             ->withData(["messaging" => true , 'conversation_id' => $this->message_entry->conversation_id,'first_name' => $this->message_entry->first_name, 'from_id' => $this->message_entry->from_id,
                 'message' => $this->message_entry->message, 'profile_image_url' => $this->message_entry->profile_image_url,]);
             
             try{
                 
                 $this->firebase_messaging->validate($message);
                 
                 echo "FCM: Message from " . $this->message_entry->from_id .  " to " . $this->message_entry->to_id . " has been sent";
                 echo "to FCM: " . $this->message_entry->fcm_token;
             }catch(\Kreait\Firebase\Exception\Messaging\InvalidMessage $error){
                 print_r($error->errors());
             }
             
             $this->firebase_messaging->send($message);
             
             return;
             
         }
            
    }
    
    private function block_user($pdo, $message_input){
        
        $from_recipient = $message_input['from'];
        $to_recipient = $message_input['to'];
        
        $message_entry->from_id = $from_recipient['id'];
        $message_entry->to_id = $to_recipient['id'];
        
        $sql = "SELECT COUNT(1) AS blocked_entry FROM blocked_users WHERE user_id = ? AND blocked_user_id = ?";
        $pdo_statement = $pdo->prepare($sql);
        $pdo_statement->execute([$message_entry->from_id, $message_entry->to_id]);
        $entry = $pdo_statement->fetch(\PDO::FETCH_ASSOC);
        
        
        if($entry['blocked_entry'] == 1){
            return;
        }
         
    }
    
    
}
?>