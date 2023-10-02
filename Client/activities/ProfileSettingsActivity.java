package com.syncadapters.czar.exchange.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datamodels.ImageSaver;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.dialogs.BlockedUsersDialog;
import com.syncadapters.czar.exchange.dialogs.ChangePasswordDialog;
import com.syncadapters.czar.exchange.dialogs.HardwareLibraryDialog;
import com.syncadapters.czar.exchange.dialogs.PPDialog;
import com.syncadapters.czar.exchange.dialogs.TosDialog;
import com.syncadapters.czar.exchange.viewmodels.ProfileSettingsActivityViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/*
*  TODO: Make sure to delete file after image has been uploaded to server, and when the back button is pressed
*
* */

@SuppressWarnings("ALL")
public class ProfileSettingsActivity extends AppCompatActivity {

    private App app;
    private ImageView profile_image_view;
    private EditText setting_first_name_editText;
    private EditText setting_last_name_editText;
    private TextView setting_distance_TextView;
    private TextView terms_of_service_textView;
    private TextView privacy_policy_textView;
    private ProgressBar progress_bar;
    private GestureDetector gesture_detector;

    private static final String TAG = "MSG";
    private static int current_value = 100;

    private boolean is_beginner = false;
    private String profile_image_thumbnail_url;
    private String profile_name_thumbnail;
    private String profile_name_full;
    private String encoded_bitmap_thumbnail;
    private String encoded_bitmap_full;
    private String uid;
    private String id;
    private String first_name;
    private String last_name;
    private ImageSaver image_saver;
    private FragmentManager fragment_manager;
    private Bitmap profile_image_bitmap;
    private ProfileSettingsActivityViewModel profile_settings_activity_view_model;

    private int full_image_width = 0;
    private int full_image_height = 0;

    private int thumbnail_image_width = 0;
    private int thumbnail_image_height = 0;

    private int preview_image_width = 0;
    private int preview_image_height = 0;
    private final int QUALITY = 75;

    private int dpi_classification;
    private boolean has_returned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_settings);

        app = ((App) getApplicationContext());
        
        profile_settings_activity_view_model = new ViewModelProvider(this).get(ProfileSettingsActivityViewModel.class);
        first_name = UserSettings.get_user_first_name(ProfileSettingsActivity.this);
        last_name = UserSettings.get_user_last_name(ProfileSettingsActivity.this);
        current_value = UserSettings.get_user_radius(ProfileSettingsActivity.this);
        image_saver = new ImageSaver();
        String user_image_full_url = UserSettings.get_user_profile_image_full_url(ProfileSettingsActivity.this);
  

        uid = UserSettings.get_user_uid(ProfileSettingsActivity.this);
        id = UserSettings.get_user_id(ProfileSettingsActivity.this);
   

        ActionBar action_bar = getSupportActionBar();

        if(getIntent().hasExtra("Beginner")) {
            is_beginner = true;
            assert action_bar != null;
            action_bar.setDisplayHomeAsUpEnabled(false);
        }
        else {
            assert action_bar != null;
            action_bar.setDisplayHomeAsUpEnabled(true);
        }

        profile_image_view = findViewById(R.id.profile_image_view_id);
        setting_first_name_editText = findViewById(R.id.setting_first_name_editText_id);
        setting_first_name_editText.setText(first_name);
        setting_last_name_editText = findViewById(R.id.setting_last_name_editText_id);
        setting_last_name_editText.setText(last_name);
        terms_of_service_textView = findViewById(R.id.terms_of_service_text_view_id);
        privacy_policy_textView = findViewById(R.id.privacy_policy_text_view_id);
        SeekBar setting_distance_seekBar = findViewById(R.id.setting_distance_seekBar_id);
        setting_distance_TextView = findViewById(R.id.setting_distance_textView_id);
        Button change_password_button = findViewById(R.id.setting_password_change_button_id);
        Button blocked_users_button = findViewById(R.id.blocked_users_button_id);
        progress_bar = findViewById(R.id.profile_settings_progress_bar_circular_id);

        if(savedInstanceState != null){

            profile_image_bitmap = profile_settings_activity_view_model.get_bitmap_live_data();
            is_beginner = savedInstanceState.getBoolean(getResources().getString(R.string.is_beginner_key));
            has_returned = savedInstanceState.getBoolean("has_returned");

        }
        else {

            Glide.with(ProfileSettingsActivity.this).asDrawable()
                    .load(user_image_full_url)
                    .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                    .into(new CustomTarget<Drawable>() {

                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {
                            super.onLoadStarted(placeholder);


                            progress_bar.setVisibility(View.VISIBLE);
                            RoundedBitmapDrawable circular_bitmap_drawable = RoundedBitmapDrawableFactory.create(getResources(), drawable_to_bitmap(ResourcesCompat.getDrawable(getResources(), R.drawable.default_profile_image, null)));
                            circular_bitmap_drawable.setCircular(true);
                            profile_image_view.setImageDrawable(circular_bitmap_drawable);

                        }

                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {


                            progress_bar.setVisibility(View.GONE);

                            int preview_image_width = 0;
                            int preview_image_height = 0;

                            DisplayMetrics display_metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(display_metrics);

                            if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.ldpi_label))){

                                preview_image_width = 195;
                                preview_image_height = 195;

                            }
                            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.mdpi_label))){

                                preview_image_width = 260;
                                preview_image_height = 260;

                            }
                            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.hdpi_label))){

                                preview_image_width = 390;
                                preview_image_height = 390;

                            }
                            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xhdpi_label))){

                                preview_image_width = 520;
                                preview_image_height = 520;

                            }
                            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xxhdpi_label))){

                                preview_image_width = 780;
                                preview_image_height = 780;

                            }
                            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xxxhdpi_label))){

                                preview_image_width = 1040;
                                preview_image_height = 1040;

                            }

                            profile_image_bitmap = scale_bitmap(drawable_to_bitmap(resource), preview_image_width, preview_image_height);

                            RoundedBitmapDrawable circular_bitmap_drawable = RoundedBitmapDrawableFactory.create(getResources(), profile_image_bitmap);
                            circular_bitmap_drawable.setCircular(true);
                            profile_image_view.setImageDrawable(circular_bitmap_drawable);

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);

                            progress_bar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });


        }

        profile_image_view.setOnClickListener(v -> {

           capture_image();

        });

         gesture_detector = new GestureDetector(this, new Gesture());

        if(is_beginner) {

            setting_first_name_editText.setVisibility(View.GONE);
            setting_last_name_editText.setVisibility(View.GONE);
            change_password_button.setVisibility(View.GONE);
            blocked_users_button.setVisibility(View.GONE);
        }

        DisplayMetrics display_metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display_metrics);
        dpi_classification = display_metrics.densityDpi;

        setting_distance_seekBar.setProgress(current_value);
        setting_distance_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                current_value = progress;

                if(current_value == 0)
                    current_value = 1;

                String distance = getResources().getString(R.string.user_profile_software_distance_label) + " " + current_value + " " + getResources().getString(R.string.user_profile_software_miles_label);
                setting_distance_TextView.setText(distance);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                String distance = getResources().getString(R.string.user_profile_software_distance_label) + " " + current_value + " " + getResources().getString(R.string.user_profile_software_miles_label);
                setting_distance_TextView.setText(distance);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                String distance = getResources().getString(R.string.user_profile_software_distance_label) + " " + current_value + " " + getResources().getString(R.string.user_profile_software_miles_label);
                setting_distance_TextView.setText(distance);
            }
        });


        String distance = getResources().getString(R.string.user_profile_software_distance_label) + " " + current_value + " " + getResources().getString(R.string.user_profile_software_miles_label);
        setting_distance_TextView.setText(distance);

        change_password_button.setOnClickListener(v -> {
            // Switch to a fragment that changes the password


            fragment_manager = getSupportFragmentManager();
            ChangePasswordDialog change_password_dialog = new ChangePasswordDialog();
            change_password_dialog.show(fragment_manager, "Change_Password_Dialog");

        });

        blocked_users_button.setOnClickListener(v -> {

            fragment_manager = getSupportFragmentManager();
            BlockedUsersDialog blocked_users_dialog = new BlockedUsersDialog();
            blocked_users_dialog.show(fragment_manager, "Blocked_Users_Dialog");


        });

        terms_of_service_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment_manager = getSupportFragmentManager();
                TosDialog tos_dialog = new TosDialog();
                tos_dialog.show(fragment_manager, "TOS_Dialog");

            }
        });

        privacy_policy_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment_manager = getSupportFragmentManager();
                PPDialog pp_dialog = new PPDialog();
                pp_dialog.show(fragment_manager, "pp_Dialog");

            }
        });

        Button confirm_image_button = findViewById(R.id.confirm_image_button_id);
        confirm_image_button.setOnClickListener(v -> {


            if(is_beginner)
                progress_bar.setVisibility(View.VISIBLE);

            boolean flag_error = false;
            first_name = setting_first_name_editText.getText().toString().trim();
            last_name = setting_last_name_editText.getText().toString().trim();

            if(first_name.isEmpty()) {
                setting_first_name_editText.setError(getResources().getString(R.string.settings_first_name_edittext_error));
                flag_error = true;
            }
            if(last_name.isEmpty()) {
                setting_last_name_editText.setError(getResources().getString(R.string.settings_last_name_edittext_error));
                flag_error = true;
            }

            if(flag_error)
                return;

            JSONObject json_settings_server = null;

            UserSettings.set_user_radius(ProfileSettingsActivity.this, current_value);

            if(!(UserSettings.get_encoded_bitmap_thumbnail(getApplicationContext()).isEmpty())) {

                encoded_bitmap_thumbnail = UserSettings.get_encoded_bitmap_thumbnail(getApplicationContext());

            }

            if(!(UserSettings.get_encoded_bitmap_full(getApplicationContext()).isEmpty())) {
                encoded_bitmap_full = UserSettings.get_encoded_bitmap_full(getApplicationContext());

            }

            if(encoded_bitmap_full != null  &&  encoded_bitmap_thumbnail != null) {

                profile_name_thumbnail = uid + "_thumbnail" + ".jpg";
                profile_name_full = uid + "_full" + ".jpg";

            }

            profile_settings_activity_view_model.remote_server(app,ProfileSettingsActivity.this, progress_bar, first_name,
                    last_name, profile_name_thumbnail, profile_name_full, encoded_bitmap_thumbnail, encoded_bitmap_full, uid, id, is_beginner, dpi_classification);

            if (is_beginner) {
                
                Intent intent = new Intent(this, HardwareInventoryActivity.class);
                intent.putExtra("Beginner", true);
                startActivity(intent);

            }
            else
                finish();

        });


    }

    private void retrieve_gallery_image(){

        final String uri = "image/*";
        retrieve_image_content.launch(uri);

    }

    private void reset_profile_image(){

        DisplayMetrics display_metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display_metrics);

        if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.ldpi_label))){

            full_image_width = display_metrics.widthPixels;
            full_image_height = 375;

            thumbnail_image_width = 75;
            thumbnail_image_height = 75;

            preview_image_width = 195;
            preview_image_height = 195;

        }
        else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.mdpi_label))){

            full_image_width = display_metrics.widthPixels;
            full_image_height = 500;

            thumbnail_image_width = 100;
            thumbnail_image_height = 100;

            preview_image_width = 260;
            preview_image_height = 260;

        }
        else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.hdpi_label))){

            full_image_width = display_metrics.widthPixels;
            full_image_height = 750;

            thumbnail_image_width = 150;
            thumbnail_image_height = 150;

            preview_image_width = 390;
            preview_image_height = 390;

        }
        else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xhdpi_label))){

            full_image_width = display_metrics.widthPixels;
            full_image_height = 1000;

            thumbnail_image_width = 200;
            thumbnail_image_height = 200;

            preview_image_width = 520;
            preview_image_height = 520;

        }
        else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xxhdpi_label))){

            full_image_width = display_metrics.widthPixels;
            full_image_height = 1500;

            thumbnail_image_width = 300;
            thumbnail_image_height = 300;

            preview_image_width = 780;
            preview_image_height = 780;

        }
        else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xxxhdpi_label))){

            full_image_width = display_metrics.widthPixels;
            full_image_height = 2000;

            thumbnail_image_width = 400;
            thumbnail_image_height = 400;

            preview_image_width = 1040;
            preview_image_height = 1040;

        }


      //  profile_image_bitmap = drawable_to_bitmap(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_default_profile_image, null));
        RoundedBitmapDrawable circular_bitmap_drawable = RoundedBitmapDrawableFactory.create(getResources(), scale_bitmap(drawable_to_bitmap(ResourcesCompat.getDrawable(getResources(), R.drawable.default_profile_image, null)), preview_image_width, preview_image_height));
        circular_bitmap_drawable.setCircular(true);
        profile_image_view.setImageDrawable(circular_bitmap_drawable);

        profile_image_bitmap = drawable_to_bitmap(ResourcesCompat.getDrawable(getResources(), R.drawable.default_profile_image, null));
        profile_image_bitmap = image_saver.scale_bitmap(profile_image_bitmap, preview_image_width, preview_image_height);
       // profile_name_thumbnail = uid + "_thumbnail" + ".jpg";
       // profile_name_full = uid + "_full" + ".jpg";

        // encoded_bitmap_thumbnail = image_saver.encoded_bitmap(profile_image_bitmap, Bitmap.CompressFormat.PNG, 80);
     //   encoded_bitmap_full = image_saver.encoded_bitmap(scale_bitmap(profile_image_bitmap, full_image_width, full_image_height), Bitmap.CompressFormat.JPEG, QUALITY);
      //  encoded_bitmap_thumbnail = image_saver.encoded_bitmap(scale_bitmap(profile_image_bitmap, thumbnail_image_width, thumbnail_image_height), Bitmap.CompressFormat.JPEG, QUALITY);

        UserSettings.set_encoded_bitmap_thumbnail(getApplicationContext(), image_saver.encoded_bitmap(scale_bitmap(profile_image_bitmap, thumbnail_image_width, thumbnail_image_height), Bitmap.CompressFormat.JPEG, QUALITY));
        UserSettings.set_encoded_bitmap_full(getApplicationContext(), image_saver.encoded_bitmap(scale_bitmap(profile_image_bitmap, full_image_width, full_image_height), Bitmap.CompressFormat.JPEG, QUALITY));

    }

    private final ActivityResultLauncher<String[]> permission_request = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),

            result -> {

                for(Map.Entry<String, Boolean> entry : result.entrySet()){

                    if(entry.getValue() == false){

                        return;
                    }
                }

                launch_camera();

            });

    private final ActivityResultLauncher<Intent> capture_image_content = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK){


                        File image_file = new  File(UserSettings.get_user_local_profile_image_path(ProfileSettingsActivity.this));


                        Uri file_uri_captured = Uri.fromFile(image_file);
                        try {

                            DisplayMetrics display_metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(display_metrics);

                            if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.ldpi_label))){

                                full_image_width = display_metrics.widthPixels;
                                full_image_height = 375;

                                thumbnail_image_width = 75;
                                thumbnail_image_height = 75;

                                preview_image_width = 195;
                                preview_image_height = 195;

                            }
                            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.mdpi_label))){

                                full_image_width = display_metrics.widthPixels;
                                full_image_height = 500;

                                thumbnail_image_width = 100;
                                thumbnail_image_height = 100;

                                preview_image_width = 260;
                                preview_image_height = 260;

                            }
                            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.hdpi_label))){

                                full_image_width = display_metrics.widthPixels;
                                full_image_height = 750;

                                thumbnail_image_width = 150;
                                thumbnail_image_height = 150;

                                preview_image_width = 390;
                                preview_image_height = 390;

                            }
                            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xhdpi_label))){

                                full_image_width = display_metrics.widthPixels;
                                full_image_height = 1000;

                                thumbnail_image_width = 200;
                                thumbnail_image_height = 200;

                                preview_image_width = 520;
                                preview_image_height = 520;

                            }
                            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xxhdpi_label))){

                                full_image_width = display_metrics.widthPixels;
                                full_image_height = 1500;

                                thumbnail_image_width = 300;
                                thumbnail_image_height = 300;

                                preview_image_width = 780;
                                preview_image_height = 780;

                            }
                            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xxxhdpi_label))){

                                full_image_width = display_metrics.widthPixels;
                                full_image_height = 2000;

                                thumbnail_image_width = 400;
                                thumbnail_image_height = 400;

                                preview_image_width = 1040;
                                preview_image_height = 1040;

                            }


                            profile_image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), file_uri_captured);
                            RoundedBitmapDrawable circular_bitmap_drawable = RoundedBitmapDrawableFactory.create(getResources(), scale_bitmap(profile_image_bitmap, preview_image_width, preview_image_height));
                            circular_bitmap_drawable.setCircular(true);
                            profile_image_view.setImageDrawable(circular_bitmap_drawable);


                            profile_image_bitmap = image_saver.decode_image_from_file(UserSettings.get_user_local_profile_image_path(ProfileSettingsActivity.this), full_image_width, full_image_height);

                           UserSettings.set_encoded_bitmap_thumbnail(getApplicationContext(), image_saver.encoded_bitmap(image_saver.scale_bitmap(profile_image_bitmap, thumbnail_image_width, thumbnail_image_height),  QUALITY));
                           UserSettings.set_encoded_bitmap_full(getApplicationContext(), image_saver.encoded_bitmap(profile_image_bitmap, QUALITY));


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    else {

                        RoundedBitmapDrawable circular_bitmap_drawable = RoundedBitmapDrawableFactory.create(getResources(), profile_image_bitmap);
                        circular_bitmap_drawable.setCircular(true);
                        profile_image_view.setImageDrawable(circular_bitmap_drawable);

                    }

                }
            });

    private final ActivityResultLauncher<String> retrieve_image_content = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri uri_result) {

            if(uri_result != null) {
                try {

                    DisplayMetrics display_metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(display_metrics);

                    if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.ldpi_label))){

                        full_image_width = display_metrics.widthPixels;
                        full_image_height = 375;

                        thumbnail_image_width = 75;
                        thumbnail_image_height = 75;

                        preview_image_width = 195;
                        preview_image_height = 195;

                    }
                    else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.mdpi_label))){

                        full_image_width = display_metrics.widthPixels;
                        full_image_height = 500;

                        thumbnail_image_width = 100;
                        thumbnail_image_height = 100;

                        preview_image_width = 260;
                        preview_image_height = 260;

                    }
                    else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.hdpi_label))){

                        full_image_width = display_metrics.widthPixels;
                        full_image_height = 750;

                        thumbnail_image_width = 150;
                        thumbnail_image_height = 150;

                        preview_image_width = 390;
                        preview_image_height = 390;

                    }
                    else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xhdpi_label))){

                        full_image_width = display_metrics.widthPixels;
                        full_image_height = 1000;

                        thumbnail_image_width = 200;
                        thumbnail_image_height = 200;

                        preview_image_width = 520;
                        preview_image_height = 520;

                    }
                    else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xxhdpi_label))){

                        full_image_width = display_metrics.widthPixels;
                        full_image_height = 1500;

                        thumbnail_image_width = 300;
                        thumbnail_image_height = 300;

                        preview_image_width = 780;
                        preview_image_height = 780;

                    }
                    else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xxxhdpi_label))){

                        full_image_width = display_metrics.widthPixels;
                        full_image_height = 2000;

                        thumbnail_image_width = 400;
                        thumbnail_image_height = 400;

                        preview_image_width = 1040;
                        preview_image_height = 1040;

                    }


                    profile_image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri_result);
                    RoundedBitmapDrawable circular_bitmap_drawable = RoundedBitmapDrawableFactory.create(getResources(), scale_bitmap(profile_image_bitmap, preview_image_width, preview_image_height));
                    circular_bitmap_drawable.setCircular(true);
                    profile_image_view.setImageDrawable(circular_bitmap_drawable);

                    InputStream input_stream = getContentResolver().openInputStream(uri_result);
                    profile_image_bitmap = image_saver.decode_image_from_input_stream(ProfileSettingsActivity.this, input_stream, uri_result, full_image_width, full_image_height);

                    UserSettings.set_encoded_bitmap_thumbnail(getApplicationContext(), image_saver.encoded_bitmap(image_saver.scale_bitmap(profile_image_bitmap, thumbnail_image_width, thumbnail_image_height),  QUALITY));
                    UserSettings.set_encoded_bitmap_full(getApplicationContext(), image_saver.encoded_bitmap(profile_image_bitmap, QUALITY));

                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
            else {
                Toast.makeText(ProfileSettingsActivity.this, "Image NOT retrieved", Toast.LENGTH_LONG).show();

                //noinspection UnnecessaryReturnStatement
                return;
            }


        }
    });

    private void capture_image(){


        String[] request_permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(ContextCompat.checkSelfPermission(ProfileSettingsActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ProfileSettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            launch_camera();
        }
        else {

           permission_request.launch(request_permission);

        }

    }

    private void launch_camera(){

        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (camera_intent.resolveActivity(getPackageManager()) != null) {

            //noinspection UnusedAssignment
            File image_file = null;
            try {
                image_file = image_saver.create_image_file(ProfileSettingsActivity.this, UserSettings.get_user_uid(ProfileSettingsActivity.this), true);

            } catch (IOException ex) {

                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (image_file != null) {
                Uri image_uri = FileProvider.getUriForFile(ProfileSettingsActivity.this,
                        getString(R.string.file_provider_authority),
                        image_file);
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
                capture_image_content.launch(camera_intent);
            }

        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gesture_detector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        super.dispatchTouchEvent(ev);
        return gesture_detector.onTouchEvent(ev);


    }

    class Gesture extends GestureDetector.SimpleOnGestureListener{
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            PopupMenu profile_image_popup_menu = new PopupMenu(ProfileSettingsActivity.this, profile_image_view);
            profile_image_popup_menu.getMenuInflater().inflate(R.menu.profile_image_popup_menu, profile_image_popup_menu.getMenu());

            profile_image_popup_menu.setOnMenuItemClickListener(item -> {


                switch(item.getItemId()){

                    case R.id.menu_gallery_id:
                        retrieve_gallery_image();
                        profile_image_popup_menu.dismiss();
                        return true;

                    case R.id.menu_camera_id:
                        capture_image();
                        profile_image_popup_menu.dismiss();
                        return true;

                    case  R.id.menu_remove_id:
                        reset_profile_image();
                        profile_image_popup_menu.dismiss();
                        return true;

                }

                // profile_image_popup_menu.dismiss();
                return true;
            });


            profile_image_popup_menu.show();

        }

    }

    @Override
    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(Menu menu) {


            MenuInflater menu_inflater = getMenuInflater();
            menu_inflater.inflate(R.menu.home_activity_menu_items, menu);


            if(is_beginner){

                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(false);
                menu.getItem(2).setVisible(false);
            }

            if (menu instanceof MenuBuilder) {

                MenuBuilder menu_builder = (MenuBuilder) menu;
                menu_builder.setOptionalIconsVisible(true);
            }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){


            case R.id.profile_platforms_item_id:
                launch_profile_platforms_activity();
                return true;

            case R.id.profile_logout_item_id:
                launch_profile_logout();
                return true;

            case android.R.id.home:
                overridePendingTransition(0, 0);
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

    private void launch_profile_platforms_activity(){

        fragment_manager = getSupportFragmentManager();
        HardwareLibraryDialog hardware_library_dialog = new HardwareLibraryDialog();
        hardware_library_dialog.show(fragment_manager, "Hardware_Library_Dialog");

    }

    private void launch_profile_logout(){

        profile_settings_activity_view_model.logout(app, ProfileSettingsActivity.this);

    }


    private static Bitmap drawable_to_bitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmap_drawable = (BitmapDrawable) drawable;
            if(bitmap_drawable.getBitmap() != null) {
                return bitmap_drawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    private static Bitmap scale_bitmap(Bitmap bitmap, int wanted_width, int wanted_height) {

        Bitmap output = Bitmap.createBitmap(wanted_width, wanted_height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix matrix = new Matrix();
        matrix.setScale((float) wanted_width / bitmap.getWidth(), (float) wanted_height / bitmap.getHeight());
        canvas.drawBitmap(bitmap, matrix, new Paint());

        return output;
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onResume() {
        super.onResume();


        if(has_returned) {

            has_returned = false;

            if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.ldpi_label))){

                preview_image_width = 195;
                preview_image_height = 195;

            }
            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.mdpi_label))){



                preview_image_width = 260;
                preview_image_height = 260;

            }
            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.hdpi_label))){

                preview_image_width = 390;
                preview_image_height = 390;

            }
            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xhdpi_label))){

                preview_image_width = 520;
                preview_image_height = 520;

            }
            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xxhdpi_label))){

                preview_image_width = 780;
                preview_image_height = 780;

            }
            else if(UserSettings.get_user_dpi(ProfileSettingsActivity.this).equals(getResources().getString(R.string.xxxhdpi_label))){

                preview_image_width = 1040;
                preview_image_height = 1040;

            }


            if(profile_image_bitmap != null){

                RoundedBitmapDrawable circular_bitmap_drawable = RoundedBitmapDrawableFactory.create(getResources(), scale_bitmap(profile_image_bitmap, preview_image_width, preview_image_height));
                circular_bitmap_drawable.setCircular(true);
                profile_image_view.setImageDrawable(circular_bitmap_drawable);

            }
            else
               reset_profile_image();


            progress_bar.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle out_state) {
        super.onSaveInstanceState(out_state);

        has_returned = true;
        profile_settings_activity_view_model.set_bitmap_live_data(profile_image_bitmap); // 6
  out_state.putString(getResources().getString(R.string.profile_image_thumbnail_url_key), profile_image_thumbnail_url); // 11
        out_state.putBoolean(getResources().getString(R.string.is_beginner_key), is_beginner); // 12
        out_state.putBoolean("has_returned", has_returned); // 13

    }
}
