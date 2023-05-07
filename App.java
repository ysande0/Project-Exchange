package com.syncadapters.czar.exchange;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.lifecycle.LifecycleObserver;
import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.syncadapters.czar.exchange.activities.HomeActivity;
import com.syncadapters.czar.exchange.activities.MessageActivity;
import com.syncadapters.czar.exchange.repositories.LocationRepository;

import java.util.Objects;

public class App extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {

    /*
    *   Target Minimum Android OS Version: Lollipop Version 5.0 (API Level 21)
    *   TODO: For development purposes only, location permission will be true AND is_user_logged_in
    * */

    private static final String TAG = "MSG";
    @SuppressWarnings("unused")
    private static boolean is_app_foreground = false;
    private static boolean is_message_activity_foreground = false;
    @SuppressWarnings("unused")
    private static boolean is_home_activity_foreground = false;
    private static boolean is_conversation_fragment_foreground = false;
    private static boolean is_home_fragment_foreground = false;
    @SuppressWarnings("unused")
    private static boolean is_inventory_fragment_foreground = false;
    private static boolean is_software_profile_dialog_foreground = false;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private static boolean is_user_logged_in = false;
    private static boolean is_location_permission_granted = false;

    // --Commented out by Inspection (1/9/2021 11:48 PM):private static boolean is_location_updating = false;

    private LocationRepository location_repository;


    @Override
    public void onCreate(){
        super.onCreate();

        Log.d(TAG,"Exchange App has Launched (onCreate)");

        if(location_repository == null){

            location_repository = LocationRepository.getInstance();

        }


        Stetho.initializeWithDefaults(this);

        check_wifi();

        this.registerActivityLifecycleCallbacks(this);
/*
        MobileAds.initialize(getApplicationContext(), initializationStatus -> {

        });
*/
    }


    private void check_wifi(){

        ConnectivityManager connectivity_manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo active_network = connectivity_manager.getActiveNetworkInfo();

        boolean is_WIFI_on = active_network != null && active_network.isConnected();

        if(!is_WIFI_on)
            Toast.makeText(App.this, "WIFI is off", Toast.LENGTH_LONG).show();

    }



    @SuppressWarnings("UnusedReturnValue")
    private boolean check_google_play_services(Activity activity){

        int google_play_services_availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(App.this);

        if(google_play_services_availability == ConnectionResult.SUCCESS){
            Log.d(TAG, "Google Play Services is available");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(google_play_services_availability)){

            String error = GoogleApiAvailability.getInstance().getErrorString(google_play_services_availability);
            Log.d(TAG, "An issue occurred " + error);
            Objects.requireNonNull(GoogleApiAvailability.getInstance().getErrorDialog(activity, google_play_services_availability, 1)).show();
        }
        else
            Log.d(TAG, "Application cannot be used");

        return false;
    }

    public void is_google_play_services_available(Activity activity){

        check_google_play_services(activity);

    }

    public boolean is_location_permission_granted(){
        return is_location_permission_granted;
    }

    public boolean is_message_activity_in_foreground(){
        return is_message_activity_foreground;
    }

    public boolean is_conversation_fragment_foreground(){
        return is_conversation_fragment_foreground;
    }

    @SuppressWarnings("unused")
    public boolean is_home_fragment_foreground(){
        return is_home_fragment_foreground;
    }

    public boolean is_software_profile_dialog_foreground(){

        return is_software_profile_dialog_foreground;
    }

    public void set_conversation_fragment_foreground(boolean is_conversation_fragment_foreground){

        App.is_conversation_fragment_foreground = is_conversation_fragment_foreground;
    }

    public void set_home_fragment_foreground(boolean is_home_fragment_foreground){

        App.is_home_fragment_foreground = is_home_fragment_foreground;

    }

    public void set_inventory_fragment_foreground(boolean is_inventory_fragment_foreground){

        App.is_inventory_fragment_foreground = is_inventory_fragment_foreground;
    }

    public void set_software_profile_dialog_foreground(boolean is_software_profile_dialog_foreground){

        App.is_software_profile_dialog_foreground = is_software_profile_dialog_foreground;
    }

    public void set_is_user_logged_in(boolean user_logged_in){

        is_user_logged_in = user_logged_in;
    }

    public void set_location_permission(boolean location_permission){

        is_location_permission_granted = location_permission;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        Log.d(TAG, "Exchange onCreate");

        // Alert if internet connection is unavailable

        // Alert if Location tracking is unavailable
    }

    @Override
    public void onActivityStarted(Activity activity) {

        Log.d(TAG, "Exchange onStart");


    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, "Exchange is in Foreground");

        is_app_foreground = true;
     //   Log.d(TAG, "  Logged In: " + is_user_logged_in + "  Location Permission Granted: " + is_location_permission_granted);
        /*
        ActivityManager activity_manager = (ActivityManager) getSystemService( ACTIVITY_SERVICE );

        List<ActivityManager.RunningTaskInfo> task_list = activity_manager.getRunningTasks(10);


        Log.d(TAG, "Number of Tasks: " + task_list.size());
    */
        if(activity instanceof MessageActivity) {
            Log.d(TAG, "MESSAGE ACTIVITY IS IN FOREGROUND");
            is_message_activity_foreground = true;
        }
        else
            is_message_activity_foreground = false;



        if(activity instanceof  HomeActivity){
            Log.d(TAG, "HOME Activity IS IN FOREGROUND");
            is_home_activity_foreground = true;

        }
        else
            is_home_activity_foreground = false;

    }

    @Override
    public void onActivityPaused(Activity activity) {

        Log.d(TAG, "Exchange is in Background");

        is_app_foreground = false;
        if(activity instanceof MessageActivity){

            Log.d(TAG, "MESSAGE ACTIVITY IS NOT IN FOREGROUND");
            is_message_activity_foreground = false;

        }

        if(activity instanceof HomeActivity){

            Log.d(TAG, "HOME ACTIVITY IS NOT IN FOREGROUND");
            is_home_activity_foreground = false;

        }

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

}
