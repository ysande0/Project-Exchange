package com.syncadapters.czar.exchange.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datamodels.Users;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.roomdatabase.Home;
import com.syncadapters.czar.exchange.roomdatabase.HomeDao;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class HomeTask extends AsyncTask<Home, Void, ArrayList<Users>> {

    private static final String TAG = "MSG";
    private WeakReference<Context> context_weak_reference;
    private MutableLiveData<ArrayList<Users>> users_data = new MutableLiveData<>();
    private Home home;
    private final HomeDao home_dao;
    private DatabaseOperations database_operations;

    public HomeTask(MutableLiveData<ArrayList<Users>> users_data, HomeDao home_dao){

        this.users_data = users_data;
        this.home_dao = home_dao;

    }

    public HomeTask(Home home, HomeDao home_dao, DatabaseOperations database_operations){

        this.home = home;
        this.home_dao = home_dao;
        this.database_operations = database_operations;

    }

    public void set_context(Context context){

        this.context_weak_reference = new WeakReference<>(context);
    }


    public void set_database_operation(DatabaseOperations database_operation){

        this.database_operations = database_operation;
    }

    private String distance_adjustment(String c_dist){

        double distance = Double.parseDouble(c_dist);

        BigDecimal big_decimal = new BigDecimal(distance);
        big_decimal = big_decimal.round(new MathContext(1));

        double rounded_distance_miles = big_decimal.doubleValue();

        String distance_adjusted;
        if(rounded_distance_miles < 1.00000){

            double distance_feet = rounded_distance_miles * 5280;

            BigDecimal decimal_precision = new BigDecimal(distance_feet);
            decimal_precision = decimal_precision.round(new MathContext(1));
            distance_feet = decimal_precision.doubleValue();
            distance_adjusted = distance_feet + " ft";

        }
        else if(rounded_distance_miles >= 1.00000)
            distance_adjusted = rounded_distance_miles + " miles";
        else
            distance_adjusted = "Not Available";

        return distance_adjusted;
    }

    @Override
    protected ArrayList<Users> doInBackground(Home... homes) {

        if(database_operations == DatabaseOperations.INSERT){

            Log.d(TAG, "HomeTask INSERT");
            List<Home> home_users = this.home_dao.query_users();
            if(home_users.isEmpty()){

                this.home_dao.insert(this.home);
            }
            else
                return null;

        }
        else if(database_operations == DatabaseOperations.UPDATE){

            Log.d(TAG, "HomeTask UPDATE");
            this.home_dao.update(home);

        }
        else if(database_operations == DatabaseOperations.READ){

             Log.d(TAG, "HomeTask READ");
             ArrayList<Users> users_data = new ArrayList<>();

            List<Home> home_users = this.home_dao.query_users();

            if(home_users.isEmpty()){

                Log.d(TAG, "1) [HomeTask] No Users Available");
                return users_data;
            }

            String users_response = home_users.get(0).get_home_response();
/*
            if(users_response.equals("NO_USERS")){

                Log.d(TAG, "[HomeTask] No Users Available");
                return users_data;
            }

 */

            if(users_response == null){

                Log.d(TAG, "[HomeTask] user_response is NULL");
                Log.d(TAG, "2) [HomeTask] No Users Available");
                return users_data;
            }

            Log.d(TAG, "[HomeTask] user_response: " + users_response);
            if( users_response.equals("[]")) {

                Log.d(TAG, "3) [HomeTask] No Users Available");
                return users_data;
            }


            try{

                JSONArray json_array = new JSONArray(users_response);
                for(int i = 0; i < json_array.length(); i++){

                    Users user = new Users();
                    user.id = json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.user_id_key));
                    Log.d(TAG, "HomeTask ID: " + user.id);
                    user.first_name = json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.first_name_key));
                   // user.fcm_token = json_array.getJSONObject(i).getString("fcm_token");

                    if(UserSettings.get_user_dpi(this.context_weak_reference.get()).equals(this.context_weak_reference.get().getResources().getString(R.string.ldpi_label))){

                        user.user_image_thumbnail_url = URL.LDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));
                        user.user_image_full_url = URL.LDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key));
                    }
                    else if(UserSettings.get_user_dpi(this.context_weak_reference.get()).equals(this.context_weak_reference.get().getResources().getString(R.string.mdpi_label))){

                        user.user_image_thumbnail_url = URL.MDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));
                        user.user_image_full_url = URL.MDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key));

                    }
                    else if(UserSettings.get_user_dpi(this.context_weak_reference.get()).equals(this.context_weak_reference.get().getResources().getString(R.string.hdpi_label))){

                        user.user_image_thumbnail_url = URL.HDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));
                        user.user_image_full_url = URL.HDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key));

                    }
                    else if(UserSettings.get_user_dpi(this.context_weak_reference.get()).equals(this.context_weak_reference.get().getResources().getString(R.string.xhdpi_label))){

                        user.user_image_thumbnail_url = URL.XHDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));
                        user.user_image_full_url = URL.XHDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key));

                    }
                    else if(UserSettings.get_user_dpi(this.context_weak_reference.get()).equals(this.context_weak_reference.get().getResources().getString(R.string.xxhdpi_label))){

                        user.user_image_thumbnail_url = URL.XXHDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));
                        user.user_image_full_url = URL.XXHDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key));

                    }
                    else if(UserSettings.get_user_dpi(this.context_weak_reference.get()).equals(this.context_weak_reference.get().getResources().getString(R.string.xxxhdpi_label))){

                        user.user_image_thumbnail_url = URL.XXXHDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));
                        user.user_image_full_url = URL.XXXHDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_full_key));

                    }

                    user.user_distance = distance_adjustment(json_array.getJSONObject(i).getString("cdist"));


                    user.software.title = json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.title_key));
                    Log.d(TAG, "[HomeTask] User Software Title: " + user.software.title);
                    user.software.platform = json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.platform_key));

                    user.software.game_publisher = json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.publisher_key));
                    user.software.game_developer = json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.developer_key));
                    user.software.upc = json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.upc_key));
                    user.software.user_description = json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.user_description_key));
                    Log.d(TAG, "[HomeTask] User Description: " + user.software.user_description);

                    if(UserSettings.get_user_dpi(this.context_weak_reference.get()).equals(this.context_weak_reference.get().getResources().getString(R.string.ldpi_label))){

                        user.software.software_image_thumbnail_url = URL.LDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_thumbnail_key));
                        user.software.software_image_full_url = URL.LDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_full_key));
                    }
                    else if(UserSettings.get_user_dpi(this.context_weak_reference.get()).equals(this.context_weak_reference.get().getResources().getString(R.string.mdpi_label))){

                        user.software.software_image_thumbnail_url = URL.MDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_thumbnail_key));
                        user.software.software_image_full_url = URL.MDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_full_key));

                    }
                    else if(UserSettings.get_user_dpi(this.context_weak_reference.get()).equals(this.context_weak_reference.get().getResources().getString(R.string.hdpi_label))){

                        user.software.software_image_thumbnail_url = URL.HDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_thumbnail_key));
                        user.software.software_image_full_url = URL.HDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_full_key));

                    }
                    else if(UserSettings.get_user_dpi(this.context_weak_reference.get()).equals(this.context_weak_reference.get().getResources().getString(R.string.xhdpi_label))){

                        user.software.software_image_thumbnail_url = URL.XHDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_thumbnail_key));
                        user.software.software_image_full_url = URL.XHDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_full_key));

                    }
                    else if(UserSettings.get_user_dpi(this.context_weak_reference.get()).equals(this.context_weak_reference.get().getResources().getString(R.string.xxhdpi_label))){

                        user.software.software_image_thumbnail_url = URL.XXHDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_thumbnail_key));
                        user.software.software_image_full_url = URL.XXHDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_full_key));

                    }
                    else if(UserSettings.get_user_dpi(this.context_weak_reference.get()).equals(this.context_weak_reference.get().getResources().getString(R.string.xxxhdpi_label))){

                        user.software.software_image_thumbnail_url = URL.XXXHDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_thumbnail_key));
                        user.software.software_image_full_url = URL.XXXHDPI + json_array.getJSONObject(i).getString(context_weak_reference.get().getResources().getString(R.string.software_image_name_full_key));

                    }

                    Log.d(TAG, "[HomeTask] User Software Image Thumbnail URL: " + user.software.software_image_thumbnail_url);
                    Log.d(TAG, "[HomeTask] User Software Full URL: " + user.software.software_image_full_url);


                    users_data.add(user);

                }


            }catch (JSONException json_error){
                json_error.printStackTrace();
            }

            Log.d(TAG, "[HomeTask] Users Available");

            return users_data;
        }


        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Users> users_data) {
        super.onPostExecute(users_data);

        if(users_data == null || users_data.isEmpty()){

            Log.d(TAG, "No Home entries exiting...");
            this.users_data.setValue(users_data);
            return;
        }

        if(this.database_operations == DatabaseOperations.READ){

            Log.d(TAG, "Generating objects...");
            this.users_data.setValue(users_data);
            Log.d(TAG, "HomeTask Finished loading users");
        }

    }
}
