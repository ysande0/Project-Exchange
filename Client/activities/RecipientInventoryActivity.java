package com.syncadapters.czar.exchange.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.adapters.SoftwareAdapter;
import com.syncadapters.czar.exchange.datamodels.Software;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datamodels.Users;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.decoration.SpacesItemDecoration;
import com.syncadapters.czar.exchange.dialogs.ImageViewerDialog;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;
import com.syncadapters.czar.exchange.interfaces.VolleyStringCallback;
import com.syncadapters.czar.exchange.viewmodels.InventoryFragmentViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

public class RecipientInventoryActivity extends AppCompatActivity {

    private static final String TAG = "MSG";
    private ImageView recipient_image_view;
    private TextView recipient_games_quantity_textView;
    private TextView no_recipient_software_available;
    private ProgressBar progress_bar;
    private RecyclerView recipient_inventory_recycler_view;
    private FragmentManager fragment_manager;
    private Users user;

    @SuppressWarnings("unused")
    private FloatingActionButton floating_action_bar;
    private ArrayList<Software> recipient_library = new ArrayList<>();
    private SoftwareAdapter software_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_inventory);


        if (getIntent().hasExtra(getResources().getString(R.string.user_key))) {

            //noinspection ConstantConditions
            user = getIntent().getExtras().getParcelable(getResources().getString(R.string.user_key));

        }

        ActionBar action_bar = getSupportActionBar();
        assert action_bar != null;
        action_bar.setDisplayHomeAsUpEnabled(true);
        action_bar.setTitle(user.first_name + "'s Library");
        action_bar.show();


        TextView recipient_first_name_textView = findViewById(R.id.recipient_first_name_textView_id);
        recipient_games_quantity_textView = findViewById(R.id.recipient_games_quantity_textView_id);
        @SuppressWarnings("unused") TextView recipient_total_posts_label_textView = findViewById(R.id.recipient_total_posts_label_textView_id);

        no_recipient_software_available = findViewById(R.id.no_recipient_software_available_textView_id);
        no_recipient_software_available.setVisibility(View.GONE);
        recipient_first_name_textView.setText(user.first_name);
        recipient_image_view = findViewById(R.id.recipient_software_dialog_image_imageView_id);

        Glide.with(RecipientInventoryActivity.this).asDrawable()
                .load(user.user_image_thumbnail_url)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .into(new CustomTarget<Drawable>() {

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);

                        Bitmap place_holder_bitmap = drawable_to_bitmap(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_default_profile_image_teal, null));
                        recipient_image_view.setImageBitmap(place_holder_bitmap);

                    }

                    @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                Bitmap source_bitmap = drawable_to_bitmap(resource);
                RoundedBitmapDrawable circular_bitmap_drawable = RoundedBitmapDrawableFactory.create(getResources(), source_bitmap);
                circular_bitmap_drawable.setCircular(true);

                recipient_image_view.setImageDrawable(circular_bitmap_drawable);
                recipient_image_view.setOnClickListener(v -> {

                    Bundle image_bundle = new Bundle();
                    image_bundle.putString("image_full_url", user.user_image_full_url);

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

        recipient_inventory_recycler_view = findViewById(R.id.recipient_inventory_recycler_view_id);
        int NUM_COLUMNS = 3;
        recipient_inventory_recycler_view.setLayoutManager(new GridLayoutManager(RecipientInventoryActivity.this, NUM_COLUMNS));
        recipient_inventory_recycler_view.setItemAnimator(new DefaultItemAnimator());
        recipient_inventory_recycler_view.addItemDecoration(new SpacesItemDecoration(10));

      progress_bar = findViewById(R.id.recipient_progress_bar_circular_id);
      progress_bar.setVisibility(View.VISIBLE);

      fragment_manager = getSupportFragmentManager();


      //  InventoryFragmentViewModel inventory_fragment_view_model = ViewModelProviders.of(this).get(InventoryFragmentViewModel.class);
        @SuppressWarnings("unused") InventoryFragmentViewModel inventory_fragment_view_model = new ViewModelProvider(this).get(InventoryFragmentViewModel.class);
        //inventory_fragment_view_model.initialize(getApplication());

        if(savedInstanceState == null){

            JSONObject json_recipient_inventory = new JSONObject();
            try {

                json_recipient_inventory.put("category", 101);
                json_recipient_inventory.put("ops", 3);
                json_recipient_inventory.put("id", user.id);
                json_recipient_inventory.put("access_token", UserSettings.get_user_token(RecipientInventoryActivity.this));

            }catch (JSONException json_error){

                json_error.printStackTrace();
            }

            final int category = 101;
            final int ops = 3;
            String id = user.id;
            String access_token = UserSettings.get_user_token(RecipientInventoryActivity.this);

            String url = URL.INVENTORY_URL + "?" + "category=" + category + "&" + "ops=" + ops + "&" + "id=" + id + "&" + "access_token=" + access_token;
            Volley volley = new Volley(getApplicationContext(), Request.Method.GET, url);
            volley.set_priority(Request.Priority.HIGH);
            volley.Execute(new VolleyStringCallback() {
                @Override
                public void network_response(String json_response) {
                    
                    try {

                        Object json_object_analyzer = new JSONTokener(json_response).nextValue();
                        if(json_object_analyzer instanceof JSONObject){

                            JSONObject json_object = new JSONObject(json_response);
                            if(json_object.has(getResources().getString(R.string.session_timeout_label))){
                                session_timeout(RecipientInventoryActivity.this);
                                return;
                            }

                        }

                        JSONArray json_array = new JSONArray(json_response);

                        if(json_array.getJSONObject(0).has(getResources().getString(R.string.database_connection_error_label))){

                            String database_connection_error_message = json_array.getJSONObject(0).getString(getResources().getString(R.string.transaction_software_error_101_3));
                            Toast.makeText(RecipientInventoryActivity.this, database_connection_error_message, Toast.LENGTH_LONG).show();
                            return;

                        }

                        if(json_array.getJSONObject(0).has(getResources().getString(R.string.transaction_software_error_101_3))){

                            String transaction_software_101_3_error_message = json_array.getJSONObject(0).getString(getResources().getString(R.string.transaction_software_error_101_3));
                            Toast.makeText(RecipientInventoryActivity.this, transaction_software_101_3_error_message, Toast.LENGTH_LONG).show();
                            return;

                        }

                        for (int i = 0; i < json_array.length(); i++) {

                            Software software = new Software();
                            JSONObject json_object = json_array.getJSONObject(i);
                            software.title = json_object.getString(getResources().getString(R.string.title_key));
                            software.platform = json_object.getString(getResources().getString(R.string.platform_key));
                            software.game_publisher = json_object.getString(getResources().getString(R.string.publisher_key));
                            software.game_developer = json_object.getString(getResources().getString(R.string.developer_key));
                            software.upc = json_object.getString(getResources().getString(R.string.upc_key));
                            software.bitmap_name_thumbnail = json_object.getString(getResources().getString(R.string.software_image_name_thumbnail_key));
                            software.bitmap_name_full = json_object.getString(getResources().getString(R.string.software_image_name_full_key));

                            if(UserSettings.get_user_dpi(RecipientInventoryActivity.this).equals(getResources().getString(R.string.ldpi_label))){

                                software.software_image_full_url = URL.LDPI + software.bitmap_name_full;
                                software.software_image_thumbnail_url = URL.LDPI + software.bitmap_name_thumbnail;

                            }
                            else if(UserSettings.get_user_dpi(RecipientInventoryActivity.this).equals(getResources().getString(R.string.mdpi_label))){

                                software.software_image_full_url = URL.MDPI + software.bitmap_name_full;
                                software.software_image_thumbnail_url = URL.MDPI + software.bitmap_name_thumbnail;

                            }
                            else if(UserSettings.get_user_dpi(RecipientInventoryActivity.this).equals(getResources().getString(R.string.hdpi_label))){

                                software.software_image_full_url = URL.HDPI + software.bitmap_name_full;
                                software.software_image_thumbnail_url = URL.HDPI + software.bitmap_name_thumbnail;

                            }
                            else if(UserSettings.get_user_dpi(RecipientInventoryActivity.this).equals(getResources().getString(R.string.xhdpi_label))){

                                software.software_image_full_url = URL.XHDPI + software.bitmap_name_full;
                                software.software_image_thumbnail_url = URL.XHDPI + software.bitmap_name_thumbnail;

                            }
                            else if(UserSettings.get_user_dpi(RecipientInventoryActivity.this).equals(getResources().getString(R.string.xxhdpi_label))){

                                software.software_image_full_url = URL.XXHDPI + software.bitmap_name_full;
                                software.software_image_thumbnail_url = URL.XXHDPI + software.bitmap_name_thumbnail;

                            }
                            else if(UserSettings.get_user_dpi(RecipientInventoryActivity.this).equals(getResources().getString(R.string.xxxhdpi_label))){

                                software.software_image_full_url = URL.XXXHDPI + software.bitmap_name_full;
                                software.software_image_thumbnail_url = URL.XXXHDPI + software.bitmap_name_thumbnail;

                            }


                            software.user_description = json_object.getString(getResources().getString(R.string.user_description_key));
                            //software.software_bitmap = user.software.software_bitmap;
                            recipient_library.add(software);
                        }

                       progress_bar.setVisibility(View.GONE);

                        if(recipient_library.isEmpty()) {

                            recipient_games_quantity_textView.setText(String.valueOf(recipient_library.size()));
                            no_recipient_software_available.setVisibility(View.VISIBLE);
                            return;
                        }

                        recipient_games_quantity_textView.setText(String.valueOf(recipient_library.size()));

                        software_adapter = new SoftwareAdapter(RecipientInventoryActivity.this, recipient_library);
                        software_adapter.set_fragment_manager(fragment_manager);
                        software_adapter.set_is_recipient_inventory(true);
                        recipient_inventory_recycler_view.setAdapter(software_adapter);


                    } catch (JSONException json_error) {
                        json_error.printStackTrace();
                    }


                }

                @Override
                public void network_error(VolleyError error) {

                }
            });

        }
        else {

            user = savedInstanceState.getParcelable(getResources().getString(R.string.user_key));
            this.recipient_library = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.recipient_library_key));

            assert recipient_library != null;
            recipient_games_quantity_textView.setText(String.valueOf(recipient_library.size()));
            software_adapter = new SoftwareAdapter(RecipientInventoryActivity.this, recipient_library);
            software_adapter.set_fragment_manager(fragment_manager);
            software_adapter.set_is_recipient_inventory(true);
            recipient_inventory_recycler_view.setAdapter(software_adapter);
           progress_bar.setVisibility(View.GONE);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle out_state) {
        super.onSaveInstanceState(out_state);

        out_state.putParcelableArrayList("recipient_library", this.recipient_library);
        out_state.putParcelable("user", user);
    
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void session_timeout(Context context){

        App app = ((App) context.getApplicationContext());

        JSONObject session_timeout_json = null;
        try{

            String email = UserSettings.get_user_email(context);
            String user_id = UserSettings.get_user_id(context);

            session_timeout_json = new JSONObject();
            session_timeout_json.put(context.getResources().getString(R.string.email_key), email);
            session_timeout_json.put(context.getResources().getString(R.string.user_id_key), user_id);


        }catch(JSONException json_error){
            Toast.makeText(context, context.getResources().getString(R.string.network_connection_error_label), Toast.LENGTH_LONG).show();
            json_error.printStackTrace();
        }

        Volley volley = new Volley(context, Request.Method.POST, URL.LOGOUT_URL, session_timeout_json);
        volley.Execute(new VolleyCallback() {
            @Override
            public void network_response(JSONObject json_response) {

                try {
                    if (json_response.has(context.getResources().getString(R.string.logout_error_100_label))) {

                        String logout_100_error_message = json_response.getString(context.getResources().getString(R.string.logout_error_100_label));
                        Toast.makeText(context, logout_100_error_message, Toast.LENGTH_LONG).show();
                        return;
                    } else if (json_response.has(context.getResources().getString(R.string.logout_error_101_label))) {

                        String logout_101_error_message = json_response.getString(context.getResources().getString(R.string.logout_error_101_label));
                        Toast.makeText(context, logout_101_error_message, Toast.LENGTH_LONG).show();
                        return;
                    } else if (json_response.has(context.getResources().getString(R.string.logout_error_103_label))) {

                        String logout_103_error_message = json_response.getString(context.getResources().getString(R.string.logout_error_103_label));
                        Toast.makeText(context, logout_103_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }


                    if (json_response.has(context.getResources().getString(R.string.logout_100_label))) {

                        if (json_response.getBoolean(context.getResources().getString(R.string.logout_100_label))) {
                            
                            launch_logout_activity(app, context);

                        }

                    }
                }catch(JSONException json_error){
                    json_error.printStackTrace();
                }


            }

            @Override
            public void network_error(VolleyError error) {

                launch_logout_activity(app, context);

            }
        });

    }


    private void launch_logout_activity(App app, Context context){

        app.set_is_user_logged_in(false);
        app.set_location_permission(false);

        UserSettings.set_is_user_logged_in(context, false);
        UserSettings.remove_user_token(context);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        (context).startActivity(intent);
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
        software_adapter = null;
        recipient_inventory_recycler_view.setAdapter(null);
    }

}
