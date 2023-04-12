package com.syncadapters.czar.exchange.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.repositories.HomeActivityRepository;

import org.json.JSONObject;

public class HomeActivityViewModel extends AndroidViewModel {

    private static HomeActivityRepository home_activity_repository;

    public HomeActivityViewModel(Application application){
        super(application);

        if(home_activity_repository == null)
            home_activity_repository = HomeActivityRepository.getInstance();

        home_activity_repository.initialize_database(application);
    }

    public void logout(App app, Context context){

        home_activity_repository.logout(app, context);

    }

    public void update_location_remote(Context context, JSONObject json_object){

        home_activity_repository.update_location_resource(context, json_object);
    }


    public void load_notification_badges(App app, Context context, BottomNavigationView bottom_navigation,
                                         UserInterface user_interface, boolean is_home_activity_foreground){

        home_activity_repository.set_notification_badges(app, context, bottom_navigation, user_interface, is_home_activity_foreground);
    }

}
