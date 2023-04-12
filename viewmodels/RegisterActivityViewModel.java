package com.syncadapters.czar.exchange.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.ProgressBar;

import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.syncadapters.czar.exchange.dialogs.RegisterDialog;
import com.syncadapters.czar.exchange.repositories.RegisterActivityRepository;

import java.lang.ref.WeakReference;

public class RegisterActivityViewModel extends AndroidViewModel {

    private static RegisterActivityRepository register_activity_repository;

    public RegisterActivityViewModel(Application application){
        super(application);

        if(register_activity_repository == null)
            register_activity_repository = RegisterActivityRepository.getInstance();


    }

    public void set_context(Context context){

        WeakReference<Context> context_weak_reference = new WeakReference<>(context);
        register_activity_repository.set_context(context_weak_reference.get());
    }

    public void set_firebase_auth(FirebaseAuth firebase_auth){

        register_activity_repository.set_firebase_auth(firebase_auth);
    }

    public void set_register_progress_bar(ProgressBar register_progress_bar){

        register_activity_repository.set_progress_bar(register_progress_bar);
    }

    public void account_creation(Activity activity, RegisterDialog register_dialog, String email, String date_of_birth_ms, String password, String first_name
            , String last_name){

        register_activity_repository.account_creation(activity, register_dialog, email, date_of_birth_ms, password, first_name, last_name);
    }

}
