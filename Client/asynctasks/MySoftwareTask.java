package com.syncadapters.czar.exchange.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.syncadapters.czar.exchange.datamodels.Software;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.roomdatabase.MySoftware;
import com.syncadapters.czar.exchange.roomdatabase.MySoftwareDao;

import org.greenrobot.eventbus.EventBus;

public class MySoftwareTask extends AsyncTask<MySoftware, Void, Void> {

    private static final String TAG = "MSG";
    private Software software;
    private final MySoftware my_software;
    private final MySoftwareDao my_software_dao;
    private final DatabaseOperations database_operations;


    public MySoftwareTask(MySoftware my_software, MySoftwareDao my_software_dao, DatabaseOperations database_operations){


        this.my_software = my_software;
        this.my_software_dao = my_software_dao;
        this.database_operations = database_operations;

    }

    public MySoftwareTask(Software software, MySoftware my_software, MySoftwareDao my_software_dao, DatabaseOperations database_operations){

        this.software = software;
        this.my_software = my_software;
        this.my_software_dao = my_software_dao;
        this.database_operations = database_operations;

    }


    @Override
    protected Void doInBackground(MySoftware... mySoftwares) {

        if(this.database_operations == DatabaseOperations.INSERT){

            Log.d(TAG, "MySoftwareTask INSERT");
            this.software.id = this.my_software_dao.insert(my_software);

        }
        else if(this.database_operations == DatabaseOperations.DELETE){

            Log.d(TAG, "MySoftwareTask DELETE");
            this.my_software_dao.delete(my_software);
            Log.d(TAG, "DELETED");
        }
        else if(this.database_operations == DatabaseOperations.UPDATE){

            Log.d(TAG, "MySoftwareTask UPDATE");
            this.my_software_dao.update(my_software);
            Log.d(TAG, "UPDATED");
        }

        return null;

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(this.database_operations == DatabaseOperations.INSERT){
            Log.d(TAG, "[MySoftwareTask] onPostExecute");
            EventBus.getDefault().post(software);
        }

    }
}
