package com.syncadapters.czar.exchange.repositories;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.LoginActivity;
import com.syncadapters.czar.exchange.activities.ProfileSettingsActivity;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.dialogs.ChangePasswordDialog;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordRepository {

    private static final String TAG = "MSG";
    private static ChangePasswordRepository change_password_repository;
    private Volley volley;

    public static ChangePasswordRepository getInstance(){

        if(change_password_repository == null)
            change_password_repository = new ChangePasswordRepository();

        return change_password_repository;
    }

    public void remote_server(Context context, ChangePasswordDialog change_password_dialog, ProgressBar progress_bar, String password){

        JSONObject json_password_change;

        try{

            json_password_change = new JSONObject();
            json_password_change.put("password", password);
            json_password_change.put("uid", UserSettings.get_user_uid(context));
            json_password_change.put("category", 101);
            json_password_change.put("id", UserSettings.get_user_id(context));
            json_password_change.put("access_token", UserSettings.get_user_token(context));

        }catch(JSONException json_error){
            json_error.printStackTrace();
            return;
        }


        volley = new Volley(context.getApplicationContext(), Request.Method.POST , URL.UPLOAD_PROFILE_SETTINGS_URL, json_password_change);
        volley.set_priority(Request.Priority.HIGH);
        volley.Execute(new VolleyCallback() {
            @Override
            public void network_response(JSONObject json_response) {
                Log.d(TAG, "ChangePasswordDialog: network_response");

                progress_bar.setVisibility(View.GONE);
                if(json_response.has(context.getResources().getString(R.string.session_timeout_label))) {
                    Log.d(TAG, "[ChangePasswordDialog] Session Timeout");
                    session_timeout(context);
                }

                if(json_response.has(context.getResources().getString(R.string.settings_upload_label))) {

                    Log.d(TAG, "[ChangePasswordDialog] password settings have been updated");
                    //((ProfileSettingsActivity) context).finish();
                    change_password_dialog.dismiss();
                }


            }

            @Override
            public void network_error(VolleyError error) {
                Log.d(TAG, "ChangePasswordFragment: network_error" );
                progress_bar.setVisibility(View.GONE);

                if(error.networkResponse == null){

                    if(error.getClass().equals(TimeoutError.class)){

                        Toast.makeText(context,
                                "Timeout error occurred. Please try again",
                                Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(context, context.getResources().getString(R.string.network_connection_error_label), Toast.LENGTH_LONG).show();

                }

            }
        });

    }


    private void session_timeout(Context context){

        FirebaseAuth.getInstance().signOut();
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

                            Log.d(TAG, "[ChangePasswordDialog] Logging out...");
                            launch_logout_activity(app, context);

                        }

                    }
                }catch(JSONException json_error){
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

    public void logout(App app, Context context){

        FirebaseAuth.getInstance().signOut();
        JSONObject json_object = null;
        try{

            json_object = new JSONObject();
            json_object.put(context.getResources().getString(R.string.user_id_key), UserSettings.get_user_id(context));
            json_object.put(context.getResources().getString(R.string.email_key), UserSettings.get_user_email(context));

        }catch(JSONException json_error){

            json_error.printStackTrace();
        }

        volley = new Volley(context, Request.Method.POST, URL.LOGOUT_URL, json_object);
        volley.set_priority(Request.Priority.IMMEDIATE);
        //noinspection unused
        volley.Execute(new VolleyCallback() {
            @Override
            public void network_response(JSONObject json_response) {

                try {
                    if(json_response.has("logout_100")){

                        if(json_response.getBoolean("logout_100")){

                            Log.d(TAG, "Logging out...");
                            launch_logout_activity(app, context);

                        }

                    }
                    else if(json_response.has("logout_error_100")){

                        if(json_response.getBoolean("logout_error_100")) {
                            Log.d(TAG, "logout_error_100");
                        }
                    }

                }catch(JSONException json_error){
                    json_error.printStackTrace();
                }

            }

            @SuppressWarnings("unused")
            @Override
            public void network_error(VolleyError error) {

                Toast.makeText(context, context.getResources().getString(R.string.network_connection_error_label), Toast.LENGTH_LONG).show();
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
