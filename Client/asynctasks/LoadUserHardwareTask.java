package com.syncadapters.czar.exchange.asynctasks;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.HomeActivity;
import com.syncadapters.czar.exchange.activities.SoftwareInventoryActivity;
import com.syncadapters.czar.exchange.adapters.HardwareRecycleViewAdapter;
import com.syncadapters.czar.exchange.datamodels.Hardware;
import com.syncadapters.czar.exchange.datamodels.Software;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;
import com.syncadapters.czar.exchange.roomdatabase.MyHardware;
import com.syncadapters.czar.exchange.roomdatabase.MyHardwareDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class LoadUserHardwareTask extends AsyncTask<Void, Void, ArrayList<Hardware>> {

    private static final String TAG = "MSG";
    private WeakReference<Context> context_weak_reference;
    private final MyHardwareDao my_hardware_dao;
    private UserInterface user_interface;
    private Software software = new Software();
    private WeakReference<Spinner> software_platform_spinner_weak_reference;
    private WeakReference<RecyclerView> hardware_platform_recycle_view_weak_reference;
    private WeakReference<Spinner> hardware_platform_spinner_weak_reference;
    private WeakReference<ProgressBar> progress_bar_weak_references;
    private WeakReference<ImageButton> add_hardware_platform_button_weak_references;
    private WeakReference<LinearLayout> hardware_display_section_linear_layout_weak_references;
    private WeakReference<TextView> no_hardware_available_textView_id_weak_references;
    private JSONObject platform_json = null;

    private String current_platform;
    private String current_manufacturer;
    private int INDEX = 0;
    private final boolean is_beginner = false;


    public LoadUserHardwareTask(MyHardwareDao my_hardware_dao, RecyclerView hardware_platform_recycle_view, Spinner hardware_platform_spinner,
                                ImageButton add_hardware_platform_button, ProgressBar progress_bar, LinearLayout hardware_display_section_linear_layout,
                                TextView no_hardware_available_textView_id, FloatingActionButton continue_action_button){

        this.my_hardware_dao = my_hardware_dao;
        this.hardware_platform_recycle_view_weak_reference = new WeakReference<>(hardware_platform_recycle_view);
        this.hardware_platform_spinner_weak_reference = new WeakReference<>(hardware_platform_spinner);
        this.add_hardware_platform_button_weak_references = new WeakReference<>(add_hardware_platform_button);
        this.progress_bar_weak_references = new WeakReference<>(progress_bar);
        this.hardware_display_section_linear_layout_weak_references = new WeakReference<>(hardware_display_section_linear_layout);
        this.no_hardware_available_textView_id_weak_references =  new WeakReference<>(no_hardware_available_textView_id);
        @SuppressWarnings("unused") WeakReference<FloatingActionButton> continue_action_button_weak_references = new WeakReference<>(continue_action_button);
    }

    public LoadUserHardwareTask(MyHardwareDao my_hardware_dao, RecyclerView hardware_platform_recycle_view, Spinner hardware_platform_spinner,
                                ImageButton add_hardware_platform_button, ProgressBar progress_bar, LinearLayout hardware_display_section_linear_layout,
                                TextView no_hardware_available_textView_id){

        this.my_hardware_dao = my_hardware_dao;
        this.hardware_platform_recycle_view_weak_reference = new WeakReference<>(hardware_platform_recycle_view);
        this.hardware_platform_spinner_weak_reference = new WeakReference<>(hardware_platform_spinner);
        this.add_hardware_platform_button_weak_references = new WeakReference<>(add_hardware_platform_button);
        this.progress_bar_weak_references = new WeakReference<>(progress_bar);
        this.hardware_display_section_linear_layout_weak_references = new WeakReference<>(hardware_display_section_linear_layout);
        this.no_hardware_available_textView_id_weak_references = new WeakReference<>(no_hardware_available_textView_id);
    }


    public LoadUserHardwareTask(MyHardwareDao my_hardware_dao, Spinner software_platform_spinner){

        this.my_hardware_dao = my_hardware_dao;
        this.software_platform_spinner_weak_reference = new WeakReference<>(software_platform_spinner);

    }

    public void set_context(Context context){

        this.context_weak_reference = new WeakReference<>(context);

    }


    public void set_software(Software software){

        this.software = software;
    }

    public void set_user_interface(UserInterface user_interface){

        this.user_interface = user_interface;

    }

    private void launch_software_activity(ArrayList<Hardware> hardwares){

        if(hardwares.size() > 0){

            Intent intent = new Intent(this.context_weak_reference.get(), SoftwareInventoryActivity.class);
            intent.putExtra(this.context_weak_reference.get().getResources().getString(R.string.is_beginner_key), is_beginner );
            this.context_weak_reference.get().startActivity(intent);

        }

    }

    private void load_hardware_platform(ArrayList<Hardware> hardwares){

        final String MSG = "TAG";
        HardwareRecycleViewAdapter hardware_recycle_view_adapter = new HardwareRecycleViewAdapter(this.context_weak_reference.get(), hardwares, my_hardware_dao, hardware_platform_recycle_view_weak_reference.get());

        LinearLayoutManager linear_layout_manager = new LinearLayoutManager(this.context_weak_reference.get());
        linear_layout_manager.setStackFromEnd(true);
        linear_layout_manager.setReverseLayout(true);
        hardware_platform_recycle_view_weak_reference.get().setLayoutManager(linear_layout_manager);
        hardware_platform_recycle_view_weak_reference.get().setHasFixedSize(true);
        hardware_platform_recycle_view_weak_reference.get().setAdapter(hardware_recycle_view_adapter);

        if(hardware_recycle_view_adapter.getItemCount() == 0){

            no_hardware_available_textView_id_weak_references.get().setVisibility(View.VISIBLE);
            hardware_platform_recycle_view_weak_reference.get().setVisibility(View.INVISIBLE);
            hardware_display_section_linear_layout_weak_references.get().setBackgroundColor(Color.GRAY);

        }
        else if(hardware_recycle_view_adapter.getItemCount() > 0){

            no_hardware_available_textView_id_weak_references.get().setVisibility(View.GONE);
            hardware_platform_recycle_view_weak_reference.get().setVisibility(View.VISIBLE);
            hardware_display_section_linear_layout_weak_references.get().setBackgroundColor(Color.WHITE);

        }

        ArrayList<String> user_platforms = new ArrayList<>();
        user_platforms.add(this.context_weak_reference.get().getResources().getString(R.string.playstation_four_platform));
        user_platforms.add("Playstation 5");
        user_platforms.add(this.context_weak_reference.get().getResources().getString(R.string.xbox_one_platform));
        user_platforms.add("Xbox Series X");
        user_platforms.add(this.context_weak_reference.get().getResources().getString(R.string.nintendo_switch_platform));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.context_weak_reference.get(), R.layout.spinner_item, user_platforms);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        hardware_platform_spinner_weak_reference.get().setAdapter(adapter);
        hardware_platform_spinner_weak_reference.get().setSelection(INDEX);

        //hardware_recycle_view_adapter.set_app(app);
        //hardware_recycle_view_adapter.set_hardware_library_dialog(hardware_library_dialog);
        hardware_recycle_view_adapter.set_progress_bar(no_hardware_available_textView_id_weak_references.get(),
                hardware_display_section_linear_layout_weak_references.get(), progress_bar_weak_references.get(), add_hardware_platform_button_weak_references.get());
   //     Log.d(TAG,  this.user_interface.name() + " Index: " + INDEX + "  Default Value " + software_platform_spinner.getSelectedItem().toString());

        hardware_platform_spinner_weak_reference.get().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                current_platform = parent.getItemAtPosition(position).toString();
                hardware_platform_spinner_weak_reference.get().setSelection(position);

                if (current_platform.equals(context_weak_reference.get().getResources().getString(R.string.playstation_four_platform)))
                    current_manufacturer = context_weak_reference.get().getResources().getString(R.string.sony_manufacturer);

                if (current_platform.equals(context_weak_reference.get().getResources().getString(R.string.xbox_one_platform)))
                    current_manufacturer = context_weak_reference.get().getResources().getString(R.string.microsoft_manufacturer);

                if (current_platform.equals(context_weak_reference.get().getResources().getString(R.string.nintendo_switch_platform)))
                    current_manufacturer = context_weak_reference.get().getResources().getString(R.string.nintendo_manufacturer);

                INDEX = position;
            }

            public void onNothingSelected(AdapterView<?> parent) {

                Log.d(TAG, user_interface.name() + " Nothing has been selected");

                hardware_platform_spinner_weak_reference.get().setSelection(INDEX);
            }

        });


        add_hardware_platform_button_weak_references.get().setOnClickListener(view -> {

            /*
            if(user_platforms.contains(context.getResources().getString(R.string.no_hardware_available_label)))
                return;
*/
            Hardware hardware = new Hardware();
            hardware.platform = current_platform;
            hardware.manufacturer = current_manufacturer;


            progress_bar_weak_references.get().setVisibility(View.VISIBLE);
            add_hardware_platform_button_weak_references.get().setEnabled(false);


            for(int i = 0; i < hardwares.size(); i++) {

                    if (hardwares.get(i).platform.equals(hardware.platform)) {


                        Log.d(MSG, "At index: " + hardwares.indexOf(hardware));
                        progress_bar_weak_references.get().setVisibility(View.GONE);
                        add_hardware_platform_button_weak_references.get().setEnabled(true);
                        return;
                    }

            }

            try {

                platform_json = new JSONObject();
                platform_json.put(context_weak_reference.get().getResources().getString(R.string.category_label), 100);
                platform_json.put(context_weak_reference.get().getResources().getString(R.string.operation_label), 1);
                platform_json.put(context_weak_reference.get().getResources().getString(R.string.id_label), UserSettings.get_user_id(context_weak_reference.get()));
                platform_json.put(context_weak_reference.get().getResources().getString(R.string.uid_key), UserSettings.get_user_uid(context_weak_reference.get()));
                platform_json.put(context_weak_reference.get().getResources().getString(R.string.access_token_key), UserSettings.get_user_token(context_weak_reference.get()));
                platform_json.put(context_weak_reference.get().getResources().getString(R.string.manufacturer_label), hardware.manufacturer);
                platform_json.put(context_weak_reference.get().getResources().getString(R.string.platform_label), hardware.platform);

            } catch (JSONException json_error) {
                json_error.printStackTrace();
            }

            Volley volley = new Volley(context_weak_reference.get(), Request.Method.POST, URL.INVENTORY_URL, platform_json);
            volley.Execute(new VolleyCallback() {
                @Override
                public void network_response(JSONObject json_response) {

                    try {

                            if(json_response.has(context_weak_reference.get().getResources().getString(R.string.database_connection_error_label))){

                                String database_connection_error_message = json_response.getString(context_weak_reference.get().getResources().getString(R.string.database_connection_error_label));
                                Toast.makeText(context_weak_reference.get(), database_connection_error_message, Toast.LENGTH_LONG).show();
                                return;

                            }

                            if (json_response.has(context_weak_reference.get().getResources().getString(R.string.transaction_hardware_error_100_1))) {

                                String load_user_hardware_100_1_error_message = json_response.getString(context_weak_reference.get().getResources().getString(R.string.transaction_hardware_error_100_1));
                                Toast.makeText(context_weak_reference.get(), load_user_hardware_100_1_error_message, Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (json_response.has(context_weak_reference.get().getResources().getString(R.string.transaction_hardware_100_1_label))) {


                                    if (json_response.getBoolean(context_weak_reference.get().getResources().getString(R.string.transaction_hardware_100_1_label))) {

                                        MyHardware my_hardware = new MyHardware(hardware.manufacturer, hardware.platform);
                                        MyHardwareTask my_hardware_task = new MyHardwareTask(my_hardware, my_hardware_dao, DatabaseOperations.INSERT);
                                        my_hardware_task.execute();


                                        progress_bar_weak_references.get().setVisibility(View.GONE);
                                        add_hardware_platform_button_weak_references.get().setEnabled(true);


                                        hardware_recycle_view_adapter.add_hardware(hardware);

                                        if(hardware_recycle_view_adapter.getItemCount() == 0){

                                            no_hardware_available_textView_id_weak_references.get().setVisibility(View.VISIBLE);
                                            hardware_platform_recycle_view_weak_reference.get().setVisibility(View.INVISIBLE);
                                            hardware_display_section_linear_layout_weak_references.get().setBackgroundColor(Color.GRAY);

                                        }
                                        else if(hardware_recycle_view_adapter.getItemCount() > 0){

                                            no_hardware_available_textView_id_weak_references.get().setVisibility(View.GONE);
                                            hardware_platform_recycle_view_weak_reference.get().setVisibility(View.VISIBLE);
                                            hardware_display_section_linear_layout_weak_references.get().setBackgroundColor(Color.WHITE);

                                        }

                                        Bundle bundle = new Bundle();

                                        // Most likely newly registered
                                        if((UserSettings.get_latitude(context_weak_reference.get()).isEmpty() || UserSettings.get_longitude(context_weak_reference.get()).isEmpty()) || (UserSettings.get_latitude(context_weak_reference.get()) == null || UserSettings.get_longitude(context_weak_reference.get()) == null))
                                            return;

                                        double latitude = Double.valueOf(UserSettings.get_latitude(context_weak_reference.get()));
                                        double longitude = Double.valueOf(UserSettings.get_longitude(context_weak_reference.get()));
                                        bundle.putDouble(context_weak_reference.get().getResources().getString(R.string.latitude), latitude);
                                        bundle.putDouble(context_weak_reference.get().getResources().getString(R.string.longitude), longitude);

                                        String email = UserSettings.get_user_email(context_weak_reference.get());
                                        if (email.isEmpty() || email == null)
                                            return;

                                        Account account = new Account(email, context_weak_reference.get().getString(R.string.account_type));
                                        ContentResolver.requestSync(account, context_weak_reference.get().getString(R.string.content_authority), bundle);

                                    }


                            }

                    } catch (JSONException json_error) {
                        json_error.printStackTrace();
                    }

                }

                @Override
                public void network_error(VolleyError error) {

                    progress_bar_weak_references.get().setVisibility(View.GONE);
                    add_hardware_platform_button_weak_references.get().setEnabled(true);

                    if(error.networkResponse == null) {

                        if(error.getClass().equals(TimeoutError.class)) {
                            Toast.makeText(context_weak_reference.get(),
                                    "Timeout error occurred! Logging Out",
                                    Toast.LENGTH_LONG).show();
                           // launch_logout_activity();
                        }
                    }
                }
            });

        });


    }
/*
    private void launch_logout_activity(){

        app.set_is_user_logged_in(false);
        app.set_location_permission(false);

        UserSettings.set_is_user_logged_in(context_weak_reference.get(), false);
        UserSettings.remove_user_token(context_weak_reference.get());

        Intent intent = new Intent(context_weak_reference.get(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context_weak_reference.get().startActivity(intent);
        this.hardware_library_dialog.dismiss();

    }
*/
    private void load_user_available_hardware(ArrayList<Hardware> hardwares) {

        final String MSG = "TAG";
        Log.d(TAG, "Loading hardware: " + hardwares.size());

        if(this.context_weak_reference.get() == null)
            Log.d(TAG, "Context is null");
        else if(this.context_weak_reference.get() != null)
            Log.d(TAG, "Context is NOT null");

        ArrayList<String> user_platforms = new ArrayList<>();
        for (int i = 0; i < hardwares.size(); i++) {

            user_platforms.add(hardwares.get(i).platform);
            Log.d(MSG, "Platform: " + user_platforms.get(i));

        }

        if(this.user_interface == UserInterface.SOFTWARE_PROFILE_DIALOG) {


            INDEX = user_platforms.indexOf(software.platform);
           // Toast.makeText(context, "--> index: " + INDEX + " Platform: " + software.platform, Toast.LENGTH_LONG).show();
            Log.d(MSG, "Software_Profile_Dialog: " + "--> index: " + INDEX + " Platform: " + software.platform);
        }

     //   Toast.makeText(context, this.user_interface.name() + " <-> index: " + INDEX + " Platform: " + software.platform, Toast.LENGTH_LONG).show();
        Log.d(MSG, "Window: " + this.user_interface.name());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context_weak_reference.get(), R.layout.spinner_item, user_platforms);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        software_platform_spinner_weak_reference.get().setAdapter(adapter);
        software_platform_spinner_weak_reference.get().setSelection(INDEX);

            software_platform_spinner_weak_reference.get().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        software.platform = parent.getItemAtPosition(position).toString();
                        software_platform_spinner_weak_reference.get().setSelection(position);
                        /*
                        String platform = context_weak_reference.get().getResources().getString(R.string.user_profile_software_platform_label) + parent.getItemAtPosition(position).toString();
                        software_platform_textView_weak_reference.get().setText(platform);
*/
                }

                public void onNothingSelected(AdapterView<?> parent) {

                    Log.d(MSG, user_interface.name() + " Nothing has been selected");

                    software_platform_spinner_weak_reference.get().setSelection(INDEX);

                }

            });



    }

    @Override
    protected ArrayList<Hardware> doInBackground(Void... voids) {

        ArrayList<Hardware> user_hardware = new ArrayList<>();
        List<MyHardware> queried_hardware = this.my_hardware_dao.query_all_hardware();

        for(int i = 0; i < queried_hardware.size(); i++){

            String hardware_manufacturer = queried_hardware.get(i).get_manufacturer();
            String hardware_platform = queried_hardware.get(i).get_platform();

            Log.d(TAG, "Manufacturer: " + hardware_manufacturer + "  Platform: " + hardware_platform);
            Hardware hardware = new Hardware(hardware_manufacturer, hardware_platform);
            user_hardware.add(hardware);
        }

        if(user_hardware.isEmpty())
            Log.d(TAG, "LoadUserHardTask: ArrayList<Hardware> is empty");
        else
            Log.d(TAG, "LoadUserHardTask: ArrayList<Hardware> is NOT empty");

        return user_hardware;
    }

    @Override
    protected void onPostExecute(ArrayList<Hardware> hardwares) {
        super.onPostExecute(hardwares);

        if(this.user_interface == UserInterface.HARDWARE_INVENTORY_ACTIVITY){

            Log.d(TAG, "USER INTERFACE: HardwareInventoryActivity");
            load_hardware_platform(hardwares);
        }

        if(is_beginner){

            launch_software_activity(hardwares);
        }

        if(this.user_interface == UserInterface.INVENTORY_IN_FRAGMENT  || this.user_interface == UserInterface.SOFTWARE_PROFILE_DIALOG){

            Log.d(TAG, "USER INTERFACE: InventoryInFragment " + hardwares.size());
            load_user_available_hardware(hardwares);

        }


    }
}
