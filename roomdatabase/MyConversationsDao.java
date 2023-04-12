package com.syncadapters.czar.exchange.roomdatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@SuppressWarnings("unused")
@Dao
public interface MyConversationsDao {

    @Insert
    void insert(MyConversations my_conversations);

    @Query("DELETE FROM myconversations WHERE conversation_id = :conversation_id")
    void delete_title(String conversation_id);

    @Query("SELECT * FROM myconversations WHERE conversation_id = :conversation_id")
    List<MyConversations> query_conversations(String conversation_id);

    @Query("SELECT * FROM myconversations WHERE recipient_user_id = :user_id")
    List<MyConversations> query_user_id(String user_id);


    @Query("SELECT * FROM myconversations")
    List<MyConversations> query_all_conversations();

    @Query("UPDATE myconversations SET is_read = :is_read WHERE conversation_id = :conversation_id")
    void update_is_read(String conversation_id, boolean is_read);

    @Query("UPDATE myconversations SET message_delivered = :message_delivered_status WHERE conversation_id = :conversation_id AND message_id = :message_id")
    void update_message_delivered(String conversation_id, String message_id, int message_delivered_status);

    @Query("SELECT * FROM myconversations WHERE id IN "  +
            "(SELECT MAX(id) FROM myconversations GROUP BY conversation_id )")
    List<MyConversations> query_recent_messages();

}
