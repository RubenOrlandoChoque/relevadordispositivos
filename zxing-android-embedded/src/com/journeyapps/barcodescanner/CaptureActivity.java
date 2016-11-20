package com.journeyapps.barcodescanner;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.client.android.R;
import com.google.zxing.client.android.camera.CameraConfigurationUtils;

/**
 *
 */
public class CaptureActivity extends Activity {
    private CaptureManager capture;
    private CompoundBarcodeView barcodeScannerView;
    public boolean isTorchOn=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.zxing_capture);
        barcodeScannerView = (CompoundBarcodeView)findViewById(R.id.zxing_barcode_scanner);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        ImageView light = (ImageView)findViewById(R.id.img_luz);
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTorchOn){
                    barcodeScannerView.setTorchOff();
                    isTorchOn = false;
                }else{
                    barcodeScannerView.setTorchOn();
                    isTorchOn = true;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
