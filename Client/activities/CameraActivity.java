package com.syncadapters.czar.exchange.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageButton;

import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.datamodels.ScannerBarcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

public class CameraActivity extends AppCompatActivity {

    private static final SparseIntArray ORIENTATION = new SparseIntArray();
    private static int counter = 0;
    static{

        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 180);

    }

    private static final String TAG = "MSG";
    private static final int REQUESTED_CAMERA_PERMISSION = 200;

    private TextureView texture_view;

    private CameraDevice camera_device;
    private CameraCaptureSession camera_capture_session;
    private CaptureRequest.Builder capture_request_builder;
    private ImageReader image_reader;
    private Size image_dimensions;
    private Image upc_image;


    private final Semaphore camera_open_close_lock = new Semaphore(1);
    private static HandlerThread background_handler_thread;
    private static Handler background_handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        texture_view =  findViewById(R.id.texture_view_id);
        texture_view.setSurfaceTextureListener(texture_listener);


        ImageButton cancel_upc_camera = findViewById(R.id.upc_cancel_capture_button_id);
        cancel_upc_camera.setOnClickListener(v -> {

            close_camera();
            finish();
        });

        ImageButton upc_capture_button = findViewById(R.id.upc_capture_button_id);
        upc_capture_button.setOnClickListener(v -> capture_image());


    }
    
    private final TextureView.SurfaceTextureListener texture_listener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            open_camera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private final CameraDevice.StateCallback camera_state_callback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {

            camera_device = camera;
            create_camera_preview();

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            close_camera();
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            super.onClosed(camera);
            camera_device = camera;
            close_camera();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera_device = camera;
            close_camera();
        }
    };

    @SuppressWarnings("unused")
    final CameraCaptureSession.CaptureCallback capture_callback_listener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            create_camera_preview();
        }
    };

    private void open_camera(){

        if(ActivityCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, REQUESTED_CAMERA_PERMISSION);
            return;
        }

        CameraManager camera_manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try{

            String camera_id = camera_manager.getCameraIdList()[0];
            CameraCharacteristics camera_characteristics = camera_manager.getCameraCharacteristics(camera_id);
            StreamConfigurationMap stream_config_map = camera_characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert stream_config_map != null;
            image_dimensions = stream_config_map.getOutputSizes(SurfaceTexture.class)[0];
            counter++;
            camera_manager.openCamera(camera_id, camera_state_callback, null);

        }catch(CameraAccessException camera_access_error){
            camera_access_error.printStackTrace();
        }
    }

    private void close_camera(){

        try {
            camera_open_close_lock.acquire();
            if (camera_device != null) {

                camera_device.close();
                camera_device = null;

            }

            if (image_reader != null) {

                image_reader.close();
                image_reader = null;

            }
        }
        catch (InterruptedException error){
            error.printStackTrace();
        }finally {
            camera_open_close_lock.release();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUESTED_CAMERA_PERMISSION){

            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish();
            }

        }

    }

    private void create_camera_preview(){

        try{

            SurfaceTexture surface_texture = texture_view.getSurfaceTexture();

            assert surface_texture != null;
            surface_texture.setDefaultBufferSize(image_dimensions.getWidth(), image_dimensions.getHeight());


            Surface surface = new Surface(surface_texture);

            capture_request_builder = camera_device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            capture_request_builder.addTarget(surface);

            camera_device.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {

                    if(camera_device == null) {
                        return;
                    }

                    camera_capture_session = session;
                    update_camera_preview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, null);



        }catch(CameraAccessException camera_access_error){

            camera_access_error.printStackTrace();
        }

        camera_open_close_lock.release();
    }

    private void update_camera_preview(){

        if(camera_device == null) {
            return;
        }

        capture_request_builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        try{

            camera_capture_session.setRepeatingRequest(capture_request_builder.build(), null, background_handler);

        }catch(CameraAccessException camera_access_error){

            camera_access_error.printStackTrace();
        }

    }

    private void start_background_thread(){

        background_handler_thread = new HandlerThread("Camera Background");
        background_handler_thread.start();
        background_handler = new Handler(background_handler_thread.getLooper());

    }

    private void stop_background_thread(){

        if(background_handler_thread != null) {
            close_camera();
            background_handler_thread.quitSafely();
            try {

                background_handler_thread.join();
                background_handler_thread = null;
                background_handler = null;
            } catch (InterruptedException error) {
                error.printStackTrace();
            }

        }

    }

    private void capture_image(){

        if(camera_device == null){

            return;
        }

        CameraManager camera_manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try{

            //noinspection unused
            CameraCharacteristics camera_characteristics = camera_manager.getCameraCharacteristics(camera_device.getId());

            int width = 1920;
            int height = 1080;
    
            image_reader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, 1);
            List<Surface> output_surface = new ArrayList<>(1);
            output_surface.add(image_reader.getSurface());
            output_surface.add(new Surface(texture_view.getSurfaceTexture()));

            final CaptureRequest.Builder capture_builder = camera_device.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            capture_builder.addTarget(image_reader.getSurface());
            capture_builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            capture_builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            ImageReader.OnImageAvailableListener reader_listener = reader -> {

                Image image = reader.acquireLatestImage();

                upc_image = image;

                if(image != null){

                    //noinspection ConstantConditions
                    upc_image = image;

                    ScannerBarcode scanner_barcode = new ScannerBarcode(CameraActivity.this, CameraActivity.this, upc_image);
                    scanner_barcode.has_storage_capacity();
                    scanner_barcode.scan_barcode();


                }



            };

            image_reader.setOnImageAvailableListener(reader_listener, background_handler);
            final CameraCaptureSession.CaptureCallback capture_callback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    create_camera_preview();
                }
            };

            camera_device.createCaptureSession(output_surface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {

                    try{
                        session.capture(capture_builder.build(), capture_callback, background_handler);

                    }catch (CameraAccessException camera_access_error){
                        camera_access_error.printStackTrace();
                    }

                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, background_handler);

        }catch(CameraAccessException camera_access_error){
            camera_access_error.printStackTrace();
        }


        camera_open_close_lock.release();

    }


    @Override
    protected void onResume() {
        super.onResume();

        start_background_thread();

        if(texture_view.isAvailable()){
            open_camera();
        }
        else {
            texture_view.setSurfaceTextureListener(texture_listener);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        stop_background_thread();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
