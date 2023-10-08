package com.syncadapters.czar.exchange.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datamodels.Users;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.http.Volley;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BlockedUsersAdapter extends  RecyclerView.Adapter<BlockedUsersAdapter.BlockedUsersViewHolder> {

    private final String TAG = "MSG";
    private final ArrayList<Users> blocked_users;
    private final WeakReference<Context> context_weak_reference;
    private WeakReference<LinearLayout> blocked_users_section_linear_layout_weak_reference;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final WeakReference<RecyclerView> blocked_users_recycler_view_weak_reference;
    private WeakReference<TextView> no_blocked_users_available_text_view_weak_reference;


    public BlockedUsersAdapter(WeakReference<Context> context_weak_reference, WeakReference<RecyclerView> blocked_users_recycler_view_weak_reference, ArrayList<Users> blocked_users){

        this.context_weak_reference = context_weak_reference;
        this.blocked_users = blocked_users;
        this.blocked_users_recycler_view_weak_reference = blocked_users_recycler_view_weak_reference;
        // UPDATE UI
        ItemTouchHelper.SimpleCallback item_touch_helper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {


            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder view_holder, int direction) {

                int position = view_holder.getAdapterPosition();
                Users user = blocked_users.get(position);
                blocked_users.remove(position);

                @SuppressWarnings("UnusedAssignment") JSONObject json_object = null;
                try {

                    json_object = new JSONObject();
                    json_object.put("ops", 2);
                    json_object.put("from_id", UserSettings.get_user_id(context_weak_reference.get()));
                    json_object.put("to_id", user.id);

                } catch (JSONException json_error) {

                    json_error.printStackTrace();
                    return;
                }

                Volley volley = new Volley(context_weak_reference.get(), Request.Method.POST, URL.BLOCKED_USER_URL, json_object);
                volley.Execute(new VolleyCallback() {
                    @Override
                    public void network_response(JSONObject json_response) {


                        if (json_response.has(context_weak_reference.get().getResources().getString(R.string.user_is_unblocked_label))) {

                            try {
                                String unblock_error = json_response.getString(context_weak_reference.get().getResources().getString(R.string.user_is_unblocked_label));
                                Toast.makeText(context_weak_reference.get(), unblock_error, Toast.LENGTH_LONG).show();
                            } catch (JSONException json_error) {

                                json_error.printStackTrace();
                                return;
                            }

                        }

                        if (json_response.has(context_weak_reference.get().getResources().getString(R.string.user_is_unblocked_label))) {

                            if (blocked_users.isEmpty()) {

                                // UPDATE UI
                                no_blocked_users_available_text_view_weak_reference.get().setVisibility(View.VISIBLE);
                                blocked_users_recycler_view_weak_reference.get().setVisibility(View.GONE);
                                blocked_users_section_linear_layout_weak_reference.get().setBackgroundColor(Color.GRAY);

                            }
                        }


                        notifyDataSetChanged();
                    }

                    @Override
                    public void network_error(VolleyError error) {

                        Toast.makeText(context_weak_reference.get(), "Network issue occurred, please try again.", Toast.LENGTH_LONG).show();

                    }
                });
            }


            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recycler_view, @NonNull RecyclerView.ViewHolder view_holder, float dX, float dY, int actionState, boolean is_currently_active) {
                super.onChildDraw(canvas, recycler_view, view_holder, dX, dY, actionState, is_currently_active);

                final ColorDrawable delete_background = new ColorDrawable(context_weak_reference.get().getResources().getColor(R.color.darker_green));
                delete_background.setBounds(0, view_holder.itemView.getTop(), (int) (view_holder.itemView.getLeft() + dX), view_holder.itemView.getBottom());
                delete_background.draw(canvas);

                Drawable unblock_user_icon = ContextCompat.getDrawable(context_weak_reference.get(), R.drawable.ic_unblock_user_yellow);

                assert unblock_user_icon != null;
                int margin = (view_holder.itemView.getHeight() - unblock_user_icon.getIntrinsicHeight()) / 2;
                int left = view_holder.itemView.getLeft() + margin;
                int right = view_holder.itemView.getLeft() + margin + unblock_user_icon.getIntrinsicWidth();
                int top = view_holder.itemView.getTop() + (view_holder.itemView.getHeight() - unblock_user_icon.getIntrinsicHeight()) / 2;
                int bottom = top + unblock_user_icon.getIntrinsicHeight();

                unblock_user_icon.setBounds(left, top, right, bottom);
                unblock_user_icon.draw(canvas);

            }
        };
        new ItemTouchHelper(item_touch_helper).attachToRecyclerView(blocked_users_recycler_view_weak_reference.get());
    }

    @NonNull
    @Override
    public BlockedUsersViewHolder onCreateViewHolder(@NonNull ViewGroup view_group, int viewType) {
        
        View view = LayoutInflater.from(view_group.getContext()).inflate(R.layout.dialog_blocked_users_items, view_group, false);

        return new BlockedUsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockedUsersViewHolder blocked_users_view_holder, int position) {

        Users user = blocked_users.get(position);
        Glide.with(this.context_weak_reference.get()).clear(blocked_users_view_holder.blocked_user_profile_image_view);
        blocked_users_view_holder.blocked_user_profile_image_view.setImageResource(android.R.color.transparent);

       Glide.with(this.context_weak_reference.get()).asDrawable()
               .load(user.user_image_thumbnail_url)
               .diskCacheStrategy(DiskCacheStrategy.ALL)
               .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
               .into(new CustomTarget<Drawable>() {
                   @Override
                   public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                       Bitmap source_bitmap = drawable_to_bitmap(resource);
                       RoundedBitmapDrawable circular_bitmap_drawable = RoundedBitmapDrawableFactory.create(context_weak_reference.get().getResources(), source_bitmap);
                       circular_bitmap_drawable.setCircular(true);
                       blocked_users_view_holder.blocked_user_profile_image_view.setImageDrawable(circular_bitmap_drawable);


                   }

                   @Override
                   public void onLoadCleared(@Nullable Drawable placeholder) {

                   }
               });


        blocked_users_view_holder.blocked_user_first_name.setText(user.first_name);

    }

    @Override
    public int getItemCount() {
        return blocked_users.size();
    }

    public void set_views(WeakReference<LinearLayout> blocked_users_section_linear_layout_weak_reference, WeakReference<TextView> no_blocked_users_available_text_view_weak_reference){

        this.blocked_users_section_linear_layout_weak_reference = blocked_users_section_linear_layout_weak_reference;
        this.no_blocked_users_available_text_view_weak_reference = no_blocked_users_available_text_view_weak_reference;

    }

    private static Bitmap drawable_to_bitmap (Drawable drawable) {
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

    public static class BlockedUsersViewHolder extends RecyclerView.ViewHolder {

        private final ImageView blocked_user_profile_image_view;
        private final TextView blocked_user_first_name;

        @SuppressWarnings("WeakerAccess")
        public BlockedUsersViewHolder(View item_by_id){
            super(item_by_id);

            this.blocked_user_profile_image_view = item_by_id.findViewById(R.id.blocked_user_profile_imageView_id);
            this.blocked_user_first_name = item_by_id.findViewById(R.id.blocked_user_first_name_textView_id);
        }

    }
}
