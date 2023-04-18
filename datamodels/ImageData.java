package com.syncadapters.czar.exchange.datamodels;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

public class ImageData  implements Parcelable {

    private final Image upc_image;
    private final String string_data;

    public static final Creator<ImageData> CREATOR = new Creator<ImageData>() {
        @Override
        public ImageData createFromParcel(Parcel source) {
            return new ImageData(source);
        }

        @Override
        public ImageData[] newArray(int size) {
            return new ImageData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private ImageData(Parcel parcel){

        this.upc_image = parcel.readParcelable(getClass().getClassLoader());
        this.string_data = parcel.readString();
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeParcelable((Parcelable)upc_image, flags);
        parcel.writeString(this.string_data);

    }

}
