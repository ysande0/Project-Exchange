package com.syncadapters.czar.exchange.datamodels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ScannerBarcode {

    private static final String TAG = "MSG";
    private BarcodeScanner barcode_scanner;
   private Context context;
    private Activity activity;
    private Image upc_image;

    private String upc;

    public ScannerBarcode(Context context, Activity activity, Image upc_image){

        this.activity = activity;
        // private FirebaseVisionBarcodeDetectorOptions firebase_detector_options;
        // private FirebaseVisionBarcodeDetector firebase_barcode_detector;
        BarcodeScannerOptions barcode_scanner_options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.UPC_A)
                .build();

       // this.firebase_barcode_detector = FirebaseVision.getInstance().getVisionBarcodeDetector(firebase_detector_options);

        barcode_scanner = BarcodeScanning.getClient(barcode_scanner_options);


        this.upc_image = upc_image;
        this.context = context;

    }


    private static final SparseIntArray ORIENTATION = new SparseIntArray();
    static{

        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 180);

    }

    public void has_storage_capacity(){

        IntentFilter low_storage_filter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
        boolean has_low_storage = context.registerReceiver(null, low_storage_filter) != null;

        if (has_low_storage) {
            Toast.makeText(this.context.getApplicationContext(),"Low Storage", Toast.LENGTH_LONG).show();
            Log.w(TAG, "ScannerBarcode: Low Storage");
        }

    }

    private int  get_Rotation_Compensation() throws CameraAccessException {

        int current_device_rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Log.d(TAG, "Current Device Orietntation: " + current_device_rotation);
        int rotation_compensation = ORIENTATION.get(current_device_rotation);
        Log.d(TAG, "pre calculation compensation: " + rotation_compensation);

        CameraManager camera_manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        String cameraId = camera_manager.getCameraIdList()[0];
        Log.d(TAG, "Camera Device: " + cameraId);
        @SuppressWarnings("ConstantConditions") int device_sensor_orientation = camera_manager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.SENSOR_ORIENTATION);
        Log.d(TAG, "device sensor orientation: " + device_sensor_orientation);

        rotation_compensation = (rotation_compensation + device_sensor_orientation + 270) % 360;
        Log.d(TAG, "post calculation compensation: " + rotation_compensation);

        return rotation_compensation;
    }


    public void scan_barcode(){

        InputImage image = null;

        if(upc_image != null)
            Log.d(TAG, "Scanning: UPC Image is available");
        else Log.d(TAG, "Scanning: UPC Image is not available");

        try {
            Log.d(TAG, "Rotation: " + get_Rotation_Compensation());
            image = InputImage.fromMediaImage(upc_image, get_Rotation_Compensation());

        }catch(CameraAccessException camera_access_error){
            camera_access_error.printStackTrace();
        }

        if(image != null)
            Log.d(TAG, "Vision Image: Image is available");
        else Log.d(TAG, "Vision Image: Image not available");

        try{

            assert image != null;
            @SuppressWarnings("unused") Task<List<com.google.mlkit.vision.barcode.Barcode>> result = barcode_scanner.process(image).addOnSuccessListener(barcodes -> {

                // Task was successful
                Log.d(TAG, "Scanning Task is successful");
                Log.d(TAG, "Barcodes: " + barcodes.size());
                for (com.google.mlkit.vision.barcode.Barcode barcode : barcodes) {

                    Rect bounding_box = barcode.getBoundingBox();

                    upc = barcode.getRawValue();
                    Log.d(TAG, "onSuccess Raw Value: " + barcode.getRawValue());
                    Log.d(TAG, "onSuccess Format: " + barcode.getFormat());
                    Log.d(TAG, "onSuccess ValueType: " + barcode.getValueType());
                    assert bounding_box != null;
                    Log.d(TAG, "TOP LEFT: " + bounding_box.left + " " + bounding_box.top);
                    Log.d(TAG, "BOTTOM RIGHT: " + bounding_box.right + " " + bounding_box.bottom);

                    Intent intent = new Intent();
                    intent.putExtra("upc_data", upc);
                    activity.setResult(RESULT_OK, intent);
                    activity.finish();

                }


            }).addOnFailureListener(error -> {

                Log.d(TAG, "Scanning Task is a failure");
                error.printStackTrace();
            }).addOnCanceledListener(() -> Log.d(TAG, "Scanning Task is canceled")).addOnCompleteListener(task -> {
                Log.d(TAG, "Task is complete");
                Log.d(TAG, "Result: " + task.getResult().toString());
            });

        }catch(Exception error){

            error.printStackTrace();
            Log.d(TAG, "Error, read fail");
        }


    }



}
