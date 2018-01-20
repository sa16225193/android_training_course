package com.example.administrator.android_training_course.nfc;

import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.administrator.android_training_course.R;

public class NFCTestActivity extends AppCompatActivity {

    private NfcAdapter mNfcAdapter;
    private boolean mNFC = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfctest);
        mNFC = hasNFCSupport();
    }

    private boolean hasNFCSupport() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);
    }
}
