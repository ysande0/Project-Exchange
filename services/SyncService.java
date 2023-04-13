package com.syncadapters.czar.exchange.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.syncadapters.czar.exchange.adapters.SyncAdapter;

public class SyncService extends Service {

    private  SyncAdapter sync_adapter = null;
    private static final Object sync_adapter_lock = new Object();

    public void onCreate(){
        super.onCreate();

        //Log.d(TAG, "SyncService onCreate");

        synchronized (sync_adapter_lock){

            if(sync_adapter == null){

                sync_adapter = new SyncAdapter(getApplicationContext(), true);
            }

        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        return sync_adapter.getSyncAdapterBinder();
    }



}
