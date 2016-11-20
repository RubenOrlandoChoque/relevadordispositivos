package com.sistematias.relevadordispositivos.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.OnScanListener;
import com.scandit.barcodepicker.ScanSession;
import com.scandit.barcodepicker.ScanSettings;
import com.scandit.barcodepicker.ScanditLicense;
import com.scandit.recognition.Barcode;

/**
 * Simple demo application illustrating the use of the Scandit BarcodeScanner SDK.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * +
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing premissions and
 * limitations under the License.
 */
public class ScanActivity extends Activity implements OnScanListener {

    // The main object for recognizing a displaying barcodes.
    private BarcodePicker mBarcodePicker;
    
    // Enter your Scandit SDK App key here.
    // Your Scandit SDK App key is available via your Scandit SDK web account.
    public static final String sScanditSdkAppKey = "yujKdr2ywbNJsvG03g3eVit56p2cC6PuhezVF8u3qmY";
	
	Toast mToast = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScanditLicense.setAppKey(sScanditSdkAppKey);

        // Initialize and start the bar code recognition.
        initializeAndStartBarcodeScanning();
    }
    
    @Override
    protected void onPause() {
        // When the activity is in the background immediately stop the 
        // scanning to save resources and free the camera.
        mBarcodePicker.stopScanning();

        super.onPause();
    }
    
    @Override
    protected void onResume() {
        // Once the activity is in the foreground again, restart scanning.
        mBarcodePicker.startScanning();
        super.onResume();
    }

    /**
     * Initializes and starts the bar code scanning.
     */
    public void initializeAndStartBarcodeScanning() {
        // Switch to full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // The scanning behavior of the barcode picker is configured through scan
        // settings. We start with empty scan settings and enable a very generous
        // set of symbologies. In your own apps, only enable the symbologies you
        // actually need.
        ScanSettings settings = ScanSettings.create();
        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_EAN13, true);
//        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_UPCA, true);
//        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_EAN8, true);
//        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_UPCE, true);
//
//        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_QR, true);
//        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_DATA_MATRIX, true);
//
//        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_CODE39, true);
//        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_CODE128, true);
//
//        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_INTERLEAVED_2_OF_5, true);

        settings.setCameraFacingPreference(ScanSettings.CAMERA_FACING_BACK);


        // Some Android 2.3+ devices do not support rotated camera feeds. On these devices, the
        // barcode picker emulates portrait mode by rotating the scan UI.
        boolean emulatePortraitMode = !BarcodePicker.canRunPortraitPicker();
        if (emulatePortraitMode) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        BarcodePicker picker = new BarcodePicker(this, settings);

        setContentView(picker);
        mBarcodePicker = picker;
        
        // Register listener, in order to be notified about relevant events 
        // (e.g. a successfully scanned bar code).
        mBarcodePicker.setOnScanListener(this);

    }

    /** 
     *  Called when a barcode has been decoded successfully.
     */
    public void didScan(ScanSession session) {
        String message = "";
        String cleanData = "";
        for (Barcode code : session.getNewlyRecognizedCodes()) {
            String data = code.getData();
            // truncate code to certain length
            cleanData = data;
            if (data.length() > 30) {
                cleanData = data.substring(0, 25) + "[...]";
            }
            if (message.length() > 0) {
                message += "\n\n\n";
            }
            message += cleanData;
            message += "\n\n(" + code.getSymbologyName().toUpperCase() + ")";
        }
        if (mToast != null) {
            mToast.cancel();
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("ean",cleanData);
        setResult(Activity.RESULT_OK,returnIntent);

//        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
//        mToast.show();
           finish();
    }

    @Override
    public void onBackPressed() {
        mBarcodePicker.stopScanning();
        finish();
    }
}
