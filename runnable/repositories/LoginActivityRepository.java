package com.syncadapters.czar.exchange.repositories;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.LoginActivity;
import com.syncadapters.czar.exchange.activities.ProfileSettingsActivity;
import com.syncadapters.czar.exchange.asynctasks.HomeTask;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datautility.FORMATS;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;
import com.syncadapters.czar.exchange.roomdatabase.ExchangeDatabase;
import com.syncadapters.czar.exchange.roomdatabase.Home;
import com.syncadapters.czar.exchange.roomdatabase.HomeDao;
import com.syncadapters.czar.exchange.roomdatabase.MyHardwareDao;
import com.syncadapters.czar.exchange.roomdatabase.MySoftwareDao;
import com.syncadapters.czar.exchange.runnable.ReloadLibrariesRunnable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;

public class LoginActivityRepository {


    private static final String TAG = "MSG";
    private static LoginActivityRepository login_activity_repository;
    private App app;
    private FirebaseAuth firebase_auth;
    private WeakReference<Context> context_weak_reference;
    private WeakReference<ProgressBar> login_progress_bar_weak_reference;

    private boolean Login_Status;

    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String auth_token;
    private String fcm_token;
    private String uid;
    private String profile_image_name_thumbnail;
    private String profile_image_name_full;
    private String user_id;

    private HomeDao home_dao;
    private MyHardwareDao my_hardware_dao;
    private MySoftwareDao my_software_dao;


    public static LoginActivityRepository getInstance(){

        if(login_activity_repository == null)
            login_activity_repository = new LoginActivityRepository();

        return login_activity_repository;
    }

    public void initialize_database(Application application){

        ExchangeDatabase exchange_database = ExchangeDatabase.get_database(application);
        my_hardware_dao = exchange_database.my_hardware_dao();
        my_software_dao = exchange_database.my_software_dao();
        this.home_dao = exchange_database.home_dao();

    }

    public void set_context(Context context){

        this.context_weak_reference = new WeakReference<>(context);
    }

    public void set_app(App app){

        this.app = app;
    }

    @SuppressWarnings("unused")
    public void set_login_progress_bar(ProgressBar login_progress_bar){

        this.login_progress_bar_weak_reference = new WeakReference<>(login_progress_bar);
    }

    public void set_firebase_auth(FirebaseAuth firebase_auth){

        this.firebase_auth = firebase_auth;
    }

    public void account_sign_in(String email, String password){

        this.email = email;
        this.password = password;

        Log.d(TAG, "Proceeding to log-in user...." + email + "  " + password);

        if(UserSettings.get_user_email(this.context_weak_reference.get()).equals(this.email) || UserSettings.get_user_email(this.context_weak_reference.get()).isEmpty()) {

            Toast.makeText(this.context_weak_reference.get(), "This is your device. Proceeding...",  Toast.LENGTH_LONG).show();


            firebase_auth.signInWithEmailAndPassword(this.email, context_weak_reference.get().getResources().getString(R.string.firebase_passcode))
                    .addOnCompleteListener((LoginActivity) context_weak_reference.get(), task -> {

                        if(task.isSuccessful()){

                            Log.d(TAG, "LoginActivity: Logging in Manaphest");
                            retrieve_user_token();
                        }
                        else{

                            login_progress_bar_weak_reference.get().setVisibility(View.GONE);
                            Toast.makeText(context_weak_reference.get(), "invalid firebase credentials", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "LoginActivity: invalid firebase credentials");
                        }

                    });
        }
        else {

            Toast.makeText(this.context_weak_reference.get(), "This is not your device. Clear data", Toast.LENGTH_LONG).show();
            login_progress_bar_weak_reference.get().setVisibility(View.GONE);
        }
    }

    private void remote_server(String token){

        boolean is_email_verified;
        final FirebaseUser firebase_user = firebase_auth.getCurrentUser();
        if(firebase_user != null){

            Log.d(TAG, "LoginActivity: User exists " + firebase_user.getDisplayName());
            is_email_verified = firebase_user.isEmailVerified();

        }
        else {

            login_progress_bar_weak_reference.get().setVisibility(View.GONE);
            Log.d(TAG, "LoginActivity: User does not exist");
            return;
        }

        final JSONObject json_object = new JSONObject();
        try{


            json_object.put(this.context_weak_reference.get().getResources().getString(R.string.email_key), email);
            json_object.put(this.context_weak_reference.get().getResources().getString(R.string.password_key), password);
            json_object.put(this.context_weak_reference.get().getResources().getString(R.string.is_email_verified_key), is_email_verified);
            json_object.put(this.context_weak_reference.get().getResources().getString(R.string.fcm_token_key), token);


        }catch(JSONException json_error){

            json_error.printStackTrace();
        }

        Volley volley = new Volley(this.context_weak_reference.get(), Request.Method.POST, URL.LOGIN_URL, json_object);
        volley.set_priority(Request.Priority.IMMEDIATE);
        volley.Execute(new VolleyCallback() {
            @Override
            public void network_response(JSONObject json_response) {

                login_progress_bar_weak_reference.get().setVisibility(View.GONE);
                Log.d(TAG, "LoginActivity: " + json_response.toString());

                JSONArray user_hardware_json_array;
                JSONArray user_software_json_array;

                try {

                    if(json_response.has(context_weak_reference.get().getResources().getString(R.string.login_error_100_label))){

                        String login_error_100_response = json_response.getString(context_weak_reference.get().getResources().getString(R.string.login_error_100_label));
                        Toast.makeText(context_weak_reference.get(), login_error_100_response, Toast.LENGTH_LONG).show();
                    }
                    else if(json_response.has(context_weak_reference.get().getResources().getString(R.string.login_error_101_label))){

                        String login_error_101_response = json_response.getString(context_weak_reference.get().getResources().getString(R.string.login_error_101_label));
                        Toast.makeText(context_weak_reference.get(), login_error_101_response, Toast.LENGTH_LONG).show();

                    }
                    else if(json_response.has(context_weak_reference.get().getResources().getString(R.string.login_error_102_label))){

                        String login_error_102_response = json_response.getString(context_weak_reference.get().getResources().getString(R.string.login_error_102_label));
                        Toast.makeText(context_weak_reference.get(), login_error_102_response, Toast.LENGTH_LONG).show();

                    }
                    else if(json_response.has(context_weak_reference.get().getResources().getString(R.string.login_error_103_label))){

                        String login_error_103_response = json_response.getString(context_weak_reference.get().getResources().getString(R.string.login_error_103_label));
                        Toast.makeText(context_weak_reference.get(), login_error_103_response, Toast.LENGTH_LONG).show();

                    }
                    else if(json_response.has(context_weak_reference.get().getResources().getString(R.string.login_error_104_label))){

                        String login_error_104_response = json_response.getString(context_weak_reference.get().getResources().getString(R.string.login_error_104_label));
                        Toast.makeText(context_weak_reference.get(), login_error_104_response, Toast.LENGTH_LONG).show();

                    }
                    else if(json_response.has(context_weak_reference.get().getResources().getString(R.string.login_error_105_label))){

                        String login_error_105_response = json_response.getString(context_weak_reference.get().getResources().getString(R.string.login_error_105_label));
                        Toast.makeText(context_weak_reference.get(), login_error_105_response, Toast.LENGTH_LONG).show();

                    }
                    else if(json_response.has(context_weak_reference.get().getResources().getString(R.string.login_error_106_label))){

                        String login_error_106_response = json_response.getString(context_weak_reference.get().getResources().getString(R.string.login_error_106_label));
                        Toast.makeText(context_weak_reference.get(), login_error_106_response, Toast.LENGTH_LONG).show();

                    }

                    if (json_response.has(context_weak_reference.get().getResources().getString(R.string.is_experienced_key))) {
                        // Is experienced
                        Log.d(TAG, "Has is_experienced_user");
                        if (json_response.getBoolean(context_weak_reference.get().getResources().getString(R.string.is_experienced_key))) {

                            Login_Status = json_response.getBoolean(context_weak_reference.get().getResources().getString(R.string.login_status_key));
                            user_id = json_response.getString(context_weak_reference.get().getResources().getString(R.string.user_id_key));
                            uid = json_response.getString(context_weak_reference.get().getResources().getString(R.string.uid_key));
                            first_name = json_response.getString(context_weak_reference.get().getResources().getString(R.string.first_name_key));
                            last_name = json_response.getString(context_weak_reference.get().getResources().getString(R.string.last_name_key));
                            email = json_response.getString(context_weak_reference.get().getResources().getString(R.string.email_key));
                            fcm_token = json_response.getString(context_weak_reference.get().getResources().getString(R.string.fcm_token_key));
                            auth_token = json_response.getString(context_weak_reference.get().getResources().getString(R.string.access_token_key));
                            profile_image_name_thumbnail = json_response.getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));
                            profile_image_name_full = json_response.getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key));
                            String hardware_collection = json_response.getString(context_weak_reference.get().getResources().getString(R.string.user_hardware_key));
                            String software_collection = json_response.getString(context_weak_reference.get().getResources().getString(R.string.user_software_key));

                            user_hardware_json_array = new JSONArray(hardware_collection);
                            user_software_json_array = new JSONArray(software_collection);

                            ReloadLibrariesRunnable reload_libraries_runnable = new ReloadLibrariesRunnable(context_weak_reference, user_hardware_json_array, user_software_json_array,
                                    my_hardware_dao, my_software_dao);
                            new Thread(reload_libraries_runnable).start();

                            Log.d(TAG, "Experienced User " + first_name + "'s AccessToken: " + auth_token);
                            if (Login_Status) {

                                Log.d(TAG, first_name + " IS A EXPERIENCED. LOGGING IN....");

                                UserSettings.set_user_first_name(context_weak_reference.get(), first_name);
                                UserSettings.set_user_last_name(context_weak_reference.get(), last_name);
                                UserSettings.set_user_email(context_weak_reference.get(), email);
                                UserSettings.set_user_uid(context_weak_reference.get(), uid);
                                UserSettings.set_user_id(context_weak_reference.get(), user_id);
                                UserSettings.set_fcm_token(context_weak_reference.get(), fcm_token);
                                UserSettings.set_user_token(context_weak_reference.get(), auth_token);
                                UserSettings.set_user_profile_image_name_thumbnail(context_weak_reference.get(), profile_image_name_thumbnail);
                                UserSettings.set_user_profile_image_name_full(context_weak_reference.get(), profile_image_name_full);

                                if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.ldpi_label))){

                                    UserSettings.set_user_profile_image_thumbnail_url(context_weak_reference.get(),URL.LDPI + profile_image_name_thumbnail);
                                    UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.LDPI + profile_image_name_full);
                                }
                                else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.mdpi_label))){

                                    UserSettings.set_user_profile_image_thumbnail_url(context_weak_reference.get(),URL.MDPI + profile_image_name_thumbnail);
                                    UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.MDPI + profile_image_name_full);

                                }
                                else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.hdpi_label))){

                                    UserSettings.set_user_profile_image_thumbnail_url(context_weak_reference.get(),URL.HDPI + profile_image_name_thumbnail);
                                    UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.HDPI + profile_image_name_full);

                                }
                                else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xhdpi_label))){

                                    UserSettings.set_user_profile_image_thumbnail_url(context_weak_reference.get(),URL.XHDPI + profile_image_name_thumbnail);
                                    UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.XHDPI + profile_image_name_full);

                                }
                                else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xxhdpi_label))){

                                    UserSettings.set_user_profile_image_thumbnail_url(context_weak_reference.get(),URL.XXHDPI + profile_image_name_thumbnail);
                                    UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.XXHDPI + profile_image_name_full);

                                }
                                else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xxxhdpi_label))){

                                    UserSettings.set_user_profile_image_thumbnail_url(context_weak_reference.get(),URL.XXXHDPI + profile_image_name_thumbnail);
                                    UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.XXXHDPI + profile_image_name_full);

                                }


                                UserSettings.set_is_user_logged_in(context_weak_reference.get(), true);

                                app.set_is_user_logged_in(true);
                                app.set_location_permission(true);

                                if(does_account_exist()) {

                                    Log.d(TAG, "[LoginActivityRepository] App Status: Logging in Application");
                                    AccountManager account_manager = AccountManager.get(context_weak_reference.get());
                                    Account[] accounts = account_manager.getAccountsByType(context_weak_reference.get().getResources().getString(R.string.account_type));
                                    account_manager.setPassword(accounts[0], null);

                                    long id = 1;
                                    String time = String.format(Locale.ENGLISH, FORMATS.time, Calendar.getInstance().getTime());
                                    String date = String.format(Locale.ENGLISH, FORMATS.date, Calendar.getInstance().getTime());

                                    Home home = new Home(id, null, time, date);
                                    HomeTask home_task = new HomeTask(home, home_dao, DatabaseOperations.INSERT);
                                    home_task.execute();

                                }
                                else {

                                    Log.d(TAG, "[LoginActivityRepository] App Status: Reinstalled Application");
                                    long id = 1;
                                    String time = String.format(Locale.ENGLISH, FORMATS.time, Calendar.getInstance().getTime());
                                    String date = String.format(Locale.ENGLISH, FORMATS.date, Calendar.getInstance().getTime());

                                    Home home = new Home(id, null, time, date);
                                    HomeTask home_task = new HomeTask(home, home_dao, DatabaseOperations.INSERT);
                                    home_task.execute();

                                    create_account(email, auth_token);
                                }
                                // account_manager.setAuthToken(accounts[0], "full_access", auth_token);

                            } else {

                                Toast.makeText(context_weak_reference.get(), "Login Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    else if (json_response.has(context_weak_reference.get().getResources().getString(R.string.is_beginner_key))) {
                        // Is beginner
                        Log.d(TAG, "Has is_beginner_user");
                        if (json_response.getBoolean(context_weak_reference.get().getResources().getString(R.string.is_beginner_key))) {

                            Login_Status = json_response.getBoolean(context_weak_reference.get().getResources().getString(R.string.login_status_key));
                            first_name = json_response.getString(context_weak_reference.get().getResources().getString(R.string.first_name_key));
                            last_name = json_response.getString(context_weak_reference.get().getResources().getString(R.string.last_name_key));
                            String email_address = json_response.getString(context_weak_reference.get().getResources().getString(R.string.email_key));
                            uid = json_response.getString(context_weak_reference.get().getResources().getString(R.string.uid_key));
                            user_id = json_response.getString(context_weak_reference.get().getResources().getString(R.string.user_id_key));
                            fcm_token = json_response.getString(context_weak_reference.get().getResources().getString(R.string.fcm_token_key));
                            auth_token = json_response.getString(context_weak_reference.get().getResources().getString(R.string.access_token_key));
                            profile_image_name_thumbnail = json_response.getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));
                            profile_image_name_full = json_response.getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key));

                            Log.d(TAG, first_name + "'s AccessToken: " + auth_token);
                            if (Login_Status) {

                                Log.d(TAG, first_name + " IS A BEGINNER. LOGGING IN....");
                                UserSettings.set_user_first_name(context_weak_reference.get(), first_name);
                                UserSettings.set_user_last_name(context_weak_reference.get(), last_name);
                                UserSettings.set_user_email(context_weak_reference.get(), email);
                                UserSettings.set_user_radius(context_weak_reference.get(), 10);
                                UserSettings.set_user_uid(context_weak_reference.get(), uid);
                                UserSettings.set_user_id(context_weak_reference.get(), user_id);
                                UserSettings.set_fcm_token(context_weak_reference.get(), fcm_token);
                                UserSettings.set_user_token(context_weak_reference.get(), auth_token);
                                UserSettings.set_is_user_logged_in(context_weak_reference.get(), true);
                                UserSettings.set_user_token(context_weak_reference.get(), auth_token);
                                UserSettings.set_user_profile_image_name_thumbnail(context_weak_reference.get(), profile_image_name_thumbnail);
                                UserSettings.set_user_profile_image_name_full(context_weak_reference.get(), profile_image_name_full);

                                if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.ldpi_label))){

                                    UserSettings.set_user_profile_image_thumbnail_url(context_weak_reference.get(),URL.LDPI + profile_image_name_thumbnail);
                                    UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.LDPI + profile_image_name_full);
                                }
                                else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.mdpi_label))){

                                    UserSettings.set_user_profile_image_thumbnail_url(context_weak_reference.get(),URL.MDPI + profile_image_name_thumbnail);
                                    UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.MDPI + profile_image_name_full);

                                }
                                else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.hdpi_label))){

                                    UserSettings.set_user_profile_image_thumbnail_url(context_weak_reference.get(),URL.HDPI + profile_image_name_thumbnail);
                                    UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.HDPI + profile_image_name_full);

                                }
                                else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xhdpi_label))){

                                    UserSettings.set_user_profile_image_thumbnail_url(context_weak_reference.get(),URL.XHDPI + profile_image_name_thumbnail);
                                    UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.XHDPI + profile_image_name_full);

                                }
                                else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xxhdpi_label))){

                                    UserSettings.set_user_profile_image_thumbnail_url(context_weak_reference.get(),URL.XXHDPI + profile_image_name_thumbnail);
                                    UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.XXHDPI + profile_image_name_full);

                                }
                                else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xxxhdpi_label))){

                                    UserSettings.set_user_profile_image_thumbnail_url(context_weak_reference.get(),URL.XXXHDPI + profile_image_name_thumbnail);
                                    UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.XXXHDPI + profile_image_name_full);

                                }

                                app.set_is_user_logged_in(true);
                                app.set_location_permission(true);

                                long id = 1;
                                String time = String.format(Locale.ENGLISH, FORMATS.time, Calendar.getInstance().getTime());
                                String date = String.format(Locale.ENGLISH, FORMATS.date, Calendar.getInstance().getTime());

                                Home home = new Home(id, null, time, date);
                                HomeTask home_task = new HomeTask(home, home_dao, DatabaseOperations.INSERT);
                                home_task.execute();


                                /*
                                ImageSaver image_saver = new ImageSaver(context);
                                image_saver.create_application_directories();
*/
                                create_account(email_address, auth_token); // <-- Keep for Sync, but not necessary

                                Intent intent = new Intent(context_weak_reference.get(), ProfileSettingsActivity.class);
                                intent.putExtra(context_weak_reference.get().getResources().getString(R.string.is_beginner_key), true);
                                context_weak_reference.get().startActivity(intent);
                                ((LoginActivity) context_weak_reference.get()).finish();

                            } else {

                                Toast.makeText(context_weak_reference.get(), "Login Failed", Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                    Log.d(TAG, "LoginActivity Finished sign in processing...");
                }
                    catch(JSONException json_error){
                    json_error.printStackTrace();
                }

                Log.d(TAG, "End of web request");
            }



            @Override
            public void network_error(VolleyError error) {

                Log.d(TAG, "LoginActivity: network_error");
                login_progress_bar_weak_reference.get().setVisibility(View.GONE);
                Toast.makeText(context_weak_reference.get().getApplicationContext(),
                        context_weak_reference.get().getResources().getString(R.string.network_connection_error_label),
                        Toast.LENGTH_LONG).show();

            }

        });

    }

    private void retrieve_user_token(){

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {

            if(task.isSuccessful()) {

                String token = task.getResult().getToken();

                remote_server(token);
            }
            else if(!task.isSuccessful()) {

                Log.d(TAG, "Token Generation Error! Could not generate FCM token");

            }

        });


    }

    private void create_account(String email, String authToken){

        Account account = new Account(email, context_weak_reference.get().getResources().getString(R.string.account_type));

        AccountManager am = AccountManager.get(context_weak_reference.get());
        am.addAccountExplicitly(account, null, null); // set the password to null
        am.setAuthToken(account, "full_access", authToken);

        ContentResolver.setSyncAutomatically(account, context_weak_reference.get().getResources().getString(R.string.content_authority), true);
    }

    private boolean does_account_exist(){

        AccountManager account_manager = (AccountManager)  context_weak_reference.get().getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts;

        if (account_manager != null) {
            accounts = account_manager.getAccounts();
            for (Account account : accounts) {
                if (account.type.intern().equals(context_weak_reference.get().getResources().getString(R.string.account_type)))
                    return true;
            }
        }

        return false;
    }



}
