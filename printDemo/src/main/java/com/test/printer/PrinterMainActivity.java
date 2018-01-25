package com.test.printer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class PrinterMainActivity extends AppCompatActivity {
    private String mFilePath = null;
    //
    private TextView mShowFileInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_main);
        //
        mShowFileInfoView = findViewById(R.id.showFilePath);
        //
        checkSelfPermission();
    }

    /**
     * 处理文件读取权限
     */
    private void checkSelfPermission() {
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     *
     */
    public void doPrint(View view) {
        if (TextUtils.isEmpty(mFilePath)) {
            Toast.makeText(this, "请选择文件", Toast.LENGTH_SHORT).show();
        } else {
            doPdfPrint(mFilePath);
        }
    }

    private void doPdfPrint(String filePath) {
        String jobName = "jobName";

        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        MyPrintPdfAdapter myPrintAdapter = new MyPrintPdfAdapter(filePath);
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        printManager.print(jobName, myPrintAdapter, null);
    }

    /**
     * @param view
     */
    public void openFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                //文件选择-回调
                Uri uri = data.getData();
                mFilePath = getPath(this, uri);
                mShowFileInfoView.setText(mFilePath);
            }
        }
    }

    private String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
