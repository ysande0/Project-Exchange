package com.syncadapters.czar.exchange.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.activities.CameraActivity;
import com.syncadapters.czar.exchange.activities.HomeActivity;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datamodels.ImageSaver;
import com.syncadapters.czar.exchange.datamodels.Software;
import com.syncadapters.czar.exchange.viewmodels.InventoryInFragmentViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import android.util.Log;

import static android.app.Activity.RESULT_OK;

@SuppressWarnings("UnusedAssignment")
public class InventoryInFragment extends Fragment {


    private static final String TAG = "OUT";
 //   private static final int REQUEST_UPC_IMAGE_CAPTURE = 1;
 //   private static final int REQUEST_SOFTWARE_IMAGE_CAPTURE = 2;
 //   private static final int REQUEST_SOFTWARE_IMAGE_GALLERY = 3;

  //  private static final int REQUESTED_CAMERA_PERMISSION = 200;

   private ImageView software_image;
   private EditText software_title_editText;
   private EditText software_publisher_editText;
   private EditText software_developer_editText;
   private EditText software_user_description_editText;
 //  private TextView software_platform_textView;
   private Spinner software_platform_spinner;
   private EditText software_upc_editText;

    private InventoryInFragmentViewModel inventory_in_fragment_view_model;

    private GestureDetector gesture_detector;

    private Context context;
    private Software software = new Software();
    private ImageSaver image_saver;
    private int dpi_classification;


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

        this.context = context;
        Log.d(TAG, "InventoryInFragment onAttach");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // inventory_in_fragment_view_model = ViewModelProviders.of(getActivity()).get(InventoryInFragmentViewModel.class);
        inventory_in_fragment_view_model = new ViewModelProvider(this).get(InventoryInFragmentViewModel.class);

        image_saver = new ImageSaver();
        DisplayMetrics display_metrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(display_metrics);
        dpi_classification = display_metrics.densityDpi;

    }

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

      View view = inflater.inflate(R.layout.fragment_inventoryin, container, false);

       // generate_software_uid();

        ScrollView scroll_view_inventory_in = view.findViewById(R.id.scroll_view_inventory_in_id);
        software_image = view.findViewById(R.id.imageView_camera_id);
        software.software_bitmap = drawable_to_bitmap(context.getDrawable(R.drawable.ic_default_software_image));
        software_image.setImageBitmap(software.software_bitmap);
        software_image.setOnTouchListener((v, event) -> gesture_detector.onTouchEvent(event));
        software_image.setOnClickListener(v -> capture_image());


        software_title_editText = view.findViewById(R.id.title_editText_id);
        software_publisher_editText = view.findViewById(R.id.game_publisher_editText_id);
        software_developer_editText = view.findViewById(R.id.game_developer_editText_id);
        software_user_description_editText = view.findViewById(R.id.user_description_editText_id);
      //  software_platform_textView = view.findViewById(R.id.hardware_textView_id);

        software_platform_spinner = view.findViewById(R.id.hardware_spinner_id);
        software_upc_editText = view.findViewById(R.id.upc_editText_id);

        ImageButton upc_camera_button = view.findViewById(R.id.upc_capture_button_id);
        FloatingActionButton save_button = view.findViewById(R.id.save_software_float_button_id);
        FloatingActionButton continue_button = view.findViewById(R.id.continue_float_button_id);

        gesture_detector = new GestureDetector(this.context, new Gesture());

        if(savedInstanceState != null){

            Log.d(TAG, "[InventoryInFragment] savedInstanceState not null");
        //    software_image.setImageResource(R.drawable.default_software_image);
            software.uid = savedInstanceState.getString(context.getResources().getString(R.string.software_uid_key));
//            software.title = savedInstanceState.getString(context.getResources().getString(R.string.title_key));
 //           software.game_publisher = savedInstanceState.getString(context.getResources().getString(R.string.publisher_key));
   //         software.game_developer = savedInstanceState.getString(context.getResources().getString(R.string.developer_key));
     //       software.platform = savedInstanceState.getString(context.getResources().getString(R.string.platform_key));
       //     software.user_description = savedInstanceState.getString(context.getResources().getString(R.string.user_description_key));
           // user_platforms = savedInstanceState.getStringArrayList("platforms");
         //   software.upc = savedInstanceState.getString(context.getResources().getString(R.string.upc_key));
          //  software.encoded_bitmap_thumbnail = savedInstanceState.getString(context.getResources().getString(R.string.image_encoded_thumbnail_key));
           // software.encoded_bitmap_full = savedInstanceState.getString(context.getResources().getString(R.string.image_encoded_full_key));
           // software.bitmap_name_thumbnail = savedInstanceState.getString(context.getResources().getString(R.string.image_name_thumbnail_key));
            //software.bitmap_name_full = savedInstanceState.getString(context.getResources().getString(R.string.image_name_full_key));
            //software.software_bitmap = savedInstanceState.getParcelable(context.getResources().getString(R.string.bitmap_key));
            software.software_bitmap = inventory_in_fragment_view_model.get_bitmap_live_data();
            //is_camera_open = savedInstanceState.getBoolean(context.getResources().getString(R.string.is_camera_open_key));

            software_image.setImageBitmap(software.software_bitmap);
            //software_title_editText.setText(software.title);
            //software_publisher_editText.setText(software.game_publisher);
            //software_developer_editText.setText(software.game_developer);
            //software_user_description_editText.setText(software.user_description);



        }

        getParentFragmentManager().setFragmentResultListener("edit_software", this, (request_key, result) -> {

            software = result.getParcelable("software_bundle");
            assert software != null;
            software_image.setImageBitmap(software.software_bitmap);
            software_title_editText.setText(software.title);
            software_publisher_editText.setText(software.game_publisher);
            software_developer_editText.setText(software.game_developer);
            // Platform

            //software_platform_textView.setText(software.platform);
            software_upc_editText.setText(software.upc);
            software_user_description_editText.setText(software.user_description);

           // ITEM_OPERATION = 4;

        });


        load_hardware();
      upc_camera_button.setOnClickListener(v -> capture_upc());

      save_button.setOnClickListener(v -> {


          scroll_view_inventory_in.fullScroll(ScrollView.FOCUS_UP);
          software.platform = software_platform_spinner.getSelectedItem().toString();
          software.upc = software_upc_editText.getText().toString().trim();

          if(software.software_bitmap == null){

              return;
          }

          Bitmap default_software_image_bitmap = drawable_to_bitmap(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_default_software_image, null));
          if(default_software_image_bitmap.sameAs(software.software_bitmap)){


              Toast.makeText(context, "Please capture an image", Toast.LENGTH_LONG).show();
              return;

          }

          software.encoded_bitmap_thumbnail = UserSettings.get_encoded_bitmap_thumbnail(context.getApplicationContext());
          software.encoded_bitmap_full = UserSettings.get_encoded_bitmap_full(context.getApplicationContext());

          if(software.platform == null){
              Toast.makeText(context, "Platform not selected", Toast.LENGTH_LONG).show();
              return;
          }

        if(software.uid.isEmpty()) {
            Log.d(TAG, "[InventoryInFragment] Software UID is generating. Retrieved image");
            generate_software_uid();
            software.bitmap_name_thumbnail = software.uid + "_thumbnail" + ".jpg";
            software.bitmap_name_full = software.uid + "_full" + ".jpg";
        }
        else {
            Log.d(TAG, "[InventoryInFragment] Software UID is already generated. Captured image");
            software.bitmap_name_thumbnail = software.uid + "_thumbnail" + ".jpg";
            software.bitmap_name_full = software.uid + "_full" + ".jpg";
        }


          store_software();

      });



      continue_button.hide();
        //noinspection ConstantConditions
        if(getActivity().getIntent().hasExtra("Beginner")){


          continue_button.show();
          continue_button.setOnClickListener(v -> alert_dialog_box());

      }

      return view;
    }

    private void generate_software_uid(){

        UUID uuid = UUID.randomUUID();
        software.uid = uuid.toString();

    }

    private void load_hardware(){

        inventory_in_fragment_view_model.query_hardware(this.context, software_platform_spinner, software);

    }

    @SuppressWarnings({"RedundantCast", "RedundantSuppression"})
    private final ActivityResultLauncher<String[]> permission_request_image =  registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),

            result -> {

                for(Map.Entry<String, Boolean> entry : result.entrySet()){

                    //noinspection PointlessBooleanExpression
                    if(entry.getValue() == false){

                        Log.d(TAG, "[InventoryInFragment] " +  entry.getKey() + " is denied");
                        return;
                    }
                }

                Log.d(TAG, "[InventoryInFragment] Permissions granted. Launching Camera...");
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
                        //  file_uri_captured = result.getData().getData();

                        try {
                            Log.d(TAG, "[InventoryInFragment] Software Image Captured");

                             int full_image_width = 0;
                             int full_image_height = 0;

                            int thumbnail_image_width = 0;
                            int thumbnail_image_height = 0;

                            int preview_image_width = 0;
                            int preview_image_height = 0;
                            final int QUALITY = 75;

                            DisplayMetrics display_metrics = new DisplayMetrics();
                            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(display_metrics);

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


                            Log.d(TAG, "[InventoryInFragment] Bitmap before image width: " + software.software_bitmap.getWidth() + "  image height: " + software.software_bitmap.getHeight());


                            software.software_bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), file_uri_captured);
                            software_image.setImageBitmap(image_saver.scale_bitmap(software.software_bitmap, preview_image_width, preview_image_height));

                            Log.d(TAG, "[InventoryInFragment] Bitmap Target image width: " + full_image_width + "  image height: " + full_image_height);
                            software.software_bitmap = image_saver.decode_image_from_file(UserSettings.get_user_local_software_image_path(context), full_image_width, full_image_height);
                            Log.d(TAG, "[InventoryInFragment] Bitmap Scaled image width: " + software.software_bitmap.getWidth() + "  image height: " + software.software_bitmap.getHeight());
                         //   software.encoded_bitmap_full = image_saver.encoded_bitmap(software.software_bitmap, QUALITY);
                         //   software.encoded_bitmap_thumbnail = image_saver.encoded_bitmap(image_saver.scale_bitmap(software.software_bitmap, thumbnail_image_width, thumbnail_image_height),  QUALITY);

                            UserSettings.set_encoded_bitmap_thumbnail(context.getApplicationContext(), image_saver.encoded_bitmap(image_saver.scale_bitmap(software.software_bitmap, thumbnail_image_width, thumbnail_image_height),  QUALITY));
                            UserSettings.set_encoded_bitmap_full(context.getApplicationContext(), image_saver.encoded_bitmap(software.software_bitmap, QUALITY));
                            //  software.software_image_full_url = software.uid + ".jpg";
                         //   software.bitmap_name_thumbnail = software.uid + "_thumbnail" + ".jpg";
                         //   software.bitmap_name_full = software.uid + "_full" + ".jpg";
                         //   Log.d(TAG, "[InventoryInFragment] Encoded Bitmap: " + software.encoded_bitmap_full);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    else {


                        Log.d(TAG, "[InventoryInFragment] Software Image NOT Captured");
                        software_image.setImageBitmap(software.software_bitmap);

                    }

                }
            });

    private final ActivityResultLauncher<String> retrieve_image_content = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri uri_result) {

            if(uri_result != null) {
                try {

                    Log.d(TAG, "[InventoryInFragment] Software Image Retrieved");

                    int full_image_width = 0;
                    int full_image_height = 0;

                    int thumbnail_image_width = 0;
                    int thumbnail_image_height = 0;

                    int preview_image_width = 0;
                    int preview_image_height = 0;


                    DisplayMetrics display_metrics = new DisplayMetrics();
                    requireActivity().getWindowManager().getDefaultDisplay().getMetrics(display_metrics);

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


                    Log.d(TAG, "[InventoryInFragment] Bitmap before image width: " + software.software_bitmap.getWidth() + "  image height: " + software.software_bitmap.getHeight());

                    generate_software_uid();
                    final int QUALITY = 75;
                    software.software_bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri_result);
                    software_image.setImageBitmap(image_saver.scale_bitmap(software.software_bitmap, preview_image_width, preview_image_height));
                     Log.d(TAG, "[InventoryInFragment] Bitmap Target image width: " + full_image_width + "  image height: " + full_image_height);
                   // Log.d(TAG, "[InventoryInFragment] Get local software image path: " + UserSettings.get_user_local_software_image_path(context));
                    InputStream input_stream = context.getContentResolver().openInputStream(uri_result);
                    software.software_bitmap = image_saver.decode_image_from_input_stream(context.getApplicationContext(), input_stream, uri_result, full_image_width, full_image_height);
              //      Log.d(TAG, "[InventoryInFragment] Bitmap Scaled image width: " + software.software_bitmap.getWidth() + "  image height: " + software.software_bitmap.getHeight());
                   // software.encoded_bitmap_full = image_saver.encoded_bitmap(software.software_bitmap, QUALITY);
                   // software.encoded_bitmap_thumbnail = image_saver.encoded_bitmap(image_saver.scale_bitmap(software.software_bitmap, thumbnail_image_width, thumbnail_image_height),  QUALITY);

                    UserSettings.set_encoded_bitmap_thumbnail(context.getApplicationContext(), image_saver.encoded_bitmap(image_saver.scale_bitmap(software.software_bitmap, thumbnail_image_width, thumbnail_image_height),  QUALITY));
                    UserSettings.set_encoded_bitmap_full(context.getApplicationContext(), image_saver.encoded_bitmap(software.software_bitmap, QUALITY));

                    //  software.software_image_full_url = software.uid + ".jpg";
                  //  software.bitmap_name_thumbnail = software.uid + "_thumbnail" + ".jpg";
                  //  software.bitmap_name_full = software.uid + "_full" + ".jpg";


                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
            else {

                Log.d(TAG, "[InventoryInFragment] Software Image NOT Retrieved");
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

            generate_software_uid();
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

    private void retrieve_gallery_image(){

        final String uri = "image/*";
        retrieve_image_content.launch(uri);
    }

    private void reset_profile_image(){

        software.software_bitmap = drawable_to_bitmap(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_default_software_image, null));
        software_image.setImageBitmap(software.software_bitmap);
    }


    private void alert_dialog_box(){

        androidx.appcompat.app.AlertDialog.Builder alert_dialog_builder = new androidx.appcompat.app.AlertDialog.Builder(this.context);
        alert_dialog_builder.setMessage("Are you sure you want to continue?");
        alert_dialog_builder.setPositiveButton("Yes", (dialog, which) -> {

            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
        });

        alert_dialog_builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        androidx.appcompat.app.AlertDialog alert_dialog = alert_dialog_builder.create();
        alert_dialog.show();

    }

    private void store_software(){

        software.title = software_title_editText.getText().toString().trim();
        software.game_publisher = software_publisher_editText.getText().toString().trim();
        software.game_developer = software_developer_editText.getText().toString().trim();
        software.user_description = software_user_description_editText.getText().toString();
        // NOTE: DO not forget image

        boolean form_error_flag = false;

        /*
        if(software_image.getDrawable() == null) {
            software_image.setBackgroundColor(Color.RED);
          form_error_flag = true;
        }

         */

        if(software.title.isEmpty()) {
            software_title_editText.setError(context.getString(R.string.software_title_edittext_error));
            form_error_flag = true;
        }


        if(software.game_publisher.isEmpty()) {
            software_publisher_editText.setError(context.getString(R.string.software_publisher_edittext_error));
            form_error_flag = true;
        }

        if(software.game_developer.isEmpty()) {
            software_developer_editText.setError(context.getString(R.string.software_developer_edittext_error));
            form_error_flag = true;
        }

        /*
        if(software.user_description.isEmpty()){

            software_user_description_editText.setBackgroundColor(Color.RED);
            form_error_flag = true;

        }
         */

        if(software.upc.isEmpty() || software.upc.length() < 12) {
            software_upc_editText.setError(context.getString(R.string.software_upc_edittext_error));
            form_error_flag = true;
        }
        else
            software_upc_editText.setBackground(getResources().getDrawable(R.drawable.light_gray_border_round_edittext));



        if(software.platform.equals("Select Platform...") || software.platform.isEmpty()){

           // software_platform_textView.setTextColor(getResources().getColor(R.color.light_pink_red));
            AlertDialog.Builder alert_dialog_platform_builder = new AlertDialog.Builder(context);
            alert_dialog_platform_builder.setMessage(context.getResources().getString(R.string.software_platform_spinner_error));
            alert_dialog_platform_builder.setNegativeButton(context.getResources().getString(R.string.ok), null);
            alert_dialog_platform_builder.show();

            form_error_flag = true;
        }

        if(software.encoded_bitmap_thumbnail.isEmpty()){

            AlertDialog.Builder alert_dialog_software_image_builder = new AlertDialog.Builder(context);
            alert_dialog_software_image_builder.setMessage(context.getResources().getString(R.string.software_image_imageview_error));
            alert_dialog_software_image_builder.setNegativeButton(context.getResources().getString(R.string.ok), null);
            alert_dialog_software_image_builder.show();

            form_error_flag = true;
        }

        if(software.encoded_bitmap_full.isEmpty()){

            AlertDialog.Builder alert_dialog_software_image_builder = new AlertDialog.Builder(context);
            alert_dialog_software_image_builder.setMessage(context.getResources().getString(R.string.software_image_imageview_error));
            alert_dialog_software_image_builder.setNegativeButton(context.getResources().getString(R.string.ok), null);
            alert_dialog_software_image_builder.show();

            form_error_flag = true;
        }

        if(form_error_flag)
            return;


        JSONObject json_object = new JSONObject();

        try {

            int ITEM_OPERATION = 1;
         //   Log.d(TAG, "[InventoryInFragment] OPS: " + ITEM_OPERATION);
            json_object.put(context.getResources().getString(R.string.category_label), 101);
            json_object.put(context.getResources().getString(R.string.operation_label), ITEM_OPERATION);
            json_object.put(context.getResources().getString(R.string.uid_label), UserSettings.get_user_uid(this.context));
            json_object.put(context.getResources().getString(R.string.id_label), UserSettings.get_user_id(this.context));
            json_object.put(context.getResources().getString(R.string.software_uid_key), software.uid);
            json_object.put(context.getResources().getString(R.string.title_key), software.title);
            json_object.put(context.getResources().getString(R.string.publisher_key), software.game_publisher);
            json_object.put(context.getResources().getString(R.string.developer_key), software.game_developer);
            json_object.put(context.getResources().getString(R.string.access_token_key), UserSettings.get_user_token(this.context));
            json_object.put(context.getResources().getString(R.string.platform_key), software.platform);
            json_object.put(context.getResources().getString(R.string.upc_key), software.upc);
            json_object.put(context.getResources().getString(R.string.user_description_key), software.user_description);
            json_object.put(context.getResources().getString(R.string.user_dpi_label), dpi_classification);
            json_object.put(context.getResources().getString(R.string.image_name_thumbnail_key),  software.bitmap_name_thumbnail);
            json_object.put(context.getResources().getString(R.string.image_name_full_key),  software.bitmap_name_full);
            json_object.put(context.getResources().getString(R.string.image_encoded_full_key), software.encoded_bitmap_full);
            json_object.put(context.getResources().getString(R.string.image_encoded_thumbnail_key), software.encoded_bitmap_thumbnail);

            if(software.encoded_bitmap_full != null) {

               // Log.d(TAG, "Encoded Bitmap Full: " + software.encoded_bitmap_full);
                json_object.put(context.getResources().getString(R.string.image_encoded_full_key), software.encoded_bitmap_full);
            }



        }catch (JSONException json_error){

            json_error.printStackTrace();
        }

        inventory_in_fragment_view_model.insert_software(software, json_object);

        software.software_bitmap = drawable_to_bitmap(context.getDrawable(R.drawable.ic_default_software_image));
        software_image.setImageBitmap(software.software_bitmap);
        software_title_editText.getText().clear();
        software_publisher_editText.getText().clear();
        software_developer_editText.getText().clear();
        software_user_description_editText.getText().clear();

        // software_platform_textView.setText("");
        software_upc_editText.setText("");

        //noinspection ConstantConditions
        if(!(getActivity().getIntent().hasExtra("Beginner"))) {

            Log.d(TAG, "[InventoryInFragment] Returning to HomeActivity. (Experienced user)");
            Bundle result = new Bundle();
            result.putBoolean("insert_item", true);
            result.putParcelable("software", software);
            getParentFragmentManager().setFragmentResult("insert_software", result);
            getActivity().onBackPressed();
        }

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


    class Gesture extends GestureDetector.SimpleOnGestureListener{

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            PopupMenu software_image_popup_menu = new PopupMenu(context, software_image);
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

                    case  R.id.menu_remove_id:
                        reset_profile_image();
                        software_image_popup_menu.dismiss();
                        return true;


                }

                // profile_image_popup_menu.dismiss();
                return true;
            });

            software_image_popup_menu.show();

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "InventoryInFragment onResume");

       // Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "InventoryInFragment onPause");
      //  Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "InventoryInFragment onStop");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "InventoryInFragment onDestroy");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "InventoryInFragment onDetach");
    }

    public void onSaveInstanceState(@NotNull Bundle out_state){
        super.onSaveInstanceState(out_state);
        Log.d(TAG, "InventoryInFragment onSaveInstanceState");

        out_state.putString(context.getResources().getString(R.string.software_uid_key), software.uid); // 1
        inventory_in_fragment_view_model.set_bitmap_live_data(software.software_bitmap); // 13

    }

}