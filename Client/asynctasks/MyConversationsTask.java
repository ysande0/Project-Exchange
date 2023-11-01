package com.syncadapters.czar.exchange.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.roomdatabase.MyConversations;
import com.syncadapters.czar.exchange.roomdatabase.MyConversationsDao;

public class MyConversationsTask extends AsyncTask<MyConversations, Void, Void> {

    private static final String TAG = "MSG";
    private MyConversations my_conversations;
    private final MyConversationsDao my_conversation_dao;
    private final DatabaseOperations database_operations;
    private String conversation_id;
    private String message_id;
    private int message_delivered;
    private int update_type;

    public MyConversationsTask(MyConversations my_conversations, MyConversationsDao my_conversations_dao,
                               DatabaseOperations database_operations){

        this.my_conversations = my_conversations;
        this.my_conversation_dao = my_conversations_dao;
        this.database_operations = database_operations;

    }


    public MyConversationsTask(String conversation_id, MyConversationsDao my_conversations_dao,
                               DatabaseOperations database_operations){

        this.my_conversation_dao = my_conversations_dao;
        this.database_operations = database_operations;
        this.conversation_id = conversation_id;

    }

    public void set_update_type(int update_type){

        this.update_type = update_type;
    }

    public void set_message_id(String message_id){

        this.message_id = message_id;
    }

    public void set_message_delivered(int message_delivered_status){

        this.message_delivered = message_delivered_status;
    }

    @Override
    protected Void doInBackground(MyConversations... myConversations) {

        if(this.database_operations == DatabaseOperations.INSERT){
            this.my_conversation_dao.insert(this.my_conversations);
        }
        else if(this.database_operations == DatabaseOperations.DELETE){

            this.my_conversation_dao.delete_title(this.conversation_id);

        }
        else if(this.database_operations == DatabaseOperations.UPDATE){

            if(update_type == 1) {

                this.my_conversation_dao.update_is_read(conversation_id, true);

            }
            else if(update_type == 2){
                
                this.my_conversation_dao.update_message_delivered(conversation_id, message_id, this.message_delivered);

            }

        }
        
        return null;
    }


}
