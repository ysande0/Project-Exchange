package com.syncadapters.czar.exchange.viewmodels;

import android.app.Application;
import android.widget.Spinner;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.syncadapters.czar.exchange.datamodels.Software;
import com.syncadapters.czar.exchange.dialogs.SoftwareProfileDialog;
import com.syncadapters.czar.exchange.repositories.SoftwareProfileDialogRepository;

import org.json.JSONObject;

import java.util.Objects;

public class SoftwareProfileDialogViewModel extends AndroidViewModel {

    private static SoftwareProfileDialogRepository software_profile_dialog_repository;

    private final MutableLiveData<Software> software_live_data = new MutableLiveData<>();
    private final MutableLiveData<Integer> position_live_data = new MutableLiveData<>();

    public SoftwareProfileDialogViewModel(Application application){
        super(application);

        if(software_profile_dialog_repository == null)
            software_profile_dialog_repository = SoftwareProfileDialogRepository.getInstance();

        software_profile_dialog_repository.initialize_database(application);
        software_profile_dialog_repository.set_context(application);
    }


    public void load_hardware(Spinner software_platform_spinner, Software software){

        software_profile_dialog_repository.load_hardware(software_platform_spinner, software);
    }

    public void set_software(Software software){

        software_live_data.setValue(software);
    }

    public void set_position(int position){

        position_live_data.setValue(position);
    }

    public Software get_software(){

        return software_live_data.getValue();
    }

    public int get_position(){

        return Objects.requireNonNull(position_live_data.getValue());
    }

    public void update_software(Software software, JSONObject software_json, SoftwareProfileDialog software_profile_dialog, FragmentManager fragment_manager, int position){

        software_profile_dialog_repository.remote_server(software, software_json, software_profile_dialog, fragment_manager,  position);
    }

    public void delete_software(Software software, JSONObject software_json, SoftwareProfileDialog software_profile_dialog, FragmentManager fragment_manager, int position){

        software_profile_dialog_repository.remote_server(software, software_json, software_profile_dialog, fragment_manager, position);

    }

}
