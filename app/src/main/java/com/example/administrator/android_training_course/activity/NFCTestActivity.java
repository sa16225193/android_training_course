package com.example.administrator.android_training_course.activity;

import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.administrator.android_training_course.R;

public class NFCTestActivity extends AppCompatActivity {

    private NfcAdapter mNfcAdapter;
    private boolean mAndroidBeamAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfctest);

//        if (!PackageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) {
//
//        } else if (Build.VERSION.SDK_INT <
//                Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            mAndroidBeamAvailable = false;
//        } else {
//            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        }
    }
}
