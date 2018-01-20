package com.example.administrator.android_training_course.nfc;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.example.administrator.android_training_course.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class NFCActivity extends AppCompatActivity {

    /**
     * 测试NFC功能
     */
    private NfcAdapter mNfcAdapter;
    private boolean mAndroidBeamAvailable = false;

    private ArrayList<Uri> mFileUris;
    private FileUriCallback mFileUriCallback;

    /**
     * 接收NFC传输的文件
     */
    private File mParentPath;
    private Intent mIntent;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        mAndroidBeamAvailable = hasNfc(this);
        Log("NFC : " + mAndroidBeamAvailable);
    }

    static void Log(String msg) {
        Log.d("TAG", "NFCActivity: " + msg);
    }

    private void handleViewIntent() {
        // 得到Intent
        mIntent = getIntent();
        String action = mIntent.getAction();
        /*
         * 过滤Intent
         */
        if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
            // 得到Uri
            Uri beamUri = mIntent.getData();
            //判断Uri的类型
            if (TextUtils.equals(beamUri.getScheme(), "file")) {
                mParentPath = new File(handleFileUri(beamUri));
            } else if (TextUtils.equals(
                    beamUri.getScheme(), "content")) {
                mParentPath = handleContentUri(beamUri);
            }
        }
    }

    public String handleFileUri(Uri beamUri) {
        //得到文件路径
        String fileName = beamUri.getPath();
        File copiedFile = new File(fileName);
        // 得到文件的父目录
        return copiedFile.getParent();
    }

    public File handleContentUri(Uri beamUri) {
        // 文件名在Cursor中的索引
        int filenameIndex;
        File copiedFile;
        String fileName;
        // Test the authority of the URI
        if (TextUtils.equals(beamUri.getAuthority(), MediaStore.AUTHORITY)) {
            // Get the column that contains the file name
            String[] projection = { MediaStore.MediaColumns.DATA };
            Cursor pathCursor =
                    getContentResolver().query(beamUri, projection,
                            null, null, null);
            // Check for a valid cursor
            if (pathCursor != null && pathCursor.moveToFirst()) {
                // Get the column index in the Cursor
                filenameIndex = pathCursor.getColumnIndex(
                        MediaStore.MediaColumns.DATA);
                // Get the full file name including path
                fileName = pathCursor.getString(filenameIndex);
                // Create a File object for the filename
                copiedFile = new File(fileName);
                // Return the parent directory of the file
                return new File(copiedFile.getParent());
            }
        }
        return null;
    }

    /**
     * 判断是否支持NFC功能
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean hasNfc(Context context) {
        if (context == null) {
            return false;
        }
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        mNfcAdapter = manager.getDefaultAdapter();
        mFileUriCallback = new FileUriCallback();
        mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback, this);
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 监听其他设备的NFC请求
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private class FileUriCallback implements
            NfcAdapter.CreateBeamUrisCallback {
        public FileUriCallback() {
        }

        /**
         * 当Android Beam文件传输监测到用户希望向另一个支持NFC的设备发送文件时，
         * 系统就会调用该函数。在该回调函数中，返回一个Uri对象数组，
         * Android Beam文件传输会将URI对应的文件拷贝给要接收这些文件的设备。
         *
         * @param event
         * @return
         */
        @Override
        public Uri[] createBeamUris(NfcEvent event) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File imageFiles = new File(getExternalFilesDir(null).getAbsoluteFile() + "/myimages");
                if (imageFiles.isDirectory()) {
                    mFileUris = new ArrayList<>();
                    for (File file : imageFiles.listFiles()) {
                        file.setReadable(true, false);
                        mFileUris.add(Uri.fromFile(file));
                    }
                }
            }
            Log("FileUri = "+ mFileUris.size() + " : " + mFileUris.toString());
            return mFileUris.toArray(new Uri[mFileUris.size()]);
        }
    }
}
