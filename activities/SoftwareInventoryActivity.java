package com.syncadapters.czar.exchange.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.adapters.ViewPagerAdapter;
import com.syncadapters.czar.exchange.datamodels.ImageSaver;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.fragments.InventoryInFragment;

public class SoftwareInventoryActivity extends AppCompatActivity {

    private static final String TAG = "MSG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_inventory);

        ViewPager2 view_pager = findViewById(R.id.view_pager2_id);

        ActionBar action_bar = getSupportActionBar();
        assert action_bar != null;
        action_bar.setDisplayHomeAsUpEnabled(true);

        InventoryInFragment inventory_in_fragment;
        ViewPagerAdapter view_pager_adapter;

        if(getIntent().getExtras() != null){

            Log.d(TAG, "SoftwareInventory intents exist");

            if(getIntent().getExtras().getBoolean("Beginner")) {
                boolean isBeginner = getIntent().getExtras().getBoolean("Beginner");

                Log.d(TAG, "This is a beginner");

                if(isBeginner){

                    Log.d(TAG, "Loading arguments");
                    Bundle argument = new Bundle();
                    argument.putBoolean("Beginner", true);

                    inventory_in_fragment = new InventoryInFragment();
                    inventory_in_fragment.setArguments(argument);

                    Log.d(TAG, "Starting fragments");

                    view_pager_adapter = new ViewPagerAdapter(this);

                    view_pager_adapter.addFragment(inventory_in_fragment, "Library Management");
                    view_pager.setAdapter(view_pager_adapter);

                }


            }

        }else {

            inventory_in_fragment = new InventoryInFragment();

            view_pager_adapter = new ViewPagerAdapter(this);

            view_pager_adapter.addFragment(inventory_in_fragment, "Library Management");
            view_pager.setAdapter(view_pager_adapter);


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "SoftwareInventoryActivity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "SoftwareInventoryActivity onResume");

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //noinspection SwitchStatementWithTooFewBranches
        switch(item.getItemId()){

            case android.R.id.home:
                ImageSaver image_saver = new ImageSaver();
                if(!(UserSettings.get_encoded_bitmap_thumbnail(getApplicationContext()).isEmpty()))
                    UserSettings.remove_encoded_bitmap_thumbnail(getApplicationContext());

                if(!(UserSettings.get_encoded_bitmap_full(getApplicationContext()).isEmpty()))
                    UserSettings.remove_encoded_bitmap_full(getApplicationContext());

                if(!(UserSettings.get_user_local_software_image_path(getApplicationContext()).isEmpty()))
                    image_saver.delete_image_file(UserSettings.get_user_local_software_image_path(getApplicationContext()));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "SoftwareInventoryActivity onPause");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "SoftwareInventoryActivity onDestroy");

    }

}
