<?php 

class MessageEntry{
    
    public $conversation_id;
    public $first_name;
    public $from_uid;
    public $to_uid;
    public $to_id;
    public $from_id;
    public $message_id;
    public $message;
    public $time;
    public $date;
    public $profile_image_thumbnail_url;
    public $profile_image_full_url;
    public $profile_image_name_thumbnail;
    public $profile_image_name_full;
    public $software_image_thumbnail_url;
    public $software_image_full_url;
    public $offer_games = array();
    public $want_games = array();
    public $recipient_fcm_token;
    public $sender_fcm_token;
    public $isActive;
    
}


?>