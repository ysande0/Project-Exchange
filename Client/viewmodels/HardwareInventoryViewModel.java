package com.syncadapters.czar.exchange.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.repositories.HardwareInventoryRepository;

public class HardwareInventoryViewModel extends AndroidViewModel {

    private static final String TAG = "MSG";

    private HardwareInventoryRepository hardware_inventory_repository;

    public HardwareInventoryViewModel(Application application){
        super(application);

        Log.d(TAG, "HardwareInventoryViewModel Constructor");

        if(hardware_inventory_repository == null)
            hardware_inventory_repository = HardwareInventoryRepository.getInstance();

        hardware_inventory_repository.initialize_database(application);

    }

    public void query_hardware(Context context, RecyclerView hardware_platform_recycle_view, Spinner hardware_platform_spinner,
                               ImageButton add_hardware_platform_button, ProgressBar progress_bar, LinearLayout hardware_display_section_linear_layout,
                               TextView no_hardware_available_textView_id, FloatingActionButton continue_action_button){


        hardware_inventory_repository.query_hardware(context, hardware_platform_recycle_view,
                hardware_platform_spinner, add_hardware_platform_button, progress_bar, hardware_display_section_linear_layout,
                no_hardware_available_textView_id, continue_action_button);

    }

    @SuppressWarnings("unused")
    public void logout(App app, Context context){

        hardware_inventory_repository.logout(app, context);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        Log.d(TAG, "HardwareInventoryViewModel Destroyed");

    }
}
