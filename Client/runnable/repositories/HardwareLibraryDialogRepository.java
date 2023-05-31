package com.syncadapters.czar.exchange.repositories;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.LoginActivity;
import com.syncadapters.czar.exchange.asynctasks.LoadUserHardwareTask;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.dialogs.HardwareLibraryDialog;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;
import com.syncadapters.czar.exchange.roomdatabase.ExchangeDatabase;
import com.syncadapters.czar.exchange.roomdatabase.MyHardwareDao;

import org.json.JSONException;
import org.json.JSONObject;


public class HardwareLibraryDialogRepository {

    private static final String TAG = "MSG";
    private MyHardwareDao my_hardware_dao;
    private static HardwareLibraryDialogRepository hardware_library_dialog_repository;

    public static HardwareLibraryDialogRepository getInstance(){

        if(hardware_library_dialog_repository == null)
            hardware_library_dialog_repository = new HardwareLibraryDialogRepository();

        return hardware_library_dialog_repository;
    }

    public void initialize_database(Application application){

        ExchangeDatabase exchange_database = ExchangeDatabase.get_database(application);
        this.my_hardware_dao = exchange_database.my_hardware_dao();
    }


    public void query_hardware(@SuppressWarnings("unused") App app, Context context, @SuppressWarnings("unused") HardwareLibraryDialog hardware_library_dialog, RecyclerView hardware_platform_recycle_view, Spinner hardware_platform_spinner,
                               ImageButton add_hardware_platform_button, ProgressBar progress_bar, LinearLayout hardware_display_section_linear_layout,
                               TextView no_hardware_available_textView_id){



        LoadUserHardwareTask load_user_hardware_task = new LoadUserHardwareTask(this.my_hardware_dao, hardware_platform_recycle_view, hardware_platform_spinner, add_hardware_platform_button,
                progress_bar, hardware_display_section_linear_layout, no_hardware_available_textView_id);
        load_user_hardware_task.set_user_interface(UserInterface.HARDWARE_INVENTORY_ACTIVITY);
        load_user_hardware_task.set_context(context);
        load_user_hardware_task.execute();
    }
/*
    public void remote_server(Context context, ArrayList<Hardware> hardware_platforms,
                              DatabaseOperations database_operations, boolean is_beginner){

        if(database_operations == DatabaseOperations.INSERT && hardware_platforms == null)
            return;



        JSONObject json_platforms = new JSONObject();
        if(database_operations == DatabaseOperations.INSERT) {
            try {

                JSONArray hardware_platforms_array = new JSONArray();
                for(int i = 0; i < hardware_platforms.size(); i++){

                    JSONObject platform = new JSONObject();
                    platform.put("manufacturer", hardware_platforms.get(i).manufacturer);
                    platform.put("platform", hardware_platforms.get(i).platform);

                    hardware_platforms_array.put(platform);

                }

                json_platforms.put("uid", UserSettings.get_user_uid(context));
                json_platforms.put("id", UserSettings.get_user_id(context));
                json_platforms.put("ops", 1);
                json_platforms.put("category", 100);
                json_platforms.put("access_token", UserSettings.get_user_token(context));
                json_platforms.put("platforms", hardware_platforms_array);

            } catch (JSONException json_error) {
                json_error.printStackTrace();
            }
        }
        else if(database_operations == DatabaseOperations.DELETE){

            try {

                // DELETE ALL OF USER_ID HARDWARE
                json_platforms.put("uid", UserSettings.get_user_uid(context));
                json_platforms.put("ops", 2);
                json_platforms.put("category", 100);
                json_platforms.put("id", UserSettings.get_user_id(context));
                json_platforms.put("access_token", UserSettings.get_user_token(context));

            } catch (JSONException json_error) {
                json_error.printStackTrace();
            }

        }
        else if(database_operations == DatabaseOperations.READ){

            try {

                json_platforms.put("uid", UserSettings.get_user_uid(context));
                json_platforms.put("ops", 3);
                json_platforms.put("id", UserSettings.get_user_id(context));
                json_platforms.put("access_token", UserSettings.get_user_token(context));
                json_platforms.put("category", 100);

            } catch (JSONException json_error) {
                json_error.printStackTrace();
            }


        }

        Log.d(TAG, "Hardware JSON: " + json_platforms.toString());
        volley = new Volley(context, Request.Method.POST, URL.INVENTORY_URL,  json_platforms);
        volley.set_priority(Request.Priority.HIGH);
        volley.Execute(new VolleyCallback() {
            @Override
            public void network_response(JSONObject json_response) {

                ArrayList<MyHardware> my_hardware_platforms = new ArrayList<>();
                try {

                    if(json_response.getBoolean("transaction_hardware_100_1")) {
                        //   Log.d(TAG, hardware_platform.platform + " added to remote server");

                        for(int i = 0; i < hardware_platforms.size(); i++){

                            Log.d(TAG, "Manufacturer: " + hardware_platforms.get(i).manufacturer + " Platform: " + hardware_platforms.get(i).platform);
                            MyHardware my_hardware = new MyHardware(hardware_platforms.get(i).manufacturer, hardware_platforms.get(i).platform);
                            my_hardware_platforms.add(my_hardware);

                        }
                        Log.d(TAG, "Number of platforms: " + my_hardware_platforms.size());
                        Log.d(TAG, "Hardware has been inserted");

                        my_hardware_task = new MyHardwareTask(my_hardware_platforms, my_hardware_dao, database_operations);
                        my_hardware_task.set_context(context);
                        my_hardware_task.set_user_interface(UserInterface.HARDWARE_INVENTORY_ACTIVITY);
                        my_hardware_task.set_user_experience(is_beginner);
                        my_hardware_task.execute();

                        if(is_beginner){

                            Intent intent = new Intent(context, SoftwareInventoryActivity.class);
                            intent.putExtra(context.getResources().getString(R.string.user_experience_one), true );
                            ((HardwareInventoryActivity) context).startActivity(intent);
                        }

                    }
                    else if(json_response.getBoolean("transaction_hardware_200")) {


                        my_hardware_task = new MyHardwareTask(my_hardware_dao, database_operations);
                        my_hardware_task.execute();


                        Log.d(TAG, "Hardware has been deleted");
                    }

                }catch (JSONException json_error){
                    json_error.printStackTrace();
                }
            }

            @Override
            public void network_error(VolleyError error) {
                Log.d(TAG, "HardwareActivity Error: " + error.toString());
            }
        });

    }
*/
    @SuppressWarnings("unused")
    public void logout(App app, Context context){

        FirebaseAuth.getInstance().signOut();
        JSONObject json_object = null;
        try{

            json_object = new JSONObject();
            json_object.put(context.getResources().getString(R.string.user_id_key), UserSettings.get_user_id(context));
            json_object.put(context.getResources().getString(R.string.email_key), UserSettings.get_user_email(context));

        }catch(JSONException json_error){

            json_error.printStackTrace();
        }

        Volley volley = new Volley(context, Request.Method.POST, URL.LOGOUT_URL, json_object);
        volley.set_priority(Request.Priority.IMMEDIATE);
        //noinspection unused,unused
        volley.Execute(new VolleyCallback() {
            @Override
            public void network_response(JSONObject json_response) {

                try {

                    if(json_response.has(context.getResources().getString(R.string.logout_error_100_label))){

                        String logout_100_error_message = json_response.getString(context.getResources().getString(R.string.logout_error_100_label));
                        Toast.makeText(context, logout_100_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(json_response.has(context.getResources().getString(R.string.logout_error_101_label))){

                        String logout_101_error_message = json_response.getString(context.getResources().getString(R.string.logout_error_101_label));
                        Toast.makeText(context, logout_101_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(json_response.has(context.getResources().getString(R.string.logout_error_103_label))){

                        String logout_103_error_message = json_response.getString(context.getResources().getString(R.string.logout_error_103_label));
                        Toast.makeText(context, logout_103_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }


                    if(json_response.has("logout_100")){

                        if(json_response.getBoolean("logout_100")){

                            Log.d(TAG, "Logging out...");
                            app.set_is_user_logged_in(false);
                            app.set_location_permission(false);

                            UserSettings.set_is_user_logged_in(context, false);
                            UserSettings.remove_user_token(context);

                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);

                        }

                    }

                }catch(JSONException json_error){
                    json_error.printStackTrace();
                }

            }

            @SuppressWarnings("unused")
            @Override
            public void network_error(VolleyError error) {
                error.printStackTrace();
            }
        });

    }

}
