package com.syncadapters.czar.exchange.repositories;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.LoginActivity;
import com.syncadapters.czar.exchange.asynctasks.LoadUserHardwareTask;
import com.syncadapters.czar.exchange.asynctasks.MySoftwareTask;
import com.syncadapters.czar.exchange.datamodels.ImageSaver;
import com.syncadapters.czar.exchange.datamodels.Software;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;
import com.syncadapters.czar.exchange.roomdatabase.ExchangeDatabase;
import com.syncadapters.czar.exchange.roomdatabase.MyHardwareDao;
import com.syncadapters.czar.exchange.roomdatabase.MySoftware;
import com.syncadapters.czar.exchange.roomdatabase.MySoftwareDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class InventoryInFragmentRepository {

    private static final String TAG = "MSG";
    private static InventoryInFragmentRepository inventory_in_fragment_repository;
    private WeakReference<Context> context_weak_reference;
    private MySoftwareDao my_software_dao;
    private MyHardwareDao my_hardware_dao;


    public static InventoryInFragmentRepository getInstance(){

        if(inventory_in_fragment_repository == null)
            inventory_in_fragment_repository = new InventoryInFragmentRepository();

        return inventory_in_fragment_repository;
    }

    public void initialize_database(Application application){

        ExchangeDatabase exchange_database = ExchangeDatabase.get_database(application);
        this.my_software_dao = exchange_database.my_software_dao();
        this.my_hardware_dao = exchange_database.my_hardware_dao();
    }

    public void set_context(Context context){

        this.context_weak_reference =  new WeakReference<>(context);
    }

    /*
    public void insert_software(Software software){

        MySoftware my_software = new MySoftware(software.title, software.game_publisher, software.game_developer, software.platform,
                software.upc, software.user_description, software.uid, software.bitmap_local_file_path, software.software_image_url);

        my_software_task = new MySoftwareTask(my_software, this.my_software_dao, DatabaseOperations.INSERT);
        my_software_task.execute();


    }
*/
    private void insert_software(Software software){

        MySoftware my_software = new MySoftware(software.title, software.game_publisher, software.game_developer,
                software.platform, software.upc,
                software.user_description, software.uid, software.bitmap_name_thumbnail, software.bitmap_name_full);

        MySoftwareTask my_software_task = new MySoftwareTask(software, my_software, this.my_software_dao, DatabaseOperations.INSERT);
        my_software_task.execute();
        Toast.makeText(this.context_weak_reference.get(), "[Software updated]!", Toast.LENGTH_LONG).show();

    }

    public void query_hardware(Context context, Spinner hardware_platform_spinner, Software software){

        LoadUserHardwareTask load_user_hardware_task = new LoadUserHardwareTask(this.my_hardware_dao, hardware_platform_spinner);
        load_user_hardware_task.set_user_interface(UserInterface.INVENTORY_IN_FRAGMENT);
        load_user_hardware_task.set_software(software);
        load_user_hardware_task.set_context(context);
        load_user_hardware_task.execute();

    }

/*
    public void query_hardware(Context context, Spinner hardware_platform_spinner, TextView software_platform_textView, Software software){

        this.context = context;
        this.hardware_platform_spinner = hardware_platform_spinner;
        this.software_platform_textView = software_platform_textView;

        try {

            JSONObject json_platforms = new JSONObject();
            json_platforms.put("uid", UserSettings.get_user_uid(context));
            json_platforms.put("ops", 3);
            json_platforms.put("id", UserSettings.get_user_id(context));
            json_platforms.put("access_token", UserSettings.get_user_token(context));
            json_platforms.put("category", 100);

            remote_server(software, json_platforms);

        } catch (JSONException json_error) {
            json_error.printStackTrace();
        }

    }
    */

    public void remote_server(Software software, JSONObject json_object){

        Log.d(TAG, "Fetching Hardware.....");
        ImageSaver image_saver = new ImageSaver();
        Volley volley = new Volley(this.context_weak_reference.get(), Request.Method.POST, URL.INVENTORY_URL, json_object);
        volley.set_priority(Request.Priority.HIGH);
        volley.Execute(new VolleyCallback() {
            @Override
            public void network_response(JSONObject json_response) {

                UserSettings.remove_encoded_bitmap_thumbnail(context_weak_reference.get().getApplicationContext());
                UserSettings.remove_encoded_bitmap_full(context_weak_reference.get().getApplicationContext());
                image_saver.delete_image_file(UserSettings.get_user_local_software_image_path(context_weak_reference.get()));
                Log.d(TAG, "InventoryInFragment network response");
                try {

                    if(json_response.has(context_weak_reference.get().getResources().getString(R.string.database_connection_error_label))){

                        String database_connection_error_message = json_response.getString(context_weak_reference.get().getResources().getString(R.string.database_connection_error_label));
                        Toast.makeText(context_weak_reference.get(), database_connection_error_message, Toast.LENGTH_LONG).show();
                        return;

                    }

                    if(json_response.has(context_weak_reference.get().getResources().getString(R.string.session_timeout_label))){

                        session_timeout(context_weak_reference.get());
                        Log.d(TAG, "[InventoryInFragmentRepository] Session Time Out");
                        Toast.makeText(context_weak_reference.get(), "SESSION TIME OUT", Toast.LENGTH_LONG).show();
                        return;


                    }

                    if(json_response.has(context_weak_reference.get().getResources().getString(R.string.transaction_software_error_101_1))){

                        String transaction_software_101_1_error_message = json_response.getString(context_weak_reference.get().getResources().getString(R.string.transaction_software_error_101_1));
                        Toast.makeText(context_weak_reference.get(), transaction_software_101_1_error_message, Toast.LENGTH_LONG).show();
                        return;

                    }


                        if(json_response.has(context_weak_reference.get().getResources().getString(R.string.transaction_software_101_1_label))){


                                if (json_response.getBoolean(context_weak_reference.get().getResources().getString(R.string.transaction_software_101_1_label))) {


                                    software.bitmap_name_thumbnail = json_response.getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_thumbnail_key));
                                    software.bitmap_name_full = json_response.getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_full_key));

                                    if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.ldpi_label))){

                                        software.software_image_thumbnail_url = URL.LDPI + software.bitmap_name_thumbnail;
                                        software.software_image_full_url = URL.LDPI + software.bitmap_name_full;
                                    }
                                    else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.mdpi_label))){

                                        software.software_image_thumbnail_url = URL.MDPI + software.bitmap_name_thumbnail;
                                        software.software_image_full_url = URL.MDPI + software.bitmap_name_full;

                                    }
                                    else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.hdpi_label))){

                                        software.software_image_thumbnail_url = URL.HDPI + software.bitmap_name_thumbnail;
                                        software.software_image_full_url = URL.HDPI + software.bitmap_name_full;

                                    }
                                    else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xhdpi_label))){

                                        software.software_image_thumbnail_url = URL.XHDPI + software.bitmap_name_thumbnail;
                                        software.software_image_full_url = URL.XHDPI + software.bitmap_name_full;

                                    }
                                    else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xxhdpi_label))){

                                        software.software_image_thumbnail_url = URL.XXHDPI + software.bitmap_name_thumbnail;
                                        software.software_image_full_url = URL.XXHDPI + software.bitmap_name_full;

                                    }
                                    else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xxxhdpi_label))){

                                        software.software_image_thumbnail_url = URL.XXXHDPI + software.bitmap_name_thumbnail;
                                        software.software_image_full_url = URL.XXXHDPI + software.bitmap_name_full;

                                    }

                                   // save_image(software);
                                   insert_software(software);

                                    Log.d(TAG, software.title + " by " + software.game_developer + " is inserted" );
                                    Toast.makeText(context_weak_reference.get(), "Software saved!", Toast.LENGTH_LONG).show();



                                }



                        }

                }catch(JSONException json_error){

                    json_error.printStackTrace();
                }


            }

            @Override
            public void network_error(VolleyError error) {

                image_saver.delete_image_file(UserSettings.get_user_local_software_image_path(context_weak_reference.get()));
                Toast.makeText(context_weak_reference.get(),
                            context_weak_reference.get().getResources().getString(R.string.network_connection_error_label),
                            Toast.LENGTH_LONG).show();



            }
        });

    }

    private void session_timeout(Context context){

        App app = ((App) context.getApplicationContext());

        JSONObject session_timeout_json = null;
        try{

            String email = UserSettings.get_user_email(context);
            String user_id = UserSettings.get_user_id(context);

            session_timeout_json = new JSONObject();
            session_timeout_json.put(context.getResources().getString(R.string.email_key), email);
            session_timeout_json.put(context.getResources().getString(R.string.user_id_key), user_id);


        }catch(JSONException json_error){
            Toast.makeText(context, context.getResources().getString(R.string.network_connection_error_label), Toast.LENGTH_LONG).show();
            json_error.printStackTrace();
        }

        Volley volley = new Volley(context, Request.Method.POST, URL.LOGOUT_URL, session_timeout_json);
        volley.Execute(new VolleyCallback() {
            @Override
            public void network_response(JSONObject json_response) {

                try {
                    if (json_response.has(context.getResources().getString(R.string.logout_error_100_label))) {

                        String logout_100_error_message = json_response.getString(context.getResources().getString(R.string.logout_error_100_label));
                        Toast.makeText(context, logout_100_error_message, Toast.LENGTH_LONG).show();
                        return;
                    } else if (json_response.has(context.getResources().getString(R.string.logout_error_101_label))) {

                        String logout_101_error_message = json_response.getString(context.getResources().getString(R.string.logout_error_101_label));
                        Toast.makeText(context, logout_101_error_message, Toast.LENGTH_LONG).show();
                        return;
                    } else if (json_response.has(context.getResources().getString(R.string.logout_error_103_label))) {

                        String logout_103_error_message = json_response.getString(context.getResources().getString(R.string.logout_error_103_label));
                        Toast.makeText(context, logout_103_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }


                    if (json_response.has(context.getResources().getString(R.string.logout_100_label))) {

                        if (json_response.getBoolean(context.getResources().getString(R.string.logout_100_label))) {

                            Log.d(TAG, "Logging out...");
                            launch_logout_activity(app, context);

                        }

                    }
                }catch(JSONException json_error){
                    json_error.printStackTrace();
                }


            }

            @Override
            public void network_error(VolleyError error) {

                        launch_logout_activity(app, context);

            }
        });

    }


    private void launch_logout_activity(App app, Context context){

        app.set_is_user_logged_in(false);
        app.set_location_permission(false);

        UserSettings.set_is_user_logged_in(context, false);
        UserSettings.remove_user_token(context);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        (context).startActivity(intent);
    }

}
