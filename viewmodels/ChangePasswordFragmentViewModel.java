package com.syncadapters.czar.exchange.viewmodels;

import android.app.Application;
import android.content.Context;
import android.widget.ProgressBar;

import androidx.lifecycle.AndroidViewModel;

import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.dialogs.ChangePasswordDialog;
import com.syncadapters.czar.exchange.repositories.ChangePasswordRepository;

public class ChangePasswordFragmentViewModel extends AndroidViewModel {

    private final ChangePasswordRepository change_password_repository;

    public ChangePasswordFragmentViewModel(Application application){
        super(application);

        change_password_repository = ChangePasswordRepository.getInstance();

    }

    public void remote_server(Context context, ChangePasswordDialog change_password_dialog, ProgressBar progress_bar, String password){

        change_password_repository.remote_server(context, change_password_dialog, progress_bar, password);
    }

    public void logout(App app, Context context){

        change_password_repository.logout(app, context);
    }
}
