package com.syncadapters.czar.exchange.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageReceived implements Parcelable {

    public String id;
    public String conversation_id;
    @SuppressWarnings("WeakerAccess")
    public String message;
    public boolean is_from_server;
    public boolean is_read;
    public int message_delivered;

    public MessageReceived(){


    }

    @SuppressWarnings("WeakerAccess")
    public MessageReceived(Parcel parcel){

        this.id = parcel.readString();
        this.conversation_id = parcel.readString();
        this.message = parcel.readString();
        this.is_from_server = parcel.readInt() == 1;
        this.is_read = parcel.readInt() == 1;
        this.message_delivered = parcel.readInt();
    }

    public static final Creator<MessageReceived> CREATOR = new Creator<MessageReceived>() {
        @Override
        public MessageReceived createFromParcel(Parcel source) {
            return new MessageReceived(source);
        }

        @Override
        public MessageReceived[] newArray(int size) {
            return new MessageReceived[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(this.id);
        parcel.writeString(this.conversation_id);
        parcel.writeString(this.message);
        parcel.writeInt(is_from_server ? 1 : 0);
        parcel.writeInt(is_read ? 1 : 0);
        parcel.writeInt(message_delivered);
    }

}
