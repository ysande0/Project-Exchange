package com.syncadapters.czar.exchange.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class ConversationEntry implements Parcelable {


    public Users current_user = new Users();
    public Users recipient_user = new Users();
    public String transaction_id;
    public String conversation_id;
    public Message recent_message = new Message(); // <-- Object has time and date
    private String recent_user_image_url;
    public Software software = new Software();

    public ArrayList<Message> messages = new ArrayList<>();

    public ConversationEntry(){


    }


    private ConversationEntry(Parcel parcel){

        this.transaction_id = parcel.readString();
        this.conversation_id = parcel.readString();
        this.recent_user_image_url = parcel.readString();
        this.current_user = parcel.readParcelable(Users.class.getClassLoader());
        this.recipient_user = parcel.readParcelable(Users.class.getClassLoader());
        this.recent_message = parcel.readParcelable(Message.class.getClassLoader());
        this.software = parcel.readParcelable(Software.class.getClassLoader());
        parcel.readTypedList(this.messages, Message.CREATOR);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ConversationEntry> CREATOR = new Creator<ConversationEntry>(){

        @Override
        public ConversationEntry createFromParcel(Parcel source) {
            return new ConversationEntry(source);
        }

        @Override
        public ConversationEntry[] newArray(int size) {
            return new ConversationEntry[size];
        }
    };


    @Override
    public void writeToParcel(Parcel parcel, int flags){


        parcel.writeString(this.transaction_id);
        parcel.writeString(this.conversation_id);
        parcel.writeString(this.recent_user_image_url);
        parcel.writeParcelable(current_user, flags);
        parcel.writeParcelable(recipient_user, flags);
        parcel.writeParcelable(recent_message, flags);
        parcel.writeParcelable(software, flags);
        parcel.writeTypedList(this.messages);
    }


}
