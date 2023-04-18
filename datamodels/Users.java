package com.syncadapters.czar.exchange.datamodels;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public class Users implements Parcelable {

    public String id;
    public String first_name;
    public String user_image_full_url;
    public String user_image_thumbnail_url;
    public String user_image_name_full;
    public String user_image_name_thumbnail;
    public String user_distance;
    public Software software = new Software();
    private String fcm_token;
    public int rating;
    private Bitmap profile_bitmap;

    public Users(){

        //this.software = new Software();

    }

    public Users(String first_name, String fcm_token, String software_title, String software_platform, String software_publisher,
                 String software_developer, String software_upc,
                 String id, String user_distance, int rating, String user_image_name_full, String user_image_name_thumbnail,
                 String software_image_name_full, String software_image_name_thumbnail){

        this.id = id;
        this.first_name = first_name;
        this.user_image_name_full = user_image_name_full;
        this.user_image_name_thumbnail = user_image_name_thumbnail;
        this.user_distance = user_distance;

        this.software.title = software_title;
        this.software.platform = software_platform;
        this.software.game_publisher = software_publisher;
        this.software.game_developer = software_developer;
        this.software.upc = software_upc;
        this.software.bitmap_name_full = software_image_name_full;
        this.software.bitmap_name_thumbnail = software_image_name_thumbnail;
        this.fcm_token = fcm_token;
        this.rating = rating;

      //  this.software = new Software();


    }

    @SuppressWarnings("unused")
    public Users(String first_name, String fcm_token, String software_title, String software_platform,
                 String id, String user_distance, int rating, String user_image_name_full, String user_image_name_thumbnail,
                 String software_image_name_full, String software_image_name_thumbnail){

        this.id = id;
        this.first_name = first_name;
        this.user_image_name_full = user_image_full_url;
        this.user_image_name_thumbnail = user_image_thumbnail_url;
        this.user_distance = user_distance;
        this.software.title = software_title;
        this.software.platform = software_platform;
        this.software.bitmap_name_full = software_image_name_full;
        this.software.bitmap_name_thumbnail = software_image_name_thumbnail;

        this.fcm_token = fcm_token;
        this.rating = rating;

        //this.software = new Software();


    }

    /*
        public Users(String first_name, String user_image_url, int rating, String user_distance, String id, ArrayList<Software> software_library){

            this.first_name = first_name;
            this.user_image_url = user_image_url;
            this.rating = rating;
            this.user_distance = user_distance;
            this.id = id;
            this.software_library = software_library;


        }
    */
    public Users(Parcel parcel){

        this.id = parcel.readString();
        this.first_name = parcel.readString();
        this.user_image_full_url = parcel.readString();
        this.user_image_thumbnail_url = parcel.readString();
        this.user_image_name_full = parcel.readString();
        this.user_image_name_thumbnail = parcel.readString();
        this.user_distance = parcel.readString();
        this.software = parcel.readParcelable(Software.class.getClassLoader());
        this.fcm_token = parcel.readString();
        this.rating = parcel.readInt();
        this.profile_bitmap = parcel.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel source) {
            return new Users(source);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(this.id);
        parcel.writeString(this.first_name);
        parcel.writeString(this.user_image_full_url);
        parcel.writeString(this.user_image_thumbnail_url);
        parcel.writeString(this.user_image_name_full);
        parcel.writeString(this.user_image_name_thumbnail);
        parcel.writeString(this.user_distance);
        parcel.writeParcelable(this.software, flags);
        parcel.writeString(this.fcm_token);
        parcel.writeInt(this.rating);
        parcel.writeParcelable(profile_bitmap, flags);
    }
}