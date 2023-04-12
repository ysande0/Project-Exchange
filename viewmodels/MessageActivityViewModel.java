package com.syncadapters.czar.exchange.viewmodels;

import android.app.Application;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datamodels.ConversationEntry;
import com.syncadapters.czar.exchange.datamodels.Message;
import com.syncadapters.czar.exchange.datamodels.MessageReceived;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.repositories.MessageRepository;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.UUID;

import okhttp3.WebSocket;

public class MessageActivityViewModel extends ViewModel {



    private static final String TAG = "MSG";
    private static MessageRepository message_repository;
    private static WeakReference<Context> context_weak_reference;

    public void initialize(Application application){

        if(message_repository == null)
            message_repository = MessageRepository.getInstance();


        message_repository.initialize_database(application);


    }

    public void set_context(Context context){

        context_weak_reference = new WeakReference<>(context);
        message_repository.set_context(context_weak_reference.get());

    }

    public static void send_message(ConversationEntry conversation_entry, Message message, WebSocket web_socket){

        Log.d(TAG, "Sending Message");
        // Sends message data to repository. Repository will send data to remote server

        Log.d(TAG, "Recipient Name: " + conversation_entry.recipient_user.first_name + " ID: " + conversation_entry.recipient_user.id);

        JSONObject correspondents_json_object = null;
        JSONObject sender_json_message;
        JSONObject receive_json_message;
        try{

            correspondents_json_object = new JSONObject();
            sender_json_message = new JSONObject();
            sender_json_message.put("name", UserSettings.get_user_first_name(context_weak_reference.get()));
            sender_json_message.put("id", UserSettings.get_user_id(context_weak_reference.get()));
            sender_json_message.put("sender_fcm_token", UserSettings.get_fcm_token(context_weak_reference.get()));
            // recipient_fcm_token
            sender_json_message.put("access_token", UserSettings.get_user_token(context_weak_reference.get()));
            sender_json_message.put("message", message.message);
            sender_json_message.put("time", message.time);
            sender_json_message.put("date", message.date);

            sender_json_message.put("message_id", message.id);
            if(conversation_entry.conversation_id == null) {
                Log.d(TAG, "conversation_id is null");

                UUID uuid = UUID.randomUUID();
                String root_conversation_id = uuid.toString();
                String conversation_id_suffix = String.valueOf(System.currentTimeMillis());

                conversation_entry.conversation_id = "con_" + root_conversation_id.concat(conversation_id_suffix);

                message.conversation_id = conversation_entry.conversation_id;
                sender_json_message.put("conversation_id", conversation_entry.conversation_id);
            }

            Log.d(TAG, "[MessageActivity] profile image full: " + conversation_entry.current_user.user_image_name_full + "  thumbnail: " + conversation_entry.current_user.user_image_name_thumbnail);
            Log.d(TAG, "[MessageActivity] profile image full: " + conversation_entry.current_user.user_image_name_full + "  thumbnail: " + conversation_entry.current_user.user_image_name_thumbnail);

            sender_json_message.put(context_weak_reference.get().getResources().getString(R.string.conversation_id_key), conversation_entry.conversation_id);
            sender_json_message.put(context_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key), UserSettings.get_user_profile_image_name_thumbnail(context_weak_reference.get()));
            sender_json_message.put(context_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key), UserSettings.get_user_profile_image_name_full(context_weak_reference.get()));
            correspondents_json_object.put("from", sender_json_message);
            correspondents_json_object.put("messaging_operation", 100);

            receive_json_message = new JSONObject();

            receive_json_message.put("id", conversation_entry.recipient_user.id);
            receive_json_message.put("name", conversation_entry.recipient_user.first_name);
            correspondents_json_object.put("to", receive_json_message);

        }catch(JSONException json_error){

            json_error.printStackTrace();
        }

        Log.d(TAG, "[Sender] JSON: " + correspondents_json_object.toString());
        boolean is_sent = web_socket.send(correspondents_json_object.toString());
        message.recipient_first_name = conversation_entry.recipient_user.first_name;
        message.conversation_id = conversation_entry.conversation_id;
        message.profile_image_thumbnail_url = UserSettings.get_user_profile_image_thumbnail_url(context_weak_reference.get());
        message.profile_image_full_url = UserSettings.get_user_profile_image_full_url(context_weak_reference.get());
        message.recipient_user_id  = conversation_entry.recipient_user.id;
        message.recipient_profile_image_thumbnail_url = conversation_entry.recipient_user.user_image_thumbnail_url;
        message.recipient_profile_image_full_url = conversation_entry.recipient_user.user_image_full_url;

        message.is_read = true;
        message.message_delivered = 0;
        message.transaction_id = "0";

        if(!is_sent) {

            message.message_delivered = -1;
        }

        EventBus.getDefault().post(message);
        message_repository.local_database(DatabaseOperations.INSERT,  message);

    }

    public void block_user(ConversationEntry conversation_entry){

        JSONObject sender_json_message = null;
        try{

            sender_json_message = new JSONObject();
            sender_json_message.put("ops", 1);
            sender_json_message.put("from_id", UserSettings.get_user_id(context_weak_reference.get()));
            sender_json_message.put("to_id", conversation_entry.recipient_user.id);

        }catch(JSONException json_error){

            json_error.printStackTrace();
        }


        message_repository.block_user(sender_json_message);

    }


    public void process_messages(DatabaseOperations database_operations, Message message){

        message_repository.local_database(database_operations,  message);
    }

    public void update_local_database(String conversation_id, DatabaseOperations database_operation,
                                       int update_type){

        message_repository.update_local_database(conversation_id, database_operation,  update_type);

    }

    public void update_message_received(MessageReceived message_receive){

        int update_type = 2;
        message_repository.update_message_received_database(message_receive, DatabaseOperations.UPDATE, update_type);
    }


    public void logout(App app, Context context){

        message_repository.logout(app, context);
    }

}
