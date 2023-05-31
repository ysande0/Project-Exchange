package com.syncadapters.czar.exchange.repositories;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.HardwareInventoryActivity;
import com.syncadapters.czar.exchange.activities.LoginActivity;
import com.syncadapters.czar.exchange.activities.ProfileSettingsActivity;
import com.syncadapters.czar.exchange.datamodels.ImageSaver;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

@SuppressWarnings("unused")
public class ProfileSettingsRepository {

    private static final String TAG = "MSG";
    private static ProfileSettingsRepository profile_settings_repository;
    private WeakReference<ProgressBar> progress_bar_weak_reference;

    public static ProfileSettingsRepository getInstance(){

        if(profile_settings_repository == null)
            profile_settings_repository = new ProfileSettingsRepository();

        return profile_settings_repository;
    }

    public void remote_server(App app, Context context, ProgressBar progress_bar, String first_name, String last_name, String profile_name_thumbnail, String profile_name_full, String encoded_bitmap_thumbnail,
                              String encoded_bitmap_full, String uid, String id, boolean isBeginner, int dpi_classification){

        progress_bar_weak_reference = new WeakReference<>(progress_bar);
        ImageSaver image_saver = new ImageSaver();
        JSONObject json_settings_server = null;
        try{


            Log.d(TAG, "ProfileSettingsActivity: Composing settings JSON");
            Log.d(TAG, "Image Encoded Thumbnail: " + encoded_bitmap_thumbnail);
            Log.d(TAG, "Image Encoded Full: " + encoded_bitmap_full);
            json_settings_server = new JSONObject();
            json_settings_server.put("category", 100);
            json_settings_server.put("uid", uid);
            json_settings_server.put("id", id);
            json_settings_server.put(context.getResources().getString(R.string.user_dpi_label), dpi_classification);
            json_settings_server.put("first_name", first_name);
            json_settings_server.put("last_name", last_name);
            json_settings_server.put("image_name_thumbnail", profile_name_thumbnail);
            json_settings_server.put("image_name_full", profile_name_full);
            json_settings_server.put("image_encoded_full", encoded_bitmap_full);
            json_settings_server.put("image_encoded_thumbnail", encoded_bitmap_thumbnail);


        }catch(JSONException json_image_server_error){

            json_image_server_error.printStackTrace();

        }

        Volley volley = new Volley(context, Request.Method.POST, URL.UPLOAD_PROFILE_SETTINGS_URL, json_settings_server);
        volley.set_priority(Request.Priority.HIGH);
        volley.Execute(new VolleyCallback() {
            @Override
            public void network_response(JSONObject json_response) {

                if(isBeginner)
                    progress_bar_weak_reference.get().setVisibility(View.GONE);

                image_saver.delete_image_file(UserSettings.get_user_local_profile_image_path(context));
                UserSettings.remove_encoded_bitmap_thumbnail(context.getApplicationContext());
                UserSettings.remove_encoded_bitmap_full(context.getApplicationContext());

                try {

                    if(json_response.has(context.getResources().getString(R.string.session_timeout_label))){

                        session_timeout(context);
                        Log.d(TAG, "[ProfileSettingsRepository] Session Time Out");
                        Toast.makeText(context, "SESSION TIME OUT", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(json_response.has(context.getResources().getString(R.string.settings_upload_error_label))){

                        String settings_upload_error_message = json_response.getString(context.getResources().getString(R.string.settings_upload_error_label));
                        Toast.makeText(context, settings_upload_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(json_response.has(context.getResources().getString(R.string.settings_upload_label))) {

                        Log.d(TAG, "[ProfileSettingsActivity] Image has been uploaded");

                        // String local_file_path = image_saver.save_image_internal(DIRECTORY.PROFILE_DIRECTORY, profile_image_bitmap, profile_name);

                        UserSettings.set_user_first_name(context, first_name);
                        UserSettings.set_user_last_name(context, last_name);

                        if(json_response.has(context.getResources().getString(R.string.profile_image_name_thumbnail_key))) {

                            UserSettings.set_user_profile_image_name_thumbnail(context.getApplicationContext(), profile_name_thumbnail);
                            // UserSettings.set_user_local_profile_image_path(context, local_file_path);
                            //UserSettings.set_user_profile_image_thumbnail_url(context, profile_image_thumbnail_url);

                            if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.ldpi_label))){

                                UserSettings.set_user_profile_image_thumbnail_url(context,URL.LDPI + profile_name_thumbnail);
                             //   UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.LDPI + profile_image_full_url);

                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.mdpi_label))){

                                UserSettings.set_user_profile_image_thumbnail_url(context,URL.MDPI + profile_name_thumbnail);
                               // UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.MDPI + profile_image_full_url);

                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.hdpi_label))){

                                UserSettings.set_user_profile_image_thumbnail_url(context,URL.HDPI + profile_name_thumbnail);
                                //UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.HDPI + profile_image_full_url);

                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xhdpi_label))){

                                UserSettings.set_user_profile_image_thumbnail_url(context,URL.XHDPI + profile_name_thumbnail);
                              //  UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.XHDPI + profile_image_full_url);

                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xxhdpi_label))){

                                UserSettings.set_user_profile_image_thumbnail_url(context,URL.XXHDPI + profile_name_thumbnail);
                                //UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.XXHDPI + profile_image_full_url);
                                Log.d(TAG, "[ProfileSettingsActivity] profile_image_thumbnail: " + URL.XXHDPI + profile_name_thumbnail);
                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xxxhdpi_label))){

                                UserSettings.set_user_profile_image_thumbnail_url(context,URL.XXXHDPI + profile_name_thumbnail);
                              //  UserSettings.set_user_profile_image_full_url(context_weak_reference.get(),URL.XXXHDPI + profile_image_full_url);

                            }


                        }

                        if(json_response.has(context.getResources().getString(R.string.profile_image_name_full_key))){


                            UserSettings.set_user_profile_image_name_full(context.getApplicationContext(), profile_name_full);

                            if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.ldpi_label))){

                               // UserSettings.set_user_profile_image_thumbnail_url(context,URL.LDPI + profile_image_full_url);
                                   UserSettings.set_user_profile_image_full_url(context,URL.LDPI + profile_name_full);
                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.mdpi_label))){

                               // UserSettings.set_user_profile_image_thumbnail_url(context,URL.MDPI + profile_image_full_url);
                                 UserSettings.set_user_profile_image_full_url(context,URL.MDPI + profile_name_full);

                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.hdpi_label))){

                                //UserSettings.set_user_profile_image_thumbnail_url(context,URL.HDPI + profile_image_thumbnail_url);
                                UserSettings.set_user_profile_image_full_url(context,URL.HDPI + profile_name_full);

                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xhdpi_label))){

                                //UserSettings.set_user_profile_image_thumbnail_url(context,URL.XHDPI + profile_image_thumbnail_url);
                                  UserSettings.set_user_profile_image_full_url(context,URL.XHDPI + profile_name_full);

                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xxhdpi_label))){

                                //UserSettings.set_user_profile_image_thumbnail_url(context,URL.XXHDPI + profile_image_thumbnail_url);
                                UserSettings.set_user_profile_image_full_url(context,URL.XXHDPI + profile_name_full);
                                Log.d(TAG, "[ProfileSettingsActivity] profile_image_full: " + URL.XXHDPI + profile_name_full );
                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xxxhdpi_label))){

                                //UserSettings.set_user_profile_image_thumbnail_url(context,URL.XXXHDPI + profile_image_thumbnail_url);
                                UserSettings.set_user_profile_image_full_url(context,URL.XXXHDPI + profile_name_full);

                            }


                        }


                        //((ProfileSettingsActivity) context).finish();

                    }



                }catch(JSONException json_error){
                    Toast.makeText(context, context.getResources().getString(R.string.network_connection_error_label), Toast.LENGTH_LONG).show();
                    json_error.printStackTrace();
                }

            }

            @Override
            public void network_error(VolleyError error) {

                if(isBeginner)
                    progress_bar_weak_reference.get().setVisibility(View.GONE);

                Log.d(TAG, "[ProfileSettingsRepository] Volley Error: " + error.getClass().getName());
                image_saver.delete_image_file(UserSettings.get_user_local_profile_image_path(context.getApplicationContext()));
                Toast.makeText(context.getApplicationContext(),
                            context.getResources().getString(R.string.network_connection_error_label),
                            Toast.LENGTH_LONG).show();

            }
        });

    }

    public void logout(App app, Context context){

        JSONObject json_object = null;
        try{

            json_object = new JSONObject();
            json_object.put(context.getResources().getString(R.string.user_id_key), UserSettings.get_user_id(context));
            json_object.put(context.getResources().getString(R.string.email_key), UserSettings.get_user_email(context));

        }catch(JSONException json_error){

            json_error.printStackTrace();
        }

       Volley volley = new Volley(context, Request.Method.POST, URL.LOGOUT_URL, json_object);
        volley.set_priority(Request.Priority.HIGH);
        volley.Execute(new VolleyCallback() {
            @Override
            public void network_response(JSONObject json_response) {

                try {

                    if(json_response.has(context.getResources().getString(R.string.logout_error_100_label))){

                        String logout_100_error_message = json_response.getString(context.getResources().getString(R.string.logout_error_100_label));
                        Toast.makeText(context, logout_100_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(json_response.has(context.getResources().getString(R.string.logout_error_101_label))){

                        String logout_101_error_message = json_response.getString(context.getResources().getString(R.string.logout_error_101_label));
                        Toast.makeText(context, logout_101_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(json_response.has(context.getResources().getString(R.string.logout_error_103_label))){

                        String logout_103_error_message = json_response.getString(context.getResources().getString(R.string.logout_error_103_label));
                        Toast.makeText(context, logout_103_error_message, Toast.LENGTH_LONG).show();
                        return;
                    }


                    if(json_response.has("logout_100")){

                        if(json_response.getBoolean("logout_100")){

                            Log.d(TAG, "Logging out...");
                            launch_logout_activity(app, context);

                        }

                    }

                }catch(JSONException json_error){
                    Toast.makeText(context, context.getResources().getString(R.string.network_connection_error_label), Toast.LENGTH_LONG).show();
                    json_error.printStackTrace();
                }

            }

            @Override
            public void network_error(VolleyError error) {


                Toast.makeText(context, context.getResources().getString(R.string.network_connection_error_label), Toast.LENGTH_LONG).show();
                        launch_logout_activity(app, context);
            }
        });

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

                            Log.d(TAG, "Logging out...");
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        ((ProfileSettingsActivity) context).finish();


    }


}
