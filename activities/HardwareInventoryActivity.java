package com.syncadapters.czar.exchange.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.viewmodels.HardwareInventoryViewModel;

import org.jetbrains.annotations.NotNull;

public class HardwareInventoryActivity extends AppCompatActivity {


    private static final String TAG = "MSG";
    private RecyclerView hardware_platform_recycle_view;
    private int INDEX  =  0;
    private boolean is_beginner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware_inventory);

        // Read database. If null then get input. If Non null, then set switch to true
        Log.d(TAG, "HardwareInventoryActivity onCreate");

        // Query database items
        // Load database items in recycle view
        // Remove any items in database that are already in database

        LinearLayout hardware_display_section_linear_layout = findViewById(R.id.hardware_display_section_linear_layout_id);
        hardware_platform_recycle_view = findViewById(R.id.hardware_platform_recycleView_id);
        ProgressBar progress_bar = findViewById(R.id.hardware_progress_bar_circular_id);
        progress_bar.setVisibility(View.GONE);
        FloatingActionButton continue_action_button = findViewById(R.id.hardware_continue_float_button_id);
        Spinner hardware_platform_spinner = findViewById(R.id.hardware_platform_spinner_id);
        ImageButton add_hardware_platform_button = findViewById(R.id.add_hardware_platform_button_id);
        TextView no_hardware_available_textView = findViewById(R.id.no_hardware_available_textView_id);


        //noinspection ConstantConditions
        if(getIntent().getExtras().getBoolean(getResources().getString(R.string.is_beginner_key))) {
            Log.d(TAG, "HardwareActivity: is Beginner ");
            is_beginner = true;

        }

        ActionBar action_bar = getSupportActionBar();
        assert action_bar != null;
        action_bar.setDisplayHomeAsUpEnabled(true);


        // Get remote platforms
        if(savedInstanceState != null){

            INDEX = savedInstanceState.getInt("INDEX");

        }

        continue_action_button.setOnClickListener(v -> alert_dialog_box());
       // HardwareInventoryViewModel hardware_inventory_view_model = ViewModelProviders.of(this).get(HardwareInventoryViewModel.class);
        HardwareInventoryViewModel hardware_inventory_view_model = new ViewModelProvider(this).get(HardwareInventoryViewModel.class);
        hardware_inventory_view_model.query_hardware(HardwareInventoryActivity.this, hardware_platform_recycle_view, hardware_platform_spinner,
                add_hardware_platform_button, progress_bar, hardware_display_section_linear_layout, no_hardware_available_textView, continue_action_button);

    }

    private void alert_dialog_box(){

        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(HardwareInventoryActivity.this);
        alert_dialog_builder.setMessage("Are you sure you want to continue?");
        alert_dialog_builder.setPositiveButton("Yes", (dialog, which) -> {

            Intent intent = new Intent(HardwareInventoryActivity.this, SoftwareInventoryActivity.class);
            intent.putExtra(getResources().getString(R.string.is_beginner_key), is_beginner );
            startActivity(intent);
        });

        alert_dialog_builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alert_dialog = alert_dialog_builder.create();
        alert_dialog.show();

    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menu_inflater = getMenuInflater();
        menu_inflater.inflate(R.menu.hardware_inventory_activity_menu_items, menu);
        return true;

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.profile_settings_item_id:
                launch_profile_settings_activity();
                return true;

            case R.id.profile_logout_item_id:
                launch_profile_logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void launch_profile_settings_activity(){

        Intent intent = new Intent(HardwareInventoryActivity.this, ProfileSettingsActivity.class);
        startActivity(intent);

    }

    private void launch_profile_logout(){

       hardware_inventory_view_model.logout(app, HardwareInventoryActivity.this);

    }
*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HardwareInventoryActivity onDestroy");

        hardware_platform_recycle_view.setAdapter(null);

    }

    public void onSaveInstanceState(@NotNull Bundle out_state){

        super.onSaveInstanceState(out_state);
        out_state.putInt("index", INDEX);
    }

}
