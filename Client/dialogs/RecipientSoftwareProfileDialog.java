package com.syncadapters.czar.exchange.dialogs;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datamodels.ImageSaver;
import com.syncadapters.czar.exchange.datamodels.Software;
import com.syncadapters.czar.exchange.viewmodels.SoftwareProfileDialogViewModel;

import java.util.Objects;

public class RecipientSoftwareProfileDialog extends DialogFragment {

    private static final String TAG = "MSG";

    private Software software = new Software();

    private ImageView recipient_software_imageView;
    private FragmentManager fragment_manager;

    @SuppressWarnings("unused")
    private ImageSaver image_saver;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.d(TAG, "SoftwareProfileDialog onAttach");

    }

    @Override
    public void onCreate(@Nullable Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);

        Log.d(TAG, "SoftwareProfileDialog onCreate");
        if(getArguments() != null) {
            Bundle software_bundle = getArguments();

            software = software_bundle.getParcelable("software");


        }

        if(saved_instance_state != null)
            software = saved_instance_state.getParcelable("software");


       // @SuppressWarnings("unused") SoftwareProfileDialogViewModel software_profile_dialog_view_model = ViewModelProviders.of(this).get(SoftwareProfileDialogViewModel.class);
        @SuppressWarnings("unused") SoftwareProfileDialogViewModel software_profile_dialog_view_model = new ViewModelProvider(this).get(SoftwareProfileDialogViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "SoftwareProfileDialog onCreateView");
        View view = inflater.inflate(R.layout.dialog_recipient_software_profile_dialog, container, false);


        ImageView recipient_software_clear_dialog_button = view.findViewById(R.id.recipient_software_dialog_clear_image_imageView_id);
        recipient_software_imageView = view.findViewById(R.id.recipient_software_dialog_image_imageView_id);
        recipient_software_imageView.setOnClickListener(v -> {


            Bundle image_bundle = new Bundle();
            image_bundle.putString("image_full_url", software.software_image_full_url);

            //noinspection ConstantConditions
            fragment_manager = getActivity().getSupportFragmentManager();
            ImageViewerDialog image_viewer_dialog = new  ImageViewerDialog();
            image_viewer_dialog.setArguments(image_bundle);
            image_viewer_dialog.show(fragment_manager, "Image_Viewer_Dialog");
        });

        TextView recipient_software_title_textView = view.findViewById(R.id.recipient_software_dialog_title_textView_id);
        TextView recipient_software_publisher_textView = view.findViewById(R.id.recipient_software_dialog_publisher_textView_id);
        TextView recipient_software_developer_textView = view.findViewById(R.id.recipient_software_dialog_developer_textView_id);
        TextView recipient_software_platform_textView = view.findViewById(R.id.recipient_software_dialog_platform_textView_id);
        TextView recipient_software_upc_textView = view.findViewById(R.id.recipient_software_dialog_upc_textView_id);
        TextView recipient_software_user_description_textView = view.findViewById(R.id.recipient_software_dialog_user_description_textView_id);

        recipient_software_clear_dialog_button.setOnClickListener(v -> dismiss());

        render_software_image();
        recipient_software_title_textView.setText(software.title);
        recipient_software_publisher_textView.setText(software.game_publisher);
        recipient_software_developer_textView.setText(software.game_developer);
        recipient_software_platform_textView.setText(software.platform);
        recipient_software_upc_textView.setText(software.upc);
        recipient_software_user_description_textView.setText(software.user_description);


        return view;
    }

    private void render_software_image() {


        //noinspection ConstantConditions
        Glide.with(getActivity()).asDrawable()
                .load(software.software_image_thumbnail_url)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                //noinspection ConstantConditions
                if(resource == null)
                    Log.d(TAG, "[RecipientSoftwareProfileDialog] RESOURCE IS NULL");
                else //noinspection ConstantConditions
                    if(resource != null)
                    Log.d(TAG, "[RecipientSoftwareProfileDialog] RESOURCE IS NOT NULL");


                Bitmap source_bitmap = drawable_to_bitmap(resource);
                Bitmap round_bitmap  = Bitmap.createBitmap(source_bitmap.getWidth(), source_bitmap.getHeight(), source_bitmap.getConfig());
                Canvas canvas = new Canvas(round_bitmap);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setShader(new BitmapShader(source_bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                canvas.drawRoundRect((new RectF(0, 0, source_bitmap.getWidth(), source_bitmap.getHeight())), 20, 20, paint);
                recipient_software_imageView.setImageBitmap(round_bitmap);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });


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


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "SoftwareProfileDialog onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "SoftwareProfileDialog onResume");

        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "SoftwareProfileDialog onPause");

       // Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle out_state) {
        super.onSaveInstanceState(out_state);

        Log.d(TAG, "SoftwareProfileDialog onSaveInstanceState");

        out_state.putParcelable("software", software);

    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "SoftwareProfileDialog onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "SoftwareProfileDialog onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "SoftwareProfileDialog onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.d(TAG, "SoftwareProfileDialog onDetach");
    }

}
