package com.syncadapters.czar.exchange.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.adapters.UsersRecycleViewAdapter;
import com.syncadapters.czar.exchange.datamodels.Event;
import com.syncadapters.czar.exchange.viewmodels.HomeFragmentViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {


    private App app;
    private static final String TAG = "OUT";
   // private static final int REQUESTED_LOCATION_PERMISSION = 100;

    //private static final int HOME_USERS_DELAY = 120000;
    //private static final int HOME_USERS_DELAY = 200;
    // --Commented out by Inspection (1/9/2021 11:51 PM):private static final int MINIMUM_NUM_USERS = 0;
    // --Commented out by Inspection (1/9/2021 11:51 PM):private static final int ITEM_PER_AD = 4;

    private HomeFragmentViewModel homefragment_viewmodel;
    private RelativeLayout relative_layout_home_fragment_header;
    private RecyclerView recycle_view;
    private ProgressBar home_progress_bar;
    private Context context;
    private UsersRecycleViewAdapter users_recycle_view_adapter;
    private TextView no_user_available_textView;
    private FrameLayout home_frame_layout;
    private AdView ad_view;

    private final ArrayList<Object> users = new ArrayList<>();

    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

        this.context = context;
        Log.d(TAG, "HomeFragment onAttach");


    }

    @SuppressLint("UsableSpace")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "HomeFragment onCreate");

        app = ((App) this.context.getApplicationContext());
        app.set_conversation_fragment_foreground(false);
        app.set_home_fragment_foreground(true);
        app.set_inventory_fragment_foreground(false);
        app.set_software_profile_dialog_foreground(false);

        File cacheDir = context.getCacheDir();
        if (cacheDir.getUsableSpace() * 100 / cacheDir.getTotalSpace() <= 10) { // Alternatively, use cacheDir.getFreeSpace()
            // Handle storage low state
            Log.d(TAG, "Exchange storage is low");
        } else {
            // Handle storage ok state
            Log.d(TAG, "Exchange storage is OK");
        }

        homefragment_viewmodel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);

        LocationManager location_manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean location_service_enabled = location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!location_service_enabled)
            location_tracking_permission();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSaveInstanceState){

      View view = inflater.inflate(R.layout.fragment_home, container, false);

        Log.d(TAG, "HomeFragment onCreateView");

        recycle_view = view.findViewById(R.id.home_fragment_recycleView_id);

       no_user_available_textView = view.findViewById(R.id.no_users_available_textView_id);
       no_user_available_textView.setVisibility(View.GONE);

       home_progress_bar = view.findViewById(R.id.home_progress_bar_circular_id);
       home_progress_bar.setVisibility(View.VISIBLE);

        home_frame_layout = view.findViewById(R.id.home_fragment_framelayout_id);
        relative_layout_home_fragment_header = view.findViewById(R.id.home_fragment_header_relative_id);

        users_recycle_view_adapter = new UsersRecycleViewAdapter(getActivity(), Glide.with(this));
        recycle_view.setAdapter(users_recycle_view_adapter);

        LinearLayoutManager linear_layout_manager = new LinearLayoutManager(this.context);
        recycle_view.setLayoutManager(linear_layout_manager);

        recycle_view.setHasFixedSize(true);

        set_banner_ads();
        load_banner_ads();


        return view;
    }


    private void set_banner_ads(){

        ad_view = new AdView(this.context.getApplicationContext());
        ad_view.setAdSize(AdSize.BANNER);
        String BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111";
        ad_view.setAdUnitId(BANNER_AD_ID);
        //users.add(0, ad_view);
        relative_layout_home_fragment_header.addView(ad_view);
        RelativeLayout.LayoutParams relative_layout_params_header = (RelativeLayout.LayoutParams) ad_view.getLayoutParams();
        relative_layout_params_header.addRule(RelativeLayout.CENTER_HORIZONTAL);

        ad_view.setLayoutParams(relative_layout_params_header);

    }

    private void load_banner_ads(){

/*
        Object item = users.get(0);

        if(item instanceof AdView){

            final AdView ad_view = (AdView) item;
            ad_view.loadAd(new AdRequest.Builder().build());

        }
*/

        ad_view.loadAd(new AdRequest.Builder().build());

    }


// --Commented out by Inspection START (1/9/2021 11:50 PM):
//    private void check_permission(){
//
//        /*
//        if( ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//
//            Log.d(TAG, "Getting user location permission");
//            //noinspection ConstantConditions
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUESTED_LOCATION_PERMISSION);
//
//        }
//*/
//        permission_request.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
//
//    }
// --Commented out by Inspection STOP (1/9/2021 11:50 PM)

/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_DENIED) {

            Log.d(TAG, "HomeActivity Permission Denied");
            app.set_location_permission(false);
        }
        else if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

            Log.d(TAG, "HomeActivity Permission Granted");
            app.set_location_permission(true);
        }

    }
*/

    @SuppressWarnings("unused")
    private final ActivityResultLauncher<String> permission_request = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {

                    if(result) {
                       // capture_image_content.launch(null);
                        app.set_location_permission(true);

                    }else {
                        app.set_location_permission(false);
                    }
                }
            });

    private void location_tracking_permission(){

        AlertDialog.Builder location_tracking_alert_message = new AlertDialog.Builder(this.context);
        location_tracking_alert_message.setMessage(getResources().getString(R.string.location_permission_alert_message_label));
        location_tracking_alert_message.setPositiveButton("OK", (dialog, which) -> {

            app.set_location_permission(false);
            dialog.dismiss();
        });

        AlertDialog location_tracking_alert_dialog = location_tracking_alert_message.create();
        location_tracking_alert_dialog.show();

    }

    private void update_home_fragment(){

        homefragment_viewmodel.getUsers().observe(getViewLifecycleOwner(), users_data -> {

            Log.d(TAG, "[HomeFragment] Entered HomeFragment getUsers.observe ");
            home_progress_bar.setVisibility(View.GONE);
            if(users_data == null) {
                Log.d(TAG, "> HomeFragment Users is null");
                return;
            }


                Log.d(TAG, "Processing loads [  ]");

                users.clear();
                users.addAll(users_data);

                //set_banner_ads();
               // load_banner_ads();

                Log.d(TAG, "[HomeFragment] Number of users: " + users.size());
                if(users.size() > 0) {

                    Log.d(TAG, "> HomeFragment Users has " + users.size());

                    if (users_recycle_view_adapter == null) {
                        Log.d(TAG, "UsersRecycleView Adapter is null" + users.size());
                        return;
                    }

                    users_recycle_view_adapter.set_users(users);
                    users_recycle_view_adapter.notifyDataSetChanged();

                    if (users_recycle_view_adapter.getItemCount() == 0) {


                        Log.d(TAG, "1) HomeFragment: No Users Available");
                        ad_view.setVisibility(View.GONE);
                        recycle_view.setVisibility(View.GONE);
                        no_user_available_textView.setVisibility(View.VISIBLE);
                        home_frame_layout.setBackgroundColor(Color.GRAY);

                    } else if (users_recycle_view_adapter.getItemCount() > 0) {

                        Log.d(TAG, "1) HomeFragment: Users Available");

                        ad_view.setVisibility(View.VISIBLE);
                        recycle_view.setVisibility(View.VISIBLE);
                        no_user_available_textView.setVisibility(View.GONE);
                        home_frame_layout.setBackgroundColor(Color.WHITE);

                    }

                }

            Log.d(TAG, "[HomeFragment] Number of users: " + users.size());
            if(users.size() > 0) {

                Log.d(TAG, "> HomeFragment Users has " + users.size());

                users_recycle_view_adapter.set_users(users);
                users_recycle_view_adapter.notifyDataSetChanged();

                if(users_recycle_view_adapter.getItemCount() == 0){

                    Log.d(TAG, "1) HomeFragment: No Users Available");
                    ad_view.setVisibility(View.GONE);
                    recycle_view.setVisibility(View.GONE);
                    no_user_available_textView.setVisibility(View.VISIBLE);
                    home_frame_layout.setBackgroundColor(Color.GRAY);

                }
                else if(users_recycle_view_adapter.getItemCount() > 0){

                    Log.d(TAG, "1) HomeFragment: Users Available");
                    ad_view.setVisibility(View.VISIBLE);
                    recycle_view.setVisibility(View.VISIBLE);
                    no_user_available_textView.setVisibility(View.GONE);
                    home_frame_layout.setBackgroundColor(Color.WHITE);

                }

            }

        });

        if(users_recycle_view_adapter.getItemCount() == 0){
            Log.d(TAG, "2) HomeFragment: No Users Available");

            ad_view.setVisibility(View.GONE);
            home_progress_bar.setVisibility(View.GONE);
            recycle_view.setVisibility(View.GONE);
            no_user_available_textView.setVisibility(View.VISIBLE);
            home_frame_layout.setBackgroundColor(Color.GRAY);
            users_recycle_view_adapter.notifyDataSetChanged();

        }
        else if(users_recycle_view_adapter.getItemCount() > 0){

            Log.d(TAG, "2) HomeFragment: Users Available");
            ad_view.setVisibility(View.VISIBLE);
            home_progress_bar.setVisibility(View.GONE);
            recycle_view.setVisibility(View.VISIBLE);
            no_user_available_textView.setVisibility(View.GONE);
            home_frame_layout.setBackgroundColor(Color.WHITE);
            users_recycle_view_adapter.notifyDataSetChanged();
        }


        Log.d(TAG, "[HomeFragment] updated home fragment");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "HomeFragment onStart");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "HomeFragment onResume");
        update_home_fragment();
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event home_fragment_event){

        Log.d(TAG, "[HomeFragment] updating home fragment");
        update_home_fragment();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "HomeFragment onPause");


        //Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "HomeFragment onStop");

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "HomeFragment onDestroyView");

        users_recycle_view_adapter = null;
        recycle_view.setAdapter(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //app.set_home_fragment_foreground(false);
        Log.d(TAG, "HomeFragment onDestroy");

/*
        if(ad_view != null && ad_view.getParent() != null)
            ((ViewGroup) ad_view.getParent()).removeView(ad_view);
*/
//        ad_view.destroy();

        if(ad_view != null) {

            //noinspection ConstantConditions
            ad_view.setAdListener(null);
            relative_layout_home_fragment_header.removeAllViews();
            relative_layout_home_fragment_header = null;
            ad_view = null;
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "HomeFragment onDetach");


    }
}
