package com.syncadapters.czar.exchange.services;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.messaging.RemoteMessage;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datamodels.MessageReceived;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;
import com.syncadapters.czar.exchange.interfaces.VolleyStringCallback;
import com.syncadapters.czar.exchange.notifications.NotificationHelper;
import com.syncadapters.czar.exchange.datamodels.ConversationEntry;
import com.syncadapters.czar.exchange.datamodels.Message;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.repositories.MessageRepository;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.TimeZone;

public class TheFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {


    private static final String TAG = "MSG";

    @Override
    @Subscribe
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        App app = ((App) getApplicationContext());

        MessageRepository message_repository = MessageRepository.getInstance();
        message_repository.initialize_database(getApplication());



        Message message;
        ConversationEntry conversation_entry;

        //EventBus.getDefault().register(this);

        Log.d(TAG, "In FirebaseMessagingService");
        if(remoteMessage.getData().containsKey("messaging")){

            Log.d(TAG, TimeZone.getDefault() + " FirebaseMessagingService: New Message");

            Log.d(TAG, "[Receiver] JSON: " + remoteMessage.getData().toString());
            Log.d(TAG, "New Message");
            message = new Message(remoteMessage.getData().get(getApplicationContext().getResources().getString(R.string.first_name_key)), remoteMessage.getData().get("from_id"), remoteMessage.getData().get("message_id"), remoteMessage.getData().get("message"));
            message.conversation_id = remoteMessage.getData().get("conversation_id");
            message.is_read = false;
            message.is_from_server = true;

            if(UserSettings.get_user_dpi(getApplicationContext()).equals(getApplicationContext().getResources().getString(R.string.ldpi_label))){

                message.profile_image_thumbnail_url = URL.LDPI + remoteMessage.getData().get(getApplicationContext().getResources().getString(R.string.profile_image_name_thumbnail_key));
                message.profile_image_full_url = URL.LDPI + remoteMessage.getData().get(getApplicationContext().getResources().getString(R.string.profile_image_name_full_key));

            }
            else if(UserSettings.get_user_dpi(getApplicationContext()).equals(getApplicationContext().getResources().getString(R.string.mdpi_label))){

                message.profile_image_thumbnail_url = URL.MDPI + remoteMessage.getData().get(getApplicationContext().getResources().getString(R.string.profile_image_name_thumbnail_key));
                message.profile_image_full_url = URL.MDPI + remoteMessage.getData().get(getApplicationContext().getResources().getString(R.string.profile_image_name_full_key));

            }
            else if(UserSettings.get_user_dpi(getApplicationContext()).equals(getApplicationContext().getResources().getString(R.string.hdpi_label))){

                message.profile_image_thumbnail_url = URL.HDPI + remoteMessage.getData().get(getApplicationContext().getResources().getString(R.string.profile_image_name_thumbnail_key));
                message.profile_image_full_url = URL.HDPI + remoteMessage.getData().get(getApplicationContext().getResources().getString(R.string.profile_image_name_full_key));

            }
            else if(UserSettings.get_user_dpi(getApplicationContext()).equals(getApplicationContext().getResources().getString(R.string.xhdpi_label))){

                message.profile_image_thumbnail_url = URL.XHDPI + remoteMessage.getData().get(getApplicationContext().getResources().getString(R.string.profile_image_name_thumbnail_key));
                message.profile_image_full_url = URL.XHDPI + remoteMessage.getData().get(getApplicationContext().getResources().getString(R.string.profile_image_name_full_key));

            }
            else if(UserSettings.get_user_dpi(getApplicationContext()).equals(getApplicationContext().getResources().getString(R.string.xxhdpi_label))){

                message.profile_image_thumbnail_url = URL.XXHDPI + remoteMessage.getData().get(getApplicationContext().getResources().getString(R.string.profile_image_name_thumbnail_key));
                message.profile_image_full_url = URL.XXHDPI + remoteMessage.getData().get(getApplicationContext().getResources().getString(R.string.profile_image_name_full_key));

            }
            else if(UserSettings.get_user_dpi(getApplicationContext()).equals(getApplicationContext().getResources().getString(R.string.xxxhdpi_label))){

                message.profile_image_thumbnail_url = URL.XXXHDPI + remoteMessage.getData().get(getApplicationContext().getResources().getString(R.string.profile_image_name_thumbnail_key));
                message.profile_image_full_url = URL.XXXHDPI + remoteMessage.getData().get(getApplicationContext().getResources().getString(R.string.profile_image_name_full_key));

            }


            conversation_entry = new ConversationEntry();
            conversation_entry.current_user.first_name = UserSettings.get_user_first_name(getApplicationContext());
            conversation_entry.current_user.id = UserSettings.get_user_id(getApplicationContext());
            Log.d(TAG, "[LoadConversation Task] 00) conversation_entry current user id:  " + conversation_entry.current_user.id);
            conversation_entry.current_user.user_image_thumbnail_url = UserSettings.get_user_profile_image_thumbnail_url(getApplicationContext());
            conversation_entry.current_user.user_image_full_url = UserSettings.get_user_profile_image_full_url(getApplicationContext());
            conversation_entry.recipient_user.first_name = remoteMessage.getData().get("first_name");
            conversation_entry.recipient_user.id = remoteMessage.getData().get("from_id");
            conversation_entry.recipient_user.user_image_thumbnail_url = message.profile_image_thumbnail_url;
            conversation_entry.recipient_user.user_image_full_url = message.profile_image_full_url;
            conversation_entry.conversation_id = remoteMessage.getData().get("conversation_id");
            conversation_entry.recent_message = message;

            message.recipient_user_id = conversation_entry.recipient_user.id;
            message.recipient_first_name = conversation_entry.recipient_user.first_name;
            message.recipient_profile_image_thumbnail_url = conversation_entry.recipient_user.user_image_thumbnail_url;
            message.recipient_profile_image_full_url = conversation_entry.recipient_user.user_image_full_url;

            Log.d(TAG, "First Name: " + conversation_entry.recipient_user.first_name + " Software Title: " + conversation_entry.software.title
            + " Platform: " + conversation_entry.software.platform + " Conversation ID: " + conversation_entry.conversation_id +
                    "  Transaction ID: " + conversation_entry.transaction_id + " Recipient ID: " + conversation_entry.recipient_user.id
            + " User Image Thumbnail URL: " + conversation_entry.recipient_user.user_image_thumbnail_url + " User Image Full URL: " + conversation_entry.recipient_user.user_image_thumbnail_url + " Recent Message: " + message.message + " Recent Message ID: " + message.id);

            message_repository.local_database(DatabaseOperations.INSERT, message);

            String conversation_id = message.conversation_id;
            String message_id = message.id;
            String fcm_token = UserSettings.get_fcm_token(getApplicationContext());
            String to_id = message.recipient_user_id;
            String message_content = message.message;

            //String url = URL.MESSAGE_DELIVERED_URL  + "?" + "conversation_id=" + conversation_id + "&" + "message_id=" + message_id + "&" + "fcm_token=" + fcm_token + "&" + "to_id=" + to_id + "&" + "message_content=" + message_content;
            String url = URL.MESSAGE_DELIVERED_URL;
            Log.d(TAG, "[TheFirebaseMessagingService] URL: " + url);
            JSONObject message_deliver_json = null;
            try{

                message_deliver_json = new JSONObject();
                message_deliver_json.put("conversation_id", conversation_id);
                message_deliver_json.put("message_id", message_id);
                message_deliver_json.put("fcm_token", fcm_token);
                message_deliver_json.put("to_id", to_id);
                message_deliver_json.put("message_content", message_content);

            }catch(JSONException json_error){
                json_error.printStackTrace();
            }

           // Volley volley = new Volley(getApplicationContext(), Request.Method.GET, url);
            Volley volley = new Volley(getApplicationContext(), Request.Method.POST, url, message_deliver_json);
            volley.Execute(new VolleyCallback()  {

                @Override
                public void network_response(JSONObject json_response) {



                        if (json_response.has("message_delivered")) {
                            Log.d(TAG, "[TheFirebaseMessagingService] Message was delivered");
                        }


                }

                @Override
                public void network_error(VolleyError error) {
                    error.printStackTrace();
                }
            });

            // Check if logged in. If logged in continue. leave it alone.
            if(!UserSettings.get_is_user_logged_in(getApplicationContext())) {
                Log.d(TAG, "[FirebaseMessagingService] User IS NOT logged in");
                return;
            }
            else
                Log.d(TAG, "[FirebaseMessagingService] User IS logged in");

                Log.d(TAG, "[LoadConversation Task] 01) conversation_entry current user id:  " + conversation_entry.current_user.id);
                NotificationHelper notification_helper = new NotificationHelper(getApplicationContext(), conversation_entry, message);
                notification_helper.create_message_notification_builder();
                notification_sound();
                EventBus.getDefault().post(message);
        }

        if(remoteMessage.getData().containsKey("message_received")){

            Log.d(TAG, "Message has been received!");
            Log.d(TAG, "[TheFirebaseMessagingService] Message:  " +  remoteMessage.getData().get("conversation_id") + " : " + remoteMessage.getData().get("message_id") );

            // update database
            MessageReceived message_received = new MessageReceived();
            message_received.conversation_id = remoteMessage.getData().get("conversation_id");
            message_received.id = remoteMessage.getData().get("message_id");
            message_received.message_delivered = 1;
            message_received.is_read = true;
            message_received.is_from_server = false;
            int update_type = 2;

            message_repository.update_message_received_database(message_received, DatabaseOperations.UPDATE, update_type);

            if(app.is_message_activity_in_foreground()){

                Log.d(TAG, "MessageActivity is in foreground. In onMessageReceived [Message] ");
                EventBus.getDefault().post(message_received);
                return;
            }
            else
                Log.d(TAG, "MessageActivity is not in foreground");

        }

        if(remoteMessage.getData().containsKey("message_error")) {

            Log.d(TAG, "Message was not delivered");
            Log.d(TAG, "[TheFirebaseMessagingService] Message:  " +  remoteMessage.getData().get("conversation_id") + " : " + remoteMessage.getData().get("message_id") );

            // update database
            MessageReceived message_error = new MessageReceived();
            message_error.conversation_id = remoteMessage.getData().get("conversation_id");
            message_error.id = remoteMessage.getData().get("message_id");
            message_error.message_delivered = -1;
            message_error.is_read = true;
            message_error.is_from_server = false;
            int update_type = 2;

            message_repository.update_message_received_database(message_error, DatabaseOperations.UPDATE, update_type);

            if(app.is_message_activity_in_foreground()){

                Log.d(TAG, "MessageActivity is in foreground. In onMessageReceived [Message] ");
                EventBus.getDefault().post(message_error);
            }
            else
                Log.d(TAG, "MessageActivity is not in foreground");

        }

        EventBus.getDefault().unregister(this);
    }

    private void notification_sound(){

        try {

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone notification_sound = RingtoneManager.getRingtone(getApplicationContext(), notification);
            notification_sound.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
