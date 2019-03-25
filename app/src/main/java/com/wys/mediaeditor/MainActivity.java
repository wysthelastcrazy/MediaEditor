package com.wys.mediaeditor;

import android.Manifest;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wys.lib.Constants;
import com.wys.lib.widget.CameraView;
import com.wys.mediaeditor.activity.BaseActivity;
import com.wys.mediaeditor.util.FileUtils;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {
    private final String TAG="MainActivity11";
    private CameraView mCameraView;
    private Button btn_record;
    ExecutorService executorService;
    private boolean recordFlag; //是否在录制

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCameraView = findViewById(R.id.cameraView);
        mCameraView.setOnTouchListener(this);
        btn_record = findViewById(R.id.btn_record);
        btn_record.setOnClickListener(this);

        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] p = new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        requestPermissions(p, 100, new IRequestPermissionsCallBack() {
            @Override
            public void succeed() {
                mCameraView.onResume();
            }

            @Override
            public void fail() {

            }
        });

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mCameraView.getCameraId() == 1) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float sRawX = event.getRawX();
                float sRawY = event.getRawY();
                float rawY = sRawY * Constants.screenWidth / Constants.screenHeight;
                float temp = sRawX;
                float rawX = rawY;
                rawY = (Constants.screenWidth - temp) * Constants.screenHeight / Constants.screenWidth;

                Point point = new Point((int) rawX, (int) rawY);
                mCameraView.onFocus(point, callback);
        }
        return true;
    }

    Camera.AutoFocusCallback callback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            //聚焦之后根据结果修改图片
            Log.e("hero", "----onAutoFocus====" + success);
        }
    };

    @Override
    public void onClick(View v) {
        Log.d(TAG,"[onClick] 3333+++++++++++++++++++");
        switch (v.getId()) {
            case R.id.btn_record:
                Log.d(TAG,"[onClick] +++++++++++++++++++");
                if (!recordFlag) {
                    Log.d(TAG,"[onClick] 11111+++++++++++++++++++");
                    executorService.execute(recordRunnable);
                    btn_record.setText("recording");
                }else{
                    Log.d(TAG,"[onClick] 22222+++++++++++++++++++");
                    recordFlag = false;
                    btn_record.setText("finish record");
                    mCameraView.stopRecord();
//                    recordComplete();
                }
                break;
        }
    }

    Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            recordFlag = true;
            long time = System.currentTimeMillis();
            String savePath = FileUtils.getPath("record/", time + "_wys.mp4");

            try {
                mCameraView.setSavePath(savePath);
                mCameraView.startRecord();


//                if (timeCount < 2000) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this, "录像时间太短", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    recordComplete(savePath);
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void recordComplete(final String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "文件保存路径：" + path, Toast.LENGTH_SHORT).show();
            }
        });
    }
}


