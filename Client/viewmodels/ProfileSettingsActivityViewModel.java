package com.syncadapters.czar.exchange.viewmodels;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ProgressBar;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.repositories.ProfileSettingsRepository;

public class ProfileSettingsActivityViewModel extends AndroidViewModel {

    private static ProfileSettingsRepository profile_settings_repository;
    private final MutableLiveData<Bitmap> bitmap_live_data = new MutableLiveData<>();

    public ProfileSettingsActivityViewModel(Application application){
        super(application);

        if(profile_settings_repository == null)
            profile_settings_repository = ProfileSettingsRepository.getInstance();

    }

    public void set_bitmap_live_data(Bitmap bitmap){

        bitmap_live_data.setValue(bitmap);
    }

    public Bitmap get_bitmap_live_data(){

        return bitmap_live_data.getValue();
    }

    public void remote_server(App app, Context context, ProgressBar progress_bar, String first_name, String last_name, String profile_name_thumbnail, String profile_name_full, String encoded_bitmap_thumbnail,
                              String encoded_bitmap_full, String uid, String id, boolean isBeginner, int dpi_classification){

        profile_settings_repository.remote_server(app, context, progress_bar, first_name, last_name, profile_name_thumbnail,
                profile_name_full, encoded_bitmap_thumbnail, encoded_bitmap_full,
                uid, id, isBeginner, dpi_classification);
    }

    public void logout(App app, Context context){

        profile_settings_repository.logout(app, context);
    }

}
