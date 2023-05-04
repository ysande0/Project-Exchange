package com.syncadapters.czar.exchange.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.asynctasks.LoadConversationTask;
import com.syncadapters.czar.exchange.datamodels.ConversationEntry;
import com.syncadapters.czar.exchange.datamodels.Message;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.roomdatabase.ExchangeDatabase;
import com.syncadapters.czar.exchange.roomdatabase.MyConversationsDao;

import java.util.concurrent.ExecutionException;


@SuppressWarnings("SuspiciousNameCombination")
public class NotificationHelper extends ContextWrapper {

    private NotificationManager notification_manager;
    private final Context context;
    private static final String TAG = "MSG";

    private static final String ANDROID_CHANNEL_ID = "com.syncadapters.czar.exchange.ANDROID";
    private static final String ANDROID_CHANNEL_NAME = "ANDROID MESSAGE CHANNEL";
    private Bitmap profile_bitmap;

    private final ConversationEntry conversation_entry;
    private final Message message;

    public NotificationHelper(Context context, ConversationEntry conversation_entry, Message message){
        super(context);

        this.context = context;
        this.conversation_entry = conversation_entry;
        this.message = message;

        Log.d(TAG, "SDK: " + Build.VERSION.SDK_INT + " / " + Build.VERSION_CODES.O);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "SDK: " + Build.VERSION.SDK_INT);
            create_notification_channel();
        }

    }


    @TargetApi(Build.VERSION_CODES.O)
    private void create_notification_channel(){

        NotificationChannel notification_channel = new NotificationChannel(ANDROID_CHANNEL_ID, ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notification_channel.enableLights(true);
        notification_channel.enableVibration(true);
        notification_channel.setLightColor(R.color.colorPrimary);
        notification_channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        get_notification_manager().createNotificationChannel(notification_channel);
    }

    public void create_message_notification_builder(){

        ExchangeDatabase exchange_database = ExchangeDatabase.get_database(context);
        MyConversationsDao my_conversations_dao = exchange_database.my_conversations_dao();


       // conversation_entry.recipient_user.user_image_name_thumbnail = "http://192.168.1.242:80/Project/TheExchange%20Project/img/xxhdpi/null";
        Log.d(TAG, "[NotificationHelper] profile bitmap: " + conversation_entry.recipient_user.user_image_thumbnail_url);
        FutureTarget<Bitmap> future_target = Glide.with(this.context)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .load(conversation_entry.recipient_user.user_image_thumbnail_url)
                    .submit();

            try{

                profile_bitmap = future_target.get();

            }catch(ExecutionException | InterruptedException execution_error){

                execution_error.printStackTrace();

            }

            if(conversation_entry.recipient_user.user_image_thumbnail_url == null || profile_bitmap == null) {
                profile_bitmap = drawable_to_bitmap(ResourcesCompat.getDrawable(context.getResources(), R.drawable.default_profile_image, null));
                Log.d(TAG, "[NotificationHelper] profile_bitmap is NULL");
            }
            else
                Log.d(TAG, "[NotificationHelper] profile_bitmap is NOT NULL");

            Bitmap circular_bitmap = get_circle_bitmap(profile_bitmap);

            @SuppressWarnings("UnusedAssignment") NotificationCompat.Builder notification_builder = null;
             //NotificationCompat.InboxStyle inbox_style = new NotificationCompat.InboxStyle();
             //String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                Log.d(TAG, "SDK: " + Build.VERSION.SDK_INT);
                notification_builder = new NotificationCompat.Builder(this.context, ANDROID_CHANNEL_ID);
                notification_builder.setSmallIcon(R.drawable.notification_icon);
                notification_builder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
                notification_builder.setLargeIcon(circular_bitmap);
                notification_builder.setContentTitle(message.first_name);
                notification_builder.setContentText(message.message);
                notification_builder.setAutoCancel(true);
            }
            else {

                Log.d(TAG, "SDK: " + Build.VERSION.SDK_INT);
                //noinspection deprecation
                notification_builder = new NotificationCompat.Builder(this.context);
                notification_builder.setSmallIcon(R.drawable.notification_icon);
                notification_builder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
                notification_builder.setLargeIcon(circular_bitmap);
                //notification_builder.setChannelId(message.conversation_id);
                notification_builder.setContentTitle(message.first_name);
                notification_builder.setContentText(message.message);
                notification_builder.setAutoCancel(true);
            }


            Log.d(TAG, "[LoadConversation Task] 1) conversation_entry current user id:  " + conversation_entry.current_user.id);
            LoadConversationTask load_conversation_task = new LoadConversationTask(this.context, my_conversations_dao);
            load_conversation_task.set_conversation_entry(conversation_entry);
            load_conversation_task.set_notification_builder(notification_builder);
            load_conversation_task.set_interface(UserInterface.MESSAGE_ACTIVITY);
            load_conversation_task.execute();

     //   Glide.with(this.context).clear(future_target);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private Bitmap get_circle_bitmap(Bitmap bitmap) {

    Bitmap output;
    Rect srcRect, dstRect;
    float r;
    final int width = bitmap.getWidth();
    final int height = bitmap.getHeight();

    if (width > height){

        Log.d(TAG, "IF --> WIDTH > HEIGHT <-- ");
        //noinspection SuspiciousNameCombination
        output = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);
        int left = (width - height) / 2;
        int right = left + height;
        srcRect = new Rect(left, 0, right, height);
        dstRect = new Rect(0, 0, height, height);
        r = height / 2;
    }else{

        Log.d(TAG, "ELSE --> WIDTH < HEIGHT <-- ");
        output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        int top = (height - width)/2;
        int bottom = top + width;
        srcRect = new Rect(0, top, width, bottom);
        //noinspection SuspiciousNameCombination
        dstRect = new Rect(0, 0, width, width);
        r = width / 2;
    }

    Canvas canvas = new Canvas(output);

    final int color = 0xff424242;
    final Paint paint = new Paint();

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    canvas.drawCircle(r, r, r, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));


    canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

    /*
        if(bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
*/
    return output;

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

    private NotificationManager get_notification_manager(){

        if(notification_manager == null)
            notification_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        return notification_manager;
    }

}
