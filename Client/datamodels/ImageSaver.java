package com.syncadapters.czar.exchange.datamodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


public class ImageSaver {

    @SuppressWarnings("FieldCanBeLocal")
    private String image_file_path;

    public ImageSaver(){

    }
    
    public File create_image_file(Context context, String image_file_name, boolean is_profile_image) throws IOException{


        File storage_dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        if(is_profile_image){

            image = File.createTempFile(image_file_name,  ".jpg", storage_dir);
            image_file_path = image.getAbsolutePath();
            UserSettings.set_user_local_profile_image_path(context, image_file_path);

        }
        else{

            image = File.createTempFile(image_file_name,  ".jpg", storage_dir);
            image_file_path = image.getAbsolutePath();
            UserSettings.set_user_local_software_image_path(context, image_file_path);

        }

        return image;

    }

    public void delete_image_file(String file_path){

        File delete_image_file = new File(file_path);

    }

    public String encoded_bitmap(Bitmap image_bitmap, int bitmap_image_quality) {


        ByteArrayOutputStream byte_array_out_stream = new ByteArrayOutputStream();

        image_bitmap.compress(Bitmap.CompressFormat.JPEG, bitmap_image_quality, byte_array_out_stream);

        byte[] images_bytes = byte_array_out_stream.toByteArray();
        return Base64.encodeToString(images_bytes, Base64.DEFAULT);

    }

    public String encoded_bitmap(Bitmap image_bitmap, Bitmap.CompressFormat bitmap_compress_format, int bitmap_image_quality) {

        ByteArrayOutputStream byte_array_out_stream = new ByteArrayOutputStream();

        image_bitmap.compress(bitmap_compress_format, bitmap_image_quality, byte_array_out_stream);

        byte[] images_bytes = byte_array_out_stream.toByteArray();
        return Base64.encodeToString(images_bytes, Base64.DEFAULT);
    }

    public Bitmap scale_bitmap(Bitmap bitmap, int wanted_width, int wanted_height) {

        Bitmap output = Bitmap.createBitmap(wanted_width, wanted_height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix matrix = new Matrix();
        matrix.setScale((float) wanted_width / bitmap.getWidth(), (float) wanted_height / bitmap.getHeight());
        canvas.drawBitmap(bitmap, matrix, new Paint());

        return output;
    }

    public Bitmap decode_image_from_file(String local_file_path, int target_width, int target_height){


        BitmapFactory.Options bitmap_factory_options = new BitmapFactory.Options();
        bitmap_factory_options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(local_file_path, bitmap_factory_options);

        bitmap_factory_options.inSampleSize = calculate_in_sample_size(bitmap_factory_options, target_width, target_height);

        bitmap_factory_options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(local_file_path, bitmap_factory_options);

    }

    public Bitmap decode_image_from_encode(String encoded_bitmap, int target_width, int target_height){

        byte[] decoded_bitmap = Base64.decode(encoded_bitmap, Base64.DEFAULT);
        BitmapFactory.Options bitmap_factory_options = new BitmapFactory.Options();
        bitmap_factory_options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(decoded_bitmap, 0, decoded_bitmap.length, bitmap_factory_options);

        bitmap_factory_options.inSampleSize = calculate_in_sample_size(bitmap_factory_options, target_width, target_height);

        bitmap_factory_options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(decoded_bitmap, 0, decoded_bitmap.length, bitmap_factory_options);

    }

    public Bitmap decode_image_from_input_stream(Context context, InputStream input_stream, Uri uri_result, int target_width, int target_height){

        BitmapFactory.Options bitmap_factory_options = new BitmapFactory.Options();
        bitmap_factory_options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input_stream, null, bitmap_factory_options);



        bitmap_factory_options.inSampleSize = calculate_in_sample_size(bitmap_factory_options, target_width, target_height);

        bitmap_factory_options.inJustDecodeBounds = false;

        try {
            input_stream.close();

        } catch (IOException e) {
            return null;
        }

        try{

            input_stream = context.getContentResolver().openInputStream(uri_result);

        }catch (IOException error){
            return null;
        }

        Bitmap bitmap = BitmapFactory.decodeStream(input_stream, null, bitmap_factory_options);

        try {
            Objects.requireNonNull(input_stream).close();
        } catch (IOException e) {
            return null;
        }

        return bitmap;

    }

    private static int calculate_in_sample_size(BitmapFactory.Options bitmap_factory_options, int target_width, int target_height){

        final int source_width = bitmap_factory_options.outWidth;
        final int source_height = bitmap_factory_options.outHeight;
        int in_sample_size = 1;

        if (source_height > target_height || source_width > target_width) {

            final int half_width = source_width / 2;
            final int half_height = source_height / 2;

            int counter = 0;

            while ((half_height / in_sample_size) >= target_height
                    && (half_width / in_sample_size) >= target_width) {
                counter++;
                in_sample_size *= 2;
            }
        }

        return in_sample_size;
    }


    @SuppressWarnings("unused")
    public Bitmap decoded_bitmap(String encoded_image) {

        byte[] decoded_string = Base64.decode(encoded_image, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(decoded_string, 0, decoded_string.length);
    }




}
