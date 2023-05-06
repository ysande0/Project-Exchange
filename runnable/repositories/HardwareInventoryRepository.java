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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.LoginActivity;
import com.syncadapters.czar.exchange.asynctasks.LoadUserHardwareTask;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;
import com.syncadapters.czar.exchange.roomdatabase.ExchangeDatabase;
import com.syncadapters.czar.exchange.roomdatabase.MyHardwareDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;


public class HardwareInventoryRepository {

    private static final String TAG = "MSG";
    private MyHardwareDao my_hardware_dao;
    private WeakReference<Context> context_weak_reference;
    private static HardwareInventoryRepository hardware_inventory_repository;
    // --Commented out by Inspection (1/9/2021 11:50 PM):private MyHardwareTask my_hardware_task;

    public static HardwareInventoryRepository getInstance(){

        if(hardware_inventory_repository == null)
            hardware_inventory_repository = new HardwareInventoryRepository();

        return hardware_inventory_repository;
    }

    public void initialize_database(Application application){

        ExchangeDatabase exchange_database = ExchangeDatabase.get_database(application);
        this.my_hardware_dao = exchange_database.my_hardware_dao();

    }

    public void query_hardware(Context context, RecyclerView hardware_platform_recycle_view, Spinner hardware_platform_spinner,
                               ImageButton add_hardware_platform_button, ProgressBar progress_bar, LinearLayout hardware_display_section_linear_layout,
                               TextView no_hardware_available_textView_id, FloatingActionButton continue_action_button){


        LoadUserHardwareTask load_user_hardware_task = new LoadUserHardwareTask(this.my_hardware_dao, hardware_platform_recycle_view, hardware_platform_spinner, add_hardware_platform_button,
                progress_bar, hardware_display_section_linear_layout, no_hardware_available_textView_id, continue_action_button);
        load_user_hardware_task.set_user_interface(UserInterface.HARDWARE_INVENTORY_ACTIVITY);
        load_user_hardware_task.set_context(context);
        load_user_hardware_task.execute();

    }

    public void logout(App app, Context context){

        FirebaseAuth.getInstance().signOut();
        context_weak_reference = new WeakReference<>(context);
        JSONObject json_object = null;
        try{

            json_object = new JSONObject();
            json_object.put(context.getResources().getString(R.string.user_id_key), UserSettings.get_user_id(context_weak_reference.get()));
            json_object.put(context.getResources().getString(R.string.email_key), UserSettings.get_user_email(context_weak_reference.get()));

        }catch(JSONException json_error){

            json_error.printStackTrace();
        }

        Volley volley = new Volley(context_weak_reference.get(), Request.Method.POST, URL.LOGOUT_URL, json_object);
        volley.set_priority(Request.Priority.IMMEDIATE);
        volley.Execute(new VolleyCallback() {
            @Override
            public void network_response(JSONObject json_response) {

                try {

                    if(json_response.has(context_weak_reference.get().getResources().getString(R.string.logout_error_100_label))){

                        String logout_100_error_message = json_response.getString(context_weak_reference.get().getResources().getString(R.string.logout_error_100_label));
                        Toast.makeText(context_weak_reference.get(), logout_100_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(json_response.has(context_weak_reference.get().getResources().getString(R.string.logout_error_101_label))){

                        String logout_101_error_message = json_response.getString(context_weak_reference.get().getResources().getString(R.string.logout_error_101_label));
                        Toast.makeText(context_weak_reference.get(), logout_101_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(json_response.has(context_weak_reference.get().getResources().getString(R.string.logout_error_103_label))){

                        String logout_103_error_message = json_response.getString(context_weak_reference.get().getResources().getString(R.string.logout_error_103_label));
                        Toast.makeText(context_weak_reference.get(), logout_103_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(json_response.has("logout_100")){

                        if(json_response.getBoolean("logout_100")){

                            Log.d(TAG, "Logging out...");
                            app.set_is_user_logged_in(false);
                            app.set_location_permission(false);

                            UserSettings.set_is_user_logged_in(context_weak_reference.get(), false);
                            UserSettings.remove_user_token(context_weak_reference.get());

                            Intent intent = new Intent(context_weak_reference.get(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context_weak_reference.get().startActivity(intent);

                        }

                    }

                }catch(JSONException json_error){
                    json_error.printStackTrace();
                }

            }

            @Override
            public void network_error(VolleyError error) {
                error.printStackTrace();
            }
        });

    }



}
