package com.syncadapters.czar.exchange.adapters;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.asynctasks.MyHardwareTask;
import com.syncadapters.czar.exchange.datamodels.Hardware;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;
import com.syncadapters.czar.exchange.roomdatabase.MyHardware;
import com.syncadapters.czar.exchange.roomdatabase.MyHardwareDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HardwareRecycleViewAdapter extends RecyclerView.Adapter<HardwareRecycleViewAdapter.HardwareViewHolder> {

    private final String TAG = "MSG";
    private final ArrayList<Hardware> hardwares;
    private ImageButton add_hardware_platform_button;
    private TextView no_hardware_available_textView_id;
    private LinearLayout hardware_display_section_linear_layout;

    private ProgressBar progress_bar;

    public HardwareRecycleViewAdapter(Context context, ArrayList<Hardware> hardwares, MyHardwareDao my_hardware_dao, RecyclerView hardware_platform_recycle_view){

        this.hardwares = hardwares;
        /*
                                if(user_platforms.contains(context.getResources().getString(R.string.no_hardware_available_label))){

                                    user_platforms.remove(context.getResources().getString(R.string.no_hardware_available_label));
                                    user_platforms.add(hardware.platform);
                                    adapter.notifyDataSetChanged();
                                }

                                user_platforms.add(hardware.platform);
                                adapter.notifyDataSetChanged();
                                Log.d(TAG, hardware.platform + " has been deleted");
*/
        ItemTouchHelper.SimpleCallback item_touch_helper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder view_holder, int direction) {

                int position = view_holder.getAdapterPosition();
                Hardware hardware = hardwares.get(position);
                hardwares.remove(position);

                JSONObject platform_json = null;

                try {

                    platform_json = new JSONObject();
                    platform_json.put("category", 100);
                    platform_json.put("ops", 2);
                    platform_json.put("id", UserSettings.get_user_id(context));
                    platform_json.put("uid", UserSettings.get_user_uid(context));
                    platform_json.put("access_token", UserSettings.get_user_token(context));
                    platform_json.put(context.getResources().getString(R.string.manufacturer_label), hardware.manufacturer);
                    platform_json.put(context.getResources().getString(R.string.platform_label), hardware.platform);

                } catch (JSONException json_error) {
                    json_error.printStackTrace();
                }

                progress_bar.setVisibility(View.VISIBLE);
                add_hardware_platform_button.setEnabled(false);
                Volley volley = new Volley(context, Request.Method.POST, URL.INVENTORY_URL, platform_json);
                volley.Execute(new VolleyCallback() {
                    @Override
                    public void network_response(JSONObject json_response) {

                        progress_bar.setVisibility(View.GONE);
                        add_hardware_platform_button.setEnabled(true);
                        Log.d(TAG, "[HardwareRecycleViewAdapter] onSwipe network response");
                        if (json_response.has("transaction_hardware_100_2")) {


                            try {

                                if (json_response.has(context.getResources().getString(R.string.transaction_hardware_error_100_2))) {

                                    String load_user_hardware_100_2_error_message = json_response.getString(context.getResources().getString(R.string.transaction_hardware_error_100_2));
                                    Toast.makeText(context, load_user_hardware_100_2_error_message, Toast.LENGTH_LONG).show();
                                    return;

                                }

                                if (json_response.getBoolean(context.getResources().getString(R.string.transaction_hardware_100_2_label))) {

                                    MyHardware my_hardware = new MyHardware(hardware.manufacturer, hardware.platform);
                                    MyHardwareTask my_hardware_task = new MyHardwareTask(my_hardware, my_hardware_dao, DatabaseOperations.DELETE);
                                    my_hardware_task.execute();
/*
                                if(user_platforms.contains(context.getResources().getString(R.string.no_hardware_available_label))){

                                    user_platforms.remove(context.getResources().getString(R.string.no_hardware_available_label));
                                    user_platforms.add(hardware.platform);
                                    adapter.notifyDataSetChanged();
                                }

                                user_platforms.add(hardware.platform);
                                adapter.notifyDataSetChanged();
                                Log.d(TAG, hardware.platform + " has been deleted");
*/
                                    if (getItemCount() == 0) {

                                        no_hardware_available_textView_id.setVisibility(View.VISIBLE);
                                        hardware_platform_recycle_view.setVisibility(View.INVISIBLE);
                                        hardware_display_section_linear_layout.setBackgroundColor(Color.GRAY);

                                    } else if (getItemCount() > 0) {


                                        no_hardware_available_textView_id.setVisibility(View.GONE);
                                        hardware_platform_recycle_view.setVisibility(View.VISIBLE);
                                        hardware_display_section_linear_layout.setBackgroundColor(Color.WHITE);

                                    }


                                    notifyDataSetChanged();
                                }

                            } catch (JSONException json_error) {
                                json_error.printStackTrace();
                            }
                        }

                        if (json_response.has("transaction_hardware_error_100_2")) {

                            try {
                                if ((json_response.getBoolean("transaction_hardware_error_100_2")))
                                    return;

                            } catch (JSONException json_error) {
                                json_error.printStackTrace();
                            }
                        }

                        notifyDataSetChanged();
                    }

                    @Override
                    public void network_error(VolleyError error) {

                        if(error.networkResponse == null) {

                            if(error.getClass().equals(TimeoutError.class)) {
                                Toast.makeText(context,
                                        "network connective issue",
                                        Toast.LENGTH_LONG).show();
                               // launch_logout_activity();
                            }
                        }

                    }
                });



            }

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recycler_view, @NonNull RecyclerView.ViewHolder view_holder, float dX, float dY, int actionState, boolean is_currently_active) {
                super.onChildDraw(canvas, recycler_view, view_holder, dX, dY, actionState, is_currently_active);

                final ColorDrawable delete_background = new ColorDrawable(Color.RED);
                delete_background.setBounds(0, view_holder.itemView.getTop(), (int) (view_holder.itemView.getLeft() + dX), view_holder.itemView.getBottom());
                delete_background.draw(canvas);

                Drawable delete_hardware_icon = ContextCompat.getDrawable(context, R.drawable.ic_delete_conversation_entry_white);

                assert delete_hardware_icon != null;
                int margin = (view_holder.itemView.getHeight() - delete_hardware_icon.getIntrinsicHeight()) / 2;
                int left = view_holder.itemView.getLeft() + margin;
                int right = view_holder.itemView.getLeft() + margin + delete_hardware_icon.getIntrinsicWidth();
                int top = view_holder.itemView.getTop() + (view_holder.itemView.getHeight() - delete_hardware_icon.getIntrinsicHeight()) / 2;
                int bottom = top + delete_hardware_icon.getIntrinsicHeight();


                Log.d(TAG, "DELETE ICON IS NOT NULL   Top: " + top + "    Bottom: " + bottom );
                delete_hardware_icon.setBounds(left, top, right, bottom);
                delete_hardware_icon.draw(canvas);

                Bundle bundle = new Bundle();

                // Most likely newly registered
                if((UserSettings.get_latitude(context).isEmpty() || UserSettings.get_longitude(context).isEmpty()) || (UserSettings.get_latitude(context) == null || UserSettings.get_longitude(context) == null))
                    return;

                double latitude = Double.valueOf(UserSettings.get_latitude(context));
                double longitude = Double.valueOf(UserSettings.get_longitude(context));
                bundle.putDouble(context.getResources().getString(R.string.latitude), latitude);
                bundle.putDouble(context.getResources().getString(R.string.longitude), longitude);

                String email = UserSettings.get_user_email(context);
                if (email.isEmpty() || email == null)
                    return;

                Account account = new Account(email, context.getString(R.string.account_type));
                ContentResolver.requestSync(account, context.getString(R.string.content_authority), bundle);

            }
        };
        new ItemTouchHelper(item_touch_helper).attachToRecyclerView(hardware_platform_recycle_view);
    }



    @NonNull
    @Override
    public HardwareViewHolder onCreateViewHolder(@NonNull ViewGroup view_parent, int viewType) {

        View view = LayoutInflater.from(view_parent.getContext()).inflate(R.layout.activity_hardware_inventory_items, view_parent, false);

        return new HardwareViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HardwareViewHolder hardware_view_holder, int position) {

        Hardware hardware = this.hardwares.get(position);

        hardware_view_holder.hardware_platform_manufacturer.setText(hardware.manufacturer);
        hardware_view_holder.hardware_platform.setText(hardware.platform);

    }


    public void add_hardware(Hardware hardware){

        this.hardwares.add(hardware);
        notifyDataSetChanged();
    }

    public void set_progress_bar(TextView no_hardware_available_textView_id,
                                 LinearLayout hardware_display_section_linear_layout,
                                 ProgressBar progress_bar, ImageButton add_hardware_platform_button){

        this.no_hardware_available_textView_id = no_hardware_available_textView_id;
        this.hardware_display_section_linear_layout = hardware_display_section_linear_layout;
        this.progress_bar = progress_bar;
        this.add_hardware_platform_button = add_hardware_platform_button;
    }



    @Override
    public int getItemCount() {
        return this.hardwares.size();
    }

    public static class HardwareViewHolder extends RecyclerView.ViewHolder {

        private final TextView hardware_platform_manufacturer;
        private final TextView hardware_platform;

        HardwareViewHolder(View item_by_id){
            super(item_by_id);

            hardware_platform_manufacturer = item_by_id.findViewById(R.id.hardware_platform_manufacturer_textView_id);
            hardware_platform = item_by_id.findViewById(R.id.hardware_platform_textView_id);


        }

    }
}
