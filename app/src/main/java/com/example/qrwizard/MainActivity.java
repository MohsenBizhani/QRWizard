package com.example.qrwizard;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;

public class MainActivity extends Activity {
    private static final int CAMERA_PERMISSION_REQUEST = 1;
    private CaptureManager captureManager;
    private DecoratedBarcodeView cameraPreview;
    private Toast currentToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            initializeApp();
        }
    }

    private void initializeApp() {
        // Initialize the QR code scanner
        cameraPreview = findViewById(R.id.cameraPreview);
        captureManager = new CaptureManager(this, cameraPreview);

        Button scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureManager.decode();
            }
        });

        captureManager.decode();

        cameraPreview.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                String scannedData = result.getText();
                if (scannedData != null) {
                    if (scannedData.startsWith("http://") || scannedData.startsWith("https://")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannedData));
                        startActivity(intent);
                    } else {
                        if (currentToast != null) {
                            currentToast.cancel();
                        }
                        currentToast = Toast.makeText(MainActivity.this, "Scanned: " + scannedData, Toast.LENGTH_LONG);
                        currentToast.show();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }
}
