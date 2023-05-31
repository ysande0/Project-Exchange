package com.syncadapters.czar.exchange.repositories;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.dialogs.RegisterDialog;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class RegisterActivityRepository {

    private static final String TAG = "MSG";
    private static RegisterActivityRepository register_activity_repository;
    private WeakReference<Context> context_weak_reference;
    private FirebaseAuth firebase_auth;

    private String first_name;
    private String last_name;
    private String date_of_birth_ms;
    private String email;
    private String password;
    private String fcm_token;
    private String uid;
    private Volley volley;
    private boolean Account_Status = false;
    private WeakReference<ProgressBar> register_progress_bar_weak_reference;
    private WeakReference<RegisterDialog> register_dialog_weak_reference;
    private WeakReference<Activity> activity_weak_reference;

    public static RegisterActivityRepository getInstance(){

        if(register_activity_repository == null)
            register_activity_repository = new RegisterActivityRepository();


        return register_activity_repository;
    }

    public void set_context(Context context){

        this.context_weak_reference = new WeakReference<>(context);
    }

    public void set_progress_bar(ProgressBar register_progress_bar){

        this.register_progress_bar_weak_reference = new WeakReference<>(register_progress_bar);
    }

    public void set_firebase_auth(FirebaseAuth firebase_auth){

        this.firebase_auth = firebase_auth;
    }

    public void account_creation(Activity activity, RegisterDialog register_dialog, String email, String date_of_birth_ms, String password, String first_name
    , String last_name){

        this.activity_weak_reference = new WeakReference<>(activity);
        this.register_dialog_weak_reference =  new WeakReference<>(register_dialog);
        this.email = email;
        this.date_of_birth_ms = date_of_birth_ms;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;

        firebase_auth.createUserWithEmailAndPassword(this.email, context_weak_reference.get().getResources().getString(R.string.firebase_passcode))
                .addOnCompleteListener(this.activity_weak_reference.get(), task -> {

                    if(task.isSuccessful()){

                        Log.d(TAG, "RegisterActivity: Firebase Account Created");

                        /*
                        UserProfileChangeRequest user_profile_change_request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(first_name + " " + last_name).build();

                        // Delete this to improve performance
                        final FirebaseUser firebase_user = FirebaseAuth.getInstance().getCurrentUser();
                        firebase_user.updateProfile(user_profile_change_request);
*/
                        //send_verification_email();
                        remote_server();
                    }
                    else if(!(task.isSuccessful())){

                        register_progress_bar_weak_reference.get().setVisibility(View.GONE);
                        register_dialog_weak_reference.get().dismiss();
                        Log.d(TAG, "RegisterActivity: Firebase NOT Account Created");
                        Toast.makeText(context_weak_reference.get(), context_weak_reference.get().getResources().getString(R.string.register_error_firebase_account_not_created), Toast.LENGTH_LONG).show();
                    }

                });

    }

    private void send_verification_email(){

        final FirebaseUser firebase_user = firebase_auth.getCurrentUser();

        if(firebase_user == null){
            Log.d(TAG, "User does not exist");
            return;
        }

        firebase_user.sendEmailVerification().addOnCompleteListener(this.activity_weak_reference.get(), task -> {

            if(task.isSuccessful()){

                Log.d(TAG, "[RegisterActivity] Email was sent");

            }
            else if(!(task.isSuccessful())){

                Log.d(TAG, "Email was not sent");
                register_dialog_weak_reference.get().dismiss();
                Toast.makeText(context_weak_reference.get(), context_weak_reference.get().getResources().getString(R.string.register_error_firebase_email_verification_failure), Toast.LENGTH_LONG).show();
            }

        });

    }

    private void remote_server(){

        FirebaseApp.initializeApp(this.context_weak_reference.get());
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                Log.d(TAG, "Token Generation Error! Could not generate FCM token");
                Toast.makeText(context_weak_reference.get(), context_weak_reference.get().getResources().getString(R.string.register_error_firebase_token_generation), Toast.LENGTH_LONG).show();
                return;

            }

            fcm_token = task.getResult().getToken();

            final FirebaseUser firebase_user = firebase_auth.getCurrentUser();
            assert firebase_user != null;
            uid = firebase_user.getUid();


            JSONObject json_object = new JSONObject();
            try {
                json_object.put("first_name", first_name);
                json_object.put("last_name", last_name);
                json_object.put("date_of_birth_ms", date_of_birth_ms);
                json_object.put("email", email);
                json_object.put("password", password);
                json_object.put("fcm_token", fcm_token);
                json_object.put("uid", uid);

            } catch (JSONException json_error) {

                json_error.printStackTrace();
            }

            volley = new Volley(context_weak_reference.get(), Request.Method.POST, URL.REGISTER_URL, json_object);
            volley.set_priority(Request.Priority.IMMEDIATE);
            volley.Execute(new VolleyCallback() {
                @Override
                public void network_response(JSONObject onResponse) {

                    Log.d(TAG, "Executing UI..." + onResponse.toString());
                    register_progress_bar_weak_reference.get().setVisibility(View.GONE);
                    try{

                        if (onResponse.has(context_weak_reference.get().getResources().getString(R.string.register_error_100_label))) {


                            String register_100_error_message = onResponse.getString(context_weak_reference.get().getResources().getString(R.string.register_error_100_label));
                            Toast.makeText(context_weak_reference.get(), register_100_error_message, Toast.LENGTH_LONG).show();
                            register_dialog_weak_reference.get().dismiss();
                            return;
                        }
                        else if(onResponse.has(context_weak_reference.get().getResources().getString(R.string.register_error_101_label))){

                            String register_101_error_message = onResponse.getString(context_weak_reference.get().getResources().getString(R.string.register_error_101_label));
                            Toast.makeText(context_weak_reference.get(), register_101_error_message, Toast.LENGTH_LONG).show();
                            register_dialog_weak_reference.get().dismiss();
                            return;

                        }
                        else if(onResponse.has(context_weak_reference.get().getResources().getString(R.string.register_error_102_label))){

                            String register_102_error_message = onResponse.getString(context_weak_reference.get().getResources().getString(R.string.register_error_102_label));
                            Toast.makeText(context_weak_reference.get(), register_102_error_message, Toast.LENGTH_LONG).show();
                            register_dialog_weak_reference.get().dismiss();
                            return;
                        }
                        else if(onResponse.has(context_weak_reference.get().getResources().getString(R.string.register_error_103_label))){


                            String register_103_error_message = onResponse.getString(context_weak_reference.get().getResources().getString(R.string.register_error_103_label));
                            Toast.makeText(context_weak_reference.get(), register_103_error_message, Toast.LENGTH_LONG).show();
                            register_dialog_weak_reference.get().dismiss();
                            return;

                        }
                        else if(onResponse.has(context_weak_reference.get().getResources().getString(R.string.register_error_104_label))){

                            String register_104_error_message = onResponse.getString(context_weak_reference.get().getResources().getString(R.string.register_error_104_label));
                            Toast.makeText(context_weak_reference.get(), register_104_error_message, Toast.LENGTH_LONG).show();
                            register_dialog_weak_reference.get().dismiss();
                            return;

                        }
                        else if(onResponse.has(context_weak_reference.get().getResources().getString(R.string.register_error_105_label))){

                            String register_105_error_message = onResponse.getString(context_weak_reference.get().getResources().getString(R.string.register_error_105_label));
                            Toast.makeText(context_weak_reference.get(), register_105_error_message, Toast.LENGTH_LONG).show();
                            register_dialog_weak_reference.get().dismiss();
                            return;

                        }

                        Account_Status = onResponse.getBoolean("account_created");

                        if (Account_Status) {

                            Log.d(TAG, "Launching LoginActivity");
                            send_verification_email();
                            register_dialog_weak_reference.get().dismiss();
                            /*
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                            ((RegisterActivity) context).finish();
                             */

                        } else {

                            Log.d(TAG, "Account Confirmation was not made");
                            Toast.makeText(context_weak_reference.get(), "Error! Account not created", Toast.LENGTH_LONG).show();
                        }
                    } catch(JSONException json_error){
                        json_error.printStackTrace();
                    }

                }

                @Override
                public void network_error(VolleyError error) {

                    register_dialog_weak_reference.get().dismiss();
                    register_progress_bar_weak_reference.get().setVisibility(View.GONE);

                    Toast.makeText(context_weak_reference.get(),
                            context_weak_reference.get().getResources().getString(R.string.network_connection_error_label),
                            Toast.LENGTH_LONG).show();

                    Toast.makeText(context_weak_reference.get(),
                            error.getMessage(),
                            Toast.LENGTH_LONG).show();

                    delete_firebase_account();
                   // remote_server();

                }


            });

        });

    }

    public void delete_firebase_account(){

        firebase_auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {

                if(task.isSuccessful()){
                    Log.d(TAG, "[RegisterDialog] deleting firebase account");
                }
                else{
                    Log.d(TAG, "[RegisterDialog] deletion unsuccessful");
                }

            }
        });
    }

}
