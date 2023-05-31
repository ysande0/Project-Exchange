package com.syncadapters.czar.exchange.repositories;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

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
import com.syncadapters.czar.exchange.dialogs.SoftwareProfileDialog;
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

@SuppressWarnings("unused")
public class SoftwareProfileDialogRepository {

    private static final String TAG = "MSG";
    private static SoftwareProfileDialogRepository software_profile_dialog_repository;
    private MySoftwareDao my_software_dao;
    private MyHardwareDao my_hardware_dao;
    private WeakReference<Context> context_weak_reference;

    public static SoftwareProfileDialogRepository getInstance() {

        if (software_profile_dialog_repository == null)
            software_profile_dialog_repository = new SoftwareProfileDialogRepository();

        return software_profile_dialog_repository;
    }

    public void initialize_database(Application application) {

        ExchangeDatabase exchange_database = ExchangeDatabase.get_database(application);
        this.my_software_dao = exchange_database.my_software_dao();
        this.my_hardware_dao = exchange_database.my_hardware_dao();
    }

    public void set_context(Context context){

        context_weak_reference = new WeakReference<>(context);
    }


    public void load_hardware(Spinner software_platform_spinner, Software software){

        Log.d(TAG, "SOFTWARE DIALOG LOADING HARDWARE");
        LoadUserHardwareTask load_user_hardware_task = new LoadUserHardwareTask(this.my_hardware_dao, software_platform_spinner);
        load_user_hardware_task.set_user_interface(UserInterface.SOFTWARE_PROFILE_DIALOG);
        load_user_hardware_task.set_software(software);
        load_user_hardware_task.set_context(context_weak_reference.get());
        load_user_hardware_task.execute();
    }

    public void remote_server(Software software, JSONObject software_json, @SuppressWarnings("unused") SoftwareProfileDialog software_profile_dialog, FragmentManager fragment_manager, int position) {

        ImageSaver image_saver = new ImageSaver();
        Volley volley = new Volley(this.context_weak_reference.get(), Request.Method.POST, URL.INVENTORY_URL, software_json);
            volley.set_priority(Request.Priority.HIGH);
            volley.Execute(new VolleyCallback() {
                @Override
                public void network_response(JSONObject json_response) {

                    UserSettings.remove_encoded_bitmap_thumbnail(context_weak_reference.get().getApplicationContext());
                    UserSettings.remove_encoded_bitmap_full(context_weak_reference.get().getApplicationContext());
                    image_saver.delete_image_file(UserSettings.get_user_local_software_image_path(context_weak_reference.get()));
                    try {

                        if(json_response.has(context_weak_reference.get().getResources().getString(R.string.database_connection_error_label))){

                            String database_connection_error_message = json_response.getString(context_weak_reference.get().getResources().getString(R.string.database_connection_error_label));
                            Toast.makeText(context_weak_reference.get(), database_connection_error_message, Toast.LENGTH_LONG).show();
                            return;

                        }

                        if(json_response.has(context_weak_reference.get().getResources().getString(R.string.transaction_software_error_101_2))){

                            String transaction_software_101_2_error_message = json_response.getString(context_weak_reference.get().getResources().getString(R.string.transaction_software_error_101_2));
                            Toast.makeText(context_weak_reference.get(), transaction_software_101_2_error_message, Toast.LENGTH_LONG).show();
                            return;

                        }
                        else if(json_response.has(context_weak_reference.get().getResources().getString(R.string.transaction_software_error_101_4))){

                            String transaction_software_101_4_error_message = json_response.getString(context_weak_reference.get().getResources().getString(R.string.transaction_software_error_101_4));
                            Toast.makeText(context_weak_reference.get(), transaction_software_101_4_error_message, Toast.LENGTH_LONG).show();
                            return;

                        }

                        if(json_response.has(context_weak_reference.get().getResources().getString(R.string.transaction_software_101_2_label))){


                            if (json_response.getBoolean(context_weak_reference.get().getResources().getString(R.string.transaction_software_101_2_label))) {

                                MySoftware my_software = new MySoftware(software.id, software.title, software.game_publisher, software.game_developer,
                                        software.platform, software.upc, software.user_description, software.uid, software.bitmap_name_thumbnail,
                                        software.bitmap_name_full);

                                MySoftwareTask my_software_task = new MySoftwareTask(my_software, my_software_dao, DatabaseOperations.DELETE);
                                my_software_task.execute();

                                Log.d(TAG, "DELETING " + software.id + "  " + software.title + " by " + software.game_developer);
                                Toast.makeText(context_weak_reference.get(), "Software deleted!", Toast.LENGTH_LONG).show();

                                Bundle result = new Bundle();
                                result.putBoolean("delete_item", true);
                                result.putInt("position", position);
                                result.putParcelable("software", software);

                                fragment_manager.setFragmentResult("delete_software",  result);
                                //software_profile_dialog.dismiss();

                            }

                    }
                    else if(json_response.has(context_weak_reference.get().getResources().getString(R.string.transaction_software_101_4_label))){


                           // delete_image(software);
                         //   save_image(software);


                        MySoftware my_software = new MySoftware(software.id, software.title, software.game_publisher, software.game_developer,
                                software.platform, software.upc, software.user_description, software.uid, software.bitmap_name_thumbnail,
                                software.bitmap_name_full);

                            MySoftwareTask my_software_task = new MySoftwareTask(my_software, my_software_dao, DatabaseOperations.UPDATE);
                            my_software_task.execute();

                        Log.d(TAG, "UPDATING " + software.title + " by " + software.game_developer);
                        Toast.makeText(context_weak_reference.get(), "Software updating!", Toast.LENGTH_LONG).show();

                        Bundle result = new Bundle();
                        result.putBoolean("update_item", true);
                        result.putInt("position", position);
                        result.putParcelable("software", software);

                        fragment_manager.setFragmentResult("update_software",  result);
                        //software_profile_dialog.dismiss();

                    }

                }catch(JSONException json_error){
                    json_error.printStackTrace();
                }


            }

                @Override
                public void network_error(VolleyError error) {

                    image_saver.delete_image_file(UserSettings.get_user_local_software_image_path(context_weak_reference.get()));
                    Toast.makeText(context_weak_reference.get().getApplicationContext(),
                                context_weak_reference.get().getResources().getString(R.string.network_connection_error_label),
                                Toast.LENGTH_LONG).show();

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