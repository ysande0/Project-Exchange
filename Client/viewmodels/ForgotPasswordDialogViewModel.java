package com.syncadapters.czar.exchange.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.syncadapters.czar.exchange.repositories.ForgotPasswordDialogRepository;

public class ForgotPasswordDialogViewModel extends AndroidViewModel {

    private static ForgotPasswordDialogRepository forgot_password_dialog_repository;

    public ForgotPasswordDialogViewModel(Application application){
        super(application);

        if(forgot_password_dialog_repository == null)
            forgot_password_dialog_repository = ForgotPasswordDialogRepository.getInstance();

    }


    public void send_reset_password_email(FirebaseAuth firebase_auth, String email){

        forgot_password_dialog_repository.send_reset_password_email(firebase_auth, email);

    }

}
