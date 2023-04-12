package com.syncadapters.czar.exchange.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.datamodels.MessageRead;
import com.syncadapters.czar.exchange.datamodels.MessageReceived;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.dialogs.HardwareLibraryDialog;
import com.syncadapters.czar.exchange.http.SocketListener;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.adapters.MessageRecycleViewAdapter;
import com.syncadapters.czar.exchange.datamodels.ConversationEntry;
import com.syncadapters.czar.exchange.datamodels.Message;
import com.syncadapters.czar.exchange.datamodels.Users;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.viewmodels.MessageActivityViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

@SuppressWarnings("ALL")
public class MessageActivity extends AppCompatActivity  {

    /*
    *
    *
    * */

    private App app;

    private static final String TAG = "MSG";
    private static final int MAX_STREAMS = 1;
    private static final int LEFT_VOLUME = 1;
    private static final int RIGHT_VOLUME = 1;
    private static final int SOUND_LOOP = 0;
    private static final int SOUND_RATE = 0;
    private static final int SOUND_PRIORITY = 1;

    private boolean is_transaction_request_icon = false;
    private RecyclerView message_recycleView;
    private EditText message_editText;

    private Users user;
    private ConversationEntry conversation_entry;
    private ArrayList<Message> messages = new ArrayList<>();

    private MessageRecycleViewAdapter message_recycle_view_adapter;
    private MessageActivityViewModel message_activity_view_model;

    private SoundPool sound_pool;
    private int recipient_message_effect;
    private int sender_message_effect;

    private WebSocket web_socket;
    private final Handler main_handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Log.d(TAG, "MessageActivity onCreate");

        message_activity_view_model = new ViewModelProvider(this).get(MessageActivityViewModel.class);
        message_activity_view_model.initialize(getApplication());
        message_activity_view_model.set_context(this);

        conversation_entry = new ConversationEntry();

        Log.d(TAG, "Executing....");
        //Log.d(TAG, "[Message Activity] First Name: " + conversation_entry.current_user.first_name + "  UID: " + conversation_entry.current_user.id);

        if(getIntent().hasExtra("user")) {
            // New Conversation

            //noinspection ConstantConditions
            user = getIntent().getExtras().getParcelable("user");

            assert user != null;
               // Log.d(TAG, "[MessageActivity] con_entry current user_id: " + con_entry.conversation_id);
             // Log.d(TAG, "[MessageActivity] recipient user id: " + user.id + " first name: " + user.first_name + " fcm_token: " + user.fcm_token + " distance: " + user.user_distance + " user url: " + user.user_image_url);

            Log.d(TAG, "[MessageActivity] recipient user id: " + user.id + " first name: " + user.first_name + " distance: " + user.user_distance + " user  thumbnail url: " + user.user_image_thumbnail_url);
            conversation_entry.current_user.first_name = UserSettings.get_user_first_name(this);
            conversation_entry.current_user.id = UserSettings.get_user_id(this);
            conversation_entry.current_user.user_image_thumbnail_url = UserSettings.get_user_profile_image_thumbnail_url(this);
            conversation_entry.current_user.user_image_full_url = UserSettings.get_user_profile_image_full_url(this);

            Log.d(TAG, "Current user id: " + conversation_entry.current_user.id);
            conversation_entry.recipient_user.first_name = user.first_name;
            conversation_entry.recipient_user.id = user.id;

            // conversation_entry.recipient_user.software = user.software;
           // conversation_entry.software.software_image_url = user.software.software_image_url;
            conversation_entry.recipient_user.user_image_thumbnail_url = user.user_image_thumbnail_url;
            conversation_entry.recipient_user.user_image_full_url = user.user_image_full_url;
          //  conversation_entry.recipient_user.fcm_token = user.fcm_token;


            if(getIntent().getExtras().getString("conversation_id") != null)
                conversation_entry.conversation_id = getIntent().getExtras().getString("conversation_id");
            else
                conversation_entry.conversation_id = null;

            if(getIntent().getExtras().getString("transaction_id") != null)
                conversation_entry.transaction_id = getIntent().getExtras().getString("transaction_id");
            else
                conversation_entry.transaction_id = null;

        }


        if(getIntent().hasExtra("conversation_entry")) {
            // New User
            // Resuming old conversation

            Log.d(TAG, "MessageActivity: Came from FirebaseMessaging");
            @SuppressWarnings("ConstantConditions") ConversationEntry conversation_entry_data = getIntent().getExtras().getParcelable("conversation_entry");

            assert conversation_entry_data != null;
            conversation_entry.current_user.first_name = UserSettings.get_user_first_name(this);
            conversation_entry.current_user.id = UserSettings.get_user_id(this);
            conversation_entry.current_user.user_image_thumbnail_url = UserSettings.get_user_profile_image_thumbnail_url(this);
            conversation_entry.current_user.user_image_full_url = UserSettings.get_user_profile_image_full_url(this);

            conversation_entry.recipient_user.first_name = conversation_entry_data.recipient_user.first_name;
            conversation_entry.recipient_user.id = conversation_entry_data.recipient_user.id;
         //   Toast.makeText(MessageActivity.this, "Recipient User ID: (" + conversation_entry.recipient_user.id + ")", Toast.LENGTH_LONG).show();
            conversation_entry.recipient_user.user_image_thumbnail_url = conversation_entry_data.recipient_user.user_image_thumbnail_url;
            conversation_entry.recipient_user.user_image_full_url = conversation_entry_data.recipient_user.user_image_full_url;
            Log.d(TAG, "[MessageActivity] Recipient Image Full URL: " + conversation_entry.recipient_user.user_image_full_url);
            conversation_entry.conversation_id = conversation_entry_data.conversation_id;
            conversation_entry.recent_message = conversation_entry_data.recent_message;

            messages = conversation_entry_data.messages;

            Log.d(TAG, "[MessageActivity] Conversation ID : " + conversation_entry.conversation_id);
            Log.d(TAG, "[MessageActivity] Message Size: " + messages.size() + " recipient " + conversation_entry.recent_message.message);
            for(int i = 0; i < messages.size(); i++){

                Log.d(TAG, "[MessageActivity] " + messages.get(i).first_name + " : " + messages.get(i).message);

            }

            //message_recycle_view_adapter = new MessageRecycleViewAdapter(MessageActivity.this, messages, UserSettings.get_user_id(MessageActivity.this), conversation_entry);
            //message_activity_view_model.load_conversation(message_recycleView, message_recycle_view_adapter, conversation_entry);
            int update_type = 1;
            message_activity_view_model.update_local_database(conversation_entry.conversation_id, DatabaseOperations.UPDATE, update_type);
            Log.d(TAG, "[Message Activity] Before MessageRecycleViewAdapter First Name: " + conversation_entry.current_user.first_name + "  UID: " + conversation_entry.current_user.id );
        }

      //  Toast.makeText(MessageActivity.this, "ID: " + conversation_entry.recipient_user.id + " First Name: " + conversation_entry.recipient_user.first_name , Toast.LENGTH_LONG).show();

        if(savedInstanceState != null){

            user = savedInstanceState.getParcelable("user");
            conversation_entry = savedInstanceState.getParcelable("conversation_entry");
            messages = savedInstanceState.getParcelableArrayList("messages");

        }

        ActionBar action_bar = getSupportActionBar();
        assert action_bar != null;
        action_bar.setDisplayShowHomeEnabled(true);
        action_bar.setDisplayUseLogoEnabled(true);
        action_bar.setDisplayHomeAsUpEnabled(true);
        action_bar.setTitle(conversation_entry.recipient_user.first_name);
        action_bar.show();

        message_recycleView = findViewById(R.id.message_recycleView_id);

        message_recycle_view_adapter = new MessageRecycleViewAdapter(MessageActivity.this, messages, conversation_entry);
        message_recycle_view_adapter.set_main_handler(main_handler);
        LinearLayoutManager linear_layout_manager = new LinearLayoutManager(MessageActivity.this);
        linear_layout_manager.setStackFromEnd(true);
        message_recycleView.setLayoutManager(linear_layout_manager);
        message_recycleView.setHasFixedSize(true);
        message_recycleView.setAdapter(message_recycle_view_adapter);

        ViewPreloadSizeProvider<Message> preload_size_provider = new ViewPreloadSizeProvider<>();

        int PRELOAD_AMOUNT = 10;
        RecyclerViewPreloader<Message> preloader =
                new RecyclerViewPreloader<>(Glide.with(this), message_recycle_view_adapter, preload_size_provider, PRELOAD_AMOUNT);

        message_recycleView.addOnScrollListener(preloader);
        message_recycleView.setItemViewCacheSize(0);

        AudioAttributes audio_attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        sound_pool = new SoundPool.Builder()
                .setMaxStreams(MAX_STREAMS)
                .setAudioAttributes(audio_attributes)
                .build();

        sender_message_effect = sound_pool.load(MessageActivity.this, R.raw.sender_message_effect, SOUND_PRIORITY);
        recipient_message_effect = sound_pool.load(MessageActivity.this, R.raw.recipient_message_effect, SOUND_PRIORITY);

        message_recycleView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {

            if(bottom < oldBottom){

                if(messages.size() >= 1) {
                    message_recycleView.post(() -> message_recycleView.smoothScrollToPosition(messages.size() - 1));
                }

            }

        });

        message_editText = findViewById(R.id.message_editText_id);
        message_editText.setOnFocusChangeListener((v, hasFocus) -> {

            int orientation = getResources().getConfiguration().orientation;
            if(orientation == Configuration.ORIENTATION_LANDSCAPE){

                Log.d(TAG, "MessageActivity onCreate(): Dismiss ActionBar");
                action_bar.hide();
            }

        });

        ImageButton send_button = findViewById(R.id.message_button_id);
        send_button.setOnClickListener(v -> {


            String message_content = message_editText.getText().toString();

            if(message_content.isEmpty())
                return;

            UUID uuid = UUID.randomUUID();
            String full_uuid = uuid.toString();
            String message_id = full_uuid.substring(0, 8);

            Log.d(TAG, "[MessageActivity] " + message_id + " : " +  message_content);
            Message message = new Message(UserSettings.get_user_first_name(getApplicationContext()), UserSettings.get_user_id(getApplicationContext()), message_id, message_content);
            Log.d(TAG, "CLICKED AND SENT!");

            Log.d(TAG, "[MessageActivity] " + message.first_name + " : " + message.message);
            message_editText.getText().clear();

            MessageActivityViewModel.send_message(conversation_entry, message, web_socket);

            sound_pool.play(sender_message_effect, LEFT_VOLUME , RIGHT_VOLUME, SOUND_PRIORITY , SOUND_LOOP , SOUND_RATE);
            messages.add(new Message(UserSettings.get_user_first_name(getApplicationContext()), UserSettings.get_user_id(getApplicationContext()), message_id, message_content));

            message_recycle_view_adapter.set_messages(messages);
            message_recycle_view_adapter.notifyDataSetChanged();

            message_recycleView.smoothScrollToPosition(messages.size() - 1);

        });

        if (message_recycle_view_adapter.getItemCount() >= 1) {

            message_recycleView.smoothScrollToPosition(messages.size() - 1);


            linear_layout_manager.scrollToPositionWithOffset(messages.size() - 1, 0);
            Log.d(TAG, "[MessageActivity] onCreate smoothScrollToPosition");
        }
        else
            Log.d(TAG, "[MessageActivity] NOT onCreate smoothScrollToPosition");

        if(is_transaction_request_icon){

            message_recycle_view_adapter.set_messages(messages);
            message_recycle_view_adapter.notifyDataSetChanged();
            is_transaction_request_icon = false;
        }

        app = ((App) getApplicationContext());
        EventBus.getDefault().register(this);


    }

    private void initialize_web_sockets(){

        SocketListener socket_listener = new SocketListener(this, message_recycleView, message_recycle_view_adapter, message_activity_view_model, conversation_entry);
        socket_listener.set_sound_effect(sound_pool, recipient_message_effect);

        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient http_client = new OkHttpClient();
                Request request = new Request.Builder().url("ws://64.227.0.161:8080/?id=" + UserSettings.get_user_id(MessageActivity.this)).build();
                web_socket = http_client.newWebSocket(request, socket_listener);
            }
        }).start();



        Log.d(TAG, "[MessageActivity] Web socket Initialized");
    }

    private void deinitialize_web_sockets(){

        //web_socket.close(1000, null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                web_socket.close(1000, null);
            }
        }).start();

        Log.d(TAG, "[MessageActivity] Web socket Deinitialized");
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageReceived message_receive){

        /*
        *  Determine if this message conversation id matches the current activities conversation id
        *  if they match, notify the Message adapter informing the user that the message was sent.
        *  if they do not match, then do nothing
        */

        Log.d(TAG, "[MessageActivity] onEvent");
        if(!message_receive.is_from_server) {

            if (conversation_entry.conversation_id.equals(message_receive.conversation_id)) {

                Log.d(TAG, "[FCM] Message Received message id: " + message_receive.id + "  message delivered status: " + message_receive.message_delivered);
                message_recycle_view_adapter.update_is_received(message_receive.id);
                message_recycle_view_adapter.notifyDataSetChanged();
            }
        }
        /*
        else{

            if (conversation_entry.conversation_id.equals(message_receive.conversation_id)) {

                Log.d(TAG, "[FCM] New Message Received message id: " + message_receive.id + "  conversation id: " + message_receive.conversation_id);

                final int update_type = 1;
                message_activity_view_model.update_local_database(message_receive.conversation_id, DatabaseOperations.UPDATE, update_type);
                message_recycle_view_adapter.addMessage(message);
                message_recycle_view_adapter.notifyDataSetChanged();
                message_recycleView.smoothScrollToPosition(message_recycle_view_adapter.getItemCount() - 1);

            }

        }
*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Message message){

        Log.d(TAG, "[MessageActivity] New Message from " + message.first_name + " : " + message.message);
        
        if(!message.is_read) {

            if (conversation_entry.conversation_id.equals(message.conversation_id)) {

                Log.d(TAG, "[FCM] New Message Received message id: " + message.id + "  conversation id: " + message.conversation_id + " message: " + message.message);

                final int update_type = 1;
                conversation_entry.recent_message = message;
                message_activity_view_model.update_local_database(message.conversation_id, DatabaseOperations.UPDATE, update_type);
                message_recycle_view_adapter.addMessage(message);
                message_recycle_view_adapter.notifyDataSetChanged();
                message_recycleView.smoothScrollToPosition(message_recycle_view_adapter.getItemCount() - 1);

            }
        }

    }


    @Override
    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.message_activity_menu_items, menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder menu_builder = (MenuBuilder) menu;
            menu_builder.setOptionalIconsVisible(true);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.recipient_library_id:
                launch_recipient_library_fragment();
                return true;

            case R.id.profile_settings_item_id:
                launch_profile_settings_activity();
                return true;

            case R.id.profile_platforms_item_id:
                launch_profile_platforms_activity();
                return true;

            case R.id.blocked_user_item_id:
                open_blocking_dialog_box();
                return true;

            case R.id.profile_logout_item_id:
                launch_profile_logout();
                return true;

            case android.R.id.home:
                Log.d(TAG, "[MessageActivity] Is NOT Root Activity");
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void launch_recipient_library_fragment(){


        Users recipient_user = new Users();
        recipient_user.id = conversation_entry.recipient_user.id;
        recipient_user.first_name = conversation_entry.recipient_user.first_name;
        recipient_user.user_image_thumbnail_url = conversation_entry.recipient_user.user_image_thumbnail_url;
        recipient_user.user_image_full_url = conversation_entry.recipient_user.user_image_full_url;
        Intent intent = new Intent(MessageActivity.this, RecipientInventoryActivity.class);
        intent.putExtra("user", recipient_user);
        startActivity(intent);


    }

    private void launch_profile_settings_activity(){

        Intent intent = new Intent(MessageActivity.this, ProfileSettingsActivity.class);
        startActivity(intent);

    }

    private void launch_profile_platforms_activity(){

        FragmentManager fragment_manager = getSupportFragmentManager();
        HardwareLibraryDialog hardware_library_dialog = new HardwareLibraryDialog();
        hardware_library_dialog.show(fragment_manager, "Hardware_Library_Dialog");

    }

    private void launch_profile_logout(){

        message_activity_view_model.logout(app, MessageActivity.this);

    }

    private void open_blocking_dialog_box(){

        AlertDialog.Builder blocking_alert_dialog = new AlertDialog.Builder(MessageActivity.this);
        blocking_alert_dialog.setMessage("Do you wish to block " + conversation_entry.recipient_user.first_name);
        blocking_alert_dialog.setPositiveButton("Yes", (dialog, which) -> message_activity_view_model.block_user(conversation_entry));

        blocking_alert_dialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());


        AlertDialog blocking_dialog = blocking_alert_dialog.create();
        blocking_dialog.show();

    }

    private void cancel_notification(){


        int NOTIFICATION_ID = 0;
        Log.d(TAG, "[MessageActivity] Notification int: " + NOTIFICATION_ID);
        NotificationManager notification_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification_manager.cancel(conversation_entry.conversation_id, NOTIFICATION_ID);

        //notification_manager.cancel(NOTIFICATION_ID);

    }


    @Override
    public void onStart(){
        super.onStart();

        Log.d(TAG, "MessageActivity onStart");

        cancel_notification();
       initialize_web_sockets();

       Log.d(TAG, "[MessageActivity] recent_message: " + conversation_entry.recent_message.message);
       if(!conversation_entry.recent_message.is_read){

           MessageRead message_read = new MessageRead();
           message_read.conversation_id = conversation_entry.recent_message.conversation_id;
           message_read.is_read = true;

           EventBus.getDefault().post(message_read);
       }

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MessageActivity onResume");
    }


    public void onSaveInstanceState(@NotNull Bundle out_state){
        super.onSaveInstanceState(out_state);

        out_state.putParcelable("user", user);
        out_state.putParcelableArrayList("messages", messages);
        out_state.putParcelable("conversation_entry", conversation_entry);

    }

    public void onRestoreInstanceState(Bundle in_state){
        super.onRestoreInstanceState(in_state);

        user = in_state.getParcelable("user");
        conversation_entry = in_state.getParcelable("conversation_entry");
        messages = in_state.getParcelableArrayList("messages");

    }

    @Override
    protected void onPause() {
        super.onPause();

       deinitialize_web_sockets();
        Log.d(TAG, "MessageActivity onPause");

    }

    @Override
    public void onStop(){
        super.onStop();

        Log.d(TAG, "MessageActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "MessageActivity onDestroy");

        EventBus.getDefault().unregister(this);
        sound_pool.release();
        sound_pool = null;
        message_recycle_view_adapter = null;
        message_recycleView.setAdapter(null);


    }
}
