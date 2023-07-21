<?php
use Firebase\JWT\JWT;

class Token
{

    private $key;

    private $pdo;

    private $user_id;

    private $token;

    private $token_decoded;

    public function __construct($pdo)
    {
        $this->pdo = $pdo;
    }

    public function set_user_id($user_id)
    {
        $this->user_id = $user_id;
    }

    public function set_token($token)
    {
        $this->token = $token;
    }
     
    public function refresh_token()
    {
        $this->read_secret_key();
        
        $token_time_issued = time();
        $token_start_valid_time = $token_time_issued + 10;
        $token_end_valid_time = $token_start_valid_time + 3600;
        
        $payload = array(
            
            "user_id" => $this->user_id,
            "iss" => "Exchange",
            "iat" => $token_time_issued,
            "nbf" => $token_start_valid_time,
            "exp" => $token_end_valid_time
        );
        
        // print_r("The key: " . $this->key);
        $new_token = JWT::encode($payload, $this->key);
        return $new_token;
    
    }

    public function is_token_expired()
    {
        $this->read_secret_key();
        
        if(empty($this->token))
            return true;
        
        try {
            
            $this->token_decoded = JWT::decode($this->token, $this->key, array(
                'HS256'
            ));
        } catch (Firebase\JWT\ExpiredException $exp_error) {
            
           // $exp_error->getMessage();
            // print_r("Token is expired" . "\n");
            return true;
        }
        
        // print_r("Token not expired" . "\n");
        return false;
    }

    private function read_secret_key()
    {
        $path = '/var/www/exchange_project/';
        $file_path = $path . "keys/exchange_key.txt";
        
        $this->key = file_get_contents($file_path);
        $this->key = base64_decode($this->key);
    }
}

?>
