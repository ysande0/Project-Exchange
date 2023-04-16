package com.syncadapters.czar.exchange.asynctasks;

import android.os.AsyncTask;
import android.util.Log;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.roomdatabase.MyHardware;
import com.syncadapters.czar.exchange.roomdatabase.MyHardwareDao;

public class MyHardwareTask extends AsyncTask<MyHardware, Void, Void> {

    private static final String TAG = "MSG";
    private final MyHardware my_hardware;
    private final MyHardwareDao my_hardware_dao;
    private final DatabaseOperations database_operations;

    public MyHardwareTask(MyHardware my_hardware, MyHardwareDao my_hardware_dao, DatabaseOperations database_operations){

        this.my_hardware = my_hardware;
        this.my_hardware_dao = my_hardware_dao;
        this.database_operations = database_operations;
    }

    @Override
    protected Void doInBackground(MyHardware... myHardwares) {

        if(this.database_operations == DatabaseOperations.INSERT){

            Log.d(TAG, "MyHardwareTask INSERT");
            this.my_hardware_dao.insert(this.my_hardware);

        }
        else if(this.database_operations == DatabaseOperations.UPDATE){

            Log.d(TAG, "MyHardwareTask UPDATE");
            this.my_hardware_dao.update(this.my_hardware);

        }
        else if(this.database_operations == DatabaseOperations.DELETE){

            Log.d(TAG, "MyHardwareTask DELETE");
           this.my_hardware_dao.delete(this.my_hardware.get_platform());

        }
        else if(this.database_operations == DatabaseOperations.READ){

            Log.d(TAG, "MyHardwareTask READ");
            this.my_hardware_dao.query_all_hardware();

        }


        return null;
    }

}
