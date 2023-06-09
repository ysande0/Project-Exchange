package com.syncadapters.czar.exchange.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;

import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.datamodels.ConversationEntry;
import com.syncadapters.czar.exchange.datamodels.Users;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.repositories.HomeUserSoftwareProfileRepository;

public class HomeUserSoftwareProfileActivityViewModel extends AndroidViewModel {

    private static HomeUserSoftwareProfileRepository home_user_software_profile_repository;

    public HomeUserSoftwareProfileActivityViewModel(Application application){
        super(application);

        if(home_user_software_profile_repository == null)
            home_user_software_profile_repository = HomeUserSoftwareProfileRepository.getInstance();

        home_user_software_profile_repository.initialize_database(application);
    }

    public void logout(App app, Context context){

        home_user_software_profile_repository.logout(app, context);

    }


    public void load_conversations(Context context, Users user, ConversationEntry conversation_entry, UserInterface user_interface){

        home_user_software_profile_repository.load_conversations(context, user, conversation_entry, user_interface);
    }

}
