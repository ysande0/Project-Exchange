package com.syncadapters.czar.exchange.repositories;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordDialogRepository {

    private static final String TAG = "MSG";
    private static ForgotPasswordDialogRepository forgot_password_dialog_repository;

    public static ForgotPasswordDialogRepository getInstance(){

        if(forgot_password_dialog_repository == null)
            forgot_password_dialog_repository = new ForgotPasswordDialogRepository();

        return forgot_password_dialog_repository;
    }

    public void send_reset_password_email(FirebaseAuth firebase_auth, String email){

        firebase_auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {

            if(task.isSuccessful()){

                Log.d(TAG, "[ForgotPasswordDialogRepository] Email has been sent");

            }
            else if(!(task.isSuccessful())){

                Log.d(TAG, "[ForgotPasswordDialogRepository] Could not send email");

            }

        });

    }
}
