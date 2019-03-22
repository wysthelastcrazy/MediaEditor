package com.wys.mediaeditor;

import android.Manifest;
import android.graphics.Point;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wys.lib.Constants;
import com.wys.lib.widget.CameraView;
import com.wys.mediaeditor.activity.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnTouchListener {
    private CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCameraView = findViewById(R.id.cameraView);
        mCameraView.setOnTouchListener(this);
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
            Log.e("hero","----onAutoFocus===="+success);
        }
    };
}
