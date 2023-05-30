package com.syncadapters.czar.exchange.roomdatabase;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.syncadapters.czar.exchange.R;


@Database(entities = {Home.class, MyConversations.class, MyHardware.class, MySoftware.class}, version = 1, exportSchema = false)
//@TypeConverters({DateConverter.class})
public abstract class ExchangeDatabase extends RoomDatabase {

    private static final String TAG= "MSG";
    private static volatile  ExchangeDatabase exchange_database_instance;

    public abstract HomeDao home_dao();
    public abstract MyConversationsDao my_conversations_dao();

    public abstract MyHardwareDao my_hardware_dao();
    public abstract MySoftwareDao my_software_dao();

    public static ExchangeDatabase get_database(final Context context){

        if(exchange_database_instance == null){

            synchronized (ExchangeDatabase.class){

                if(exchange_database_instance == null){

                    Log.d(TAG,"DB Room is null, Creating DB");
                    exchange_database_instance = Room.databaseBuilder(context.getApplicationContext(),
                            ExchangeDatabase.class, context.getResources().getString(R.string.room_database_name))
                            .build();


                }
                else
                    Log.d(TAG,"DB Room is not null");
            }
        }

        return exchange_database_instance;
    }


}
