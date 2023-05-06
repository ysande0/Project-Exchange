package com.syncadapters.czar.exchange.repositories;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.adapters.BlockedUsersAdapter;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datamodels.Users;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyStringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BlockedUsersRepository {

    private final String TAG = "MSG";
    private static BlockedUsersRepository blocked_users_repository;
    private WeakReference<Context> context_weak_reference;
    private WeakReference<LinearLayout> blocked_users_section_linear_layout_weak_reference;
    private WeakReference<RecyclerView> blocked_users_recycler_view_weak_reference;
    private WeakReference<TextView> no_blocked_users_available_text_view_weak_reference;
    private WeakReference<ProgressBar> blocked_users_progress_bar_weak_reference;

    public static BlockedUsersRepository getInstance(){

        if(blocked_users_repository == null)
            blocked_users_repository = new BlockedUsersRepository();

        return blocked_users_repository;
    }

    public void query_blocked_users(Context context, LinearLayout blocked_users_section_linear_layout, RecyclerView blocked_users_recycler_view,
                                    TextView no_blocked_users_available_text_view, ProgressBar blocked_users_progress_bar){

        this.context_weak_reference =  new WeakReference<>(context);
        this.blocked_users_section_linear_layout_weak_reference = new WeakReference<>(blocked_users_section_linear_layout);
        this.blocked_users_recycler_view_weak_reference = new WeakReference<>(blocked_users_recycler_view);
        this.no_blocked_users_available_text_view_weak_reference = new WeakReference<>(no_blocked_users_available_text_view);
        this.blocked_users_progress_bar_weak_reference = new WeakReference<>(blocked_users_progress_bar);

        ArrayList<Users> blocked_users = new ArrayList<>();
        String url = URL.BLOCKED_USER_URL + "?" + "ops=" + 3 + "&" + "from_id=" + UserSettings.get_user_id(context_weak_reference.get());
        Volley volley = new Volley(context_weak_reference.get(), Request.Method.GET, url);
        volley.set_priority(Request.Priority.HIGH);
        volley.Execute(new VolleyStringCallback() {
            @Override
            public void network_response(String json_response) {

                Log.d(TAG, "[BlockedUsersRepository] " + json_response);
                blocked_users_progress_bar_weak_reference.get().setVisibility(View.GONE);
                try {

                    JSONArray json_array = new JSONArray(json_response);
                    for (int i = 0; i < json_array.length(); i++) {

                        Users user = new Users();
                        JSONObject json_object = json_array.getJSONObject(i);
                        user.id = json_object.getString(context_weak_reference.get().getResources().getString(R.string.user_id_key));
                        user.first_name = json_object.getString(context_weak_reference.get().getResources().getString(R.string.first_name_key));
                        user.user_image_name_thumbnail = json_object.getString(context_weak_reference.get().getResources().getString(R.string.profile_image_name_thumbnail_key));

                        if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.ldpi_label))){

                            user.user_image_thumbnail_url = URL.LDPI + user.user_image_name_thumbnail;

                        }
                        else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.mdpi_label))){

                            user.user_image_thumbnail_url = URL.MDPI + user.user_image_name_thumbnail;
                        }
                        else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.hdpi_label))){

                            user.user_image_thumbnail_url = URL.HDPI + user.user_image_name_thumbnail;

                        }
                        else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xhdpi_label))){

                            user.user_image_thumbnail_url = URL.XHDPI + user.user_image_name_thumbnail;

                        }
                        else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xxhdpi_label))){

                            user.user_image_thumbnail_url = URL.XXHDPI + user.user_image_name_thumbnail;
                        }
                        else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xxxhdpi_label))){

                            user.user_image_thumbnail_url = URL.XXXHDPI + user.user_image_name_thumbnail;
                        }

                        blocked_users.add(user);
                    }

                    if(blocked_users.isEmpty()){

                        Log.d(TAG, "[BlockedUsersRepository] No Blocked Users Available");
                        no_blocked_users_available_text_view_weak_reference.get().setVisibility(View.VISIBLE);
                        blocked_users_recycler_view_weak_reference.get().setVisibility(View.GONE);
                        blocked_users_section_linear_layout_weak_reference.get().setBackgroundColor(Color.GRAY);
                        return;
                    }

                    for(int i = 0; i < blocked_users.size(); i++){

                        Log.d(TAG, "[BlockedUsersRepository] " + " " + blocked_users.get(i).id + " -- " + blocked_users.get(i).first_name);

                    }

                    BlockedUsersAdapter blocked_users_adapter = new BlockedUsersAdapter(context_weak_reference, blocked_users_recycler_view_weak_reference, blocked_users);
                    blocked_users_adapter.set_views(blocked_users_section_linear_layout_weak_reference, no_blocked_users_available_text_view_weak_reference);
                    blocked_users_recycler_view_weak_reference.get().setAdapter(blocked_users_adapter);

                    Log.d(TAG, "[BlockedUsersRepository] adapter size: " + blocked_users_adapter.getItemCount());

                }catch (JSONException json_error){
                    json_error.printStackTrace();
                }

            }

            @Override
            public void network_error(VolleyError error) {

                blocked_users_progress_bar_weak_reference.get().setVisibility(View.GONE);
                Toast.makeText(context_weak_reference.get(),
                        context_weak_reference.get().getResources().getString(R.string.network_connection_error_label),
                        Toast.LENGTH_LONG).show();

            }
        });

    }


}
