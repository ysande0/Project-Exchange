package com.syncadapters.czar.exchange.dialogs;

import android.content.Context;
import android.content.pm.ActivityInfo;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datamodels.ImageSaver;
import com.syncadapters.czar.exchange.datamodels.UserSettings;

import java.util.Objects;

public class ImageViewerDialog extends DialogFragment {

    private static final String TAG = "MSG";
    private Context context;
    private ImageView image_viewer;
    private Bitmap image_bitmap;
    private String image_full_url;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            Bundle image_bundle = getArguments();
            image_full_url = image_bundle.getString("image_full_url");
        }

        if(savedInstanceState != null){

            image_full_url = savedInstanceState.getString("image_full_url");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.dialog_image_viewer, container, false);

         image_viewer = view.findViewById(R.id.image_viewer_id);
         ImageView image_viewer_clear_dialog_button = view.findViewById(R.id.image_viewer_dialog_clear_image_imageView_id);
        @SuppressWarnings("unused") CustomTarget<Drawable> target = Glide.with(this.context).asDrawable()
                .load(image_full_url)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .into(new CustomTarget<Drawable>() {

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                        ImageSaver image_saver = new ImageSaver();
                        final int QUALITY = 75;

                        int target_image_width = 0;
                        int target_image_height = 0;


                        if (UserSettings.get_user_dpi(context).equals(getResources().getString(R.string.ldpi_label))) {

                            target_image_width = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getDecorView().getWidth();
                            target_image_height = 375;

                        } else if (UserSettings.get_user_dpi(context).equals(getResources().getString(R.string.mdpi_label))) {

                            target_image_width = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getDecorView().getWidth();
                            target_image_height = 500;

                        } else if (UserSettings.get_user_dpi(context).equals(getResources().getString(R.string.hdpi_label))) {

                            target_image_width = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getDecorView().getWidth();
                            target_image_height = 750;

                        } else if (UserSettings.get_user_dpi(context).equals(getResources().getString(R.string.xhdpi_label))) {

                            target_image_width = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getDecorView().getWidth();
                            target_image_height = 1000;

                        } else if (UserSettings.get_user_dpi(context).equals(getResources().getString(R.string.xxhdpi_label))) {


                            target_image_width = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getDecorView().getWidth();
                            target_image_height = 1500;

                        } else if (UserSettings.get_user_dpi(context).equals(getResources().getString(R.string.xxxhdpi_label))) {


                            target_image_width = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getDecorView().getWidth();
                            target_image_height = 2000;

                        }
                        
                        image_bitmap = drawable_to_bitmap(resource);

                        image_bitmap = image_saver.decode_image_from_encode(image_saver.encoded_bitmap(image_bitmap, QUALITY), target_image_width, target_image_height);
                        image_viewer.setImageBitmap(image_bitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                });

        image_viewer_clear_dialog_button.setOnClickListener(v -> dismiss());


        return view;
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
    }

    @Override
    public void onResume() {
        super.onResume();

        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onPause() {
        super.onPause();
        
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle out_state) {
        super.onSaveInstanceState(out_state);

        out_state.putString("image_full_url", image_full_url);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        image_viewer.setImageResource(0);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
