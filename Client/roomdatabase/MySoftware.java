package com.syncadapters.czar.exchange.roomdatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@SuppressWarnings("WeakerAccess")
@Entity(tableName = "mysoftware")
public class MySoftware {

    @SuppressWarnings("WeakerAccess")
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    protected long id;

    @SuppressWarnings("WeakerAccess")
    @ColumnInfo(name = "title")
    protected final String title;

    @ColumnInfo(name = "publisher")
    protected final String publisher;

    @ColumnInfo(name = "developer")
    protected final String developer;

    @ColumnInfo(name = "platform")
    protected final String platform;

    @ColumnInfo(name = "upc")
    protected final String upc;

    @ColumnInfo(name = "user_description")
    protected final String user_description;

    @ColumnInfo(name = "software_uid")
    protected final String software_uid;

    @ColumnInfo(name = "remote_software_image_thumbnail_url")
    protected final String software_image_thumbnail_url;

    @ColumnInfo(name = "remote_software_image_full_url")
    protected final String software_image_full_url;


    public MySoftware(String title, String publisher, String developer, String platform, String upc, String user_description,
                      String software_uid, String software_image_thumbnail_url, String software_image_full_url){

        this.title = title;
        this.publisher = publisher;
        this.developer = developer;
        this.platform = platform;
        this.upc = upc;
        this.user_description = user_description;
        this.software_uid = software_uid;
        this.software_image_thumbnail_url = software_image_thumbnail_url;
        this.software_image_full_url = software_image_full_url;

    }

    @Ignore
    public MySoftware(long id, String title, String publisher, String developer, String platform, String upc, String user_description,
                      String software_uid, String software_image_thumbnail_url, String software_image_full_url){

        this.id = id;
        this.title = title;
        this.publisher = publisher;
        this.developer = developer;
        this.platform = platform;
        this.upc = upc;
        this.user_description = user_description;
        this.software_uid = software_uid;
        this.software_image_thumbnail_url = software_image_thumbnail_url;
        this.software_image_full_url = software_image_full_url;

    }

    /*
        public MySoftware(long id, String software_collection, String time, String date){

            this.id = id;
            this.software_collection = software_collection;
            this.time = time;
            this.date = date;

        }

     */





    public long get_id(){
        return this.id;
    }

    public String get_title(){
        return this.title;
    }

    public String get_publisher(){
        return this.publisher;
    }

    public String get_developer(){
        return this.developer;
    }

    public String get_platform(){
        return this.platform;
    }

    public String get_upc(){
        return this.upc;
    }

    public String get_user_description(){
        return this.user_description;
    }

    public String get_software_uid(){
        return this.software_uid;
    }

    public String get_remote_software_image_thumbnail_url(){
        return this.software_image_thumbnail_url;
    }

    public String get_remote_software_image_full_url(){
        return this.software_image_full_url;
    }



}
