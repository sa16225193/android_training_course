package cn.edu.zafu.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.edu.zafu.camera.activity.CameraActivity;
import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 身份证识别
 * 控制相机硬件
 * 自定义相机预览
 */
public class MainActivity extends Activity {
    OkHttpClient mOkHttpClient;
    private Button mCatch, mRecognize;
    private EditText mPath, mName, mType;
    private ImageView mPhoto;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private String mPhotoPath;
    private static final int UPDATE_TEXTVIEW = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_TEXTVIEW:
                    mTextView.setText(msg.obj.toString());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initClient();
        initView();
        initClickListener();
    }

    private void initClickListener() {
        mCatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                String pathStr = mPath.getText().toString();
                String nameStr = mName.getText().toString();
                String typeStr = mType.getText().toString();
                if (!TextUtils.isEmpty(pathStr)) {
                    intent.putExtra("path", pathStr);
                }
                if (!TextUtils.isEmpty(nameStr)) {
                    intent.putExtra("name", nameStr);
                }
                if (!TextUtils.isEmpty(typeStr)) {
                    intent.putExtra("type", typeStr);
                }
                startActivityForResult(intent, 100);
            }
        });

        mRecognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAndRecognize();
            }
        });
    }

    private void uploadAndRecognize() {
        if (!TextUtils.isEmpty(mPhotoPath)) {
            File file = new File(mPhotoPath);
            //构造上传请求，类似web表单
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addPart(Headers.of("Content-Disposition", "form-data; name=\"callbackurl\""), RequestBody.create(null, "/idcard/"))
                    .addPart(Headers.of("Content-Disposition", "form-data; name=\"action\""), RequestBody.create(null, "idcard"))
                    .addPart(Headers.of("Content-Disposition", "form-data; name=\"img\"; filename=\"idcardFront_user.jpg\""), RequestBody.create(MediaType.parse("image/jpeg"), file))
                    .build();
            //这个是ui线程回调，可直接操作UI
            RequestBody progressRequestBody = ProgressHelper.withProgress(requestBody, new ProgressUIListener() {
                @Override
                public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                    Log.e("TAG", "numBytes:" + numBytes);
                    Log.e("TAG", "totalBytes" + totalBytes);
                    Log.e("TAG", percent * 100 + " % done ");
                    Log.e("TAG", "done:" + (percent >= 1.0));
                    Log.e("TAG", "================================");
                    //ui层回调
                    mProgressBar.setProgress((int) (100 * percent));
                }
            });
            //进行包装，使其支持进度回调
            final Request request = new Request.Builder()
                    .header("Host", "ocr.ccyunmai.com:8080")
                    .header("Origin", "http://ocr.ccyunmai.com:8080")
                    .header("Referer", "http://ocr.ccyunmai.com:8080/idcard/")
                    .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2398.0 Safari/537.36")
                    .url("http://ocr.ccyunmai.com:8080/UploadImage.action")
                    .post(progressRequestBody)
                    .build();
            //开始请求
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Document parse = Jsoup.parse(result);
                    Elements select = parse.select("div#ocrresult");
                    Log.e("TAG", "select：" + select.text());
                    Message obtain = Message.obtain();
                    obtain.what = UPDATE_TEXTVIEW;
                    obtain.obj = select.text();
                    mHandler.sendMessage(obtain);
                }
            });
        }
    }

    private void initView() {
        mPath = (EditText) findViewById(R.id.path);
        mName = (EditText) findViewById(R.id.name);
        mType = (EditText) findViewById(R.id.type);
        mCatch = (Button) findViewById(R.id.btn);
        mRecognize = (Button) findViewById(R.id.recognize);
        mTextView = (TextView) findViewById(R.id.tv);
        mPhoto = (ImageView) findViewById(R.id.photo);
        mProgressBar = (ProgressBar) findViewById(R.id.upload_progress);
        File externalFile = getExternalFilesDir("/idcard/");
        mPath.setText(externalFile.getAbsolutePath());
        Log.e("TAG", externalFile.getAbsolutePath() + "\n" + externalFile.getAbsolutePath());
    }

    private void initClient() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.MINUTES)
                .readTimeout(1000, TimeUnit.MINUTES)
                .writeTimeout(1000, TimeUnit.MINUTES)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("TAG", "onActivityResult");
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                String path = extras.getString("path");
                String type = extras.getString("type");
                mPhotoPath = path;
                mPhoto.setImageBitmap(BitmapFactory.decodeFile(mPhotoPath));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
