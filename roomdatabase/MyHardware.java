package com.syncadapters.czar.exchange.roomdatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@SuppressWarnings("ALL")
@Entity(tableName = "myhardware")
public class MyHardware {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    protected long id;

    @ColumnInfo(name = "manufacturer")
    protected final String manufacturer;

    @ColumnInfo(name = "platform")
    protected final String platform;

    public MyHardware(String manufacturer, String platform){

        this.manufacturer = manufacturer;
        this.platform = platform;

    }

// --Commented out by Inspection START (1/9/2021 11:52 PM):
//    public long get_id(){
//
//        return this.id;
//    }
// --Commented out by Inspection STOP (1/9/2021 11:52 PM)

    public String get_manufacturer(){
        return this.manufacturer;
    }

    public String get_platform(){
        return this.platform;
    }

}
