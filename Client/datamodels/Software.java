package com.syncadapters.czar.exchange.datamodels;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class Software implements Parcelable {

    public long id;// local DB id
    public String uid;
    public String title;
    public String game_publisher;
    public String game_developer;
    public String platform;
    public String user_description;
    public String upc;
    public String software_image_full_url;
    public String software_image_thumbnail_url;
    private String bitmap_local_file_path;
    public String encoded_bitmap_thumbnail;
    public String encoded_bitmap_full;
    public Bitmap software_bitmap;
    public String bitmap_name_thumbnail;
    public String bitmap_name_full;
    public Uri software_bitmap_uri;

    public Software(){


    }

    public Software(String title, String game_publisher, String game_developer, String platform, String upc, String user_description){

        this.title = title;
        this.game_publisher = game_publisher;
        this.game_developer = game_developer;
        this.platform = platform;
        this.upc = upc;
        this.user_description = user_description;

    }

    @SuppressWarnings("unused")
    public Software(String title, String game_publisher, String game_developer, ArrayList<String> platforms, String upc){

        this.title = title;
        this.game_publisher = game_publisher;
        this.game_developer = game_developer;
        this.upc = upc;

    }



    public Software(Parcel parcel){

        this.id = parcel.readLong();
        this.uid = parcel.readString();
        this.title = parcel.readString();
        this.game_publisher = parcel.readString();
        this.game_developer = parcel.readString();
        this.platform = parcel.readString();
        this.upc = parcel.readString();
        this.user_description = parcel.readString();
        this.software_image_full_url = parcel.readString();
        this.software_image_thumbnail_url = parcel.readString();
        this.encoded_bitmap_thumbnail = parcel.readString();
        this.encoded_bitmap_full = parcel.readString();
        this.bitmap_local_file_path = parcel.readString();
        this.software_bitmap = parcel.readParcelable(Bitmap.class.getClassLoader());
        this.bitmap_name_thumbnail = parcel.readString();
        this.bitmap_name_full = parcel.readString();
        this.software_bitmap_uri = parcel.readParcelable(Uri.class.getClassLoader());

    }

    public static final Creator<Software> CREATOR = new Creator<Software>() {
        @Override
        public Software createFromParcel(Parcel source) {
            return new Software(source);
        }

        @Override
        public Software[] newArray(int size) {
            return new Software[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeLong(this.id);
        parcel.writeString(this.uid);
        parcel.writeString(this.title);
        parcel.writeString(this.game_publisher);
        parcel.writeString(this.game_developer);
        parcel.writeString(this.platform);
        parcel.writeString(this.upc);
        parcel.writeString(this.user_description);
        parcel.writeString(this.software_image_full_url);
        parcel.writeString(this.software_image_thumbnail_url);
        parcel.writeString(this.encoded_bitmap_thumbnail);
        parcel.writeString(this.encoded_bitmap_full);
        parcel.writeString(this.bitmap_local_file_path);
        parcel.writeParcelable(this.software_bitmap, flags);
        parcel.writeString(this.bitmap_name_thumbnail);
        parcel.writeString(this.bitmap_name_full);
        parcel.writeParcelable(this.software_bitmap_uri, flags);

    }
}
