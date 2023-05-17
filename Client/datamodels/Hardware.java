package com.syncadapters.czar.exchange.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

public class Hardware implements Parcelable {

    public String manufacturer;
    public String platform;


    public Hardware(String manufacturer, String platform){

        this.manufacturer = manufacturer;
        this.platform = platform;

    }

    public Hardware(){

    }

    private Hardware(Parcel parcel){

        this.platform = parcel.readString();
        this.manufacturer = parcel.readString();
    }

    public static final Creator<Hardware> CREATOR = new Creator<Hardware>() {
        @Override
        public Hardware createFromParcel(Parcel source) {
            return new Hardware(source);
        }

        @Override
        public Hardware[] newArray(int size) {
            return new Hardware[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(this.platform);
        parcel.writeString(this.manufacturer);

    }

}
