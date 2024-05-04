package com.syncadapters.czar.exchange.http;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.adapters.MessageRecycleViewAdapter;
import com.syncadapters.czar.exchange.datamodels.ConversationEntry;
import com.syncadapters.czar.exchange.datamodels.Message;
import com.syncadapters.czar.exchange.datamodels.MessageReceived;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.notifications.NotificationHelper;
import com.syncadapters.czar.exchange.viewmodels.MessageActivityViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SocketListener extends WebSocketListener {

    private static final String TAG = "MSG";
    private static final int LEFT_VOLUME = 1;
    private static final int RIGHT_VOLUME = 1;
    private static final int SOUND_LOOP = 0;
    private static final int SOUND_RATE = 0;
    private static final int SOUND_PRIORITY = 1;
    private final WeakReference<Activity> activity_weak_reference;
    private final WeakReference<RecyclerView> message_recycler_view_weak_reference;
    private final WeakReference<MessageActivityViewModel> message_activity_view_model_weak_reference;
    private final WeakReference<MessageRecycleViewAdapter> message_recycle_view_adapter_weak_reference;
    private Message message;
    // --Commented out by Inspection (1/9/2021 11:53 PM):private MessageRepository message_repository = null;
    private final ConversationEntry conversation_entry;
    private SoundPool sound_pool;
    private int recipient_sound_effect;

    public SocketListener(Activity activity, RecyclerView message_recycleView, MessageRecycleViewAdapter message_recycle_view_adapter,
                          MessageActivityViewModel message_activity_view_model, ConversationEntry conversation_entry){

        this.activity_weak_reference = new WeakReference<>(activity);
        this.message_recycler_view_weak_reference = new WeakReference<>(message_recycleView);
        this.message_activity_view_model_weak_reference = new WeakReference<>(message_activity_view_model);
        this.message_recycle_view_adapter_weak_reference = new WeakReference<>(message_recycle_view_adapter);

        this.conversation_entry = conversation_entry;
    }

    public void set_sound_effect(SoundPool sound_pool, int recipient_sound_effect){

        this.sound_pool = sound_pool;
        this.recipient_sound_effect = recipient_sound_effect;

    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);

    }

    @Override
    public void onMessage(@NotNull WebSocket web_socket, @NotNull String text) {
        super.onMessage(web_socket, text);

        message = new Message();
        JSONObject message_json;

        ConversationEntry incoming_conversation_entry = new ConversationEntry();
        try{

            message_json = new JSONObject(text);
            if(message_json.has("messaging")){

                message.first_name = message_json.getString("first_name");
                message.user_id = message_json.getString("from_id");
                message.id = message_json.getString("message_id");
                message.message = message_json.getString("message");
                message.conversation_id = message_json.getString("conversation_id");
                message.is_from_server = true;
                message.is_read = true;
                message.recipient_user_id = message_json.getString("from_id");
                message.recipient_first_name = message_json.getString("first_name");

                if(UserSettings.get_user_dpi(activity_weak_reference.get()).equals(activity_weak_reference.get().getResources().getString(R.string.ldpi_label))){

                    message.profile_image_thumbnail_url = URL.LDPI + message_json.getString(activity_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));
                    message.profile_image_full_url = URL.LDPI + message_json.getString(activity_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key));
                    message.recipient_profile_image_thumbnail_url = message.profile_image_thumbnail_url;
                    message.recipient_profile_image_full_url = message.profile_image_full_url;

            }
                else if(UserSettings.get_user_dpi(activity_weak_reference.get()).equals(activity_weak_reference.get().getResources().getString(R.string.mdpi_label))){

                    message.profile_image_thumbnail_url = URL.MDPI + message_json.getString(activity_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));
                    message.profile_image_full_url = URL.MDPI + message_json.getString(activity_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key));
                    message.recipient_profile_image_thumbnail_url = message.profile_image_thumbnail_url;
                    message.recipient_profile_image_full_url = message.profile_image_full_url;

                }
                else if(UserSettings.get_user_dpi(activity_weak_reference.get()).equals(activity_weak_reference.get().getResources().getString(R.string.hdpi_label))){

                    message.profile_image_thumbnail_url = URL.HDPI + message_json.getString(activity_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));
                    message.profile_image_full_url = URL.HDPI + message_json.getString(activity_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key));
                    message.recipient_profile_image_thumbnail_url = message.profile_image_thumbnail_url;
                    message.recipient_profile_image_full_url = message.profile_image_full_url;
                }
                else if(UserSettings.get_user_dpi(activity_weak_reference.get()).equals(activity_weak_reference.get().getResources().getString(R.string.xhdpi_label))){

                    message.profile_image_thumbnail_url = URL.XHDPI + message_json.getString(activity_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));
                    message.profile_image_full_url = URL.XHDPI + message_json.getString(activity_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key));
                    message.recipient_profile_image_thumbnail_url = message.profile_image_thumbnail_url;
                    message.recipient_profile_image_full_url = message.profile_image_full_url;
                }
                else if(UserSettings.get_user_dpi(activity_weak_reference.get()).equals(activity_weak_reference.get().getResources().getString(R.string.xxhdpi_label))){

                    message.profile_image_thumbnail_url = URL.XXHDPI + message_json.getString(activity_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));
                    message.profile_image_full_url = URL.XXHDPI + message_json.getString(activity_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key));
                    message.recipient_profile_image_thumbnail_url = message.profile_image_thumbnail_url;
                    message.recipient_profile_image_full_url = message.profile_image_full_url;
                }
                else if(UserSettings.get_user_dpi(activity_weak_reference.get()).equals(activity_weak_reference.get().getResources().getString(R.string.xxxhdpi_label))){

                    message.profile_image_thumbnail_url = URL.XXXHDPI + message_json.getString(activity_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));
                    message.profile_image_full_url = URL.XXXHDPI + message_json.getString(activity_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key));
                    message.recipient_profile_image_thumbnail_url = message.profile_image_thumbnail_url;
                    message.recipient_profile_image_full_url = message.profile_image_full_url;
                }


                message.message_delivered = 1;
                message_activity_view_model_weak_reference.get().process_messages(DatabaseOperations.INSERT, message);

                incoming_conversation_entry.current_user.id = UserSettings.get_user_id(activity_weak_reference.get().getApplicationContext());


                try{

                    JSONObject ack_json = new JSONObject();
                    ack_json.put("messaging_operation", 101);
                    ack_json.put("conversation_id", message.conversation_id);
                    ack_json.put("message_id", message.id);
                    ack_json.put("message", message.message);
                    ack_json.put("to_id", message.recipient_user_id);

                    web_socket.send(ack_json.toString());

                }catch(JSONException json_error){
                    json_error.printStackTrace();
                }

            }

            if(message_json.has("message_received")){

                MessageReceived message_received = new MessageReceived();
                message_received.conversation_id = message_json.getString("conversation_id");
                message_received.id = message_json.getString("message_id");
                message_received.message_delivered = 1;

                message_activity_view_model_weak_reference.get().update_message_received(message_received);
                activity_weak_reference.get().runOnUiThread(() -> {

                    message_recycle_view_adapter_weak_reference.get().update_is_received(message_received.id);
                    message_recycle_view_adapter_weak_reference.get().notifyDataSetChanged();

                    message_recycler_view_weak_reference.get().smoothScrollToPosition(message_recycle_view_adapter_weak_reference.get().getItemCount() - 1);
        
                });
                return;
            }

            if(message_json.has("message_error")){

            
                MessageReceived message_error = new MessageReceived();
                message_error.conversation_id = message_json.getString("conversation_id");
                message_error.id = message_json.getString("message_id");
                message_error.message_delivered = -1;

                message_activity_view_model_weak_reference.get().update_message_received(message_error);
            
                activity_weak_reference.get().runOnUiThread(() -> {

                    message_recycle_view_adapter_weak_reference.get().update_is_error(message_error.id);
                    message_recycle_view_adapter_weak_reference.get().notifyDataSetChanged();

                    message_recycler_view_weak_reference.get().smoothScrollToPosition(message_recycle_view_adapter_weak_reference.get().getItemCount() - 1);
                    // play effect
                });
                return;
            }

        }catch(JSONException json_error){

            json_error.printStackTrace();

        }


        //noinspection ConstantConditions
        if(conversation_entry.conversation_id == null || !(conversation_entry.conversation_id.equals(message.conversation_id))) {

                message.is_read = false;
                EventBus.getDefault().post(message);
                incoming_conversation_entry.recipient_user.id = message.recipient_user_id;
                incoming_conversation_entry.recipient_user.first_name = message.first_name;
                incoming_conversation_entry.recipient_user.user_image_thumbnail_url = message.profile_image_thumbnail_url;
                incoming_conversation_entry.recipient_user.user_image_full_url = message.profile_image_full_url;
                incoming_conversation_entry.conversation_id = message.conversation_id;

                incoming_conversation_entry.recent_message = message;

                NotificationHelper notification_helper = new NotificationHelper(activity_weak_reference.get().getApplicationContext(), incoming_conversation_entry, message);
                notification_helper.create_message_notification_builder();
                notification_sound();
                return;
            }
            else{

                EventBus.getDefault().post(message);
            }

        activity_weak_reference.get().runOnUiThread(() -> {

            this.sound_pool.play(recipient_sound_effect, LEFT_VOLUME , RIGHT_VOLUME, SOUND_PRIORITY , SOUND_LOOP , SOUND_RATE);
            message_recycle_view_adapter_weak_reference.get().addMessage(message);
            message_recycle_view_adapter_weak_reference.get().notifyDataSetChanged();

            message_recycler_view_weak_reference.get().smoothScrollToPosition(message_recycle_view_adapter_weak_reference.get().getItemCount() - 1);
            // play effect
        });

    }

    private void notification_sound(){

        try {

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone notification_sound = RingtoneManager.getRingtone(activity_weak_reference.get(), notification);
            notification_sound.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosing(webSocket, code, reason);
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);

        Log.d(TAG, "--> MessageActivity SocketListener: Connection is Closed <-- ");
        Log.d(TAG, "MessageActivity SocketListener Closed Reason: " + reason);

    }


    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);


    }

}
