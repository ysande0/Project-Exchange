package com.syncadapters.czar.exchange.adapters;

import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.ads.AdView;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.HomeUserSoftwareProfileActivity;
import com.syncadapters.czar.exchange.datamodels.Users;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UsersRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String TAG = "MSG";
    private ArrayList<Object> users_list;
    private final Context context;
    private final RequestManager glide_request_manager;
    private static final int ITEM_USER = 1;
  //  private static final int ITEM_AD = 2;
    // --Commented out by Inspection (1/9/2021 11:54 PM):private static final int ITEM_PER_AD = 4;


    public UsersRecycleViewAdapter(Context context, RequestManager glide_request_manager){

        this.context = context;
        this.glide_request_manager = glide_request_manager;
    }

    public void set_users(ArrayList<Object> users_list){

        this.users_list = users_list;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int view_type) {

        Log.d(TAG, "onCreateViewHolder");


        //noinspection SwitchStatementWithTooFewBranches
        switch(view_type){

            case ITEM_USER:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_home_items, viewGroup, false);
                return new UsersViewHolder(view);
/*
            case ITEM_AD:
                View banner_view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_home_banner_ad_item, viewGroup, false);
                return new BannerAdViewHolder(banner_view);

 */
        }

        //noinspection ConstantConditions
        return null;
    }

    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView){

        super.onAttachedToRecyclerView(recyclerView);



    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder view_holder, int position) {

        Log.d(TAG, "onBindViewHolder");

        int view_type = getItemViewType(position);

        switch(view_type){

            case ITEM_USER:
                    user_layout((UsersViewHolder) view_holder, position);
                break;
        }

    }

    @Override
    public int getItemCount() {

        if(users_list == null || users_list.size() == 0)
            return 0;

        return users_list.size();
    }

    @Override
    public int getItemViewType(int position) {

        /*
        if(position == 0)
            return ITEM_AD;
        else
            return ITEM_USER;

         */
        return ITEM_USER;
    }

    private void user_layout(UsersViewHolder users_view_holder, int position){

        Object item = users_list.get(position);
        if(item instanceof AdView)
            return;

        Users user = (Users) users_list.get(position);
        // Cache the images


        glide_request_manager.asDrawable()
                .load(user.software.software_image_thumbnail_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .into(new CustomTarget<Drawable>() {

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);


                    }

                    @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                Log.d(TAG, "UsersRecycleViewAdapter RESOURCE IS NOT NULL");

                Bitmap source_bitmap = drawable_to_bitmap(resource);
                        Bitmap round_bitmap  = Bitmap.createBitmap(source_bitmap.getWidth(), source_bitmap.getHeight(), source_bitmap.getConfig());

                        Canvas canvas = new Canvas(round_bitmap);
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setShader(new BitmapShader(source_bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                        canvas.drawRoundRect((new RectF(0, 0, source_bitmap.getWidth(), source_bitmap.getHeight())), 20, 20, paint);
                users_view_holder.user_software_imageView.setImageBitmap(round_bitmap);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });


        users_view_holder.first_name_TextView.setText(user.first_name);
        users_view_holder.title_textView.setText(user.software.title);
        users_view_holder.platform_textView.setText(user.software.platform);
        users_view_holder.distance_textView.setText(user.user_distance);


        users_view_holder.set_user(user);
        users_view_holder.set_context(context);


    }

    private static Bitmap drawable_to_bitmap(Drawable drawable) {
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


    static class UsersViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView user_software_imageView;
      //  private ImageView user_picture_imageView;
        private final TextView first_name_TextView;
        private final TextView title_textView;
        private final TextView platform_textView;
        private final TextView distance_textView;

        private Context context;
        private Users user;

        UsersViewHolder(View item_by_id){

            super(item_by_id);
            item_by_id.setOnClickListener(this);
            this.user_software_imageView = item_by_id.findViewById(R.id.home_user_software_id);
            //this.user_picture_imageView = item_by_id.findViewById(R.id.home_user_picture_id);
            this.first_name_TextView = item_by_id.findViewById(R.id.home_first_name_textView_id);
            this.title_textView = item_by_id.findViewById(R.id.home_title_textView_id);
            this.platform_textView = item_by_id.findViewById(R.id.home_platform_textView_id);
            this.distance_textView = item_by_id.findViewById(R.id.home_distance_textView_id);

        }

        void set_user(Users user){
            this.user = user;
        }

        void set_context(Context context){
            this.context = context;
        }


        @Override
        public void onClick(View v) {


            Intent intent = new Intent(context, HomeUserSoftwareProfileActivity.class);
           intent.putExtra("user", user);
           this.context.startActivity(intent);

        }
    }

}
