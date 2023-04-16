package com.syncadapters.czar.exchange.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.syncadapters.czar.exchange.activities.HomeActivity;
import com.syncadapters.czar.exchange.activities.LoginActivity;
import com.syncadapters.czar.exchange.roomdatabase.MyHardware;
import com.syncadapters.czar.exchange.roomdatabase.MyHardwareDao;
import com.syncadapters.czar.exchange.roomdatabase.MySoftware;
import com.syncadapters.czar.exchange.roomdatabase.MySoftwareDao;

import java.lang.ref.WeakReference;
import java.util.List;

public class ReloadLibrariesTask extends AsyncTask<Void, Void, Void> {

    private final WeakReference<Context> context_weak_reference;
    private final List<MyHardware> hardware_inventory;
    private final List<MySoftware> software_inventory;
    private final MyHardwareDao my_hardware_dao;
    private final MySoftwareDao my_software_dao;

    public ReloadLibrariesTask(Context context, List<MyHardware> hardware_inventory, List<MySoftware> software_inventory,
                           MyHardwareDao my_hardware_dao, MySoftwareDao my_software_dao){

        context_weak_reference = new WeakReference<>(context);
        this.hardware_inventory = hardware_inventory;
        this.software_inventory = software_inventory;
        this.my_hardware_dao = my_hardware_dao;
        this.my_software_dao = my_software_dao;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if(this.hardware_inventory.isEmpty())
            this.my_hardware_dao.delete_all_hardware();
        else
            this.my_hardware_dao.update_all_hardware(this.hardware_inventory);

        if(this.software_inventory.isEmpty())
            this.my_software_dao.delete_all_software();
        else
            this.my_software_dao.update_all_software(this.software_inventory);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Intent intent = new Intent(context_weak_reference.get(), HomeActivity.class);
        context_weak_reference.get().startActivity(intent);
        ((LoginActivity) context_weak_reference.get()).finish();
    }
}
