package com.wys.mediaeditor;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wys.lib.widget.CameraView;
import com.wys.mediaeditor.activity.BaseActivity;

public class MainActivity extends BaseActivity {
    private CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCameraView = findViewById(R.id.cameraView);
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
}
