package com.syncadapters.czar.exchange.viewmodels;

import android.app.Application;
import android.content.Context;
import android.widget.ProgressBar;
import androidx.lifecycle.AndroidViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.repositories.LoginActivityRepository;

import java.lang.ref.WeakReference;

public class LoginActivityViewModel extends AndroidViewModel {

    private static LoginActivityRepository login_activity_repository;

    public LoginActivityViewModel(Application application){
        super(application);

        if(login_activity_repository == null)
            login_activity_repository = LoginActivityRepository.getInstance();


        login_activity_repository.initialize_database(application);
    }

    public void set_app(App app){

        login_activity_repository.set_app(app);
    }

    public void set_context(Context context){

        WeakReference<Context> context_weak_reference = new WeakReference<>(context);
        login_activity_repository.set_context(context_weak_reference.get());
    }

    public void set_login_progress_bar(ProgressBar login_progress_bar){

        login_activity_repository.set_login_progress_bar(login_progress_bar);
    }

    public void set_firebase_auth(FirebaseAuth firebase_auth){

        login_activity_repository.set_firebase_auth(firebase_auth);
    }

    public void account_sign_in(String email, String password){

        login_activity_repository.account_sign_in(email, password);
    }


}
