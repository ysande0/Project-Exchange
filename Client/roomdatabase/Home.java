package com.syncadapters.czar.exchange.roomdatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@SuppressWarnings("WeakerAccess")
@Entity(tableName = "home")
public class Home {

    @SuppressWarnings({"unused"})
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    protected final long id;

    @ColumnInfo(name = "home_response")
    protected final String home_response;

   @SuppressWarnings({"unused"})
   @ColumnInfo(name = "time")
    protected final String time;

    @SuppressWarnings({"unused"})
    @ColumnInfo(name = "date")
    protected final String date;

    public Home(long id, String home_response, String time, String date){

        this.id = id;
        this.home_response = home_response;
        this.time = time;
        this.date = date;

    }


    public String get_home_response(){

        return this.home_response;
    }


}
