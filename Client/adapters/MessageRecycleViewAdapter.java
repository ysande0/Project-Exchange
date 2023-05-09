package com.syncadapters.czar.exchange.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.MessageActivity;
import com.syncadapters.czar.exchange.datamodels.ConversationEntry;
import com.syncadapters.czar.exchange.datamodels.ImageSaver;
import com.syncadapters.czar.exchange.datamodels.Message;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.dialogs.ImageViewerDialog;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ListPreloader.PreloadModelProvider<Message> {

    private static final String TAG = "MSG";

    private static final int EMPTY_ITEM = 0;
    private static final int MESSAGE_TYPE_ONE = 1;  // Sender
    private static final int MESSAGE_TYPE_TWO = 2;  // Recipient

    private static final int MESSAGE_ERROR = -1;
    private static final int MESSAGE_PENDING = 0;
    private static final int MESSAGE_RECEIVED = 1;

    private Handler main_handler;
    private int WANTED_WIDTH = 0;
    private int WANTED_HEIGHT = 0;
    private ArrayList<Message> messages_list;
    private final Context context;
    private FragmentManager fragment_manager;
    private final ConversationEntry conversation_entry;

    public MessageRecycleViewAdapter(Context context, ArrayList<Message> messages_list, ConversationEntry conversation_entry){

   //     Log.d(TAG, "MessageRecycleViewAdapter: ");
   //     Log.d(TAG, "Size of Messages: " + messages_list.size() + "  UID: " + device_owner_uid);
       this.context = context;
       this.messages_list = messages_list;
       this.conversation_entry = conversation_entry;
       Log.d(TAG, "[MessageRecycleViewAdapter  Constructor] first name: " + this.conversation_entry.current_user.first_name + "  id: " + this.conversation_entry.current_user.id);

    }

    public void set_messages(ArrayList<Message> messages){
        this.messages_list = messages;
    }

    public void set_main_handler(Handler main_handler){
        this.main_handler = main_handler;
    }

    public void update_is_received(String message_id){

       for(int i = 0; i < messages_list.size(); i++){

           if(messages_list.get(i).id.equals(message_id)) {
               messages_list.get(i).message_delivered = MESSAGE_RECEIVED;
               break;
           }

       }

    }

    public void update_is_error(String message_id){

        for(int i = 0; i < messages_list.size(); i++){

            if(messages_list.get(i).id.equals(message_id)) {
                messages_list.get(i).message_delivered = MESSAGE_ERROR;
                break;
            }

        }

    }

    @Override
    public int getItemViewType(int position) {

        //  Log.d(TAG, "MessageRecycleViewAdapter: getItemViewType called");
        Message message = messages_list.get(position);
        //noinspection SpellCheckingInspection
        Log.d(TAG, "MRVA: " + message.is_from_server);
        if(!message.is_from_server) {

            Log.d(TAG, "--> Sender MESSAGE MODE <---");
            return MESSAGE_TYPE_ONE;
        }
        else {
            Log.d(TAG, "--> Recipient MESSAGE MODE <---");
            return MESSAGE_TYPE_TWO;
        }
    }

    @Override
    public int getItemCount() {

        return messages_list == null ? 0 : messages_list.size();
    }

    public void addMessage(Message message){

        messages_list.add(message);
        notifyItemInserted(messages_list.size());

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup view_group, int view_type) {

     //   Log.d(TAG, "MessageActivity: onCreateViewHolder");
        if(view_type == EMPTY_ITEM) {
            View view = LayoutInflater.from(view_group.getContext()).inflate(R.layout.activity_message_zero_items, view_group, false);
            return new MessageEmptyViewHolder(view);
        }

        if(view_type == MESSAGE_TYPE_ONE){
           Log.d(TAG, "onCreateViewHolder: Sender [Message]");
            View view = LayoutInflater.from(view_group.getContext()).inflate(R.layout.activity_message_sender_items, view_group, false);
            return new MessageSenderViewHolder(view);
        }
        else if(view_type == MESSAGE_TYPE_TWO){
          Log.d(TAG, "onCreateViewHolder: Recipient [Message]");
            View view = LayoutInflater.from(view_group.getContext()).inflate(R.layout.activity_message_recipient_items, view_group, false);
            return new MessageRecipientViewHolder(view);
        }
        else
            throw new RuntimeException("View Type is not ONE or Two");


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder view_holder, int position) {

       // Log.d(TAG, "MessageActivity: onBindViewHolder");
        switch(view_holder.getItemViewType()){

            case MESSAGE_TYPE_ONE:
                sender_layout((MessageSenderViewHolder) view_holder, position);
                break;

            case MESSAGE_TYPE_TWO:
                recipient_layout((MessageRecipientViewHolder) view_holder, position);
                break;

            default:
                break;

        }

    }

    private void sender_layout(MessageSenderViewHolder sender_view_holder, int position){

           // Log.d(TAG, "MessageRecycleViewAdapter: [Sender] ");
        Log.d(TAG, "[MessageRecycleViewAdapter] SENDER PROFILE_IMAGE: " + this.messages_list.get(position).profile_image_thumbnail_url);

        Message message = this.messages_list.get(position);

            Glide.with(this.context).asDrawable()
                    .load(message.profile_image_thumbnail_url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                    .into(new CustomTarget<Drawable>() {

                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {
                            super.onLoadStarted(placeholder);

                            //   Bitmap place_holder_bitmap = drawable_to_bitmap(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_default_profile_image_teal, null));
                            //    sender_view_holder.sender_profile_imageView.setImageBitmap(place_holder_bitmap);

                        }

                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                            Log.d(TAG, "[MessageRecycleViewAdapter] RESOURCE IS NOT NULL");

                            if(UserSettings.get_user_dpi(context).equals(context.getString(R.string.ldpi_label))){

                                Log.d(TAG, " Client device is ldpi");
                                WANTED_WIDTH = 30;
                                WANTED_HEIGHT = 30;
                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getString(R.string.mdpi_label))){

                                Log.d(TAG, "[MessageRecycleViewAdapter] Client device is mdpi");
                                WANTED_WIDTH = 40;
                                WANTED_HEIGHT = 40;
                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getString(R.string.hdpi_label))){

                                Log.d(TAG, "[MessageRecycleViewAdapter] Client device is hdpi");
                                WANTED_WIDTH = 60;
                                WANTED_HEIGHT = 60;

                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getString(R.string.xhdpi_label))){

                                Log.d(TAG, "[MessageRecycleViewAdapter] Client device is xhdpi");
                                WANTED_WIDTH = 80;
                                WANTED_HEIGHT = 80;
                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getString(R.string.xxhdpi_label))){

                                Log.d(TAG, "[MessageRecycleViewAdapter] Client device is xxhdpi");
                                WANTED_WIDTH = 120;
                                WANTED_HEIGHT = 120;
                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getString(R.string.xxxhdpi_label))){

                                Log.d(TAG, "[MessageRecycleViewAdapter] Client device is xxxhdpi");
                                WANTED_WIDTH = 160;
                                WANTED_HEIGHT = 160;

                            }

                            ImageSaver image_saver = new ImageSaver();
                            Bitmap bitmap = drawable_to_bitmap(resource);
                            new Thread(() -> {
                                Log.d(TAG, "[MessageRecycleViewAdapter] Sender Before Image Width: " + bitmap.getWidth() + "  Height: " + bitmap.getHeight() + " memory size: " +  bitmap.getAllocationByteCount());
                                Bitmap source_bitmap = image_saver.decode_image_from_encode(image_saver.encoded_bitmap(bitmap, 100), WANTED_WIDTH, WANTED_HEIGHT);
                                Log.d(TAG, "[MessageRecycleViewAdapter] Sender After Image Width: " + source_bitmap.getWidth() + "  Height: " + source_bitmap.getHeight() + " memory size: " +  source_bitmap.getAllocationByteCount());
                                RoundedBitmapDrawable circular_bitmap_drawable = RoundedBitmapDrawableFactory.create(context.getResources(), source_bitmap);
                                circular_bitmap_drawable.setCircular(true);

                                main_handler.post(() -> {
                                    sender_view_holder.sender_profile_imageView.setImageDrawable(circular_bitmap_drawable);
                                    sender_view_holder.sender_profile_imageView.setOnClickListener(v -> {

                                        Bundle image_bundle = new Bundle();
                                        image_bundle.putString("image_full_url", message.profile_image_full_url);

                                        fragment_manager = ((MessageActivity) context).getSupportFragmentManager();
                                        ImageViewerDialog image_viewer_dialog = new ImageViewerDialog();
                                        image_viewer_dialog.setArguments(image_bundle);
                                        image_viewer_dialog.show(fragment_manager, "Image_Viewer_Dialog");
                                    });
                                });


                            }).start();

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
       // sender_view_holder.sender_profile_imageView.setBackground(context.getResources().getDrawable(R.drawable.image_view_circular));

            Log.d(TAG, "[MessageRecycleViewAdapter] SENDER FIRST: " + messages_list.get(position).first_name);
        //    sender_view_holder.sender_first_name_textView.setText(messages_list.get(position).first_name);
            sender_view_holder.sender_message_textView.setText(message.message);
            sender_view_holder.sender_message_textView.setBackground(context.getResources().getDrawable(R.drawable.sender_message_bubble));
            sender_view_holder.sender_time_textView.setText(message.time);
            sender_view_holder.sender_date_textView.setText(message.date);

        if(message.message_delivered == MESSAGE_ERROR){

            Log.d(TAG, "[FCM] Message Received ERROR");
            sender_view_holder.sender_message_received_imageView.setVisibility(View.GONE);
            sender_view_holder.sender_message_error_imageView.setVisibility(View.VISIBLE);

        }
        else if(message.message_delivered == MESSAGE_PENDING){

            sender_view_holder.sender_message_received_imageView.setVisibility(View.GONE);
            sender_view_holder.sender_message_error_imageView.setVisibility(View.GONE);
        }
        else if(message.message_delivered == MESSAGE_RECEIVED){

            Log.d(TAG, "[FCM] Message Received CHECK");
            sender_view_holder.sender_message_received_imageView.setVisibility(View.VISIBLE);
            sender_view_holder.sender_message_error_imageView.setVisibility(View.GONE);

        }
        else{

            sender_view_holder.sender_message_received_imageView.setVisibility(View.GONE);
            sender_view_holder.sender_message_error_imageView.setVisibility(View.GONE);

        }


    }

    private void recipient_layout(MessageRecipientViewHolder recipient_view_holder, int position){

           // Log.d(TAG, "MessageRecycleViewAdapter: [Recipient]");
        Log.d(TAG, "[MessageRecycleViewAdapter] RECIPIENT PROFILE_IMAGE: " + this.conversation_entry.recipient_user.user_image_thumbnail_url);

        Message message = this.messages_list.get(position);

       // Glide.with(context.getApplicationContext()).clear(recipient_view_holder.recipient_profile_imageView);
       // recipient_view_holder.recipient_profile_imageView.setImageResource(android.R.color.transparent);

         Glide.with(context).asDrawable()
                    .load(message.profile_image_thumbnail_url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                    .into(new CustomTarget<Drawable>() {

                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {
                            super.onLoadStarted(placeholder);

                            //  Bitmap place_holder_bitmap = drawable_to_bitmap(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_default_profile_image_teal, null));
                            //  recipient_view_holder.recipient_profile_imageView.setImageBitmap(place_holder_bitmap);

                        }

                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                            Log.d(TAG, "[MessageRecycleViewAdapter] RESOURCE IS NOT NULL");

                            if(UserSettings.get_user_dpi(context).equals(context.getString(R.string.ldpi_label))){

                                Log.d(TAG, " Client device is ldpi");
                                WANTED_WIDTH = 30;
                                WANTED_HEIGHT = 30;
                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getString(R.string.mdpi_label))){

                                Log.d(TAG, "[MessageRecycleViewAdapter] Client device is mdpi");
                                WANTED_WIDTH = 40;
                                WANTED_HEIGHT = 40;
                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getString(R.string.hdpi_label))){

                                Log.d(TAG, "[MessageRecycleViewAdapter] Client device is hdpi");
                                WANTED_WIDTH = 60;
                                WANTED_HEIGHT = 60;

                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getString(R.string.xhdpi_label))){

                                Log.d(TAG, "[MessageRecycleViewAdapter] Client device is xhdpi");
                                WANTED_WIDTH = 80;
                                WANTED_HEIGHT = 80;
                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getString(R.string.xxhdpi_label))){

                                Log.d(TAG, "[MessageRecycleViewAdapter] Client device is xxhdpi");
                                WANTED_WIDTH = 120;
                                WANTED_HEIGHT = 120;
                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getString(R.string.xxxhdpi_label))){

                                Log.d(TAG, "[MessageRecycleViewAdapter] Client device is xxxhdpi");
                                WANTED_WIDTH = 160;
                                WANTED_HEIGHT = 160;

                            }

                            //Bitmap source_bitmap = scale_bitmap(drawable_to_bitmap(resource), WANTED_WIDTH, WANTED_HEIGHT);
                            ImageSaver image_saver = new ImageSaver();
                            Bitmap bitmap = drawable_to_bitmap(resource);

                            new Thread(() -> {
                                Log.d(TAG, "[MessageRecycleViewAdapter] Recipient Before Image Width: " + bitmap.getWidth() + "  Height: " + bitmap.getHeight() + " memory size: " +  bitmap.getAllocationByteCount());
                                Bitmap source_bitmap = image_saver.decode_image_from_encode(image_saver.encoded_bitmap(bitmap, 100), WANTED_WIDTH, WANTED_HEIGHT);
                                Log.d(TAG, "[MessageRecycleViewAdapter] Recipient After Image Width: " + source_bitmap.getWidth() + "  Height: " + source_bitmap.getHeight() + " memory size: " +  source_bitmap.getAllocationByteCount());
                                RoundedBitmapDrawable circular_bitmap_drawable = RoundedBitmapDrawableFactory.create(context.getResources(), source_bitmap);
                                circular_bitmap_drawable.setCircular(true);

                                main_handler.post(() -> {

                                    recipient_view_holder.recipient_profile_imageView.setImageDrawable(circular_bitmap_drawable);
                                    recipient_view_holder.recipient_profile_imageView.setOnClickListener(v -> {

                                        Bundle image_bundle = new Bundle();
                                        image_bundle.putString("image_full_url", message.profile_image_full_url);

                                        fragment_manager = ((MessageActivity) context).getSupportFragmentManager();
                                        ImageViewerDialog image_viewer_dialog = new ImageViewerDialog();
                                        image_viewer_dialog.setArguments(image_bundle);
                                        image_viewer_dialog.show(fragment_manager, "Image_Viewer_Dialog");
                                    });
                                });

                            }).start();

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });

         //   recipient_view_holder.recipient_profile_imageView.setBackground(context.getResources().getDrawable(R.drawable.image_view_circular));

            Log.d(TAG, "[MessageRecycleViewAdapter] RECIPIENT FIRST: " + messages_list.get(position).first_name);
        //Bitmap software_bitmap = ((BitmapDrawable)recipient_view_holder.recipient_profile_imageView.getDrawable()).getBitmap();


            // recipient_view_holder.recipient_first_name_textView.setText(messages_list.get(position).first_name);
            recipient_view_holder.recipient_message_textView.setText(messages_list.get(position).message);
            recipient_view_holder.recipient_message_textView.setBackground(context.getResources().getDrawable(R.drawable.recipient_message_bubble));
            recipient_view_holder.recipient_time_textView.setText(messages_list.get(position).time);
            recipient_view_holder.recipient_date_textView.setText(messages_list.get(position).date);

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


    @NonNull
    @Override
    public List<Message> getPreloadItems(int position) {
        String url = messages_list.get(position).profile_image_thumbnail_url;
        if(TextUtils.isEmpty(url))
            return Collections.emptyList();

        return messages_list.subList(position, position + 1);
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull Message item) {
        return Glide.with(context).asDrawable()
                .load(item.profile_image_thumbnail_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())));

    }

    static class MessageSenderViewHolder extends RecyclerView.ViewHolder{

        final ImageView sender_profile_imageView;
        final ImageView sender_message_received_imageView;
        final ImageView sender_message_error_imageView;
        final TextView sender_message_textView;
        final TextView sender_time_textView;
        final TextView sender_date_textView;

        MessageSenderViewHolder(View item_by_id){

            super(item_by_id);
            this.sender_profile_imageView = item_by_id.findViewById(R.id.sender_profile_imageView_id);
            this.sender_message_received_imageView = item_by_id.findViewById(R.id.sender_message_received_imageView_id);
            this.sender_message_error_imageView = item_by_id.findViewById(R.id.sender_message_error_imageView_id);
            this.sender_message_textView = item_by_id.findViewById(R.id.sender_message_textView_id);
            this.sender_time_textView = item_by_id.findViewById(R.id.sender_time_textView_id);
            this.sender_date_textView = item_by_id.findViewById(R.id.sender_date_textView_id);
        }

    }

    static class MessageEmptyViewHolder extends RecyclerView.ViewHolder{

        MessageEmptyViewHolder(View item_by_id){

            super(item_by_id);

        }


    }

    static class MessageRecipientViewHolder extends RecyclerView.ViewHolder{

        final ImageView recipient_profile_imageView;
        final TextView recipient_message_textView;
        final TextView recipient_time_textView;
        final TextView recipient_date_textView;

        MessageRecipientViewHolder(View item_by_id){

            super(item_by_id);
            this.recipient_profile_imageView = item_by_id.findViewById(R.id.recipient_profile_imageView_id);
            this.recipient_message_textView = item_by_id.findViewById(R.id.recipient_message_textView_id);
            this.recipient_time_textView = item_by_id.findViewById(R.id.recipient_time_textView_id);
            this.recipient_date_textView = item_by_id.findViewById(R.id.recipient_date_textView_id);
        }



    }

}
