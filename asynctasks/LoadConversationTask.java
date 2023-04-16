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
    //private WeakReference<BottomNavigationView> bottom_navigation_weak_reference;
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
        Log.d(TAG, "[LoadConversation Task] 2) conversation_entry current user id:  " + conversation_entry.current_user.id);
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

       /*
        message_recycle_view_adapter = new MessageRecycleViewAdapter(this.context, messages, UserSettings.get_user_id(this.context), conversation_entry);
        LinearLayoutManager linear_layout_manager = new LinearLayoutManager(this.context);
        linear_layout_manager.setStackFromEnd(true);
        message_recycleView.setLayoutManager(linear_layout_manager);
        message_recycleView.setHasFixedSize(true);
        message_recycleView.setAdapter(message_recycle_view_adapter);
        message_recycle_view_adapter.set_messages(messages);
        message_recycle_view_adapter.set_conversation_entry(conversation_entry);
        */
            conversation_entry.messages = messages;

            Intent intent = new Intent(context_weak_reference.get(), MessageActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("conversation_entry", conversation_entry);

            TaskStackBuilder task_stack_builder = TaskStackBuilder.create(context_weak_reference.get());
            task_stack_builder.addParentStack(MessageActivity.class);
            task_stack_builder.addNextIntent(intent);

            int NOTIFICATION_ID = 0;
          //  PendingIntent pending_intent = PendingIntent.getActivity(context_weak_reference.get(), NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pending_intent = task_stack_builder.getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT);
        notification_builder.setContentIntent(pending_intent);

        NotificationManager notification_manager = (NotificationManager) this.context_weak_reference.get().getSystemService(Context.NOTIFICATION_SERVICE);
        notification_manager.notify(conversation_entry.conversation_id, NOTIFICATION_ID, notification_builder.build());
        //notification_manager.notify(NOTIFICATION_ID, notification_builder.build());

    }

    @SuppressWarnings("unused")
    private void load_notification_badges(ArrayList<Message> messages){

        final String MSG = "INFO";

        if(app.is_software_profile_dialog_foreground()){

            Log.d(MSG, "SoftwareProfileDialog in foreground");
            return;
        }

        Log.d(MSG, "Conversations Fragment is in foreground: " + app.is_conversation_fragment_foreground());
        if(app.is_conversation_fragment_foreground()) {

            if (!is_home_activity_foreground) {


                ((HomeActivity) context_weak_reference.get()).recreate();
                Log.d(MSG, "Conversation Fragment in foreground. Recreating...");
                //is_home_activity_foreground = true;
            }
        }
        else
            Log.d(MSG, "Conversation Fragment NOT in foreground.");

/*
        BottomNavigationMenuView bottom_navigation_menu_view = (BottomNavigationMenuView) bottom_navigation_weak_reference.get().getChildAt(0);
        BottomNavigationItemView bottom_navigation_item_view = (BottomNavigationItemView) bottom_navigation_menu_view.getChildAt(0);

        View navigation_notification_badge = LayoutInflater.from(context_weak_reference.get()).inflate(R.layout.home_navigation_notification_badge, bottom_navigation_menu_view, false);

        TextView number_of_notifications = navigation_notification_badge.findViewById(R.id.message_notification_badge);

        //Toast.makeText(context_weak_reference.get(), "[HomeActivity] Message Size: " + messages.size(), Toast.LENGTH_LONG).show();


        int not_read_counter = 0;
        for(int i = 0; i < messages.size(); i++){

            Log.d(MSG, "Message: " + messages.get(i).message + "  is_read: " + messages.get(i).is_read);
            if(!messages.get(i).is_read)
                not_read_counter++;


        }

        Log.d(MSG, "Number  messages: " + messages.size());
        Log.d(MSG, "Number of unread messages: " + not_read_counter);

        if(not_read_counter > 9) {
            Log.d(MSG, "Exceeded notification threshold");
            number_of_notifications.setText("9+");
            number_of_notifications.setPadding(context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.new_padding_left),
                    context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.new_padding_top),
                    context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.new_padding_right),
                    context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.new_padding_bottom));
        }
        else {
            Log.d(MSG, "Under notification threshold");
            number_of_notifications.setText(String.valueOf(not_read_counter));

            number_of_notifications.setPadding(context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.default_padding_left),
                    context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.default_padding_top),
                    context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.default_padding_right),
                    context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.default_padding_bottom));
        }

        if(not_read_counter == 0) {
            // navigation_notification_badge.setVisibility(View.GONE);
            number_of_notifications.setVisibility(View.GONE);
            Log.d(MSG, "No Messages available. Notification Badge is Gone");

        }

        bottom_navigation_item_view.addView(navigation_notification_badge);
        */

        //((HomeActivity) context_weak_reference.get()).runOnUiThread(update_notification_badge(messages));

    }
/*
    private Runnable update_notification_badge(ArrayList<Message> messages){

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                BottomNavigationView bottom_navigation_view = bottom_navigation_weak_reference.get().findViewById(R.id.bottom_navigation_id);
                BottomNavigationMenuView bottom_navigation_menu_view = (BottomNavigationMenuView) bottom_navigation_view.getChildAt(0);
                BottomNavigationItemView bottom_navigation_item_view = (BottomNavigationItemView) bottom_navigation_menu_view.getChildAt(0);

                View navigation_notification_badge = LayoutInflater.from(context_weak_reference.get()).inflate(R.layout.home_navigation_notification_badge, bottom_navigation_menu_view, false);

                TextView number_of_notifications = navigation_notification_badge.findViewById(R.id.message_notification_badge);

                //Toast.makeText(context_weak_reference.get(), "[HomeActivity] Message Size: " + messages.size(), Toast.LENGTH_LONG).show();

                final String MSG = "INFO";

                int not_read_counter = 0;
                for(int i = 0; i < messages.size(); i++){

                    Log.d(MSG, "Message: " + messages.get(i).message + "  is_read: " + messages.get(i).is_read);
                    if(!messages.get(i).is_read)
                        not_read_counter++;


                }
                Toast.makeText(context_weak_reference.get(), "[HomeActivity] Unread Messages: " + not_read_counter, Toast.LENGTH_LONG).show();
                Log.d(MSG, "Number  messages: " + messages.size());
                Log.d(MSG, "Number of unread messages: " + not_read_counter);

                if(not_read_counter > 9) {
                    Log.d(MSG, "Exceeded notification threshold");
                    Toast.makeText(context_weak_reference.get(), "[HomeActivity] Exceeded notification threshold", Toast.LENGTH_LONG).show();
                    number_of_notifications.setText("9+");
                    number_of_notifications.setPadding(context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.new_padding_left),
                            context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.new_padding_top),
                            context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.new_padding_right),
                            context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.new_padding_bottom));
                }
                else {
                    Log.d(MSG, "Under notification threshold");
                    Toast.makeText(context_weak_reference.get(), "[HomeActivity] Under notification threshold", Toast.LENGTH_LONG).show();
                    number_of_notifications.setText(String.valueOf(not_read_counter));

                    number_of_notifications.setPadding(context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.default_padding_left),
                            context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.default_padding_top),
                            context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.default_padding_right),
                            context_weak_reference.get().getResources().getDimensionPixelSize(R.dimen.default_padding_bottom));
                }

                if(not_read_counter == 0) {
                    // navigation_notification_badge.setVisibility(View.GONE);
                  //  Toast.makeText(context_weak_reference.get(), "[HomeActivity] No Messages Badge " + not_read_counter, Toast.LENGTH_LONG).show();

                    number_of_notifications.setText("");
                    number_of_notifications.setVisibility(View.GONE);
                    Log.d(MSG, "No Messages available. Notification Badge is Gone");
                }
                else
                    Toast.makeText(context_weak_reference.get(), "[HomeActivity] Messages Badge  " + not_read_counter, Toast.LENGTH_LONG).show();

                bottom_navigation_item_view.addView(navigation_notification_badge);


            }
        };

        return runnable;
    }
*/
    private void is_new_conversation(ArrayList<Message> messages){

        if(messages.isEmpty()){
            // New User Conversation

            Log.d(TAG, "[HomeUserProfileActivity] Conversation is new");
            Intent intent = new Intent(context_weak_reference.get(), MessageActivity.class);

            Log.d(TAG, "[HomeUserProfileActivity] user id: " + user.id + " distance: " + user.user_distance + " first name: " + user.first_name);
            intent.putExtra("user", this.user);
            (context_weak_reference.get()).startActivity(intent);

        }
        else{
            // Same User conversation

            Log.d(TAG, "[HomeUserProfileActivity] Conversation is not new");

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

            Log.d(TAG, "[HomeUserProfileActivity] Recipient User ID: " + conversation_entry.recipient_user.id);
            Log.d(TAG, "[HomeUserProfileActivity] Conversation ID : " + conversation_entry.conversation_id);
            //Toast.makeText(context, "[HomeUserProfileActivity] Number of messages: " + conversation_entry.messages.size(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context_weak_reference.get(), MessageActivity.class);
            intent.putExtra("conversation_entry", conversation_entry);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context_weak_reference.get().startActivity(intent);

        }

    }

    private void load_recent_messages(){

        Collections.reverse(messages_conversation_entries);

        Log.d(TAG, "Number of conversations: " + messages_conversation_entries.size());

        for(int i = 0; i < messages_conversation_entries.size(); i++){

            Log.d(TAG, "[LoadConversationTask] conversations id: " + messages_conversation_entries.get(i).conversation_id + " |  message: " + messages_conversation_entries.get(i).message + " |  is read: " + messages_conversation_entries.get(i).is_read + " | index: " + i);

        }

        conversations_entry_adapter = new ConversationsEntryAdapter(context_weak_reference.get(), glide_request_manager, messages_conversation_entries, conversations_entry_recycle_view_weak_reference.get());
        conversations_entry_adapter.set_conversation_entry(conversation_entry);

       // conversations_entry_adapter.set_conversation_recycle_view(conversations_entry_recycle_view_weak_reference);
        conversations_entry_adapter.set_no_messages_textview(no_messages_available_textView_weak_reference);
        conversations_entry_adapter.set_conversation_fragment_frame_layout(conversations_fragment_frame_layout_weak_reference);
       // conversations_entry_adapter.set_conversation_fragment(conversation_fragment_weak_reference);

        LinearLayoutManager linear_layout_manager = new LinearLayoutManager(context_weak_reference.get());
        // linear_layout_manager.setStackFromEnd(true);
        // linear_layout_manager.setReverseLayout(true);

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

        Log.d(TAG, "ConversationsFragment: onEvent(ConversationEntry)");
        Log.d(TAG, "ConversationsFragment: is_read: " + message_entry.is_read + "  first_name: " + message_entry.first_name  +  "  message: " + message_entry.message );
        //cancel_notifications();
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

            // This is not a new conversation entry
            Message temp_entry = messages_conversation_entries.get(i);

            Log.d(TAG, "[LoadConversationTask] present first name: " + temp_entry.first_name + " |  is_read " + temp_entry.is_read);
            final int from_position = i;
            final int to_position = 0;

            if(from_position != to_position) {

                // Move conversation entry
                messages_conversation_entries.remove(from_position);
                messages_conversation_entries.add(to_position, temp_entry);

                if(temp_entry.is_read)
                    conversations_entry_adapter.update_read_message(to_position);
                else
                    conversations_entry_adapter.update_unread_message_badge(to_position);

                conversations_entry_adapter.notifyItemMoved(from_position, to_position);
                conversations_entry_adapter.notifyItemChanged(to_position);
                Log.d(TAG, "[Message][MOVED] from: (" + from_position + ") " +  messages_conversation_entries.get(from_position).message + "  |  to: (" + to_position + ") " + messages_conversation_entries.get(to_position).message);

            }
            else{

                // Update conversation entry
                Log.d(TAG, "from_position == to_position");
                messages_conversation_entries.set(from_position, temp_entry);

                if(temp_entry.is_read)
                    conversations_entry_adapter.update_read_message(to_position);
                else
                    conversations_entry_adapter.update_unread_message_badge(to_position);

                conversations_entry_adapter.notifyItemChanged(from_position);
            }

            Log.d(TAG, "[Message][Entry Present] First Element: " + messages_conversation_entries.get(0).message + "  Size: " + messages_conversation_entries.size() + " Moved from " + from_position + " to " + to_position);
        }
        else{
            // This is a new entry
            final int to_position = 0;
            messages_conversation_entries.add(to_position, message_entry);

            if(message_entry.is_read)
                conversations_entry_adapter.update_read_message(to_position);
            else
                conversations_entry_adapter.update_unread_message_badge(to_position);


            conversations_entry_adapter.notifyItemInserted(to_position);
            Log.d(TAG, "[Message][Entry Not Present] First Element: " + messages_conversation_entries.get(0).message + "  Size: " + messages_conversation_entries.size());
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

        Log.d(TAG, "ConversationsFragment: onEvent(ConversationEntry)[Ending]");
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


        if(conversations_entry_adapter != null) {
            Log.d(TAG, "ConversationsFragment: updating conversation adapter");
/*
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (!messages_conversation_entries.isEmpty())
                        messages_conversation_entries.clear();

                    List<MyConversations> my_conversations = my_conversation_dao.query_recent_messages();

                    for (int i = 0; i < my_conversations.size(); i++) {


                        Message message_entry = new Message();
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
                        Log.d(TAG, "[LoadConversation Task] Recipient Profile Image Full URL: " + message_entry.recipient_profile_image_full_url);
                        message_entry.message_delivered = my_conversations.get(i).get_message_delivered();

                        Log.d(TAG, "ConversationFragment Recent Message: " + message_entry.message);
                        if (conversation_entry.recent_message.message == null)
                            conversation_entry.recent_message.message = "Null";

                        if (!(conversation_entry.current_user.id.equals(conversation_entry.recent_message.user_id))) {

                            conversation_entry.recent_message.is_from_server = true;
                        }

                        messages_conversation_entries.add(message_entry);

                        Collections.reverse(messages_conversation_entries);

                        // handler
                        main_handler.post(new Runnable() {
                            @Override
                            public void run() {


                                conversations_entry_adapter.set_message_conversation_entries(messages_conversation_entries);
                                conversations_entry_adapter.notifyDataSetChanged();
                            }
                        });

                    }
                }
            }).start();
*/
/*
            for(int i = 0; i < messages_conversation_entries.size(); i++)
                messages_conversation_entries.get(i).is_read = true;
*/
            conversations_entry_adapter.set_message_conversation_entries(messages_conversation_entries);
            conversations_entry_adapter.notifyDataSetChanged();

            Log.d(TAG, "ConversationsFragment: conversations_entry_adapter is NOT null");

        }
        else{

            Log.d(TAG, "ConversationsFragment: conversations_entry_adapter is null");
        }
    }

/*
    private void cancel_notifications(){

       // Toast.makeText(context_weak_reference.get(), "load conversation task: cancel all notifications", Toast.LENGTH_LONG).show();
        NotificationManager notification_manager = (NotificationManager) context_weak_reference.get().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notification_manager.cancelAll();
    }
*/
    @Override
    protected ArrayList<Message> doInBackground(Void... voids) {

        ArrayList<Message> messages = new ArrayList<>();

        if(this.user_interface == UserInterface.MESSAGE_ACTIVITY) {

            // Mainly called from notification card. Upon clicked, launches MessageActivity

            List<MyConversations> my_conversations = this.my_conversation_dao.query_conversations(this.conversation_entry.conversation_id);
            Log.d(TAG, "[LoadConversation Task] 3) conversation_entry current user id:  " + conversation_entry.current_user.id);


            // New Conversation
            if(my_conversations.isEmpty()) {

                Log.d(TAG, "[LoadConversation Task] New Conversation " + conversation_entry.conversation_id);
               // this.my_conversation_dao.update_is_read(conversation_entry.conversation_id, true);
                if (!(conversation_entry.current_user.id.equals(conversation_entry.recent_message.id)))
                    conversation_entry.recent_message.is_from_server = true;
                messages.add(conversation_entry.recent_message);
            }
            else {

                Log.d(TAG, "[LoadConversation Task] Old Conversation " + conversation_entry.conversation_id);
                //this.my_conversation_dao.update_is_read(conversation_entry.conversation_id, true);
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
                    Log.d(TAG, "[LoadConversation Task] Recipient Profile Image Full URL: " + message.recipient_profile_image_full_url);
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
                Log.d(TAG, "[LoadConversation Task] Recipient Profile Image Full URL: " + message_entry.recipient_profile_image_full_url);
                message_entry.message_delivered = my_conversations.get(i).get_message_delivered();

                Log.d(TAG, "ConversationFragment Recent Message: " + message_entry.message);
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

            Log.d(TAG, "Recipient ID: " + user.id);
            Log.d(TAG, "Current Conversation ID: " + conversation_entry.conversation_id);
           // List<MyConversations> my_conversations_info = this.my_conversation_dao.query_user_id(user.id);
            List<MyConversations> my_conversations_info = this.my_conversation_dao.query_conversations(conversation_entry.conversation_id);

            if(my_conversations_info.isEmpty())
                Log.d(TAG, "My Conversations Info is empty");

            if(!(my_conversations_info.isEmpty())) {

                Log.d(TAG, "INFO Conversation ID: " + my_conversations_info.get(0).get_conversation_id());
               // List<MyConversations> my_conversations = this.my_conversation_dao.query_conversations(my_conversations_info.get(0).get_conversation_id());
               // Log.d(TAG, "--> Conversations ID: " + my_conversations.get(0).get_conversation_id());
               // this.my_conversation_dao.update_is_read(my_conversations.get(0).get_conversation_id(), true);

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

            Log.d(TAG, "USER INTERFACE: MessageActivity");
            load_conversation(messages);
        }

        if(this.user_interface == UserInterface.HOME_ACTIVITY){

            Log.d(TAG, "USER INTERFACE: HomeActivity");
            load_notification_badges(messages);
        }

        else if(this.user_interface == UserInterface.CONVERSATIONS_FRAGMENT){

            Log.d(TAG, "USER INTERFACE: ConversationsFragment");
            load_recent_messages();
        }


        if(this.user_interface == UserInterface.HOME_USER_SOFTWARE_PROFILE_ACTIVITY){

            Log.d(TAG, "USER INTERFACE: HomeUserSoftwareProfileActivity");
            is_new_conversation(messages);

        }

    }
}
