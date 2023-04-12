package com.syncadapters.czar.exchange.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.syncadapters.czar.exchange.datamodels.Users;
import com.syncadapters.czar.exchange.repositories.UserRepository;

import java.util.ArrayList;

public class HomeFragmentViewModel extends AndroidViewModel {

    private static final String TAG = "MSG";
    private UserRepository user_repository;
    @SuppressWarnings("CanBeFinal")
    private Application application;

    @SuppressWarnings({"UnusedAssignment", "RedundantSuppression", "FieldCanBeLocal"})
    private MutableLiveData<ArrayList<Users>> users = new MutableLiveData<>();


    public HomeFragmentViewModel(Application application){
        super(application);

        //noinspection ConstantConditions
        if(user_repository == null) {
            Log.d(TAG, "HomeFragmentViewModel Repository is  null");
            user_repository = UserRepository.getInstance();
        }

        this.application = application;
/*
        user_repository.initialize_database(application);
        users = user_repository.get_users_data();

 */
    }

    public MutableLiveData<ArrayList<Users>> getUsers(){

        Log.d(TAG, "[HomeFragment] returning users (getUsers)");
        user_repository.initialize_database(application);
        users = user_repository.get_users_data();
        return users;
    }


}
