package com.amrizal.example.cameratester;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {

    public static final String IMAGE_PATH = "image_path";
    public static final int CAMERA_ACTIVITY_RESULT = 1001;
    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final int CAMERA_RESULT = 1002;
    private static final String BITMAP = "bitmap";
    private static final String FILE_URI = "file_uri";

    private TextView textGPS;
    private TextView textCurrentTime;
    private ImageView placeholder;
    private ProgressBar progressBar;
    private Uri fileUri;
    private Bitmap displayedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        if (savedInstanceState != null) {
            displayedBitmap = savedInstanceState.getParcelable(BITMAP);
            placeholder.setImageBitmap(displayedBitmap);
            fileUri = Uri.parse(savedInstanceState.getString(FILE_URI));
        }
        else{
            takePicture();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(BITMAP, displayedBitmap);
        outState.putString(FILE_URI, fileUri.toString());
    }

    private void initView() {
        setContentView(R.layout.activity_camera);

        textGPS = (TextView)findViewById(R.id.gps);
        textCurrentTime = (TextView)findViewById(R.id.current_time);
        placeholder = (ImageView)findViewById(R.id.placeholder);
        progressBar = (ProgressBar)findViewById(R.id.progress_indicator);

        findViewById(R.id.retake_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        findViewById(R.id.save_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhoto();
            }
        });
    }

    private void savePhoto() {
        // get the Entered  message
        Intent intentMessage=new Intent();
        // put the message in Intent
        intentMessage.putExtra(IMAGE_PATH, fileUri.getPath());
        // Set The Result in Intent
        setResult(CAMERA_ACTIVITY_RESULT,intentMessage);
        this.finish();
    }

    private void takePicture() {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri();
        intent.putExtra( MediaStore.EXTRA_OUTPUT, fileUri );

        startActivityForResult(intent, CAMERA_RESULT);
    }

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getResources().getString(R.string.app_name));
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_RESULT
                && data != null) {

            fileUri = data.getData();
            displayedBitmap = getBitmap(fileUri);

            if(displayedBitmap != null){
                Canvas canvas = new Canvas(displayedBitmap);
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setShadowLayer(20, 0,0, Color.BLACK);
                paint.setTextSize(20);
                canvas.drawText("SOMETHING KOOOLL", 10, 10, paint);

                Log.d(TAG, "width: " + displayedBitmap.getWidth() + ", height: " + displayedBitmap.getHeight());

                placeholder.setImageBitmap(displayedBitmap);
            }

            progressBar.setVisibility(View.GONE);
        }
    }

    Bitmap getBitmap(Uri uri){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(uri.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        int exifOrientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        int rotate = 0;

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
        }

        Bitmap sourceBitmap = null;
        try {
            sourceBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
        } catch (IOException e) {
            return null;
        }

        Bitmap.Config bitmapConfig =   sourceBitmap.getConfig();
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }

        if (rotate != 0) {
            int w = sourceBitmap.getWidth();
            int h = sourceBitmap.getHeight();

// Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);

            // Rotating Bitmap & convert to ARGB_8888, required by tess
            sourceBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, w, h, mtx, false);
            //sourceBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true);
        }

        return sourceBitmap;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private Bitmap tagImage(Bitmap bp) {
        int width = bp.getWidth();
        int height = bp.getHeight();

        Log.d(TAG, "width: " + width + ", height: " + height);

        Bitmap.Config config = bp.getConfig();
        if(config == null){
            config = Bitmap.Config.ARGB_8888;
        }

        Bitmap newBitmap = Bitmap.createBitmap(bp.getWidth(), bp.getHeight(), config);

        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bp, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        paint.setStyle(Paint.Style.FILL);
        paint.setShadowLayer(20f, 0, 0, Color.BLACK);

        canvas.drawText("Some Text here", 10, 10, paint);

        return newBitmap;
    }
}
