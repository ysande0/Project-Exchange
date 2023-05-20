package com.syncadapters.czar.exchange.dialogs;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.CameraActivity;
import com.syncadapters.czar.exchange.datamodels.ImageSaver;
import com.syncadapters.czar.exchange.datamodels.Software;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.viewmodels.SoftwareProfileDialogViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

@SuppressWarnings("unused")
public class SoftwareProfileDialog extends DialogFragment {

    private static final String TAG = "MSG";
    private Context context;
    private App app;
    private Software software = new Software();
    private int position;

   // private static final int REQUEST_UPC_IMAGE_CAPTURE = 1;
   // private static final int REQUEST_SOFTWARE_IMAGE_CAPTURE = 2;
   // private static final int REQUESTED_CAMERA_PERMISSION = 200;
    private ImageView software_imageView;

    private EditText software_title_editText;
    private EditText software_publisher_editText;
    private EditText software_developer_editText;
   // private TextView software_platform_textView;
    private Spinner software_platform_spinner;
    private EditText software_upc_editText;
    private EditText software_user_description_editText;
    private FragmentManager fragment_manager;
    private ImageSaver image_saver;
    private SoftwareProfileDialogViewModel software_profile_dialog_view_model;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.d(TAG, "SoftwareProfileDialog onAttach");
        this.context = context;


    }

    @Override
    public void onCreate(@Nullable Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);

        app = ((App) this.context.getApplicationContext());
        app.set_conversation_fragment_foreground(false);
        app.set_home_fragment_foreground(false);
        app.set_inventory_fragment_foreground(false);
        app.set_software_profile_dialog_foreground(true);

        Log.d(TAG, "SoftwareProfileDialog onCreate");
        software_profile_dialog_view_model = new ViewModelProvider(this).get(SoftwareProfileDialogViewModel.class);

        if(getArguments() != null) {

            Bundle software_bundle = getArguments();
            software = software_bundle.getParcelable("software");
            position = software_bundle.getInt("position");
            getArguments().clear();
        }

/*
        if(saved_instance_state != null) {
          //  software = saved_instance_state.getParcelable("software");
           // position = saved_instance_state.getInt("position");

            software = software_profile_dialog_view_model.get_software();
            position = software_profile_dialog_view_model.get_position();
        }
*/
        image_saver = new ImageSaver();

       // software_profile_dialog_view_model = ViewModelProviders.of(this).get(SoftwareProfileDialogViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "SoftwareProfileDialog onCreateView");
        View view = inflater.inflate(R.layout.dialog_software_profile,container, false);

        fragment_manager = getParentFragmentManager();

       // GestureDetector gesture_detector = new GestureDetector(this.context, new Gesture());

        ImageView software_clear_dialog_button = view.findViewById(R.id.software_dialog_clear_image_imageView_id);
        software_imageView = view.findViewById(R.id.software_dialog_image_imageView_id);
        software_title_editText = view.findViewById(R.id.software_dialog_title_textView_id);
        software_publisher_editText = view.findViewById(R.id.software_dialog_publisher_textView_id);
        software_developer_editText = view.findViewById(R.id.software_dialog_developer_textView_id);
        software_platform_spinner = view.findViewById(R.id.software_dialog_hardware_spinner_id);
        //software_platform_textView = view.findViewById(R.id.software_dialog_platform_textView);
        software_upc_editText = view.findViewById(R.id.software_dialog_upc_textView_id);
        ImageButton software_upc_buttonView = view.findViewById(R.id.software_dialog_upc_capture_button_id);
        software_user_description_editText = view.findViewById(R.id.software_dialog_user_description_editText_id);
        FloatingActionButton edit_floating_action_bar = view.findViewById(R.id.software_dialog_edit_float_button_id);
        FloatingActionButton delete_floating_action_bar = view.findViewById(R.id.software_dialog_delete_float_button_id);

        if(savedInstanceState != null){

            Log.d(TAG, "SoftwareProfileDialog ReCreate");
            int preview_image_width = 0;
            int preview_image_height = 0;

            software = software_profile_dialog_view_model.get_software();
            position = software_profile_dialog_view_model.get_position();

            if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.ldpi_label))){

                preview_image_width = 195;
                preview_image_height = 195;

            }
            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.mdpi_label))){

                preview_image_width = 260;
                preview_image_height = 260;

            }
            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.hdpi_label))){

                preview_image_width = 390;
                preview_image_height = 390;

            }
            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xhdpi_label))){

                preview_image_width = 520;
                preview_image_height = 520;

            }
            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xxhdpi_label))){


                preview_image_width = 780;
                preview_image_height = 780;

            }
            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xxxhdpi_label))){


                preview_image_width = 1040;
                preview_image_height = 1040;

            }


            if(software.software_bitmap == null){

                software.software_bitmap = image_saver.scale_bitmap(drawable_to_bitmap(ResourcesCompat.getDrawable(context.getResources(), R.drawable.default_software_image, null)), preview_image_width, preview_image_height);
                software_imageView.setImageBitmap(software.software_bitmap);
            }
            else{

                software.software_bitmap = image_saver.scale_bitmap(software.software_bitmap, preview_image_width, preview_image_height);
                software_imageView.setImageBitmap(software.software_bitmap);

            }


        }
        else
            render_software_image();


        software_clear_dialog_button.setOnClickListener(
                v -> {

                    if(!(UserSettings.get_encoded_bitmap_thumbnail(context.getApplicationContext()).isEmpty()))
                         UserSettings.remove_encoded_bitmap_thumbnail(context.getApplicationContext());

                    if(!(UserSettings.get_encoded_bitmap_full(context.getApplicationContext()).isEmpty()))
                        UserSettings.remove_encoded_bitmap_full(context.getApplicationContext());

                    if(!(UserSettings.get_user_local_software_image_path(context.getApplicationContext()).isEmpty()))
                         image_saver.delete_image_file(UserSettings.get_user_local_software_image_path(context.getApplicationContext()));

                    dismiss();
                });

        software_imageView.setOnClickListener(v -> capture_image());

        software_upc_buttonView.setOnClickListener(v -> capture_upc());



        software_title_editText.setText(software.title);
        software_publisher_editText.setText(software.game_publisher);
        software_developer_editText.setText(software.game_developer);

        Log.d(TAG, "SoftwareProfileDialog: " + software.upc);
        load_hardware();
        software_upc_editText.setText(software.upc);
        software_user_description_editText.setText(software.user_description);
        edit_floating_action_bar.setOnClickListener(v -> {


            boolean flag_error = false;
            Log.d(TAG, "Editing " + software.title + " by " + software.game_developer);


            if(!(UserSettings.get_encoded_bitmap_thumbnail(context.getApplicationContext()).isEmpty()))
                software.encoded_bitmap_thumbnail = UserSettings.get_encoded_bitmap_thumbnail(context.getApplicationContext());

            if(!(UserSettings.get_encoded_bitmap_full(context.getApplicationContext()).isEmpty()))
                software.encoded_bitmap_full = UserSettings.get_encoded_bitmap_full(context.getApplicationContext());

            if(software.bitmap_name_full != null && software.bitmap_name_thumbnail != null) {

                software.bitmap_name_thumbnail = software.uid + "_thumbnail" + ".jpg";
                software.bitmap_name_full = software.uid + "_full" + ".jpg";
            }

            try{

                Log.d(TAG, "Updating....");

                if(software_title_editText.getText().toString().trim().isEmpty()){

                    Log.d(TAG, "[SoftwareProfileDialog] title is empty");
                    software_title_editText.setError(context.getString(R.string.software_title_edittext_error));
                    flag_error = true;

                }

                if(software_publisher_editText.getText().toString().trim().isEmpty()){

                    Log.d(TAG, "[SoftwareProfileDialog] publisher is empty");
                    software_publisher_editText.setError(context.getString(R.string.software_publisher_edittext_error));
                    flag_error = true;

                }

                if(software_developer_editText.getText().toString().trim().isEmpty()){

                    Log.d(TAG, "[SoftwareProfileDialog] developer is empty");
                    software_developer_editText.setError(context.getString(R.string.software_developer_edittext_error));
                    flag_error = true;
                }

                if(software_upc_editText.getText().toString().trim().isEmpty()){
                    Log.d(TAG, "[SoftwareProfileDialog] upc is empty");
                    software_upc_editText.setError(context.getString(R.string.software_upc_edittext_error));
                    flag_error = true;

                }

                if(flag_error)
                    return;


                software.title = software_title_editText.getText().toString().trim();
                software.game_publisher = software_publisher_editText.getText().toString().trim();
                software.game_developer = software_developer_editText.getText().toString().trim();
                software.user_description = software_user_description_editText.getText().toString().trim();
                software.upc = software_upc_editText.getText().toString().trim();

                DisplayMetrics display_metrics = new DisplayMetrics();
                Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(display_metrics);
                int dpi_classification = display_metrics.densityDpi;

                Log.d(TAG, "Software Dialog Profile URL: " + software.software_image_thumbnail_url);
                JSONObject update_software_json = new JSONObject();
                update_software_json.put(context.getResources().getString(R.string.category_label), 101);
                update_software_json.put(context.getResources().getString(R.string.operation_label), 4);
                update_software_json.put(context.getResources().getString(R.string.uid_label), UserSettings.get_user_uid(context));
                update_software_json.put(context.getResources().getString(R.string.id_label), UserSettings.get_user_id(context));
                update_software_json.put(context.getResources().getString(R.string.title_key), software.title);
                update_software_json.put(context.getResources().getString(R.string.publisher_key), software.game_publisher);
                update_software_json.put(context.getResources().getString(R.string.developer_key), software.game_developer);
                update_software_json.put(context.getResources().getString(R.string.platform_key), software.platform);
                update_software_json.put(context.getResources().getString(R.string.user_description_key), software.user_description);
                update_software_json.put(context.getResources().getString(R.string.software_image_name_full_key), software.bitmap_name_full);
                update_software_json.put(context.getResources().getString(R.string.software_image_name_thumbnail_key), software.bitmap_name_thumbnail);
               // update_software_json.put("image_name", software.bitmap_name);
                update_software_json.put(context.getResources().getString(R.string.upc_key), software.upc);
                update_software_json.put(context.getResources().getString(R.string.access_token_key), UserSettings.get_user_token(context));
                update_software_json.put(context.getResources().getString(R.string.user_dpi_label), dpi_classification);

                if(software.encoded_bitmap_full != null)
                    update_software_json.put(context.getResources().getString(R.string.image_encoded_full_key), software.encoded_bitmap_full);

                if(software.encoded_bitmap_thumbnail != null)
                    update_software_json.put(context.getResources().getString(R.string.image_encoded_thumbnail_key), software.encoded_bitmap_thumbnail);

                update_software_json.put(context.getResources().getString(R.string.software_uid_key), software.uid);
             //   update_software_json.put("image_name", software.bitmap_name);

                software_profile_dialog_view_model.update_software(software, update_software_json, SoftwareProfileDialog.this, fragment_manager, position);
                dismiss();
            }catch(JSONException json_error){
                json_error.printStackTrace();
            }

        });


        delete_floating_action_bar.setOnClickListener(v -> {


            try {

                software.bitmap_name_thumbnail = software.uid + "_thumbnail" + ".jpg";
                software.bitmap_name_full = software.uid + "_full" + ".jpg";
                Log.d(TAG, "Deleting....");
                JSONObject delete_software_json = new JSONObject();
                delete_software_json.put(context.getResources().getString(R.string.category_label), 101);
                delete_software_json.put(context.getResources().getString(R.string.operation_label), 2);
                delete_software_json.put(context.getResources().getString(R.string.access_token_key), UserSettings.get_user_token(context));
                delete_software_json.put(context.getResources().getString(R.string.uid_label), UserSettings.get_user_uid(context));
                delete_software_json.put(context.getResources().getString(R.string.id_label), UserSettings.get_user_id(context));
                delete_software_json.put(context.getResources().getString(R.string.software_image_full_url_key), software.software_image_full_url);
                delete_software_json.put(context.getResources().getString(R.string.software_image_thumbnail_url_key), software.software_image_thumbnail_url);
                delete_software_json.put(context.getResources().getString(R.string.software_uid_key), software.uid);
/*

                delete_software_json.put("title", software.title);
                delete_software_json.put("publisher", software.game_publisher);
                delete_software_json.put("developer", software.game_developer);
                delete_software_json.put("platform", software.platform);
                delete_software_json.put("description", software.user_description);
                delete_software_json.put("image_name", software.bitmap_name);
                delete_software_json.put("upc", software.upc);

                delete_software_json.put("encoded_image", software.encoded_bitmap);
                delete_software_json.put("image_name", software.bitmap_name);
                 */



                software_profile_dialog_view_model.delete_software(software, delete_software_json, SoftwareProfileDialog.this, fragment_manager ,position);
                dismiss();
            }catch(JSONException json_error){
                json_error.printStackTrace();
            }
        });


        return view;
    }

     private void render_software_image() {

         //software_imageView.setImageBitmap(software.software_bitmap);
         Glide.with(this.context).asDrawable()
                 .load(this.software.software_image_full_url)
                 .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                 .into(new CustomTarget<Drawable>() {

             @Override
             public void onLoadStarted(@Nullable Drawable placeholder) {
                 super.onLoadStarted(placeholder);

                 Bitmap place_holder_bitmap = drawable_to_bitmap(ResourcesCompat.getDrawable(context.getResources(), R.drawable.default_software_image, null));
                 software_imageView.setImageBitmap(place_holder_bitmap);
             }

             @Override
             public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                 Log.d(TAG, "SoftwareProfileDialog RESOURCE IS NOT NULL");

                 int preview_image_width = 0;
                 int preview_image_height = 0;


                 if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.ldpi_label))){

                     preview_image_width = 195;
                     preview_image_height = 195;

                 }
                 else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.mdpi_label))){

                     preview_image_width = 260;
                     preview_image_height = 260;

                 }
                 else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.hdpi_label))){

                     preview_image_width = 390;
                     preview_image_height = 390;

                 }
                 else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xhdpi_label))){

                     preview_image_width = 520;
                     preview_image_height = 520;

                 }
                 else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xxhdpi_label))){


                     preview_image_width = 780;
                     preview_image_height = 780;

                 }
                 else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xxxhdpi_label))){


                     preview_image_width = 1040;
                     preview_image_height = 1040;

                 }

                 software.software_bitmap = image_saver.scale_bitmap(drawable_to_bitmap(resource), preview_image_width, preview_image_height);
                 software_imageView.setImageBitmap(software.software_bitmap);


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
/*
    private void capture_image(){


        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //noinspection ConstantConditions
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUESTED_CAMERA_PERMISSION);
            return;
        }


        Intent take_image = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(take_image.resolveActivity(context.getPackageManager()) != null)
            startActivityForResult(take_image, REQUEST_SOFTWARE_IMAGE_CAPTURE);

    }

    private void capture_upc(){

        Intent intent = new Intent(context, CameraActivity.class);
        startActivityForResult(intent, REQUEST_UPC_IMAGE_CAPTURE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivity called...");
        Log.d(TAG, "Request Code: " + requestCode + "    Result Code: " + resultCode );
        if(requestCode == REQUEST_UPC_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Log.d(TAG, "Getting upc bitmap");


            Bundle extras = data.getExtras();
            assert extras != null;
            this.software.upc = (String) extras.get("upc_data");
            this.software_upc_editText.setText(this.software.upc);

        }
        else if(requestCode == REQUEST_SOFTWARE_IMAGE_CAPTURE && resultCode == RESULT_OK){

            Bundle extras = data.getExtras();
            assert extras != null;
            software.software_bitmap = (Bitmap) extras.get("data");
            assert software.software_bitmap != null;
            software.encoded_bitmap = image_saver.encoded_bitmap(software.software_bitmap);
            software.bitmap_name = software.uid + ".jpg";
            software_imageView.setImageBitmap(software.software_bitmap);
        }

    }
*/
private final ActivityResultLauncher<String[]> permission_request_image = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),

        result -> {

            for(Map.Entry<String, Boolean> entry : result.entrySet()){

                //noinspection PointlessBooleanExpression
                if(entry.getValue() == false){

                    Log.d(TAG, "[SoftwareProfileDialog] " +  entry.getKey() + " is denied");
                    return;
                }
            }

            Log.d(TAG, "[SoftwareProfileDialog] Permissions granted. Launching Camera...");
            // Launch Camera
            launch_camera();

        });



    private final ActivityResultLauncher<Intent> capture_image_content = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {


                    if (result.getResultCode() == RESULT_OK){


                        File image_file = new  File(UserSettings.get_user_local_software_image_path(context));

                        Uri file_uri_captured = Uri.fromFile(image_file);

                        try {
                            int full_image_width = 0;
                            int full_image_height = 0;

                            int thumbnail_image_width = 0;
                            int thumbnail_image_height = 0;

                            int preview_image_width = 0;
                            int preview_image_height = 0;


                            DisplayMetrics display_metrics = new DisplayMetrics();
                            Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(display_metrics);

                            if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.ldpi_label))){

                                full_image_width = display_metrics.widthPixels;
                                full_image_height = 375;

                                thumbnail_image_width = 75;
                                thumbnail_image_height = 75;

                                preview_image_width = 195;
                                preview_image_height = 195;

                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.mdpi_label))){

                                full_image_width = display_metrics.widthPixels;
                                full_image_height = 500;

                                thumbnail_image_width = 100;
                                thumbnail_image_height = 100;

                                preview_image_width = 260;
                                preview_image_height = 260;

                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.hdpi_label))){

                                full_image_width = display_metrics.widthPixels;
                                full_image_height = 750;

                                thumbnail_image_width = 150;
                                thumbnail_image_height = 150;

                                preview_image_width = 390;
                                preview_image_height = 390;

                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xhdpi_label))){

                                full_image_width = display_metrics.widthPixels;
                                full_image_height = 1000;

                                thumbnail_image_width = 200;
                                thumbnail_image_height = 200;

                                preview_image_width = 520;
                                preview_image_height = 520;

                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xxhdpi_label))){

                                full_image_width = display_metrics.widthPixels;
                                full_image_height = 1500;

                                thumbnail_image_width = 300;
                                thumbnail_image_height = 300;

                                preview_image_width = 780;
                                preview_image_height = 780;

                            }
                            else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xxxhdpi_label))){

                                full_image_width = display_metrics.widthPixels;
                                full_image_height = 2000;

                                thumbnail_image_width = 400;
                                thumbnail_image_height = 400;

                                preview_image_width = 1040;
                                preview_image_height = 1040;

                            }


                            final int QUALITY = 75;
                            software.software_bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), file_uri_captured);
                            software_imageView.setImageBitmap(image_saver.scale_bitmap(software.software_bitmap, preview_image_width, preview_image_height));
                            Log.d(TAG, "[SoftwareProfileDialog] Bitmap Target image width: " + full_image_width + "  image height: " + full_image_height);
                            software.software_bitmap = image_saver.decode_image_from_file(UserSettings.get_user_local_software_image_path(context), full_image_width, full_image_height);
                            Log.d(TAG, "[SoftwareProfileDialog] Bitmap Scaled image width: " + software.software_bitmap.getWidth() + "  image height: " + software.software_bitmap.getHeight());
                          //  software.encoded_bitmap_full = image_saver.encoded_bitmap(software.software_bitmap, QUALITY);
                          //  software.encoded_bitmap_thumbnail = image_saver.encoded_bitmap(image_saver.scale_bitmap(software.software_bitmap, thumbnail_image_width, thumbnail_image_height),  QUALITY);

                            UserSettings.set_encoded_bitmap_thumbnail(context.getApplicationContext(), image_saver.encoded_bitmap(image_saver.scale_bitmap(software.software_bitmap, thumbnail_image_width, thumbnail_image_height),  QUALITY));
                            UserSettings.set_encoded_bitmap_full(context.getApplicationContext(), image_saver.encoded_bitmap(software.software_bitmap, QUALITY));


                            //  software.software_image_full_url = software.uid + ".jpg";
                           // software.bitmap_name_thumbnail = software.uid + "_thumbnail" + ".jpg";
                           // software.bitmap_name_full = software.uid + "_full" + ".jpg";


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    else {


                        software_imageView.setImageBitmap(software.software_bitmap);

                    }

                }
            });

    private final ActivityResultLauncher<String> retrieve_image_content = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri uri_result) {

            if(uri_result != null) {
                try {

                    Log.d(TAG, "[SoftwareProfileDialog] Software Image Retrieved");

                    int full_image_width = 0;
                    int full_image_height = 0;

                    int thumbnail_image_width = 0;
                    int thumbnail_image_height = 0;

                    int preview_image_width = 0;
                    int preview_image_height = 0;


                    DisplayMetrics display_metrics = new DisplayMetrics();
                    Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(display_metrics);

                    if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.ldpi_label))){

                        full_image_width = display_metrics.widthPixels;
                        full_image_height = 375;

                        thumbnail_image_width = 75;
                        thumbnail_image_height = 75;

                        preview_image_width = 195;
                        preview_image_height = 195;

                    }
                    else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.mdpi_label))){

                        full_image_width = display_metrics.widthPixels;
                        full_image_height = 500;

                        thumbnail_image_width = 100;
                        thumbnail_image_height = 100;

                        preview_image_width = 260;
                        preview_image_height = 260;

                    }
                    else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.hdpi_label))){

                        full_image_width = display_metrics.widthPixels;
                        full_image_height = 750;

                        thumbnail_image_width = 150;
                        thumbnail_image_height = 150;

                        preview_image_width = 390;
                        preview_image_height = 390;

                    }
                    else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xhdpi_label))){

                        full_image_width = display_metrics.widthPixels;
                        full_image_height = 1000;

                        thumbnail_image_width = 200;
                        thumbnail_image_height = 200;

                        preview_image_width = 520;
                        preview_image_height = 520;

                    }
                    else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xxhdpi_label))){

                        full_image_width = display_metrics.widthPixels;
                        full_image_height = 1500;

                        thumbnail_image_width = 300;
                        thumbnail_image_height = 300;

                        preview_image_width = 780;
                        preview_image_height = 780;

                    }
                    else if(UserSettings.get_user_dpi(context).equals(context.getResources().getString(R.string.xxxhdpi_label))){

                        full_image_width = display_metrics.widthPixels;
                        full_image_height = 2000;

                        thumbnail_image_width = 400;
                        thumbnail_image_height = 400;

                        preview_image_width = 1040;
                        preview_image_height = 1040;

                    }


                    Log.d(TAG, "[SoftwareProfileDialog] Bitmap before image width: " + software.software_bitmap.getWidth() + "  image height: " + software.software_bitmap.getHeight());

                    final int QUALITY = 75;
                    software.software_bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri_result);
                    software_imageView.setImageBitmap(image_saver.scale_bitmap(software.software_bitmap, preview_image_width, preview_image_height));
                    Log.d(TAG, "[SoftwareProfileDialog] Bitmap Target image width: " + full_image_width + "  image height: " + full_image_height);
                    software.software_bitmap = image_saver.decode_image_from_file(UserSettings.get_user_local_software_image_path(context), full_image_width, full_image_height);
                    Log.d(TAG, "[SoftwareProfileDialog] Bitmap Scaled image width: " + software.software_bitmap.getWidth() + "  image height: " + software.software_bitmap.getHeight());
                    //software.encoded_bitmap_full = image_saver.encoded_bitmap(software.software_bitmap, QUALITY);
                    //software.encoded_bitmap_thumbnail = image_saver.encoded_bitmap(image_saver.scale_bitmap(software.software_bitmap, thumbnail_image_width, thumbnail_image_height),  QUALITY);

                    UserSettings.set_encoded_bitmap_thumbnail(context.getApplicationContext(), image_saver.encoded_bitmap(image_saver.scale_bitmap(software.software_bitmap, thumbnail_image_width, thumbnail_image_height),  QUALITY));
                    UserSettings.set_encoded_bitmap_full(context.getApplicationContext(), image_saver.encoded_bitmap(software.software_bitmap, QUALITY));

                    //  software.software_image_full_url = software.uid + ".jpg";
                    //software.bitmap_name_thumbnail = software.uid + "_thumbnail" + ".jpg";
                    //software.bitmap_name_full = software.uid + "_full" + ".jpg";


                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
            else {
                Log.d(TAG, "[SoftwareProfileDialog] Software Image NOT Retrieved");
            }


        }
    });

    private final ActivityResultLauncher<Intent> start_activity_result = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult intent_activity_result) {

                    if(intent_activity_result.getResultCode() == RESULT_OK){

                        Intent intent_result = intent_activity_result.getData();
                        assert intent_result != null;
                        Bundle extras = intent_result.getExtras();
                        assert extras != null;
                        software.upc = (String) extras.get("upc_data");
                        software_upc_editText.setText(software.upc);
                    }

                }
            });


    private void capture_image(){

        String[] request_permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(ContextCompat.checkSelfPermission(this.context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            // Launch Camera
            launch_camera();
        }
        else {

            permission_request_image.launch(request_permission);

        }

    }


    private void launch_camera(){

        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (camera_intent.resolveActivity(context.getPackageManager()) != null) {

            //noinspection UnusedAssignment
            File image_file = null;
            try {
                image_file = image_saver.create_image_file(this.context, software.uid, false);

            } catch (IOException ex) {

                Toast.makeText(this.context,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (image_file != null) {
                Uri image_uri = FileProvider.getUriForFile(this.context,
                        getString(R.string.file_provider_authority),
                        image_file);
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
                capture_image_content.launch(camera_intent);
            }

        }

    }

    private void capture_upc(){

        Intent intent = new Intent(context, CameraActivity.class);
        start_activity_result.launch(intent);

    }

    private void load_hardware(){

       software_profile_dialog_view_model.load_hardware(software_platform_spinner, software);
    }

/*
    private class Gesture extends GestureDetector.SimpleOnGestureListener{

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            PopupMenu software_image_popup_menu = new PopupMenu(context, software_imageView);
            software_image_popup_menu.getMenuInflater().inflate(R.menu.software_image_popup_menu, software_image_popup_menu.getMenu());

            software_image_popup_menu.setOnMenuItemClickListener(item -> {


                switch(item.getItemId()){

                    case R.id.menu_gallery_id:
                        retrieve_gallery_image();
                        software_image_popup_menu.dismiss();
                        return true;

                    case R.id.menu_camera_id:
                        capture_image();
                        software_image_popup_menu.dismiss();
                        return true;



                }

                // profile_image_popup_menu.dismiss();
                return true;
            });

            software_image_popup_menu.show();

        }
    }
*/

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

      //  Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle out_state) {
        super.onSaveInstanceState(out_state);

        Log.d(TAG, "SoftwareProfileDialog onSaveInstanceState");

       // out_state.putParcelable("software", software);
        //out_state.putInt("position", position);

        software_profile_dialog_view_model.set_software(software);
        software_profile_dialog_view_model.set_position(position);

    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "SoftwareProfileDialog onStop");
        app.set_software_profile_dialog_foreground(false);
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
       // image_saver.delete_image_file(UserSettings.get_user_local_software_image_path(context));
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.d(TAG, "SoftwareProfileDialog onDetach");
    }
}
