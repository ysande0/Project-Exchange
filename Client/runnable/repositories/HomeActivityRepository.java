package com.syncadapters.czar.exchange.repositories;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.HomeActivity;
import com.syncadapters.czar.exchange.activities.LoginActivity;
import com.syncadapters.czar.exchange.asynctasks.LoadConversationTask;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;
import com.syncadapters.czar.exchange.roomdatabase.ExchangeDatabase;
import com.syncadapters.czar.exchange.roomdatabase.MyConversationsDao;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivityRepository {

    private static final String TAG = "MSG";
    private static HomeActivityRepository home_activity_repository;
    private MyConversationsDao my_conversations_dao;

    public static HomeActivityRepository getInstance(){

        if(home_activity_repository == null)
            home_activity_repository = new HomeActivityRepository();

        return home_activity_repository;
    }

    public void initialize_database(Application application){

        ExchangeDatabase exchange_database = ExchangeDatabase.get_database(application);
        my_conversations_dao = exchange_database.my_conversations_dao();
    }

    public void update_location_resource(Context context, JSONObject json_object){

        Volley volley = new Volley(context.getApplicationContext(), Request.Method.PUT, URL.HOME_URL, json_object);
        volley.Execute(new VolleyCallback() {
            @Override
            public void network_response(JSONObject json_response) {

                if(json_response.has(context.getResources().getString(R.string.home_error_102_label))){
                    Log.d(TAG, "[HomeActivityRepository] Home Error 1");
                }

                if(json_response.has(context.getResources().getString(R.string.location_updated))){
                    Log.d(TAG, "[HomeActivityRepository] Location is Updated");
                }

            }

            @Override
            public void network_error(VolleyError error) {

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

        Volley volley = new Volley(context, Request.Method.POST, URL.LOGOUT_URL, json_object);
        volley.set_priority(Request.Priority.IMMEDIATE);
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


                     if(json_response.has(context.getResources().getString(R.string.logout_100_label))){

                         if(json_response.getBoolean(context.getResources().getString(R.string.logout_100_label))){

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

    public void set_notification_badges(App app, Context context, @SuppressWarnings("unused") BottomNavigationView bottom_navigation,
                                        UserInterface user_interface, boolean is_home_activity_foreground){

        LoadConversationTask load_conversation_task = new LoadConversationTask(context, my_conversations_dao);
        load_conversation_task.set_app(app);
        load_conversation_task.set_interface(user_interface);
       // load_conversation_task.set_bottom_navigation(bottom_navigation);
        load_conversation_task.set_home_activity_foreground(is_home_activity_foreground);
        load_conversation_task.execute();

    }

    private void launch_logout_activity(App app, Context context){

        app.set_is_user_logged_in(false);
        app.set_location_permission(false);

        UserSettings.set_is_user_logged_in(context, false);
        UserSettings.remove_user_token(context);

        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((HomeActivity) context).finish();

    }

}
