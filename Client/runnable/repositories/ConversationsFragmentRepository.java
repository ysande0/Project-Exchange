package com.syncadapters.czar.exchange.repositories;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.syncadapters.czar.exchange.adapters.ConversationsEntryAdapter;
import com.syncadapters.czar.exchange.asynctasks.LoadConversationTask;
import com.syncadapters.czar.exchange.datamodels.ConversationEntry;
import com.syncadapters.czar.exchange.datamodels.Message;
import com.syncadapters.czar.exchange.datamodels.MessageRead;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.roomdatabase.ExchangeDatabase;
import com.syncadapters.czar.exchange.roomdatabase.MyConversationsDao;

public class ConversationsFragmentRepository {

    private MyConversationsDao my_conversations_dao;
    private LoadConversationTask load_conversation_task;
    /*
       private static ConversationsFragmentRepository conversations_fragment_repository

           public static ConversationsFragmentRepository getInstance(){

               if(conversations_fragment_repository == null)
                   conversations_fragment_repository = new ConversationsFragmentRepository();

               return conversations_fragment_repository;
           }
   */
    public void initialize_database(Application application){

        ExchangeDatabase exchange_database = ExchangeDatabase.get_database(application);
        my_conversations_dao = exchange_database.my_conversations_dao();
    }

    public void load_recent_messages(Context context, RequestManager glide_request_manager, FrameLayout conversations_fragment_frame_layout, RecyclerView conversations_entry_recycle_view, TextView no_messages_available_textView, ConversationsEntryAdapter conversations_entry_adapter,
                                     UserInterface user_interface, ConversationEntry conversation_entry){


        load_conversation_task = new LoadConversationTask(context, conversations_entry_recycle_view, conversations_entry_adapter,
                this.my_conversations_dao);
        load_conversation_task.set_interface(user_interface);
        load_conversation_task.set_request_manager(glide_request_manager);
        load_conversation_task.set_conversation_entry(conversation_entry);
       // load_conversation_task.set_fragment(conversations_fragment);
        load_conversation_task.set_conversations_frame_layout(conversations_fragment_frame_layout);
        load_conversation_task.set_no_messages_textView(no_messages_available_textView);
        load_conversation_task.execute();

    }

    public void cancel_load_recent_message(){

            if(load_conversation_task != null) {
                String TAG = "OUT";
                Log.d(TAG, "[ConversationsFragmentRepository] canceling load_conversation_task");
                load_conversation_task.cancel(true);
            }

    }

    public void update_conversation_entry(Message message){

        load_conversation_task.update_conversation_entry(message);

    }

    public void update_message_received(MessageRead message_read){

        load_conversation_task.update_message_received(message_read);
    }

    public void update_conversation_adapter(Handler main_handler){


        load_conversation_task.update_conversation_adapter(main_handler);
    }

}
