package com.example.administrator.android_training_course.photo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.administrator.android_training_course.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 拍照并打印
 */
public class PhotoActivity extends AppCompatActivity {

    private final String TAG = PhotoActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImageView;
    private String mCurrentPhotoPath;
    private boolean isPrinting;
    private WebView mWebView;
    private ArrayList<PrintJob> mPrintJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        initView();
        init();
    }

    private void initView() {
        mImageView = findViewById(R.id.iv_photo);
    }

    private void init() {
        hasCameraSupport();
    }

    @Override
    protected void onRestart() {
        isPrinting = false;
        super.onRestart();
    }

    /**
     * 检测是否支持相机功能
     *
     * @return
     */
    private boolean hasCameraSupport() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * 第一种方式调用相机，返回照片的缩略图，较模糊
     */
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {//检查有没有能处理这个Intent的Activity
//            //这种方式得到的照片是缩略图，较模糊
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }

    /**
     * 取回拍摄照片的缩略图，对应第一种调用相机方式
     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);
//        }
//    }



    /**
     * 第二种调用系统相机程序，将拍摄的全尺寸照片存入指定文件中
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {//检查有没有能处理这个Intent的Activity
            // 下面的方式得到的新照片是全尺寸，较清晰
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {//将新全尺寸照片存放到指定的目录

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * 第二种，取回拍照的照片，从存入的指定文件中读取
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
        }
    }

    /**
     * 创建新照片的文件对象，用于保存全尺寸照片
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //存放系统拍摄图片的目录，这种目录其他应用可读取
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * 将照片添加到相册中
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    /**
     * 解码缩放图片(Decode a Scaled Image)
     */
    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        // 该 值设为true那么将不返回实际的bitmap，也不给其分配内存空间这样就避免内存溢出了。但是允许我们查询图片的信息这其中就包括图片大小信息
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        // Math.min求最小值
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        // 设置恰当的inSampleSize可以使BitmapFactory分配更少的空间以消除该错误
        bmOptions.inSampleSize = 20;
        // 如果inPurgeable设为True的话表示使用BitmapFactory创建的Bitmap,用于存储Pixel的内存空间在系统内存不足时可以被回收
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }


    public void takePhoto(View view) {
        dispatchTakePictureIntent();
    }

    public void sendPhotoAlbum(View view) {
        Log.e(TAG, "sendPhotoAlbum");
        galleryAddPic();
    }

    public void compressPhoto(View view) {
        Log.e(TAG, "compressPhoto");
        setPic();
    }

    public void printHtml  (View view) {
        Log.e(TAG, "compressPhoto");
        doWebViewPrint();
    }

    public void printPhoto(View view) {
        if (!isPrinting) {
            isPrinting = true;
            Log.e(TAG, "printPhoto");
            PrintHelper photoPrinter = new PrintHelper(this);
            photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            Log.e(TAG, mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf("/") + 1));
            photoPrinter.printBitmap(mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf("/")), BitmapFactory.decodeFile(mCurrentPhotoPath));
        }
    }

    private void doWebViewPrint() {
        mPrintJob = new ArrayList<>();
        // Create a WebView object specifically for printing
        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "page finished loading " + url);
                createWebPrintJob(view);
                mWebView = null;
            }
        });

        // Generate an HTML document on the fly:
        String htmlDocument = "<html><body><h1>Test Content</h1><p>Testing, " +
                "testing, testing...</p></body></html>";

        webView.loadDataWithBaseURL("file:///android_asset/", "1.jpg", "text/HTML", "UTF-8", null);
        String body="<img  src=\"file:///android_asset/1.jpg\"/>";
        String html="<html><body>"+body+"</html></body>";
//            // 本地文件处理(能显示图片)
            webView.loadDataWithBaseURL(null, html, "text/HTML", "UTF-8", null);

//        webView.loadUrl("https://www.hao123.com/");
        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        mWebView = webView;
    }

    private void createWebPrintJob(WebView webView) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            printAdapter = webView.createPrintDocumentAdapter();
        }

        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Document";
        PrintJob printJob = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            printJob = printManager.print(jobName, printAdapter,
                    new PrintAttributes.Builder().build());
        }

        // Save the job object for later status checking
        mPrintJob.add(printJob);
    }
}
