package com.syncadapters.czar.exchange.adapters;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.repositories.UserRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private final Context context;
    private static final String user_settings = "preferences";
    private double latitude;
    private double longitude;

    public SyncAdapter(Context context, boolean auto_initialize){
       super(context, auto_initialize);

       this.context = context;

       latitude = 0.0;
       longitude = 0.0;
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        String TAG = "MSG";

        if(extras.isEmpty()) {
            return;
        }
        
        latitude = extras.getDouble("latitude");
        longitude = extras.getDouble("longitude");

            SharedPreferences user_preferences = context.getApplicationContext().getSharedPreferences(user_settings, Context.MODE_PRIVATE);
            String uid = user_preferences.getString("uid", "");
            String id = UserSettings.get_user_id(this.context.getApplicationContext());
            String access_token = UserSettings.get_user_token(context.getApplicationContext());
            int user_radius = user_preferences.getInt("radius", 100);

            JSONObject json_object = new JSONObject();
            try {
                json_object.put("uid", uid);
                json_object.put("id", id);
                json_object.put("access_token", UserSettings.get_user_token(getContext()));
                json_object.put("latitude", latitude);
                json_object.put("longitude", longitude);
                json_object.put("radius", user_radius);

                // Get Latitude and Longitude data
            }
            catch (JSONException json_error){
                json_error.printStackTrace();
            }

          JSONArray json_array = new JSONArray();
            json_array.put(json_object);

            String url = URL.HOME_URL + "?" + "latitude=" + latitude + "&" + "longitude=" + longitude + "&" + "radius=" + user_radius + "&" + "uid=" + uid + "&" + "id=" + id + "&" + "access_token=" + access_token;
            UserRepository user_repository = UserRepository.getInstance();
            user_repository.local_server(this.context.getApplicationContext(), url, DatabaseOperations.UPDATE);

    }


}
