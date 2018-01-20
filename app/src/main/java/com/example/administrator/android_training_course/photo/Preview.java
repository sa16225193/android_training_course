package com.example.administrator.android_training_course.photo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by liuyong on 2018/1/21.
 * 相机预览界面
 * 这个类需要实现android.view.SurfaceHolder.Callback接口，
 * 它会用这个接口把相机硬件获取到的图像数据传递给应用程序。
 */

public class Preview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = Preview.class.getSimpleName();

    /**
     * 控制相机方向
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    SurfaceHolder mHolder;
    Camera mCamera;
    //持有Activity引用，为了获取屏幕方向，改成内部类会比较好
    private Activity mActivity;

    private List<Camera.Size> mSupportedPreviewSizes;


    public Preview(Activity activity) {
        super(activity);
        mActivity = activity;
        mHolder = getHolder();
        mHolder.addCallback(this);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            //API 11及以后废弃，需要时自动配置
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            if(mCamera != null) {
                //surface创建，设置预览SurfaceHolder
                mCamera.setPreviewDisplay(holder);
                //开启预览
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    /**
     * 修改相机的预览大小
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        refreshCamera();
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();
        }
    }

//    public void setCamera(Camera camera) {
//        mCamera = camera;
//    }

    /**
     * 刷新相机
     */
    private void refreshCamera() {
        if (mCamera != null) {
            requestLayout();
            //获取当前手机屏幕方向
            int rotation = mActivity.getWindowManager()
                    .getDefaultDisplay().getRotation();
            //调整相机方向
            mCamera.setDisplayOrientation(
                    ORIENTATIONS.get(rotation));
            // 设置相机参数
            mCamera.setParameters(settingParameters());
        }
    }

    /**
     * 配置相机参数
     * @return
     */
    private Camera.Parameters settingParameters() {
        // 获取相机参数
        Camera.Parameters params = mCamera.getParameters();
        List<String> focusModes = params.getSupportedFocusModes();
        //设置持续的对焦模式
        if (focusModes.contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        //设置闪光灯自动开启
        if (focusModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            params.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
        }

        return params;
    }

    public void setCamera(Camera camera) {
        if (mCamera == camera) {
            return;
        }

        stopPreviewAndFreeCamera();

        mCamera = camera;

        if (mCamera != null) {
            List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPreviewSizes = localSizes;
            requestLayout();

            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            mCamera.startPreview();
        }
    }

    /**
     * 停止预览，释放Camera
     */
    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();

            mCamera = null;
        }
    }

}
