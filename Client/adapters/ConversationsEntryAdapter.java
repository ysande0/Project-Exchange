package com.syncadapters.czar.exchange.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.asynctasks.LoadConversationTask;
import com.syncadapters.czar.exchange.asynctasks.MyConversationsTask;
import com.syncadapters.czar.exchange.datamodels.ConversationEntry;
import com.syncadapters.czar.exchange.datamodels.Message;
import com.syncadapters.czar.exchange.datamodels.Users;
import com.syncadapters.czar.exchange.enums.DatabaseOperations;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.roomdatabase.ExchangeDatabase;
import com.syncadapters.czar.exchange.roomdatabase.MyConversationsDao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ConversationsEntryAdapter extends RecyclerView.Adapter<ConversationsEntryAdapter.ConversationViewHolder>{

    private static final String TAG = "MSG";

    private ArrayList<Message> messages_conversation_entries;
    private final Context context;
    @SuppressWarnings("CanBeFinal")
    private RequestManager glide_request_manager;
    private ConversationEntry conversation_entry;
    private final MyConversationsDao my_conversations_dao;
    private final WeakReference<RecyclerView> conversations_entry_recycle_view_weak_reference;
    private WeakReference<TextView> no_messages_available_textView_weak_reference;
    private WeakReference<FrameLayout> conversations_fragment_frame_layout_weak_reference;

    public ConversationsEntryAdapter(Context context, RequestManager glide_request_manager, ArrayList<Message> messages_conversation_entries, RecyclerView conversations_entry_recycle_view){

        this.conversations_entry_recycle_view_weak_reference = new WeakReference<>(conversations_entry_recycle_view);
        this.glide_request_manager = glide_request_manager;
        this.messages_conversation_entries = messages_conversation_entries;
        this.context = context;
        ExchangeDatabase exchange_database = ExchangeDatabase.get_database(this.context);
        this.my_conversations_dao = exchange_database.my_conversations_dao();
        new ItemTouchHelper(item_touch_helper).attachToRecyclerView(conversations_entry_recycle_view_weak_reference.get());

    }

    public void set_conversation_entry(ConversationEntry conversation_entry){

        this.conversation_entry = conversation_entry;
    }

    public void set_message_conversation_entries(ArrayList<Message> messages_conversation_entries){

        this.messages_conversation_entries = messages_conversation_entries;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int view_type) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_conversations_items, viewGroup, false);

        ConversationViewHolder conversation_view_holder = new ConversationViewHolder(view);

        return conversation_view_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder conversation_view_holder, int position) {

        Message recent_message = messages_conversation_entries.get(position);
        ConversationEntry current_conversation_entry = new ConversationEntry();

        current_conversation_entry.conversation_id = recent_message.conversation_id;
        current_conversation_entry.recipient_user.id = recent_message.recipient_user_id;
        current_conversation_entry.recipient_user.first_name = recent_message.recipient_first_name;
        current_conversation_entry.recipient_user.user_image_thumbnail_url = recent_message.recipient_profile_image_thumbnail_url;
        current_conversation_entry.recipient_user.user_image_full_url = recent_message.recipient_profile_image_full_url;

        current_conversation_entry.current_user = conversation_entry.current_user;

        Users user = new Users();
        user.id = current_conversation_entry.recipient_user.id;
        user.first_name = current_conversation_entry.recipient_user.first_name;
        user.user_image_thumbnail_url = current_conversation_entry.recipient_user.user_image_thumbnail_url;
        user.user_image_full_url = current_conversation_entry.recipient_user.user_image_full_url;

        conversation_view_holder.set_conversation_entry(current_conversation_entry);
        conversation_view_holder.set_context(this.context);
        conversation_view_holder.set_conversation_dao(my_conversations_dao);
        conversation_view_holder.set_user(user);

            glide_request_manager.asDrawable()
                    .load(recent_message.recipient_profile_image_thumbnail_url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                    .into(new CustomTarget<Drawable>() {

                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {
                            super.onLoadStarted(placeholder);

                            //  Bitmap place_holder_bitmap = drawable_to_bitmap(ResourcesCompat.getDrawable(context.getResources(), R.drawable.default_profile_image, null));
                            //  conversation_view_holder.profile_image.setImageBitmap(place_holder_bitmap);

                        }

                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            Bitmap source_bitmap = drawable_to_bitmap(resource);

                            RoundedBitmapDrawable circular_bitmap_drawable = RoundedBitmapDrawableFactory.create(context.getResources(), source_bitmap);
                            circular_bitmap_drawable.setCircular(true);
                            conversation_view_holder.profile_image.setImageDrawable(circular_bitmap_drawable);

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });

        if(!recent_message.is_read){

            conversation_view_holder.first_name.setText(recent_message.recipient_first_name);
            conversation_view_holder.recent_entry_message.setText(recent_message.message.trim());
            conversation_view_holder.recent_entry_message.setTypeface(Typeface.DEFAULT_BOLD);
            conversation_view_holder.time.setText(recent_message.time);
            conversation_view_holder.date.setText(recent_message.date);
            conversation_view_holder.unread_message_badge.setVisibility(View.VISIBLE);
            return;
        }


        conversation_view_holder.first_name.setText(recent_message.recipient_first_name);

        conversation_view_holder.recent_entry_message.setText(recent_message.message.trim());
        conversation_view_holder.recent_entry_message.setTypeface(Typeface.DEFAULT);
        conversation_view_holder.time.setText(recent_message.time);
        conversation_view_holder.date.setText(recent_message.date);
        conversation_view_holder.unread_message_badge.setVisibility(View.GONE);

    }

    public void update_unread_message_badge(int position){

        messages_conversation_entries.get(position).is_read = false;
        notifyItemChanged(position);
    }

    public void update_read_message(int position){

        messages_conversation_entries.get(position).is_read = true;
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {

        return messages_conversation_entries.size();
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void set_no_messages_textview(WeakReference<TextView> no_messages_available_textView_weak_reference){

        this.no_messages_available_textView_weak_reference = no_messages_available_textView_weak_reference;
    }

    public void set_conversation_fragment_frame_layout(WeakReference<FrameLayout> conversations_fragment_frame_layout_weak_reference){

        this.conversations_fragment_frame_layout_weak_reference = conversations_fragment_frame_layout_weak_reference;
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final ItemTouchHelper.SimpleCallback item_touch_helper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder view_holder, int direction) {

            int position = view_holder.getAdapterPosition();
            Message recent_message_entry = messages_conversation_entries.get(position);
            MyConversationsTask my_conversation_task = new MyConversationsTask(recent_message_entry.conversation_id, my_conversations_dao, DatabaseOperations.DELETE);
            my_conversation_task.execute();
            messages_conversation_entries.remove(view_holder.getAdapterPosition());

            if(messages_conversation_entries.isEmpty()){

                no_messages_available_textView_weak_reference.get().setVisibility(View.VISIBLE);
                conversations_entry_recycle_view_weak_reference.get().setVisibility(View.GONE);
                conversations_fragment_frame_layout_weak_reference.get().setBackgroundColor(Color.GRAY);

            }

            notifyDataSetChanged();
        }

        @Override
        public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recycler_view, @NonNull RecyclerView.ViewHolder view_holder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(canvas, recycler_view, view_holder, dX, dY, actionState, isCurrentlyActive);

            final ColorDrawable delete_background = new ColorDrawable(Color.RED);
            delete_background.setBounds(0, view_holder.itemView.getTop(), (int) (view_holder.itemView.getLeft() + dX), view_holder.itemView.getBottom());
            delete_background.draw(canvas);

            Drawable delete_conversation_icon = ContextCompat.getDrawable(context, R.drawable.ic_delete_conversation_entry_white);

            assert delete_conversation_icon != null;
            int margin = (view_holder.itemView.getHeight() - delete_conversation_icon.getIntrinsicHeight()) / 2;
            int left = view_holder.itemView.getLeft() + margin;
            int right = view_holder.itemView.getLeft() + margin + delete_conversation_icon.getIntrinsicWidth();
            int top = view_holder.itemView.getTop() + (view_holder.itemView.getHeight() - delete_conversation_icon.getIntrinsicHeight()) / 2;
            int bottom = top + delete_conversation_icon.getIntrinsicHeight();

                delete_conversation_icon.setBounds(left, top, right, bottom);
                delete_conversation_icon.draw(canvas);

        }
    };

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

    @SuppressWarnings("WeakerAccess")
    public class ConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView profile_image;
        private final TextView first_name;
        private final TextView recent_entry_message;
        private final TextView time;
        private final TextView date;
        private final TextView unread_message_badge;
        private ConversationEntry conversation_entry;
        private MyConversationsDao my_conversations_dao;
        private Context context;
        private Users user;

        public ConversationViewHolder(@NonNull View item_by_id) {
            super(item_by_id);
            item_by_id.setOnClickListener(this);

            this.profile_image = item_by_id.findViewById(R.id.profile_imageView_id);
            this.first_name = item_by_id.findViewById(R.id.first_name_textView_id);
            this.recent_entry_message = item_by_id.findViewById(R.id.recent_message_textView_id);
            this.time = item_by_id.findViewById(R.id.time_message_textView_id);
            this.date = item_by_id.findViewById(R.id.date_message_textView_id);
            this.unread_message_badge = item_by_id.findViewById(R.id.unread_message_badge_textView_id);

        }

        public void set_conversation_entry(ConversationEntry conversation_entry){
            this.conversation_entry = conversation_entry;
        }

        public void set_context(Context context){
            this.context = context;
        }

        public void set_conversation_dao(MyConversationsDao my_conversations_dao){

            this.my_conversations_dao = my_conversations_dao;
        }

        public void set_user(Users user){

            this.user = user;
        }



        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();

            Message message_entry = messages_conversation_entries.get(position);
            message_entry.is_read = true;

            messages_conversation_entries.set(position, message_entry);
            notifyItemChanged(position);

            LoadConversationTask load_conversation = new LoadConversationTask(this.context, my_conversations_dao);
            load_conversation.set_interface(UserInterface.HOME_USER_SOFTWARE_PROFILE_ACTIVITY);
            load_conversation.set_conversation_entry(this.conversation_entry);
            load_conversation.set_user(user);
            load_conversation.execute();


        }


    }

}
