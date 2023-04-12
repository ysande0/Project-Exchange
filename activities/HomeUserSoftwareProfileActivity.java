package com.syncadapters.czar.exchange.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datamodels.ConversationEntry;
import com.syncadapters.czar.exchange.datamodels.ImageSaver;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datamodels.Users;
import com.syncadapters.czar.exchange.dialogs.HardwareLibraryDialog;
import com.syncadapters.czar.exchange.dialogs.ImageViewerDialog;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.viewmodels.HomeUserSoftwareProfileActivityViewModel;

import org.jetbrains.annotations.NotNull;

public class HomeUserSoftwareProfileActivity extends AppCompatActivity {

    private App app;
    private static final String TAG = "MSG";

    private RelativeLayout relative_layout_home_user_profiler_header;
    private ImageView software_imageview;
    private AdView ad_view;
    private String platform_abbr;

    private FragmentManager fragment_manager;
    private HomeUserSoftwareProfileActivityViewModel home_user_software_profile_activity_view_model;
    private Users user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user_software_profile);

        Log.d(TAG, "[HomeUserSoftwareProfileActivity] onCreate");
        app = ((App) getApplicationContext());

        if(getIntent().hasExtra("user"))
            //noinspection ConstantConditions
            user = getIntent().getExtras().getParcelable("user");

        if(savedInstanceState != null){
            // Restore data
            user = savedInstanceState.getParcelable("user");
        }


        home_user_software_profile_activity_view_model = new ViewModelProvider(this).get(HomeUserSoftwareProfileActivityViewModel.class);
        ActionBar action_bar = getSupportActionBar();
        assert action_bar != null;
        action_bar.setDisplayShowHomeEnabled(true);
        action_bar.setDisplayHomeAsUpEnabled(true);
        action_bar.setDisplayUseLogoEnabled(true);

        if(user.software.platform.equals(getResources().getString(R.string.playstation_four_platform)))
            platform_abbr = getResources().getString(R.string.playstation_four_abbr);
        else if(user.software.platform.equals(getResources().getString(R.string.xbox_one_platform)))
            platform_abbr = getResources().getString(R.string.xbox_one_abbr);
        else if(user.software.platform.equals(getResources().getString(R.string.nintendo_switch_platform)))
            platform_abbr = getResources().getString(R.string.nintendo_switch_abbr);

        action_bar.setTitle(user.software.title + " (" + platform_abbr  + ")");
        action_bar.show();

        software_imageview = findViewById(R.id.home_user_software_imageView_id);


        Log.d(TAG, "[HomeUserSoftwareProfileActivity] Software Image Thumbnail URL: " + user.software.software_image_thumbnail_url);
        Log.d(TAG, "[HomeUserSoftwareProfileActivity] Software Image Full URL: " + user.software.software_image_full_url);

        Glide.with(HomeUserSoftwareProfileActivity.this).asDrawable()
                .load(user.software.software_image_full_url)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .into(new CustomTarget<Drawable>() {

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);

                        Bitmap place_holder_bitmap = drawable_to_bitmap(ResourcesCompat.getDrawable(getResources(), R.drawable.default_software_image, null));
                        software_imageview.setImageBitmap(place_holder_bitmap);


                    }

                    @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                Log.d(TAG, "[HomeUserSoftwareProfileActivity] RESOURCE IS NOT NULL");

                ImageSaver image_saver = new ImageSaver();
                Bitmap source_bitmap = drawable_to_bitmap(resource);

                        int image_width = 0;
                        int image_height = 0;


                        DisplayMetrics display_metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(display_metrics);

                        if(UserSettings.get_user_dpi(HomeUserSoftwareProfileActivity.this).equals(getResources().getString(R.string.ldpi_label))){

                            image_width = 105;
                            image_height = 105;

                        }
                        else if(UserSettings.get_user_dpi(HomeUserSoftwareProfileActivity.this).equals(getResources().getString(R.string.mdpi_label))){

                            image_width = 140;
                            image_height = 140;

                        }
                        else if(UserSettings.get_user_dpi(HomeUserSoftwareProfileActivity.this).equals(getResources().getString(R.string.hdpi_label))){

                            image_width = 210;
                            image_height = 210;

                        }
                        else if(UserSettings.get_user_dpi(HomeUserSoftwareProfileActivity.this).equals(getResources().getString(R.string.xhdpi_label))){

                            image_width = 280;
                            image_height = 280;

                        }
                        else if(UserSettings.get_user_dpi(HomeUserSoftwareProfileActivity.this).equals(getResources().getString(R.string.xxhdpi_label))){


                            image_width = 420;
                            image_height = 420;

                        }
                        else if(UserSettings.get_user_dpi(HomeUserSoftwareProfileActivity.this).equals(getResources().getString(R.string.xxxhdpi_label))){


                            image_width = 560;
                            image_height = 560;

                        }


                software_imageview.setImageBitmap(image_saver.scale_bitmap(source_bitmap, image_width, image_height));
                software_imageview.setOnClickListener(v -> {

                    Bundle image_bundle = new Bundle();
                    image_bundle.putString("image_full_url", user.software.software_image_full_url);

                    fragment_manager = getSupportFragmentManager();
                    ImageViewerDialog image_viewer_dialog = new  ImageViewerDialog();
                    image_viewer_dialog.setArguments(image_bundle);
                    image_viewer_dialog.show(fragment_manager, "Image_Viewer_Dialog");
                });

            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });

        relative_layout_home_user_profiler_header = findViewById(R.id.user_software_profile_header_relative_id);

        TextView user_first_name_textView = findViewById(R.id.home_user_software_first_name_textView_id);
        user_first_name_textView.setText(user.first_name);

        TextView software_title_textView = findViewById(R.id.home_user_software_title_textView_id);
        software_title_textView.setText(user.software.title);

        TextView software_platform_textView = findViewById(R.id.home_user_software_platform_textView_id);
        software_platform_textView.setText(user.software.platform);

        TextView software_distance_textView = findViewById(R.id.home_user_software_distance_textView_id);
        software_distance_textView.setText(user.user_distance);

        TextView user_description = findViewById(R.id.home_user_description_textView_id);

        Log.d(TAG, "Description: " + user.software.user_description);
        user_description.setText(user.software.user_description);

        load_banner_ad();

        FloatingActionButton message_floating_button = findViewById(R.id.home_user_software_message_floating_button_id);
        message_floating_button.setOnClickListener(view -> {


           ConversationEntry conversation_entry = new ConversationEntry();
            conversation_entry.current_user.first_name = UserSettings.get_user_first_name(getApplicationContext());
            conversation_entry.current_user.id = UserSettings.get_user_id(getApplicationContext());
            conversation_entry.current_user.user_image_thumbnail_url = UserSettings.get_user_profile_image_thumbnail_url(getApplicationContext());
            conversation_entry.current_user.user_image_full_url = UserSettings.get_user_profile_image_full_url(getApplicationContext());
            conversation_entry.current_user.user_image_name_thumbnail = UserSettings.get_user_profile_image_name_thumbnail(getApplicationContext());
            conversation_entry.current_user.user_image_name_full = UserSettings.get_user_profile_image_name_full(getApplicationContext());

            home_user_software_profile_activity_view_model.load_conversations(HomeUserSoftwareProfileActivity.this, user, conversation_entry,
                    UserInterface.HOME_USER_SOFTWARE_PROFILE_ACTIVITY);

        });

        FloatingActionButton view_recipient_library_floating_button = findViewById(R.id.home_user_software_recipient_library_floating_button_id);
        view_recipient_library_floating_button.setOnClickListener(v -> {

            Intent intent = new Intent(HomeUserSoftwareProfileActivity.this, RecipientInventoryActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);

        });


    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(ad_view != null) {
            ad_view.setAdListener(null);
            relative_layout_home_user_profiler_header.removeAllViews();
            relative_layout_home_user_profiler_header = null;
            //  ad_view.destroy();
            ad_view = null;
        }


    }

    private void load_banner_ad(){


        ad_view = new AdView(getApplicationContext());
        ad_view.setAdSize(AdSize.BANNER);
        String BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111";
        ad_view.setAdUnitId(BANNER_AD_ID);
        relative_layout_home_user_profiler_header.addView(ad_view);
        RelativeLayout.LayoutParams relative_layout_params_header = (RelativeLayout.LayoutParams) ad_view.getLayoutParams();
        relative_layout_params_header.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        relative_layout_params_header.addRule(RelativeLayout.CENTER_HORIZONTAL);

        ad_view.setLayoutParams(relative_layout_params_header);
        AdRequest ad_request = new AdRequest.Builder().build();
        ad_view.loadAd(ad_request);





    }

    public void onSaveInstanceState(@NotNull Bundle out_state){
        super.onSaveInstanceState(out_state);

        out_state.putParcelable("user", user);


    }

    public void onRestoreInstanceState(Bundle saved_state){
        super.onRestoreInstanceState(saved_state);

        user = saved_state.getParcelable("user");


    }

    @Override
    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menu_inflater = getMenuInflater();
        menu_inflater.inflate(R.menu.home_user_software_profile_activity_menu_items, menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder menu_builder = (MenuBuilder) menu;
            menu_builder.setOptionalIconsVisible(true);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.profile_settings_item_id:
                launch_profile_settings_activity();
                return true;

            case R.id.profile_platforms_item_id:
                launch_profile_platforms_activity();
                return true;

            case R.id.profile_logout_item_id:
                launch_profile_logout();
                return true;

            case android.R.id.home:
                overridePendingTransition(0, 0);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void launch_profile_settings_activity(){

        Intent intent = new Intent(HomeUserSoftwareProfileActivity.this, ProfileSettingsActivity.class);
        startActivity(intent);

    }

    private void launch_profile_platforms_activity(){

        fragment_manager = getSupportFragmentManager();
        HardwareLibraryDialog hardware_library_dialog = new HardwareLibraryDialog();
        hardware_library_dialog.show(fragment_manager, "Hardware_Library_Dialog");

    }

    private void launch_profile_logout(){

        home_user_software_profile_activity_view_model.logout(app, HomeUserSoftwareProfileActivity.this);

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


}
