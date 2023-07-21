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

    /*
     * public function validate(){
     *
     * $this->read_secret_key();
     * $does_token_exist = false;
     * // Check if this user has been assigned this token
     *
     * $sql = "SELECT EXISTS (SELECT * FROM tokens WHERE user_id = ? AND token = ?)";
     * $pdo_statement = $this->pdo->prepare($sql);
     * $pdo_statement->execute([$this->user_id, $this->token]);
     * $query_user_id = $pdo_statement->fetch(PDO::FETCH_NUM);
     *
     * if($query_user_id[0] == 1)
     * $does_token_exist = true;
     * else if($query_user_id[0] == 0)
     * $does_token_exist = false;
     *
     * // print_r("DB STATUS: " . $query_user_id[0] . "\n");
     *
     * if($does_token_exist){
     *
     * // print_r("This token exists " . "\n");
     * $is_expired = $this->is_token_expired();
     *
     * if($is_expired)
     * return $is_expired;
     * else if(!$is_expired)
     * return $is_expired;
     *
     * }
     * else if(!$does_token_exist){
     * // print_r("This token does not exist" . "\n");
     * return true;
     * }
     *
     *
     *
     * }
     */
     
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
        /*
        $sql = "UPDATE tokens SET token = ?, token_issue_time = ?, token_start_valid_time = ?, token_end_valid_time = ? WHERE user_id = ?";
        $pdo_statement = $this->pdo->prepare($sql);
        $pdo_statement->execute([
            $new_token,
            $token_time_issued,
            $token_start_valid_time,
            $token_end_valid_time,
            $this->user_id
        ]);
        */
        return $new_token;
    
    }

    /*
     * public function create_token_table_entry(){
     *
     * $sql = "INSERT INTO tokens (token, token_issue_time, token_start_valid_time, token_end_valid_time) VALUES (0, 0, 0, 0)";
     * $pdo_statement = $this->pdo->prepare($sql);
     * $pdo_statement->execute();
     * }
     */
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
       // $path = 'C:/Web/Project/TheExchange Project/';
        $file_path = $path . "keys/exchange_key.txt";
        
        $this->key = file_get_contents($file_path);
        $this->key = base64_decode($this->key);
    }
}

?>