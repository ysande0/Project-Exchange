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

import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.dialogs.HardwareLibraryDialog;
import com.syncadapters.czar.exchange.repositories.HardwareLibraryDialogRepository;

public class HardwareLibraryDialogViewModel extends AndroidViewModel {

    private static final String TAG = "MSG";
    private HardwareLibraryDialogRepository hardware_library_dialog_repository;

    public HardwareLibraryDialogViewModel(Application application){
        super(application);

        Log.d(TAG, "HardwareLibraryDialogViewModel Constructor");

        if(hardware_library_dialog_repository == null)
            hardware_library_dialog_repository = HardwareLibraryDialogRepository.getInstance();

        hardware_library_dialog_repository.initialize_database(application);
    }

    public void query_hardware(App app, Context context, HardwareLibraryDialog hardware_library_dialog, RecyclerView hardware_platform_recycle_view, Spinner hardware_platform_spinner,
                               ImageButton add_hardware_platform_button, ProgressBar progress_bar, LinearLayout hardware_display_section_linear_layout,
                               TextView no_hardware_available_textView_id){


        hardware_library_dialog_repository.query_hardware(app, context, hardware_library_dialog, hardware_platform_recycle_view,
                hardware_platform_spinner, add_hardware_platform_button, progress_bar,
                hardware_display_section_linear_layout, no_hardware_available_textView_id);

    }

    @Override
    protected void onCleared() {
        super.onCleared();

        Log.d(TAG, "HardwareLibraryDialogViewModel Destroyed");

    }

}
