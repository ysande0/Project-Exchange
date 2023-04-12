package com.syncadapters.czar.exchange.roomdatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;


@SuppressWarnings("unused")
@Entity(tableName = "myconversations")
public class MyConversations {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    protected long id;

    @NonNull
    @ColumnInfo(name = "first_name")
    protected final String first_name;

    @NonNull
    @ColumnInfo(name = "user_id")
    protected final String user_id;

    @NonNull
    @ColumnInfo(name = "message_id")
    protected final String message_id;

    @NonNull
    @ColumnInfo(name = "message")
    protected final String message;

    @NonNull
    @ColumnInfo(name = "conversation_id")
    protected final String conversation_id;

    @NonNull
    @ColumnInfo(name = "time")
    protected final String time;

    @NonNull
    @ColumnInfo(name = "date")
    protected final String date;

    @NonNull
    @ColumnInfo(name = "profile_image_thumbnail_url")
    protected final String profile_image_thumbnail_url;

    @NonNull
    @ColumnInfo(name = "profile_image_full_url")
    protected final String profile_image_full_url;

    @NonNull
    @ColumnInfo(name = "recipient_first_name")
    protected final String recipient_first_name;

    @NonNull
    @ColumnInfo(name = "recipient_user_id")
    protected final String recipient_user_id;

    @NonNull
    @ColumnInfo(name = "recipient_profile_image_thumbnail_url")
    protected final String recipient_profile_image_thumbnail_url;

    @NonNull
    @ColumnInfo(name = "recipient_profile_image_full_url")
    protected final String recipient_profile_image_full_url;

    @ColumnInfo(name = "is_read")
    protected final boolean is_read;

    @ColumnInfo(name = "message_delivered")
    protected final int message_delivered;

    public MyConversations(@NotNull String first_name, @NotNull String user_id, @NotNull String message_id, @NotNull String message,
                           @NotNull String conversation_id, @NotNull String time, @NotNull String date, @NotNull String profile_image_thumbnail_url, @NotNull String profile_image_full_url, @NotNull String recipient_user_id,
                           @NotNull String recipient_first_name, @NotNull String recipient_profile_image_thumbnail_url, @NotNull String recipient_profile_image_full_url,
                           boolean is_read, int message_delivered){

        this.first_name = first_name;
        this.message_id = message_id;
        this.user_id = user_id;
        this.message = message;
        this.conversation_id = conversation_id;
        this.time = time;
        this.date = date;
        this.profile_image_thumbnail_url = profile_image_thumbnail_url;
        this.profile_image_full_url = profile_image_full_url;
        this.recipient_user_id = recipient_user_id;
        this.recipient_first_name = recipient_first_name;
        this.recipient_profile_image_thumbnail_url = recipient_profile_image_thumbnail_url;
        this.recipient_profile_image_full_url = recipient_profile_image_full_url;
        this.is_read = is_read;
        this.message_delivered = message_delivered;
    }

// --Commented out by Inspection START (1/9/2021 11:52 PM):
//    public long get_id(){
//
//        return this.id;
//    }
// --Commented out by Inspection STOP (1/9/2021 11:52 PM)

    public String get_first_name(){
        return this.first_name;
    }

    public String get_user_id(){
        return this.user_id;
    }

    public String get_message_id(){
        return this.message_id;
    }


    public String get_profile_image_thumbnail_url(){
        return this.profile_image_thumbnail_url;
    }

    public String get_profile_image_full_url(){
        return this.profile_image_full_url;
    }

    public String get_message(){
        return this.message;
    }

    public String get_conversation_id(){
        return this.conversation_id;
    }

    public String get_time(){
        return this.time;
    }

    public String get_date(){
        return this.date;
    }

    public String get_recipient_id(){
        return this.recipient_user_id;
    }

    public String get_recipient_first_name(){
        return this.recipient_first_name;
    }

    public String get_recipient_profile_image_thumbnail_url(){
        return this.recipient_profile_image_thumbnail_url;
    }

    public String get_recipient_profile_image_full_url(){
        return this.recipient_profile_image_full_url;
    }

    public boolean get_is_read(){
        return is_read;
    }

    public int get_message_delivered(){
        return message_delivered;
    }
}
