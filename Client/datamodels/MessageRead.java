package com.syncadapters.czar.exchange.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageRead implements Parcelable {

    public String conversation_id;
    public boolean is_read;

    public MessageRead(){


    }

    @SuppressWarnings("WeakerAccess")
    public MessageRead(Parcel parcel){

        this.conversation_id = parcel.readString();
        this.is_read = parcel.readInt() == 1;

    }

    public static final Creator<MessageRead> CREATOR = new Creator<MessageRead>() {
        @Override
        public MessageRead createFromParcel(Parcel source) {
            return new MessageRead(source);
        }

        @Override
        public MessageRead[] newArray(int size) {
            return new MessageRead[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {


        parcel.writeString(this.conversation_id);
        parcel.writeInt(is_read ? 1 : 0);
    }

}
