package com.syncadapters.czar.exchange.runnable;

import android.content.Context;

import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.asynctasks.ReloadLibrariesTask;
import com.syncadapters.czar.exchange.roomdatabase.MyHardware;
import com.syncadapters.czar.exchange.roomdatabase.MyHardwareDao;
import com.syncadapters.czar.exchange.roomdatabase.MySoftware;
import com.syncadapters.czar.exchange.roomdatabase.MySoftwareDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ReloadLibrariesRunnable implements Runnable {

    private final WeakReference<Context> context_weak_reference;
    private final JSONArray user_hardware_json_array;
    private final JSONArray user_software_json_array;
    private final MyHardwareDao my_hardware_dao;
    private final MySoftwareDao my_software_dao;

    public ReloadLibrariesRunnable(WeakReference<Context> context_weak_reference, JSONArray user_hardware_json_array,
                                   JSONArray user_software_json_array, MyHardwareDao my_hardware_dao, MySoftwareDao my_software_dao){

        this.context_weak_reference = context_weak_reference;
        this.user_hardware_json_array = user_hardware_json_array;
        this.user_software_json_array = user_software_json_array;
        this.my_hardware_dao = my_hardware_dao;
        this.my_software_dao = my_software_dao;
    }

    @Override
    public void run() {

        try {
            List<MyHardware> my_hardware_list = new ArrayList<>();
            for (int i = 0; i < user_hardware_json_array.length(); i++) {

                JSONObject platform_json = user_hardware_json_array.getJSONObject(i);
                MyHardware my_hardware = new MyHardware(platform_json.getString(context_weak_reference.get().getResources().getString(R.string.manufacturer_label)),
                        platform_json.getString(context_weak_reference.get().getResources().getString(R.string.platform_label)));
                my_hardware_list.add(my_hardware);

            }

            List<MySoftware> my_software_list = new ArrayList<>();
            for(int i = 0; i < user_software_json_array.length(); i++){

                JSONObject software_json = user_software_json_array.getJSONObject(i);
                MySoftware my_software = new MySoftware(software_json.getString(context_weak_reference.get().getResources().getString(R.string.title_key)), software_json.getString(context_weak_reference.get().getResources().getString(R.string.publisher_key)), software_json.getString(context_weak_reference.get().getResources().getString(R.string.developer_key)),
                        software_json.getString(context_weak_reference.get().getResources().getString(R.string.platform_key)), software_json.getString(context_weak_reference.get().getResources().getString(R.string.upc_key)), software_json.getString(context_weak_reference.get().getResources().getString(R.string.user_description_key)),
                        software_json.getString(context_weak_reference.get().getResources().getString(R.string.software_uid_key)), software_json.getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_thumbnail_key)),
                        software_json.getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_full_key)));

                my_software_list.add(my_software);
            }

            // Retrieve Hardware and Software
            ReloadLibrariesTask reload_libraries_task = new ReloadLibrariesTask(context_weak_reference.get(), my_hardware_list, my_software_list,
                    my_hardware_dao, my_software_dao);
            reload_libraries_task.execute();

        }catch (JSONException json_error){
            json_error.printStackTrace();
        }

    }
}
