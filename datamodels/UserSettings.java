package com.syncadapters.czar.exchange.datamodels;

import android.content.Context;
import android.content.SharedPreferences;

import com.syncadapters.czar.exchange.R;

@SuppressWarnings("ALL")
public class UserSettings {

    private static SharedPreferences user_preferences;
    private static final String USER_SETTINGS = "preferences";

    public static void set_user_id(Context context, String user_id){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.user_id_key), user_id);
        editor.apply();

    }

    public static void set_user_uid(Context context, String user_uid){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.uid_key), user_uid);
        editor.apply();

    }

    public static void set_user_first_name(Context context, String user_first_name){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.first_name_key), user_first_name);
        editor.apply();
    }

    public static void set_user_last_name(Context context, String user_last_name){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.last_name_key), user_last_name);
        editor.apply();

    }

    public static void set_user_email(Context context, String user_email){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.email_key), user_email);
        editor.apply();

    }

    public static void set_fcm_token(Context context, String fcm_token){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.fcm_token_key), fcm_token);
        editor.apply();

    }

    public static void set_user_token(Context context, String user_token){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.access_token_key), user_token);
        editor.apply();

    }

    public static void set_dpi(Context context, String user_dpi){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.user_dpi_label), user_dpi);
        editor.apply();

    }

    @SuppressWarnings("unused")
    public static void set_local_user_profile_image_url(Context context, String local_profile_image_url){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.local_profile_image_path_key), local_profile_image_url);
        editor.apply();

    }


    public static void set_user_profile_image_thumbnail_url(Context context, String user_profile_image_thumbnail_url){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.profile_image_thumbnail_url_key), user_profile_image_thumbnail_url);
        editor.apply();

    }


    public static void set_user_profile_image_full_url(Context context, String user_profile_image_full_url){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.profile_image_full_url_key), user_profile_image_full_url);
        editor.apply();

    }

    public static void set_user_profile_image_name_thumbnail(Context context, String user_profile_image_name_thumbnail){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.profile_image_name_thumbnail_key), user_profile_image_name_thumbnail);
        editor.apply();

    }

    public static void set_user_profile_image_name_full(Context context, String user_profile_image_name_full){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.profile_image_name_full_key), user_profile_image_name_full);
        editor.apply();

    }

    @SuppressWarnings("unused")
    public static void set_user_local_profile_image_path(Context context, String user_local_profile_image_path){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.local_profile_image_path_key), user_local_profile_image_path);
        editor.apply();

    }

    @SuppressWarnings("unused")
    public static void set_user_local_software_image_path(Context context, String user_local_software_images_path){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.local_software_image_path_key), user_local_software_images_path);
        editor.apply();

    }

    public static void set_user_radius(Context context, int user_radius){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putInt(context.getResources().getString(R.string.radius_key), user_radius);
        editor.apply();

    }

    public static void set_latitude_and_longitude(Context context, double latitude, double longitude){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.latitude), String.valueOf(latitude));
        editor.putString(context.getResources().getString(R.string.longitude), String.valueOf(longitude));
        editor.apply();


    }

    public static void set_is_user_logged_in(Context context, boolean is_user_logged_in){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.is_user_logged_in), is_user_logged_in);
        editor.apply();


    }

    public static void set_encoded_bitmap_thumbnail(Context context, String encoded_bitmap_thumbnail){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.image_encoded_thumbnail_key), encoded_bitmap_thumbnail);
        editor.apply();

    }

    public static void set_encoded_bitmap_full(Context context, String encoded_bitmap_full){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putString(context.getResources().getString(R.string.image_encoded_full_key), encoded_bitmap_full);
        editor.apply();

    }

    public static String get_user_id(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);

        return user_preferences.getString(context.getResources().getString(R.string.user_id_key), "");
    }

    public static String get_user_uid(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);

        return user_preferences.getString(context.getResources().getString(R.string.uid_key), "");
    }

    public static String get_user_first_name(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);

        return user_preferences.getString(context.getResources().getString(R.string.first_name_key), "");
    }

    public static String get_user_last_name(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);

        return user_preferences.getString(context.getResources().getString(R.string.last_name_key), "");
    }

    public static String get_user_email(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);

        return user_preferences.getString(context.getResources().getString(R.string.email_key), "");
    }

    public static String get_fcm_token(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);

        return user_preferences.getString(context.getResources().getString(R.string.fcm_token_key), "");

    }

    public static String get_user_token(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);

        return user_preferences.getString(context.getResources().getString(R.string.access_token_key), "");
    }

    public static String get_user_profile_image_thumbnail_url(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        return user_preferences.getString(context.getResources().getString(R.string.profile_image_thumbnail_url_key), "");
    }

    public static String get_user_profile_image_full_url(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        return user_preferences.getString(context.getResources().getString(R.string.profile_image_full_url_key), "");

    }

    public static String get_user_profile_image_name_thumbnail(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        return user_preferences.getString(context.getResources().getString(R.string.profile_image_name_thumbnail_key), "");

    }

    public static String get_user_profile_image_name_full(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        return user_preferences.getString(context.getResources().getString(R.string.profile_image_name_full_key), "");

    }

    public static String get_user_dpi(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        return user_preferences.getString(context.getResources().getString(R.string.user_dpi_label), "");

    }

    @SuppressWarnings("unused")
    public static String get_user_local_profile_image_path(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        return user_preferences.getString(context.getResources().getString(R.string.local_profile_image_path_key), "");

    }

    @SuppressWarnings("unused")
    public static String get_user_local_software_image_path(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        return user_preferences.getString(context.getResources().getString(R.string.local_software_image_path_key), "");
    }

    public static int get_user_radius(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        return user_preferences.getInt(context.getResources().getString(R.string.radius_key), 1);
    }

    public static String get_latitude(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        return user_preferences.getString(context.getResources().getString(R.string.latitude), null);

    }

    public static String get_longitude(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        return user_preferences.getString(context.getResources().getString(R.string.longitude), null);

    }

    public static boolean get_is_user_logged_in(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);

        return user_preferences.getBoolean(context.getResources().getString(R.string.is_user_logged_in), false);

    }

    public static String get_encoded_bitmap_thumbnail(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        return user_preferences.getString(context.getResources().getString(R.string.image_encoded_thumbnail_key), "");

    }

    public static String get_encoded_bitmap_full(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        return user_preferences.getString(context.getResources().getString(R.string.image_encoded_full_key), "");

    }

    @SuppressWarnings("unused")
    public static void remove_user_data(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.clear();
        editor.apply();

    }

    public static void remove_user_token(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.remove(context.getResources().getString(R.string.access_token_key));
        editor.apply();

    }

    public static void remove_encoded_bitmap_thumbnail(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.remove(context.getResources().getString(R.string.image_encoded_thumbnail_key));
        editor.apply();

    }

    public static void remove_encoded_bitmap_full(Context context){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.remove(context.getResources().getString(R.string.image_encoded_full_key));
        editor.apply();

    }



    public static boolean has(Context context, String key){

        user_preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        if(user_preferences.contains(key))
            return true;
        else if(!(user_preferences.contains(key)))
            return false;
        else
            throw new RuntimeException("Cannot determine if " + key + " exists");
    }

}
