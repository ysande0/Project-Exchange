package com.syncadapters.czar.exchange.viewmodels;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.syncadapters.czar.exchange.adapters.ConversationsEntryAdapter;
import com.syncadapters.czar.exchange.datamodels.ConversationEntry;
import com.syncadapters.czar.exchange.datamodels.Message;
import com.syncadapters.czar.exchange.datamodels.MessageRead;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.repositories.ConversationsFragmentRepository;

public class ConversationsFragmentViewModel extends AndroidViewModel {

    private final ConversationsFragmentRepository conversations_fragment_repository;

    public ConversationsFragmentViewModel(Application application){
        super(application);

        conversations_fragment_repository = new ConversationsFragmentRepository();

        conversations_fragment_repository.initialize_database(application);
    }

    public void load_recent_messages(Context context, RequestManager glide_request_manager, FrameLayout conversations_fragment_frame_layout, RecyclerView conversations_entry_recycle_view, TextView no_messages_available_textView, ConversationsEntryAdapter conversations_entry_adapter,
                                     UserInterface user_interface, ConversationEntry conversation_entry){

        conversations_fragment_repository.load_recent_messages(context, glide_request_manager, conversations_fragment_frame_layout , conversations_entry_recycle_view, no_messages_available_textView, conversations_entry_adapter, user_interface, conversation_entry);

    }

    public void update_conversation_entry(Message message){

        conversations_fragment_repository.update_conversation_entry(message);

    }

    public void update_message_received(MessageRead message_read){

        conversations_fragment_repository.update_message_received(message_read);
    }

    public void update_conversation_adapter(Handler main_handler){

        conversations_fragment_repository.update_conversation_adapter(main_handler);
    }

    public void cancel_load_recent_message(){

        conversations_fragment_repository.cancel_load_recent_message();
    }

}
