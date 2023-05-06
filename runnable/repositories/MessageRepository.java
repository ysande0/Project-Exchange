package com.syncadapters.czar.exchange.repositories;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.LoginActivity;
import com.syncadapters.czar.exchange.activities.MessageActivity;
import com.syncadapters.czar.exchange.asynctasks.MyConversationsTask;
import com.syncadapters.czar.exchange.datamodels.MessageReceived;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;
import com.syncadapters.czar.exchange.datamodels.Message;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.roomdatabase.ExchangeDatabase;
import com.syncadapters.czar.exchange.roomdatabase.MyConversations;
import com.syncadapters.czar.exchange.roomdatabase.MyConversationsDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

@SuppressWarnings("unused")
public class MessageRepository {

    private static final String TAG = "MSG";

    private WeakReference<Context> context_weak_reference;
    private static MessageRepository message_repository;
    private MyConversationsTask my_conversations_task;
    private Volley volley;
    private MyConversationsDao my_conversation_dao;



    public static MessageRepository getInstance(){

        if(message_repository == null)
            message_repository = new MessageRepository();

        return message_repository;
    }

    public void initialize_database(Context context){

        ExchangeDatabase exchange_database = ExchangeDatabase.get_database(context);
        this.my_conversation_dao = exchange_database.my_conversations_dao();
    }

    public void set_context(Context context){

        this.context_weak_reference = new WeakReference<>(context);
    }

    public void local_database(DatabaseOperations db_ops, Message message){

        if (db_ops == DatabaseOperations.INSERT) {
            insert_local_database(message);
        }


    }

    public void logout(App app, Context context){

        FirebaseAuth.getInstance().signOut();
        JSONObject json_object = null;
        try{

            json_object = new JSONObject();
            json_object.put(context.getResources().getString(R.string.user_id_key), UserSettings.get_user_id(context.getApplicationContext()));
            json_object.put(context.getResources().getString(R.string.email_key), UserSettings.get_user_email(context.getApplicationContext()));

        }catch(JSONException json_error){

            json_error.printStackTrace();
        }

        volley = new Volley(context.getApplicationContext(), Request.Method.POST, URL.LOGOUT_URL, json_object);
        volley.set_priority(Request.Priority.IMMEDIATE);
        volley.Execute(new VolleyCallback() {
            @Override
            public void network_response(JSONObject json_response) {

                try {

                    if(json_response.has(context.getApplicationContext().getResources().getString(R.string.logout_error_100_label))){

                        String logout_100_error_message = json_response.getString(context.getApplicationContext().getResources().getString(R.string.logout_error_100_label));
                        Toast.makeText(context.getApplicationContext(), logout_100_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(json_response.has(context.getApplicationContext().getResources().getString(R.string.logout_error_101_label))){

                        String logout_101_error_message = json_response.getString(context.getResources().getString(R.string.logout_error_101_label));
                        Toast.makeText(context.getApplicationContext(), logout_101_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(json_response.has(context.getApplicationContext().getResources().getString(R.string.logout_error_103_label))){

                        String logout_103_error_message = json_response.getString(context.getApplicationContext().getResources().getString(R.string.logout_error_103_label));
                        Toast.makeText(context.getApplicationContext(), logout_103_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }


                    if(json_response.has("logout_100")){

                        if(json_response.getBoolean("logout_100")){

                            Log.d(TAG, "Logging out...");
                            launch_logout_activity(app, context);

                        }

                    }

                }catch(JSONException json_error){
                    json_error.printStackTrace();
                }

            }

            @Override
            public void network_error(VolleyError error) {

                launch_logout_activity(app, context.getApplicationContext());

            }
        });

    }

    public void block_user(JSONObject sender_json_message){

        volley = new Volley(context_weak_reference.get(), Request.Method.POST, URL.BLOCKED_USER_URL, sender_json_message);
        volley.Execute(new VolleyCallback() {
            @SuppressWarnings("unused")
            @Override
            public void network_response(JSONObject json_response) {

                try {

                    if (json_response.has(context_weak_reference.get().getResources().getString(R.string.user_already_blocked_label))) {

                        if (json_response.getBoolean(context_weak_reference.get().getResources().getString(R.string.user_already_blocked_label))) {
                            return;
                        }

                    }

                    if(json_response.has(context_weak_reference.get().getResources().getString(R.string.block_user_error_100_label))){

                        //noinspection unused
                        String user_is_blocked_error_message = json_response.getString(context_weak_reference.get().getResources().getString(R.string.block_user_error_100_label));
                        Toast.makeText(context_weak_reference.get(), user_is_blocked_error_message, Toast.LENGTH_LONG).show();
                        return;

                    }

                    if(json_response.has(context_weak_reference.get().getResources().getString(R.string.user_is_blocked_label))){

                            String user_is_blocked_error_message = json_response.getString(context_weak_reference.get().getResources().getString(R.string.user_is_blocked_label));
                            Toast.makeText(context_weak_reference.get(), user_is_blocked_error_message, Toast.LENGTH_LONG).show();

                    }

                }catch(JSONException json_error){

                    json_error.printStackTrace();
                }

            }

            @Override
            public void network_error(VolleyError error) {

                if(error.networkResponse == null){

                        Toast.makeText(context_weak_reference.get(), "internet connection error, please try again.", Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    private void insert_local_database(Message message){

        Log.d(TAG, "[MessageRepository] Current Message is read: " + message.is_read);
        MyConversations my_conversations = new MyConversations(message.first_name, message.user_id, message.id, message.message, message.conversation_id,
                 message.time, message.date, message.profile_image_thumbnail_url, message.profile_image_full_url, message.recipient_user_id,
                message.recipient_first_name, message.recipient_profile_image_thumbnail_url, message.recipient_profile_image_full_url,
                message.is_read, message.message_delivered);

        my_conversations_task = new MyConversationsTask(my_conversations, my_conversation_dao, DatabaseOperations.INSERT);
        my_conversations_task.execute();


    }

    public void update_local_database(String conversation_id,
                                          DatabaseOperations database_operations, int update_type){

        my_conversations_task = new MyConversationsTask(conversation_id, this.my_conversation_dao, database_operations);
        my_conversations_task.set_update_type(update_type);
        my_conversations_task.execute();

    }

    public void update_message_received_database(MessageReceived message_received, DatabaseOperations database_operations, int update_type){

        my_conversations_task = new MyConversationsTask(message_received.conversation_id, this.my_conversation_dao, database_operations);
        my_conversations_task.set_update_type(update_type);
        my_conversations_task.set_message_id(message_received.id);
        my_conversations_task.set_message_delivered(message_received.message_delivered);
        my_conversations_task.execute();

    }

    private void launch_logout_activity(App app, Context context){

        app.set_is_user_logged_in(false);
        app.set_location_permission(false);

        UserSettings.set_is_user_logged_in(context, false);
        UserSettings.remove_user_token(context);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((MessageActivity) context).finish();
    }


}
