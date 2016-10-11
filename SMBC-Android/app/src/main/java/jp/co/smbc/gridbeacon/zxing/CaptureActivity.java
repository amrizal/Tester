/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.smbc.gridbeacon.zxing;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.prefs.Preferences;

import jp.co.smbc.gridbeacon.BaseActivity;
import jp.co.smbc.gridbeacon.BeaconConstants;
import jp.co.smbc.gridbeacon.BeaconService;
import jp.co.smbc.gridbeacon.BeaconUtility;
import jp.co.smbc.gridbeacon.MainActivity;
import jp.co.smbc.gridbeacon.R;
import jp.co.smbc.gridbeacon.zxing.camera.CameraManager;
import jp.co.smbc.gridbeacon.zxing.result.ResultHandler;
import jp.co.smbc.gridbeacon.zxing.result.ResultHandlerFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a viewfinder to help the user place the barcode correctly, shows feedback as the image processing is
 * happening, and then overlays the results when a scan is successful.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends BaseActivity implements SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;
    public static final int HISTORY_REQUEST_CODE = 0x0000bacc;
    private SharedPreferences prefs;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private Result savedResultToShow;
    private ViewfinderView viewfinderView;
    private TextView statusView;
    private View resultView;
    private Result lastResult;
    private boolean hasSurface;
    private boolean copyToClipboard;
    private IntentSource source;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    CaptureActivityReceiver receiver = new CaptureActivityReceiver();

    private AmbientLightManager ambientLightManager;
    private String gid;
    private String vdid;
    private String url;
    private String cloudid;

    // private Timer restartTimer;

    ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        // requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(icicle);

        Log.d(TAG, "running onCreate()...");
        // getSupportActionBar().show();
        // hide the icon ion in action bar
        // getActionBar().setIcon(android.R.color.transparent);

        /*Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.capture);

        hasSurface = false;

        inactivityTimer = new InactivityTimer(this);

        ambientLightManager = new AmbientLightManager(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        cameraManager = new CameraManager(getApplication());
        // showHelpOnFirstLaunch();
        setupActionBar(String.valueOf(getTitle()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "running onResume()...");
        // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
        // want to open the camera driver and measure the screen size if we're going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
        // off screen.

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);

        resultView = findViewById(R.id.result_view);
        statusView = (TextView) findViewById(R.id.status_view);

        lastResult = null;

        resetStatusView();

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        Log.d(TAG, "onResume(), hasSurface = " + hasSurface);
        if (hasSurface) {

            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.

            if (handler != null) {
                handler.quitSynchronously();
                handler = null;
            }
            cameraManager.closeDriver();

            initCamera(surfaceHolder);

        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            handler = null;
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        ambientLightManager.start(cameraManager);

        inactivityTimer.onResume();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent = getIntent();
        copyToClipboard = prefs.getBoolean(PreferencesActivity.KEY_COPY_TO_CLIPBOARD, true) && (intent == null || intent.getBooleanExtra(Intents.Scan.SAVE_HISTORY, true));

        source = IntentSource.NONE;
        decodeFormats = null;
        characterSet = null;

        if (intent != null) {

            String action = intent.getAction();
            String dataString = intent.getDataString();

            Log.d(TAG, "action = " + action + ", dataString = " + dataString);
            // RestartTimer(5000);

            if (Intents.Scan.ACTION.equals(action)) {

                // Scan the formats the intent requested, and return the result to the calling activity.
                source = IntentSource.NATIVE_APP_INTENT;
                decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
                decodeHints = DecodeHintManager.parseDecodeHints(intent);

                if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
                    int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
                    int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
                    if (width > 0 && height > 0) {
                        cameraManager.setManualFramingRect(width, height);
                    }
                }

                String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
                if (customPromptMessage != null) {
                    statusView.setText(customPromptMessage);
                }
                Log.d(TAG, "intent extra customPromptMessage = " + customPromptMessage);
            }

            characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

            Log.d(TAG, "is camera open = " + cameraManager.isOpen());

        } else {
            Log.d(TAG, "intent is null?");
        }

        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BeaconService.ACTION_PROCESS_QR_CONTENT);
        intentFilter.addAction(BeaconService.ACTION_REGISTER_BEACON);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "running onPause()...");
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        ambientLightManager.stop();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "running onStart()");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "running onDestroy()...");
        inactivityTimer.shutdown();

        super.onDestroy();
    }

    /*
     * public void stopRestartTimer() { if (restartTimer != null) { restartTimer.cancel(); restartTimer.purge(); restartTimer = null; }
     * 
     * }
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
            if (source == IntentSource.NATIVE_APP_INTENT) {
                setResult(RESULT_CANCELED);
                finish();
                return true;
            }
            /*
             * if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) { restartPreviewAfterDelay(0L); return true; }
             */
            break;
        case KeyEvent.KEYCODE_FOCUS:
        case KeyEvent.KEYCODE_CAMERA:
            // Handle these events so they don't launch the Camera app
            return true;
            // Use volume up/down to turn on light
            /*
             * case KeyEvent.KEYCODE_VOLUME_DOWN: cameraManager.setTorch(false); return true; case KeyEvent.KEYCODE_VOLUME_UP: cameraManager.setTorch(true); return true;
             */
        }
        return super.onKeyDown(keyCode, event);
    }

    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        // Bitmap isn't used yet -- will be used soon
        Log.d(TAG, "running decodeOrStoreSavedBitmap()...");
        if (handler == null) {
            savedResultToShow = result;
        } else {
            if (result != null) {
                savedResultToShow = result;
            }
            if (savedResultToShow != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
                handler.sendMessage(message);
            }
            savedResultToShow = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "running surfaceCreated()...");
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "running surfaceDestroyed()...");
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "running surfaceChanged()...");
    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     * 
     * @param rawResult The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        Log.d(TAG, "running handleDecode()...");
        inactivityTimer.onActivity();
        lastResult = rawResult;
        ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);

        boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {

            drawResultPoints(barcode, scaleFactor, rawResult);
        }
        handleDecodeExternally(rawResult, resultHandler, barcode);

    }

    /**
     * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
     * 
     * @param barcode A bitmap of the captured image.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param rawResult The decoded results which contains the points to draw.
     */
    private void drawResultPoints(Bitmap barcode, float scaleFactor, Result rawResult) {
        Log.d(TAG, "running drawResultPoints()...");
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.result_points));
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
            } else if (points.length == 4 && (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
                // Hacky special case -- draw two lines, for the barcode and metadata
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
                drawLine(canvas, paint, points[2], points[3], scaleFactor);
            } else {
                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {
                    canvas.drawPoint(scaleFactor * point.getX(), scaleFactor * point.getY(), paint);
                }
            }
        }
    }

    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, float scaleFactor) {
        Log.d(TAG, "running drawLine()...");
        if (a != null && b != null) {
            canvas.drawLine(scaleFactor * a.getX(), scaleFactor * a.getY(), scaleFactor * b.getX(), scaleFactor * b.getY(), paint);
        }
    }

    // Briefly show the contents of the barcode, then handle the result outside Barcode Scanner.
    private void handleDecodeExternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
        Log.d(TAG, "running handleDecodeExternally()...");
        if (barcode != null) {

            viewfinderView.drawResultBitmap(barcode);
        }

        long resultDurationMS;
        if (getIntent() == null) {
            resultDurationMS = DEFAULT_INTENT_RESULT_DURATION_MS;
        } else {
            resultDurationMS = getIntent().getLongExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, DEFAULT_INTENT_RESULT_DURATION_MS);
        }

        if (copyToClipboard && !resultHandler.areContentsSecure()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            CharSequence text = resultHandler.getDisplayContents();
            if (text != null) {
                try {
                    clipboard.setText(text);
                } catch (NullPointerException npe) {
                    // Some kind of bug inside the clipboard implementation, not due to null input
                    Log.w(TAG, "Clipboard bug", npe);
                }
            }
        }

        if (source == IntentSource.NATIVE_APP_INTENT) {
            Log.w(TAG, "handleDecodeExternally2 ");
            // Hand back whatever action they requested - this can be changed to Intents.Scan.ACTION when
            // the deprecated intent is retired.
            Intent intent = new Intent(getIntent().getAction());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
            intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
            byte[] rawBytes = rawResult.getRawBytes();
            if (rawBytes != null && rawBytes.length > 0) {
                intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
            }
            Map<ResultMetadataType, ?> metadata = rawResult.getResultMetadata();
            if (metadata != null) {
                if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
                    intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION, metadata.get(ResultMetadataType.UPC_EAN_EXTENSION).toString());
                }
                Integer orientation = (Integer) metadata.get(ResultMetadataType.ORIENTATION);
                if (orientation != null) {
                    intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
                }
                String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
                if (ecLevel != null) {
                    intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
                }
                Iterable<byte[]> byteSegments = (Iterable<byte[]>) metadata.get(ResultMetadataType.BYTE_SEGMENTS);
                if (byteSegments != null) {
                    int i = 0;
                    for (byte[] byteSegment : byteSegments) {
                        intent.putExtra(Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
                        i++;
                    }
                }
            }
            Log.w(TAG, "handleDecodeExternally3 ");
            sendReplyMessage(R.id.return_scan_result, intent, resultDurationMS);

        }
    }

    private void sendReplyMessage(int id, Object arg, long delayMS) {
        Log.d(TAG, "running sendReplyMessage()...");
        Message message = Message.obtain(handler, id, arg);
        if (delayMS > 0L) {
            handler.sendMessageDelayed(message, delayMS);
        } else {
            handler.sendMessage(message);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "running initCamera()...");
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
            }
            decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        Log.d(TAG, "running displayFrameworkBugMessageAndExit()...");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        Log.d(TAG, "running restartPreviewAfterDelay()...");
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
    }

    private void resetStatusView() {
        Log.d(TAG, "running resetStatusView()...");
        resultView.setVisibility(View.GONE);
        // statusView.setText(R.string.qr_description);
        statusView.setVisibility(View.VISIBLE);
        viewfinderView.setVisibility(View.VISIBLE);
        lastResult = null;
    }

    public void drawViewfinder() {
        Log.d(TAG, "running drawViewfinder()...");
        viewfinderView.drawViewfinder();
    }

    public void processScanResult(String contents) {
        BeaconService.processQrContent(this, contents);
    }

    class CaptureActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BeaconService.ACTION_PROCESS_QR_CONTENT.equals(intent.getAction())) {
                if (intent.getIntExtra(BeaconConstants.EVENT_RESULT, BeaconConstants.EVENT_FAILED) == BeaconConstants.EVENT_SUCCESS) {
                    gid = intent.getStringExtra("gid");
                    vdid = intent.getStringExtra("vdid");
                    url = intent.getStringExtra("url");
                    cloudid = prefs.getString(BeaconConstants.CLOUDID, "");
                    String s = intent.getStringExtra("s");
                    BeaconService.registerBeacon(context, gid, vdid, cloudid, url);
                } else {
                }
            } else if (BeaconService.ACTION_REGISTER_BEACON.equals(intent.getAction())) {
                int resultCode = intent.getIntExtra(BeaconConstants.EVENT_RESULT, BeaconConstants.EVENT_FAILED);
                if(resultCode == BeaconUtility.GOO_SUCCESS){
                    String hashSeed = intent.getStringExtra(BeaconConstants.HASH_SEED);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(BeaconConstants.GRID_URL, url);
                    editor.putString(BeaconConstants.GUID, gid);
                    editor.putString(BeaconConstants.HASH_SEED, hashSeed);
                    editor.putString(BeaconConstants.CLOUDID, cloudid);
                    editor.commit();

                    startMainActivity();
                }else{
                    Toast.makeText(getApplicationContext(), "Error : Return code " + resultCode, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void startMainActivity() {
        Intent result = new Intent();
        setResult(Activity.RESULT_OK, result);
        finish();
    }
}