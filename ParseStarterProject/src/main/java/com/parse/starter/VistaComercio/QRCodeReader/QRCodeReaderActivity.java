package com.parse.starter.VistaComercio.QRCodeReader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;
import com.parse.starter.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeReaderActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_reader);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        //mScannerView = (ZXingScannerView) findViewById(R.id.zxscan);




    }

    @Override
    protected void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

    }

    @Override
    protected void onPause() {
        super.onPause();

        mScannerView.stopCamera();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void handleResult(Result rawResult) {

        Log.i("Prueba", rawResult.getText());

        mScannerView.resumeCameraPreview(this);

    }
}
