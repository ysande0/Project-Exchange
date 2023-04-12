package com.syncadapters.czar.exchange.viewmodels;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Spinner;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.syncadapters.czar.exchange.datamodels.Software;
import com.syncadapters.czar.exchange.repositories.InventoryInFragmentRepository;

import org.json.JSONObject;

public class InventoryInFragmentViewModel extends AndroidViewModel {

    private final InventoryInFragmentRepository inventory_in_fragment_repository;
    private final MutableLiveData<Bitmap> bitmap_live_data = new MutableLiveData<>();

    public InventoryInFragmentViewModel(Application application){
        super(application);

        inventory_in_fragment_repository = InventoryInFragmentRepository.getInstance();

       inventory_in_fragment_repository.initialize_database(application);
       inventory_in_fragment_repository.set_context(application);

    }

    public void insert_software(Software software, JSONObject json_object){

      //  inventory_in_fragment_repository.set_image_saver(image_saver);
        inventory_in_fragment_repository.remote_server(software, json_object);

    }

    public void set_bitmap_live_data(Bitmap bitmap){

        bitmap_live_data.setValue(bitmap);
    }

    public Bitmap get_bitmap_live_data(){
        return bitmap_live_data.getValue();
    }
    public void query_hardware(Context context, Spinner hardware_platform_spinner, Software software){

        inventory_in_fragment_repository.query_hardware(context, hardware_platform_spinner, software);

    }


}
