package com.example.administrator.android_training_course.photo;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;

import com.example.administrator.android_training_course.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 控制相机
 * 此页面有Ｂｕｇ，待解决
 */
public class CameraActivity extends AppCompatActivity implements Camera.PictureCallback {

    private Camera mCamera;
    private Preview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mPreview = new Preview(this);
        FrameLayout vPreview = findViewById(R.id.camera_preview);
        vPreview.addView(mPreview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCameraAndPreview();
    }

    public void takePhoto(View v) {
        mCamera.takePicture(null, this, null);
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            //TODO 未检测到系统的相机
        } else {
            //获取cameraId ，在api8及以前，直接调用Camera.open()方法会打开后置摄像头
            //获取系统后置摄像头的id（工具方法，后面附代码）
            int cameraId = CameraUtil.findBackFacingCamera();
            if (!CameraUtil.isCameraIdValid(cameraId)) {
                //TODO 检测camera id无效
            } else {
                //是否可以安全打开相机
                if (safeCameraOpen(cameraId)) {
                    mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//                    mPreview.getHolder().addCallback(this);
                    mCamera.startPreview();  //开启相机预览
                    mPreview.setCamera(mCamera);
                } else {
                    //TODO 无法安全打开相机
                }
            }
        }
    }

    /**
     * 打开Camera
     * @param id
     * @return
     */
    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;

        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(id);
            qOpened = (mCamera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;
    }

    /**
     * 释放Camera
     */
    private void releaseCameraAndPreview() {
        mPreview.setCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        //自行创建一个file文件用于保存照片
        String imageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
        //存放系统拍摄图片的目录，这种目录其他应用可读取
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */);
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStream output = null;
        if (image != null) {
            try {
                output = new FileOutputStream(image.getAbsolutePath());
                output.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //保存成功后的处理
        resetCamera();
    }

    /**
     * 重置相机
     */
    private void resetCamera() {
        mCamera.startPreview();
        mPreview.setCamera(mCamera);
    }
}
