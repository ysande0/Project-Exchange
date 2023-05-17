package com.syncadapters.czar.exchange.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.syncadapters.czar.exchange.datautility.FORMATS;

import java.util.Calendar;
import java.util.Locale;

public class Message implements Parcelable {

    public String first_name;
    public String user_id;
    public String id;
    public String message;
    public String time;
    public String date;
    public String conversation_id;
    public String transaction_id;
    public String profile_image_full_url;
    public String profile_image_thumbnail_url;
    public String recipient_user_id;
    public String recipient_first_name;
    public String recipient_profile_image_full_url;
    public String recipient_profile_image_thumbnail_url;
    public boolean is_from_server;
    public boolean is_read;
    public int message_delivered;

    public Message(String first_name, String user_id, String id, String message){

        this.first_name = first_name;
        this.user_id = user_id;
        this.message = message;
        this.id = id;
        this.time = String.format(Locale.ENGLISH, FORMATS.time, Calendar.getInstance().getTime());
        this.date = String.format(Locale.ENGLISH, FORMATS.date, Calendar.getInstance().getTime());
        this.is_from_server = false;
        this.is_read = false;
        this.message_delivered = 0;

    }

    public Message(){

        this.time = String.format(Locale.ENGLISH, FORMATS.time, Calendar.getInstance().getTime());
        this.date = String.format(Locale.ENGLISH, FORMATS.date, Calendar.getInstance().getTime());
    }


    public Message(Parcel parcel){

        this.first_name = parcel.readString();
        this.user_id = parcel.readString();
        this.id = parcel.readString();
        this.message = parcel.readString();
        this.time = parcel.readString();
        this.date = parcel.readString();
        this.conversation_id = parcel.readString();
        this.transaction_id = parcel.readString();
        this.profile_image_full_url = parcel.readString();
        this.profile_image_thumbnail_url = parcel.readString();
        this.recipient_user_id = parcel.readString();
        this.recipient_first_name = parcel.readString();
        this.recipient_profile_image_full_url = parcel.readString();
        this.recipient_profile_image_thumbnail_url = parcel.readString();
        this.is_from_server = parcel.readInt() == 1;
        this.is_read = parcel.readInt() == 1;
        this.message_delivered = parcel.readInt();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(this.first_name);
        parcel.writeString(this.user_id);
        parcel.writeString(this.id);
        parcel.writeString(this.message);
        parcel.writeString(this.time);
        parcel.writeString(this.date);
        parcel.writeString(this.conversation_id);
        parcel.writeString(this.transaction_id);
        parcel.writeString(this.profile_image_full_url);
        parcel.writeString(this.profile_image_thumbnail_url);
        parcel.writeString(this.recipient_user_id);
        parcel.writeString(this.recipient_first_name);
        parcel.writeString(this.recipient_profile_image_full_url);
        parcel.writeString(this.recipient_profile_image_thumbnail_url);
        parcel.writeInt(is_from_server ? 1 : 0);
        parcel.writeInt(is_read ? 1 : 0);
        parcel.writeInt(message_delivered);
    }
}
