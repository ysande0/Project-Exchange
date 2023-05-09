<?php 
require_once 'User.php';

use Kreait\Firebase\Exception\FirebaseException;
use Kreait\Firebase\Auth;

class Account{
  
    private $firebase;
    private $user;
    
    
    public function __construct($firebase, $user){
        
        $this->firebase = $firebase->getAuth();
        $this->user = new User();
        $this->user = $user;
        
    }
    
    public function create_account(){
        
        
        
        $user_information = [
           
            "uid" => $this->user->uid,
          "email" => $this->user->email,
          "password" => $this->user->password,
           "emailverified" => false, 
           "displayName" => $this->user->first_name . " " . $this->user->last_name,
               
        ];
        
        $account_information = null;
        try{
            $account_information = $this->firebase->createUser($user_information);
        }catch(FirebaseException $firebase_error){
            $firebase_error->getMessage();
            return;
        }


    }
    
    public function update_password(){
        
        try{
           
            $this->firebase->changeUserPassword($this->user->uid, $this->user->password);
            
        }catch(FirebaseException $firebase_error){
            $firebase_error->getMessage();
            return;
        }
        
        
    }
    
    public function get_unique_id(){
        
        return $this->user->uid;
    }

    
    public function get_access_token(){
        
       return $this->user->access_token;
    }
    
    public function isEmailVerified(){
        
        try{
            $this->firebase->getUserByEmail($this->user->email)->emailVerified;
          return true;
        }catch (FirebaseException $firebase_error){
            
            $firebase_error->getMessage();
            return false;
        }
        
       
    }
    
    public function getDisplayName(){
        
        $display_name;
        try{
            
            $display_name = $this->firebase->getUserByEmail($this->user->email);
            
        }catch(FirebaseException $firebase_error){
            $firebase_error->getMessage();
            return;
        }
        
        return $display_name;
    }
    
    public function sign_in(){
        
        try{
            
           $this->firebase->verifyPassword($this->user->email, $this->user->password);
          
        }catch (Kreait\Firebase\Exception\Auth\InvalidPassword $e){
            
            echo $e->getMessage();
            return false;
        }

 
        return true;
    }
    
    
    public function sendEmailConfirmation(){
        
        $is_email_sent;
        
        try{ 
            $is_email_sent = true;
            $this->firebase->sendEmailVerification((string) $this->user->uid);
           
        }catch (\Kreait\Firebase\Exception\FirebaseException $firebase_error){
            
            $is_email_sent = false;
            $firebase_error->getMessage();
        }
        
        return $is_email_sent;
    }
    
    
   public function generate_Auth(){
        
   
       try{
          $this->user->access_token = (string) $this->firebase->createCustomToken($this->user->uid);
       }catch(FirebaseException $firebase_error){
           $firebase_error->getMessage();
       }
          return $this->user->access_token;
    }

}


?>