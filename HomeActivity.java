package com.syncadapters.czar.exchange.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datamodels.Event;
import com.syncadapters.czar.exchange.datamodels.LocationCallbackReference;
import com.syncadapters.czar.exchange.datamodels.Message;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.dialogs.HardwareLibraryDialog;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.fragments.ConversationsFragment;
import com.syncadapters.czar.exchange.fragments.HomeFragment;
import com.syncadapters.czar.exchange.fragments.InventoryFragment;
import com.syncadapters.czar.exchange.viewmodels.HomeActivityViewModel;

import android.Manifest;
import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

@SuppressWarnings("unused")
public class HomeActivity extends AppCompatActivity {

    private App app;
    private static final String TAG = "OUT";
    private static final int REQUESTED_LOCATION_PERMISSION = 100;
    private static final long LOCATION_TIME_INTERVAL = 600000; // Every 10 MINUTES, location is computed
    private static final long FASTEST_LOCATION_TIME_INTERVAL = 120000; // Max 2 minutes, if location data is available from another app/source
    private static final long MAX_LOCATION_TIME_WAIT_INTERVAL = 3600000; // Delays location delivery.
    private BottomNavigationView bottom_navigation;
    private FragmentManager fragment_manager;
    private final ConversationsFragment conversation_fragment = new ConversationsFragment();
    private final HomeFragment home_fragment = new HomeFragment();
    private final InventoryFragment inventory_fragment = new InventoryFragment();
    private Fragment current_fragment = conversation_fragment;
    private static String fragment_tag;
    private HomeActivityViewModel home_activity_view_model;

    @SuppressWarnings("FieldCanBeLocal")
    private FusedLocationProviderClient fused_location_provider_client;
    private WeakReference<FusedLocationProviderClient> fused_location_provider_client_weak_reference;
    private LocationRequest location_request;
    private LocationCallback location_callback;
    private WeakReference<LocationCallback> location_callback_weak_reference;
    private boolean is_home_activity_foreground = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        Log.d(TAG, "HomeActivity onCreate");

        app = ((App) getApplicationContext());

        if (app.is_location_permission_granted()) {

            Log.d(TAG, "[HomeActivity] Location is enabled");
        } else
            Log.d(TAG, "[HomeActivity] Location is disabled");

        EventBus.getDefault().register(this);

        location_request = LocationRequest.create();
        location_request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        location_request.setInterval(LOCATION_TIME_INTERVAL);
        location_request.setFastestInterval(FASTEST_LOCATION_TIME_INTERVAL);
        location_request.setMaxWaitTime(MAX_LOCATION_TIME_WAIT_INTERVAL);

        location_callback = new LocationCallback() {

            @Override
            public void onLocationResult(@NotNull LocationResult location_result) {
                super.onLocationResult(location_result);

                update(location_result.getLastLocation());
            }
        };

/*
        location_callback_weak_reference = new WeakReference<>(new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult location_result) {
                super.onLocationResult(location_result);

                update(location_result.getLastLocation());
            }


        });
*/
        // home_activity_view_model = ViewModelProviders.of(this).get(HomeActivityViewModel.class);
        home_activity_view_model = new ViewModelProvider(this).get(HomeActivityViewModel.class);

        ActionBar action_bar = getSupportActionBar();
        assert action_bar != null;
        action_bar.show();

        @SuppressWarnings("unused") ViewPager view_pager = findViewById(R.id.home_view_pager_id);

        fragment_manager = getSupportFragmentManager();
        //current_fragment = new ConversationsFragment();
        /*
        if(savedInstanceState != null){

            fragment_tag = savedInstanceState.getString("current_fragment");

            assert fragment_tag != null;
            switch (fragment_tag) {
                case "fragment_conversations_id":

                    fragment_manager.beginTransaction().add(R.id.fragment_container_id, conversation_fragment, "fragment_conversations_id").commit();
                    fragment_manager.beginTransaction().add(R.id.fragment_container_id, home_fragment, "fragment_home_id").hide(home_fragment).commit();
                    fragment_manager.beginTransaction().add(R.id.fragment_container_id, inventory_fragment, "fragment_inventory_id").hide(inventory_fragment).commit();
                    current_fragment = conversation_fragment;
                    Log.d(TAG, "Rotating: CONVERSATIONS FRAGMENT");
                    break;

                case "fragment_home_id":
                    fragment_manager.beginTransaction().add(R.id.fragment_container_id, conversation_fragment, "fragment_conversations_id").hide(conversation_fragment).commit();
                    fragment_manager.beginTransaction().add(R.id.fragment_container_id, home_fragment, "fragment_home_id").commit();
                    fragment_manager.beginTransaction().add(R.id.fragment_container_id, inventory_fragment, "fragment_inventory_id").hide(inventory_fragment).commit();
                    current_fragment = home_fragment;
                    Log.d(TAG, "Rotating: HOME FRAGMENT");
                    break;
                case "fragment_inventory_id":
                    fragment_manager.beginTransaction().add(R.id.fragment_container_id, conversation_fragment, "fragment_conversations_id").hide(conversation_fragment).commit();
                    fragment_manager.beginTransaction().add(R.id.fragment_container_id, home_fragment, "fragment_home_id").hide(home_fragment).commit();
                    fragment_manager.beginTransaction().add(R.id.fragment_container_id, inventory_fragment, "fragment_inventory_id").commit();
                    current_fragment = inventory_fragment;
                    Log.d(TAG, "Rotating: INVENTORY FRAGMENT");
                    break;
            }

        }
        else {
            fragment_tag = "fragment_conversations_id";

        }
*/
        bottom_navigation = findViewById(R.id.bottom_navigation_id);
        bottom_navigation.setOnNavigationItemSelectedListener(menuItem -> {

            Log.d(TAG, "Home Activity onNavigationItem");
            Log.d(TAG, "Menu Item: " + menuItem.getItemId());
            switch (menuItem.getItemId()) {

                case R.id.nav_conversation_id:
                    Log.d(TAG, "HomeActivity: Conversation Fragment");
                    fragment_manager.beginTransaction().hide(current_fragment).show(conversation_fragment).commit();
                    current_fragment = conversation_fragment;
                    fragment_tag = "fragment_conversations_id";
                    break;

                case R.id.nav_home_id:
                    Log.d(TAG, "HomeActivity: Home Fragment");
                    fragment_manager.beginTransaction().hide(current_fragment).show(home_fragment).commit();
                    current_fragment = home_fragment;
                    fragment_tag = "fragment_home_id";
                    Event home_fragment_event = new Event();
                    EventBus.getDefault().post(home_fragment_event);
                    break;

                case R.id.nav_inventory_id:
                    Log.d(TAG, "HomeActivity: Inventory Fragment");
                    fragment_manager.beginTransaction().hide(current_fragment).show(inventory_fragment).commit();
                    current_fragment = inventory_fragment;
                    fragment_tag = "fragment_inventory_id";
                    break;

            }

            Log.d(TAG, "(BottomNavigation) Back Stack Count: " + fragment_manager.getBackStackEntryCount());

            return true;
        });


        Log.d(TAG, "HomeActivity Back Stack Count: " + fragment_manager.getBackStackEntryCount());      //  home_activity_view_model.load_notification_badges(HomeActivity.this, bottom_navigation, UserInterface.HOME_ACTIVITY);
        fragment_manager.beginTransaction().add(R.id.fragment_container_id, conversation_fragment, "fragment_conversations_id").commit();
        fragment_manager.beginTransaction().add(R.id.fragment_container_id, home_fragment, "fragment_home_id").hide(home_fragment).commit();
        fragment_manager.beginTransaction().add(R.id.fragment_container_id, inventory_fragment, "fragment_inventory_id").hide(inventory_fragment).commit();
        //     location_updates();
        start_location_updates();

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "HomeActivity onStart");
        Log.d(TAG, "HomeActivity is_home_foreground: " + is_home_activity_foreground);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "HomeActivity onResume");
/*
        location_callback_weak_reference = new WeakReference<>(new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult location_result) {
                super.onLocationResult(location_result);

                update(location_result.getLastLocation());
                start_location_updates();
                Log.d(TAG, "[HomeActivity] onLocationResult Size: " + location_result.getLocations().size());
            }


        });
*/

        //  location_updates();
        // start_location_updates();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        home_activity_view_model.load_notification_badges(app, HomeActivity.this, bottom_navigation,
                UserInterface.HOME_ACTIVITY, is_home_activity_foreground);

    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle out_state) {
        super.onSaveInstanceState(out_state);

        Log.d(TAG, "HomeActivity onSaveInstanceState: " + fragment_tag);
        //out_state.putParcelableArrayList("conversation_entries", this.conversation_entries);
        out_state.putString("current_fragment", fragment_tag);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Message message) {


        final String MSG = "TAG";
        Log.d(MSG, "[HomeActivity] onEvent First name: " + message.first_name + " message: " + message.message);
        home_activity_view_model.load_notification_badges(app, HomeActivity.this, bottom_navigation, UserInterface.HOME_ACTIVITY, is_home_activity_foreground);
    }


    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "HomeActivity onPause");
/*
        if(location_callback_weak_reference.get() != null){

            if(fused_location_provider_client_weak_reference.get() != null)
                    stop_location_updates();

            location_callback_weak_reference.clear();
        }
*/

        //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "HomeActivity onStop");

        is_home_activity_foreground = false;


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "HomeActivity onDestroy");
        current_fragment = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menu_inflater = getMenuInflater();
        menu_inflater.inflate(R.menu.home_activity_menu_items, menu);

        if (menu instanceof MenuBuilder) {
            MenuBuilder menu_builder = (MenuBuilder) menu;
            menu_builder.setOptionalIconsVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.profile_settings_item_id:
                launch_profile_settings_activity();
                return true;

            case R.id.profile_platforms_item_id:
                launch_profile_platforms_activity();
                return true;

            case R.id.profile_logout_item_id:
                launch_profile_logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void check_permission() {

        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "Getting user location permission");
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUESTED_LOCATION_PERMISSION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

            Log.d(TAG, "LoginActivity Permission Denied");
            app.set_location_permission(false);

        } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "LoginActivity Permission Granted");
            app.set_location_permission(true);
            start_location_updates();
        }

    }

    @SuppressLint("MissingPermission")
    private void location_updates() {

        fused_location_provider_client = LocationServices.getFusedLocationProviderClient(HomeActivity.this);
        //noinspection ConstantConditions

        if(app.is_location_permission_granted()){

            fused_location_provider_client.requestLocationUpdates(location_request, new LocationCallbackReference(location_callback), null);
            fused_location_provider_client.getLastLocation().addOnSuccessListener(this, this::update);

        }
        else
            check_permission();

    }

    private void update(Location location){

        if(location == null)
            return;

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();


         Log.d(TAG, "App Latitude: " + latitude + "    Longitude: " + longitude);

        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);

        UserSettings.set_latitude_and_longitude(HomeActivity.this, latitude, longitude);

        String email = UserSettings.get_user_email(HomeActivity.this);
        String user_id = UserSettings.get_user_id(HomeActivity.this);

        //noinspection ConstantConditions
        if (email.isEmpty() || email == null)
            return;

        try {

            JSONObject json_object = new JSONObject();
            json_object.put("id", UserSettings.get_user_id(getApplicationContext()));
            json_object.put("latitude", latitude);
            json_object.put("longitude", longitude);
            //json_object.put("radius", UserSettings.get_user_radius(getApplicationContext()));

            home_activity_view_model.update_location_remote(getApplicationContext(), json_object);

        }catch (JSONException json_error){
            json_error.printStackTrace();
        }
        IntentFilter low_storage_filter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
        boolean has_low_storage = registerReceiver(null, low_storage_filter) != null;

        if (has_low_storage) {
            Toast.makeText(getApplicationContext(),"Low Storage, Manaphest may not function optimally", Toast.LENGTH_LONG).show();
            Log.d(TAG, "HomeActivity: Low Storage");
        }

        Account account = new Account(email, getString(R.string.account_type));
        ContentResolver.setMasterSyncAutomatically(true);
        ContentResolver.requestSync(account, getString(R.string.content_authority), bundle);


    }

    private void start_location_updates(){

        location_updates();
        Log.d(TAG, "Start Location Updates");
    }

    private void stop_location_updates(){

        fused_location_provider_client_weak_reference.get().removeLocationUpdates(location_callback_weak_reference.get());
        Log.d(TAG, "Stop Location Updates");
    }

    private void launch_profile_settings_activity(){

        Intent intent = new Intent(HomeActivity.this, ProfileSettingsActivity.class);
        startActivity(intent);

    }

    private void launch_profile_platforms_activity(){

        /*
        Intent intent = new Intent(HomeActivity.this, HardwareInventoryActivity.class);
        startActivity(intent);
         */

        HardwareLibraryDialog hardware_library_dialog = new HardwareLibraryDialog();
        hardware_library_dialog.show(fragment_manager, "Hardware_Library_Dialog");

    }

    private void launch_profile_logout(){

        home_activity_view_model.logout(app, HomeActivity.this);

    }
}
