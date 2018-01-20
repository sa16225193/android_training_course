package com.example.administrator.android_training_course.fileshare;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.android_training_course.R;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class FileSelectActivity extends ListActivity {

    private ParcelFileDescriptor mInputPFD;
    private File mPrivateRootDir;
    private File mImagesDir;
    private Intent mResultIntent;
    File[] mImageFiles;
    List<String> mImageFilenames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        mPrivateRootDir = getFilesDir();
        mImagesDir = new File(mPrivateRootDir, "images/myimages");
        mImageFiles = mImagesDir.listFiles();
        mImageFilenames = new ArrayList<String>();
        for (File file: mImageFiles) {
            Log.e("TAG", file.getAbsolutePath());
            mImageFilenames.add(file.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, mImageFilenames);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
        File requestFile = new File(mImageFiles[position].getAbsolutePath());

        try {
            Uri fileUri = FileProvider.getUriForFile(
                    this, "com.example.administrator.android_training_course.fileprovider"
                    ,requestFile);
            if (fileUri != null){
                mResultIntent = new Intent();
                mResultIntent.setDataAndType(
                        fileUri,
                        getContentResolver().getType(fileUri));
                mResultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                setResult(Activity.RESULT_OK, mResultIntent);
                Log.e("TAG","ok");
            }else {
                mResultIntent.setDataAndType(null, "");
                setResult(RESULT_CANCELED, mResultIntent);
                Log.e("TAG", "error");
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void shareFile(View v){
        finish();
    }

}
