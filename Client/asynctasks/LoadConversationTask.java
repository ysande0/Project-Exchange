package com.syncadapters.czar.exchange.asynctasks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.RequestManager;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.activities.HomeActivity;
import com.syncadapters.czar.exchange.activities.MessageActivity;
import com.syncadapters.czar.exchange.adapters.ConversationsEntryAdapter;
import com.syncadapters.czar.exchange.datamodels.ConversationEntry;
import com.syncadapters.czar.exchange.datamodels.Message;
import com.syncadapters.czar.exchange.datamodels.MessageRead;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datamodels.Users;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.roomdatabase.MyConversations;
import com.syncadapters.czar.exchange.roomdatabase.MyConversationsDao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoadConversationTask extends AsyncTask<Void, Void, ArrayList<Message>> {

    private static final String TAG = "OUT";

    private final WeakReference<Context> context_weak_reference;
    private RequestManager glide_request_manager;
    private WeakReference<RecyclerView> conversations_entry_recycle_view_weak_reference;
    private WeakReference<TextView> no_messages_available_textView_weak_reference;
    private NotificationCompat.Builder notification_builder;
    private WeakReference<FrameLayout> conversations_fragment_frame_layout_weak_reference;
    private ConversationsEntryAdapter conversations_entry_adapter;
    private ConversationEntry conversation_entry;
    private final MyConversationsDao my_conversation_dao;
    private UserInterface user_interface;
    private final ArrayList<Message> messages_conversation_entries = new ArrayList<>();
    private Users user;
    private App app;
    private Boolean is_home_activity_foreground = false;

    public LoadConversationTask(Context context, RecyclerView conversations_entry_recycle_view, ConversationsEntryAdapter conversations_entry_adapter,
                                MyConversationsDao my_conversation_dao){

        context_weak_reference = new WeakReference<>(context);
        this.conversations_entry_recycle_view_weak_reference = new WeakReference<>(conversations_entry_recycle_view);
        this.conversations_entry_adapter = conversations_entry_adapter;
        this.my_conversation_dao = my_conversation_dao;

    }



    public LoadConversationTask(Context context, MyConversationsDao my_conversation_dao){

        context_weak_reference = new WeakReference<>(context);
        this.my_conversation_dao = my_conversation_dao;

    }

    public void set_request_manager(RequestManager glide_request_manager){
        this.glide_request_manager = glide_request_manager;
    }

    public void set_home_activity_foreground(boolean is_home_activity_foreground){

        this.is_home_activity_foreground = is_home_activity_foreground;
    }

    public void set_app(App app){

        this.app = app;
    }

    public void set_conversations_frame_layout(FrameLayout conversations_fragment_frame_layout){

        this.conversations_fragment_frame_layout_weak_reference = new WeakReference<>(conversations_fragment_frame_layout);
    }

    public void set_conversation_entry(ConversationEntry conversation_entry){
        this.conversation_entry = conversation_entry;
    }

    public void set_notification_builder(NotificationCompat.Builder notification_builder){

        this.notification_builder = notification_builder;
    }

    public void set_no_messages_textView(TextView no_messages_available_textView){

        this.no_messages_available_textView_weak_reference = new WeakReference<>(no_messages_available_textView);
    }

    public void set_user(Users user){

        this.user = user;
    }
    public void set_interface(UserInterface user_interface){

        this.user_interface = user_interface;

    }

    private void load_conversation(ArrayList<Message> messages){

            conversation_entry.messages = messages;

            Intent intent = new Intent(context_weak_reference.get(), MessageActivity.class);
            intent.putExtra("conversation_entry", conversation_entry);

            TaskStackBuilder task_stack_builder = TaskStackBuilder.create(context_weak_reference.get());
            task_stack_builder.addParentStack(MessageActivity.class);
            task_stack_builder.addNextIntent(intent);

        int NOTIFICATION_ID = 0;
        PendingIntent pending_intent = task_stack_builder.getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT);
        notification_builder.setContentIntent(pending_intent);

        NotificationManager notification_manager = (NotificationManager) this.context_weak_reference.get().getSystemService(Context.NOTIFICATION_SERVICE);
        notification_manager.notify(conversation_entry.conversation_id, NOTIFICATION_ID, notification_builder.build());

    }

    @SuppressWarnings("unused")
    private void load_notification_badges(ArrayList<Message> messages){

        final String MSG = "INFO";

        if(app.is_software_profile_dialog_foreground()){

            return;
        }

        if(app.is_conversation_fragment_foreground()) {

            if (!is_home_activity_foreground) {


                ((HomeActivity) context_weak_reference.get()).recreate();
            }
        }

    }

    private void is_new_conversation(ArrayList<Message> messages){

        if(messages.isEmpty()){

            Intent intent = new Intent(context_weak_reference.get(), MessageActivity.class);
            
            intent.putExtra("user", this.user);
            (context_weak_reference.get()).startActivity(intent);

        }
        else{

            conversation_entry.current_user.first_name = UserSettings.get_user_first_name(context_weak_reference.get());
            conversation_entry.current_user.id = UserSettings.get_user_id(context_weak_reference.get());
            conversation_entry.current_user.user_image_thumbnail_url = UserSettings.get_user_profile_image_thumbnail_url(context_weak_reference.get());
            conversation_entry.current_user.user_image_full_url = UserSettings.get_user_profile_image_full_url(context_weak_reference.get());
            conversation_entry.recipient_user.first_name = user.first_name;
            conversation_entry.recipient_user.id = user.id;
            conversation_entry.recipient_user.user_image_thumbnail_url = user.user_image_thumbnail_url;
            conversation_entry.recipient_user.user_image_full_url = user.user_image_full_url;
            conversation_entry.conversation_id = messages.get(0).conversation_id;
            conversation_entry.recent_message = messages.get((messages.size() - 1));
            conversation_entry.messages = messages;
            
            Intent intent = new Intent(context_weak_reference.get(), MessageActivity.class);
            intent.putExtra("conversation_entry", conversation_entry);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context_weak_reference.get().startActivity(intent);

        }

    }

    private void load_recent_messages(){

        Collections.reverse(messages_conversation_entries);

        conversations_entry_adapter = new ConversationsEntryAdapter(context_weak_reference.get(), glide_request_manager, messages_conversation_entries, conversations_entry_recycle_view_weak_reference.get());
        conversations_entry_adapter.set_conversation_entry(conversation_entry);
        conversations_entry_adapter.set_no_messages_textview(no_messages_available_textView_weak_reference);
        conversations_entry_adapter.set_conversation_fragment_frame_layout(conversations_fragment_frame_layout_weak_reference);

        LinearLayoutManager linear_layout_manager = new LinearLayoutManager(context_weak_reference.get());

        conversations_entry_recycle_view_weak_reference.get().setLayoutManager(linear_layout_manager);
        conversations_entry_recycle_view_weak_reference.get().setHasFixedSize(true);
        conversations_entry_recycle_view_weak_reference.get().setAdapter(conversations_entry_adapter);

        if(conversations_entry_adapter.getItemCount() == 0){

            no_messages_available_textView_weak_reference.get().setVisibility(View.VISIBLE);
            conversations_entry_recycle_view_weak_reference.get().setVisibility(View.GONE);
            conversations_fragment_frame_layout_weak_reference.get().setBackgroundColor(Color.GRAY);

        }
        else if(conversations_entry_adapter.getItemCount() > 0){

            no_messages_available_textView_weak_reference.get().setVisibility(View.GONE);
            conversations_entry_recycle_view_weak_reference.get().setVisibility(View.VISIBLE);
            conversations_fragment_frame_layout_weak_reference.get().setBackgroundColor(Color.WHITE);

        }


    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public void update_conversation_entry(Message message_entry){

        boolean is_conversation_id_present = false;
        int i;
        for( i = 0; i < messages_conversation_entries.size(); i++){

            if(messages_conversation_entries.get(i).conversation_id.equals(message_entry.conversation_id)){

                is_conversation_id_present = true;
                messages_conversation_entries.get(i).first_name = message_entry.first_name;
                messages_conversation_entries.get(i).message = message_entry.message;
                messages_conversation_entries.get(i).is_read = message_entry.is_read;
                messages_conversation_entries.get(i).time = message_entry.time;
                messages_conversation_entries.get(i).date = message_entry.date;
                messages_conversation_entries.get(i).profile_image_thumbnail_url = message_entry.profile_image_thumbnail_url;
                break;
            }

        }

        if(is_conversation_id_present) {
            
            Message temp_entry = messages_conversation_entries.get(i);
            final int from_position = i;
            final int to_position = 0;

            if(from_position != to_position) {
                
                messages_conversation_entries.remove(from_position);
                messages_conversation_entries.add(to_position, temp_entry);

                if(temp_entry.is_read)
                    conversations_entry_adapter.update_read_message(to_position);
                else
                    conversations_entry_adapter.update_unread_message_badge(to_position);

                conversations_entry_adapter.notifyItemMoved(from_position, to_position);
                conversations_entry_adapter.notifyItemChanged(to_position);

            }
            else{
                
                messages_conversation_entries.set(from_position, temp_entry);

                if(temp_entry.is_read)
                    conversations_entry_adapter.update_read_message(to_position);
                else
                    conversations_entry_adapter.update_unread_message_badge(to_position);

                conversations_entry_adapter.notifyItemChanged(from_position);
            }

        }
        else{

            final int to_position = 0;
            messages_conversation_entries.add(to_position, message_entry);

            if(message_entry.is_read)
                conversations_entry_adapter.update_read_message(to_position);
            else
                conversations_entry_adapter.update_unread_message_badge(to_position);


            conversations_entry_adapter.notifyItemInserted(to_position);
        }

        if(conversations_entry_adapter.getItemCount() == 0){

            no_messages_available_textView_weak_reference.get().setVisibility(View.VISIBLE);
            conversations_entry_recycle_view_weak_reference.get().setVisibility(View.GONE);
            conversations_fragment_frame_layout_weak_reference.get().setBackgroundColor(Color.GRAY);

        }
        else if(conversations_entry_adapter.getItemCount() > 0){

            no_messages_available_textView_weak_reference.get().setVisibility(View.GONE);
            conversations_entry_recycle_view_weak_reference.get().setVisibility(View.VISIBLE);
            conversations_fragment_frame_layout_weak_reference.get().setBackgroundColor(Color.WHITE);

        }
    }

    public void update_message_received(MessageRead message_read){

       for(int i = 0; i < messages_conversation_entries.size(); i++){

            if(messages_conversation_entries.get(i).conversation_id.equals(message_read.conversation_id)){

                    conversations_entry_adapter.update_read_message(i);
                    conversations_entry_adapter.notifyItemChanged(i);
                    break;
            }

       }

    }

    @SuppressWarnings("unused")
    public void update_conversation_adapter(Handler main_handler){

            conversations_entry_adapter.set_message_conversation_entries(messages_conversation_entries);
            conversations_entry_adapter.notifyDataSetChanged();

        }

    }

    @Override
    protected ArrayList<Message> doInBackground(Void... voids) {

        ArrayList<Message> messages = new ArrayList<>();

        if(this.user_interface == UserInterface.MESSAGE_ACTIVITY) {

            List<MyConversations> my_conversations = this.my_conversation_dao.query_conversations(this.conversation_entry.conversation_id);
            
            if(my_conversations.isEmpty()) {

                if (!(conversation_entry.current_user.id.equals(conversation_entry.recent_message.id)))
                    conversation_entry.recent_message.is_from_server = true;
                messages.add(conversation_entry.recent_message);
            }
            else {

                for (int i = 0; i < my_conversations.size(); i++) {

                    Message message = new Message();
                    message.first_name = my_conversations.get(i).get_first_name();
                    message.user_id = my_conversations.get(i).get_user_id();
                    message.id = my_conversations.get(i).get_message_id();
                    message.message = my_conversations.get(i).get_message();
                    message.time = my_conversations.get(i).get_time();
                    message.date = my_conversations.get(i).get_date();
                    message.is_read = my_conversations.get(i).get_is_read();
                    message.conversation_id = my_conversations.get(i).get_conversation_id();
                    message.profile_image_thumbnail_url = my_conversations.get(i).get_profile_image_thumbnail_url();
                    message.profile_image_full_url = my_conversations.get(i).get_profile_image_full_url();
                    message.recipient_user_id = my_conversations.get(i).get_recipient_id();
                    message.recipient_first_name = my_conversations.get(i).get_recipient_first_name();
                    message.recipient_profile_image_thumbnail_url = my_conversations.get(i).get_recipient_profile_image_thumbnail_url();
                    message.recipient_profile_image_full_url = my_conversations.get(i).get_recipient_profile_image_full_url();
                    message.message_delivered = my_conversations.get(i).get_message_delivered();


                    if (!(conversation_entry.current_user.id.equals(message.user_id)))
                        message.is_from_server = true;

                    messages.add(message);
                }
            }

        }
        else if(this.user_interface == UserInterface.CONVERSATIONS_FRAGMENT){

            if(!messages_conversation_entries.isEmpty())
                messages_conversation_entries.clear();

            List<MyConversations> my_conversations = this.my_conversation_dao.query_recent_messages();

            for(int i = 0; i < my_conversations.size(); i++){


                Message message_entry  = new Message();
                message_entry.conversation_id = my_conversations.get(i).get_conversation_id();
                message_entry.first_name = my_conversations.get(i).get_first_name();
                message_entry.user_id = my_conversations.get(i).get_user_id();
                message_entry.id = my_conversations.get(i).get_message_id();
                message_entry.message = my_conversations.get(i).get_message();
                message_entry.time = my_conversations.get(i).get_time();
                message_entry.date = my_conversations.get(i).get_date();
                message_entry.is_read = my_conversations.get(i).get_is_read();
                message_entry.profile_image_thumbnail_url = my_conversations.get(i).get_profile_image_thumbnail_url();
                message_entry.profile_image_full_url = my_conversations.get(i).get_profile_image_full_url();
                message_entry.recipient_user_id = my_conversations.get(i).get_recipient_id();
                message_entry.recipient_first_name = my_conversations.get(i).get_recipient_first_name();
                message_entry.recipient_profile_image_thumbnail_url = my_conversations.get(i).get_recipient_profile_image_thumbnail_url();
                message_entry.recipient_profile_image_full_url = my_conversations.get(i).get_recipient_profile_image_full_url();
                message_entry.message_delivered = my_conversations.get(i).get_message_delivered();

                if(conversation_entry.recent_message.message == null)
                    conversation_entry.recent_message.message = "Null";

                if (!(conversation_entry.current_user.id.equals(conversation_entry.recent_message.user_id))) {

                    conversation_entry.recent_message.is_from_server = true;
                }

                messages_conversation_entries.add(message_entry);
            }

        }
        else if(this.user_interface == UserInterface.HOME_ACTIVITY){

            List<MyConversations> my_conversations = this.my_conversation_dao.query_all_conversations();
            for(int i = 0; i < my_conversations.size(); i++){

                Message message = new Message();
                message.first_name = my_conversations.get(i).get_first_name();
                message.message = my_conversations.get(i).get_message();
                message.is_read = my_conversations.get(i).get_is_read();
                message.conversation_id = my_conversations.get(i).get_conversation_id();
                message.user_id = my_conversations.get(i).get_user_id();
                message.id = my_conversations.get(i).get_message_id();
                message.time = my_conversations.get(i).get_time();
                message.date = my_conversations.get(i).get_date();
                message.recipient_user_id = my_conversations.get(i).get_recipient_id();
                message.recipient_first_name = my_conversations.get(i).get_recipient_first_name();
                message.recipient_profile_image_thumbnail_url = my_conversations.get(i).get_recipient_profile_image_thumbnail_url();
                message.recipient_profile_image_full_url = my_conversations.get(i).get_recipient_profile_image_full_url();
                message.message_delivered = my_conversations.get(i).get_message_delivered();

                messages.add(message);
            }


        }
        else if(this.user_interface == UserInterface.HOME_USER_SOFTWARE_PROFILE_ACTIVITY){

            List<MyConversations> my_conversations_info = this.my_conversation_dao.query_conversations(conversation_entry.conversation_id);

            if(!(my_conversations_info.isEmpty())) {

                for (int i = 0; i < my_conversations_info.size(); i++) {

                    Message message = new Message();
                    message.first_name = my_conversations_info.get(i).get_first_name();
                    message.user_id = my_conversations_info.get(i).get_user_id();
                    message.message = my_conversations_info.get(i).get_message();
                    message.is_read = my_conversations_info.get(i).get_is_read();
                    message.conversation_id = my_conversations_info.get(i).get_conversation_id();
                    message.profile_image_thumbnail_url = my_conversations_info.get(i).get_profile_image_thumbnail_url();
                    message.profile_image_full_url = my_conversations_info.get(i).get_profile_image_full_url();
                    message.recipient_profile_image_thumbnail_url = my_conversations_info.get(i).get_recipient_profile_image_thumbnail_url();
                    message.recipient_profile_image_full_url = my_conversations_info.get(i).get_recipient_profile_image_full_url();
                    message.id = my_conversations_info.get(i).get_message_id();
                    message.time = my_conversations_info.get(i).get_time();
                    message.date = my_conversations_info.get(i).get_date();
                    message.message_delivered = my_conversations_info.get(i).get_message_delivered();

                    if (!(conversation_entry.current_user.id.equals(message.user_id)))
                        message.is_from_server = true;

                    messages.add(message);
                }
            }
        }

        return messages;
    }

    @Override
    protected void onPostExecute(ArrayList<Message> messages) {
        super.onPostExecute(messages);

        if(this.user_interface == UserInterface.MESSAGE_ACTIVITY) {
            load_conversation(messages);
        }

        if(this.user_interface == UserInterface.HOME_ACTIVITY){

            load_notification_badges(messages);
        }

        else if(this.user_interface == UserInterface.CONVERSATIONS_FRAGMENT){

            load_recent_messages();
        }


        if(this.user_interface == UserInterface.HOME_USER_SOFTWARE_PROFILE_ACTIVITY){

            is_new_conversation(messages);

        }

    }
}
