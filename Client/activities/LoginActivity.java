package com.syncadapters.czar.exchange.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.dialogs.ForgotPasswordDialog;
import com.syncadapters.czar.exchange.dialogs.RegisterDialog;

import com.syncadapters.czar.exchange.viewmodels.LoginActivityViewModel;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MSG";
    private static final int REQUESTED_LOCATION_PERMISSION = 100;
    private EditText Email_editText;
    private EditText Password_editText;
    private ProgressBar login_progress_bar;

    private String email_address;
    private String password;

    private App app;
    private FragmentManager fragment_manager;
    private LoginActivityViewModel login_activity_view_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //noinspection ConstantConditions
        getSupportActionBar().hide();
       // login_activity_view_model = ViewModelProviders.of(this).get(LoginActivityViewModel.class);
        login_activity_view_model = new ViewModelProvider(this).get(LoginActivityViewModel.class);
        login_activity_view_model.set_context(LoginActivity.this);

        FirebaseAuth firebase_auth = FirebaseAuth.getInstance();
        app = ((App) getApplicationContext());
        app.is_google_play_services_available(LoginActivity.this);

        login_activity_view_model.set_app(app);
        login_activity_view_model.set_firebase_auth(firebase_auth);


       //  launch_home_activity();

        check_permission();
        Log.d(TAG,"Login Activity onCreate");

        Email_editText = findViewById(R.id.email_editText_id);

        login_progress_bar = findViewById(R.id.login_progress_bar_circular_id);
        login_progress_bar.setVisibility(View.GONE);
        login_activity_view_model.set_login_progress_bar(login_progress_bar);

        Password_editText = findViewById(R.id.password_editText_id);
        Button login_button = findViewById(R.id.login_button_id);
        login_button.setOnClickListener(v -> {

            boolean flag_error = false;
            email_address = Email_editText.getText().toString().trim();
            password = Password_editText.getText().toString();


            if(email_address.isEmpty()){

                Email_editText.setError(getResources().getString(R.string.login_email_address_edittext_error));
                flag_error = true;
            }

            if(password.isEmpty()){

                Password_editText.setError(getResources().getString(R.string.login_password_edittext_error));
                flag_error = true;
            }

            if(flag_error)
                return;

            login_progress_bar.setVisibility(View.VISIBLE);
            login_activity_view_model.account_sign_in(email_address, password);

        });


        Button register_button = findViewById(R.id.register_button_id);
        register_button.setOnClickListener(v -> {


            fragment_manager = getSupportFragmentManager();
            RegisterDialog register_dialog = new RegisterDialog();
            register_dialog.show(fragment_manager, "Register_Dialog");


        });

        TextView forgot_password_textView = findViewById(R.id.forgot_password_textView_id);
        forgot_password_textView.setOnClickListener(v -> {

            fragment_manager = getSupportFragmentManager();
            ForgotPasswordDialog forgot_password_dialog = new ForgotPasswordDialog();
            forgot_password_dialog.show(fragment_manager, "Forgot_Password_Dialog");


        });


    }


    private void check_permission(){

        if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            Log.d(TAG, "Getting user location permission");
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUESTED_LOCATION_PERMISSION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String [] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_DENIED) {

            Log.d(TAG, "LoginActivity Permission Denied");
            app.set_location_permission(false);
            location_tracking_permission();

        }
        else if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

            Log.d(TAG, "LoginActivity Permission Granted");
            app.set_location_permission(true);
        }

    }

    private void location_tracking_permission(){

        AlertDialog.Builder location_tracking_alert_message = new AlertDialog.Builder(LoginActivity.this);
        location_tracking_alert_message.setMessage(getResources().getString(R.string.location_permission_alert_message_label));
        location_tracking_alert_message.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog location_tracking_alert_dialog = location_tracking_alert_message.create();
        location_tracking_alert_dialog.show();

    }

    @SuppressWarnings("unused")
    private void launch_home_activity(){


        if(UserSettings.has(LoginActivity.this, getResources().getString(R.string.is_user_logged_in))){

            boolean is_user_logged_in = UserSettings.get_is_user_logged_in(LoginActivity.this);
            if(is_user_logged_in){

                //noinspection ConstantConditions
                app.set_is_user_logged_in(is_user_logged_in);
                app.set_location_permission(true);
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }


        }else {

            UserSettings.set_is_user_logged_in(LoginActivity.this, false);
        }



    }
    protected void onStart(){
        super.onStart();

        Log.d(TAG,"Login Activity onStart");

    }

    protected void onSaveInstanceState(@NotNull Bundle out_state){
        super.onSaveInstanceState(out_state);

        out_state.putString("email", Email_editText.getText().toString());
        out_state.putString("password", Password_editText.getText().toString());

    }

 protected void onRestoreInstanceState(Bundle saveInstanceState){
        super.onRestoreInstanceState(saveInstanceState);

        Email_editText.setText(saveInstanceState.getString("email"));
        Password_editText.setText(saveInstanceState.getString("password"));

 }


    protected void onResume(){
        super.onResume();

        Log.d(TAG,"Login Activity onResume");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    protected  void onPause(){
        super.onPause();
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        Log.d(TAG,"Login Activity onPause");

    }

    protected void onStop(){
        super.onStop();

        Log.d(TAG,"Login Activity onStop");

    }

    protected void onRestart(){
        super.onRestart();

        Log.d(TAG,"Login Activity onRestart");

    }

    protected void onDestroy(){
        super.onDestroy();

        Log.d(TAG,"Login Activity onDestroy");

    }

}
