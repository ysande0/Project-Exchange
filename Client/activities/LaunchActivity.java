package com.syncadapters.czar.exchange.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datamodels.UserSettings;

import java.util.Objects;

public class LaunchActivity extends AppCompatActivity {

    private static final String TAG = "INTRO";
    private App app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        DisplayMetrics display_metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display_metrics);
        int dpi_classification = display_metrics.densityDpi;

        if(dpi_classification <= 120){

            UserSettings.set_dpi(LaunchActivity.this, getString(R.string.ldpi_label));
        }
        else if(dpi_classification <= 160){

            UserSettings.set_dpi(LaunchActivity.this, getString(R.string.mdpi_label));
        }
        else if(dpi_classification <= 240){


            UserSettings.set_dpi(LaunchActivity.this, getString(R.string.hdpi_label));

        }
        else if(dpi_classification <= 320){

            UserSettings.set_dpi(LaunchActivity.this, getString(R.string.xhdpi_label));
        }
        else if(dpi_classification <= 480){

            UserSettings.set_dpi(LaunchActivity.this, getString(R.string.xxhdpi_label));
        }
        else if(dpi_classification <= 640){

            UserSettings.set_dpi(LaunchActivity.this, getString(R.string.xxxhdpi_label));

        }


        Objects.requireNonNull(getSupportActionBar()).hide();
        app = ((App) getApplicationContext());

        int SPLASH_SCREEN_TIMEOUT = 4000;
        new Handler().postDelayed(() -> {
            
           launch_application();

       }, SPLASH_SCREEN_TIMEOUT);


    }


    private void launch_application() {

        if (UserSettings.has(LaunchActivity.this, getResources().getString(R.string.is_user_logged_in))) {

            boolean is_user_logged_in = UserSettings.get_is_user_logged_in(LaunchActivity.this);
            if (is_user_logged_in) {

                //noinspection ConstantConditions
                app.set_is_user_logged_in(is_user_logged_in);
                app.set_location_permission(true);
                Intent intent = new Intent(LaunchActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }
            else{

                UserSettings.set_is_user_logged_in(LaunchActivity.this, false);
                app.set_is_user_logged_in(false);
                app.set_location_permission(false);
                Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }



        } else {

            UserSettings.set_is_user_logged_in(LaunchActivity.this, false);
            app.set_is_user_logged_in(false);
            app.set_location_permission(false);
            Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }

    }

}
