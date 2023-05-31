package com.syncadapters.czar.exchange.repositories;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.LoginActivity;
import com.syncadapters.czar.exchange.asynctasks.HomeTask;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datamodels.Users;
import com.syncadapters.czar.exchange.datautility.FORMATS;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;
import com.syncadapters.czar.exchange.interfaces.VolleyStringCallback;
import com.syncadapters.czar.exchange.roomdatabase.ExchangeDatabase;
import com.syncadapters.czar.exchange.roomdatabase.Home;
import com.syncadapters.czar.exchange.roomdatabase.HomeDao;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class UserRepository {

    /*
    *  NOTE: Make glide call to image
    *  NOTE: Insert user into MutableLiveData
    * */


    private static final String TAG = "HOME";
    private static UserRepository user_repository;
    private final MutableLiveData<ArrayList<Users>> users_data = new MutableLiveData<>();
    private ExchangeDatabase exchange_database;
    private HomeDao home_dao;

    public static UserRepository getInstance(){

        if(user_repository == null)
            user_repository = new UserRepository();

        return user_repository;
    }


    public void initialize_database(Context context){

        exchange_database = ExchangeDatabase.get_database(context.getApplicationContext());
        home_dao = exchange_database.home_dao();


        Log.d(TAG, "Home DAO is initialize. dao is not null");
        HomeTask home_task = new HomeTask(users_data, home_dao);
        home_task.set_context(context.getApplicationContext());
        home_task.set_database_operation(DatabaseOperations.READ);
        home_task.execute();

    }


    @Subscribe
  private void remote_server(final Context context, String url){


          //   Log.d(TAG, "Remote server...");

        // Retrieve json object from Network Call
      exchange_database = ExchangeDatabase.get_database(context.getApplicationContext());
      this.home_dao = exchange_database.home_dao();
        Volley volley = new Volley(context.getApplicationContext(), Request.Method.GET, url);
        volley.set_priority(Request.Priority.HIGH);
        volley.Execute(new VolleyStringCallback() {
            @Override
            public void network_response(String json_response) {

              Log.d(TAG, "[UserRepository] Raw Server " + json_response);

                if(json_response.isEmpty()) {

                    Log.d(TAG, "[UserRepository] No Users Available");

                    return;
                }

                JSONArray server_response = null;
              try{

                   server_response = new JSONArray(json_response);

                   if(server_response.length() == 1){

                      JSONObject server_response_error_message = server_response.getJSONObject(0);


                      if(server_response_error_message.has(context.getResources().getString(R.string.session_timeout_label))){

                          session_timeout(context);
                          return;
                      }

                      if(server_response_error_message.has(context.getResources().getString(R.string.home_error_100_label))){

                          String home_error_100_message = context.getResources().getString(R.string.home_error_100_label);
                          Toast.makeText(context, home_error_100_message, Toast.LENGTH_LONG).show();
                          return;

                      }
                      else if(server_response_error_message.has(context.getResources().getString(R.string.home_error_101_label))){

                          String home_error_101_message = context.getResources().getString(R.string.home_error_101_label);
                          Toast.makeText(context, home_error_101_message, Toast.LENGTH_LONG).show();
                          return;
                      }
                      else if(server_response_error_message.has(context.getResources().getString(R.string.home_error_102_label))){

                          String home_error_102_message = context.getResources().getString(R.string.home_error_102_label);
                          Toast.makeText(context, home_error_102_message, Toast.LENGTH_LONG).show();
                          return;

                      }


                  }


              }catch (JSONException json_error){
                  json_error.printStackTrace();
              }


              if(server_response == null){

                  Log.d(TAG, "[UserRepository] No Users Available. server_response array is null");

                  return;
              }

                long id = 1;
                String time = String.format(Locale.ENGLISH, FORMATS.time, Calendar.getInstance().getTime());
                String date = String.format(Locale.ENGLISH, FORMATS.date, Calendar.getInstance().getTime());

                Home home = new Home(id, server_response.toString(), time, date);
                HomeTask home_task = new HomeTask(home, home_dao, DatabaseOperations.UPDATE);
                home_task.execute();

                Log.d(TAG, "[UserRepository] Users Updated");

            }

            @Override
            public void network_error(VolleyError error) {
                Log.d(TAG, "UserRepository Error: " + error.getMessage());
                error.printStackTrace();

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
                            app.set_is_user_logged_in(false);
                            app.set_location_permission(false);

                            Toast.makeText(context.getApplicationContext(), "SESSION TIMEOUT", Toast.LENGTH_LONG).show();
                            UserSettings.set_is_user_logged_in(context.getApplicationContext(), false);
                            UserSettings.remove_user_token(context.getApplicationContext());

                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            (context).startActivity(intent);

                        }

                    }
                }catch(JSONException json_error){
                    json_error.printStackTrace();
                }


            }

            @Override
            public void network_error(VolleyError error) {

            }
        });

    }

    private void update_local_database(Context context, String url){

        remote_server(context, url);

    }


    public void local_server(Context context, String url, DatabaseOperations db_ops){


        switch(db_ops){

            // Called by sync adapter
            case UPDATE:
                update_local_database(context, url);
                break;

             // Called by HomeFragmentViewModel
            case READ:
                 break;


        }

    }

    @Subscribe
    public MutableLiveData<ArrayList<Users>> get_users_data(){
        // Make network call here;
        return users_data;
    }


}
