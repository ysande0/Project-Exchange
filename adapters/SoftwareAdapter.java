package com.syncadapters.czar.exchange.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datamodels.Software;
import com.syncadapters.czar.exchange.dialogs.RecipientSoftwareProfileDialog;
import com.syncadapters.czar.exchange.dialogs.SoftwareProfileDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SoftwareAdapter extends RecyclerView.Adapter<SoftwareAdapter.SoftwareViewHolder> {

    private static final String TAG = "MSG";

    //private final Context context;
    private final WeakReference<Context> context_weak_reference;
    private ArrayList<Software> software;
   // private FragmentManager fragment_manager;
    private WeakReference<FragmentManager> fragment_manager_weak_reference;
    private boolean is_recipient_inventory = false;

    public SoftwareAdapter(Context context, ArrayList<Software> software){

        //this.context = context;
        this.context_weak_reference = new WeakReference<>(context);
        this.software = software;

    }

    public SoftwareAdapter(Context context){

        this.context_weak_reference = new WeakReference<>(context);

    }

@NonNull
@Override
    public SoftwareViewHolder onCreateViewHolder(@NonNull ViewGroup view_group, int viewType) {

         View view = LayoutInflater.from(view_group.getContext()).inflate(R.layout.fragment_inventory_grid_subviews, view_group, false);

         Log.d(TAG, "[SoftwareAdapter] onCreateViewHolder");

        return new SoftwareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoftwareViewHolder software_view_holder, int position) {

        Log.d(TAG, "[SoftwareAdapter] onBindViewHolder");
        Software software_item = this.software.get(position);

        String platform = "N/A";
        if(software_item.platform.equals(this.context_weak_reference.get().getApplicationContext().getResources().getString(R.string.playstation_four_platform)))
            platform = this.context_weak_reference.get().getApplicationContext().getResources().getString(R.string.playstation_four_abbr);
        else if(software_item.platform.equals(this.context_weak_reference.get().getApplicationContext().getResources().getString(R.string.playstation_five_platform)))
            platform = this.context_weak_reference.get().getApplicationContext().getResources().getString(R.string.playstation_five_abbr);
        else if(software_item.platform.equals(this.context_weak_reference.get().getApplicationContext().getResources().getString(R.string.xbox_one_platform)))
            platform = this.context_weak_reference.get().getApplicationContext().getResources().getString(R.string.xbox_one_abbr);
        else if(software_item.platform.equals(this.context_weak_reference.get().getApplicationContext().getResources().getString(R.string.xbox_x_platform)))
            platform = this.context_weak_reference.get().getApplicationContext().getResources().getString(R.string.xbox_x_abbr);
        else if(software_item.platform.equals(this.context_weak_reference.get().getApplicationContext().getResources().getString(R.string.nintendo_switch_platform)))
            platform = this.context_weak_reference.get().getApplicationContext().getResources().getString(R.string.nintendo_switch_abbr);

        software_view_holder.software_titleView.setText(software_item.title);
        software_view_holder.software_platformView.setText(platform);

        software_view_holder.set_software(software_item, position);
        software_view_holder.is_recipient_inventory(is_recipient_inventory);
        software_view_holder.set_fragment_manager(fragment_manager_weak_reference.get());

        Glide.with(this.context_weak_reference.get()).asDrawable()
                .load(software_item.software_image_thumbnail_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .into(new CustomTarget<Drawable>() {

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);

                        Bitmap place_holder_bitmap = drawable_to_bitmap(ResourcesCompat.getDrawable(context_weak_reference.get().getResources(), R.drawable.default_software_image, null));
                        software_view_holder.software_imageView.setImageBitmap(place_holder_bitmap);

                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                        Log.d(TAG, "SoftwareAdapter RESOURCE IS NOT NULL");


                        Bitmap source_bitmap = drawable_to_bitmap(resource);
                        if(source_bitmap == null)
                            Log.d(TAG, "Source Bitmap is null for " + software_item.software_image_thumbnail_url);
                        else
                            Log.d(TAG, "Source Bitmap is not null for " + software_item.software_image_thumbnail_url);

                        software.get(position).software_bitmap = source_bitmap;
                        software_view_holder.software_imageView.setImageBitmap(source_bitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return this.software.size();
    }

    public void set_is_recipient_inventory(boolean is_recipient_inventory){

        this.is_recipient_inventory = is_recipient_inventory;
    }

    public void set_fragment_manager(FragmentManager fragment_manager){

      //  this.fragment_manager = fragment_manager;
        this.fragment_manager_weak_reference = new WeakReference<>(fragment_manager);
    }

    public void insert(Software software_item){

        this.software.add(software_item);

    }

    public void set_softwares(ArrayList<Software> software){

        this.software = software;
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

    public void delete(Software software_item){

        if(this.software.contains(software_item)){

            this.software.remove(software_item);
            Log.d(TAG, "[SoftwareAdapter] Item " + software_item.title + "  by " + software_item.game_developer + " deleted");
            return;
        }

        Log.d(TAG, "[SoftwareAdapter] Could not delete: " + software_item.title);
    }

    public void update(Software software_item, int position){

            this.software.set(position, software_item);
            Log.d(TAG, "[SoftwareAdapter] Item " + software_item.title + "  by " + software_item.game_developer + " updated");

    }

    public static class SoftwareViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        private Software software;
        private final ImageView software_imageView;
        private final TextView software_titleView;
        private final TextView software_platformView;
        private int position;
        private boolean is_recipient_inventory;
       // private FragmentManager fragment_manager;
       private WeakReference<FragmentManager> fragment_manager_weak_reference;

        SoftwareViewHolder(View item_by_id){
            super(item_by_id);

            item_by_id.setOnClickListener(this);
            this.software_imageView = item_by_id.findViewById(R.id.software_imageView_id);
            this.software_titleView = item_by_id.findViewById(R.id.software_titleText_id);
            this.software_platformView = item_by_id.findViewById(R.id.software_platformText_id);

        }

        void set_software(Software software, int position){

            this.software = software;
            this.position = position;
        }

        void is_recipient_inventory(boolean is_recipient_inventory){

            this.is_recipient_inventory = is_recipient_inventory;
        }

        void set_fragment_manager(FragmentManager fragment_manager){

           // this.fragment_manager = fragment_manager;
            this.fragment_manager_weak_reference = new WeakReference<>(fragment_manager);
        }

        @Override
        public void onClick(View view) {

            view.setSelected(true);

            Bundle software_bundle = new Bundle();
            software_bundle.putParcelable("software", software);
            software_bundle.putInt("position", position);


            if(!this.is_recipient_inventory) {
                Log.d(TAG, "[Software Adapter] User Inventory");

                SoftwareProfileDialog software_profile_dialog = new SoftwareProfileDialog();
                software_profile_dialog.setArguments(software_bundle);
                software_profile_dialog.show(fragment_manager_weak_reference.get(), "Software_Profile_Dialog");
            }
            else {
                Log.d(TAG, "[Software Adapter] Recipient Inventory");
                RecipientSoftwareProfileDialog recipient_software_profile_dialog = new RecipientSoftwareProfileDialog();
                recipient_software_profile_dialog.setArguments(software_bundle);
                recipient_software_profile_dialog.show(fragment_manager_weak_reference.get(), "Recipient_Software_Profile_Dialog");
            }

        }
    }

}
