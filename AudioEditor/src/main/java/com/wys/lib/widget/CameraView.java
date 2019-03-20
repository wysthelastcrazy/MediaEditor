package com.wys.lib.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.wys.lib.camera.CameraController;
import com.wys.lib.drawer.CameraDrawer;
import com.wys.lib.utils.OpenGLUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yas on 2019/3/19
 * Describe:
 */
public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG="CameraView";

    private CameraDrawer mCameraDrawer;
    private CameraController mCamera;
    private int dataWidth = 0,dataHeight = 0;

    private boolean isSetParam=false;

    private int cameraId;

    public CameraView(Context context) {
        this(context,null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        /**初始化OpenGL相关信息*/
        setEGLContextClientVersion(2);//设置版本
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);//设置主动调用渲染
        setCameraDistance(100); //相机距离

        mCameraDrawer = new CameraDrawer(getResources());
        mCamera=new CameraController();
    }

    private void open(int cameraId){
        mCamera.close();
        mCamera.open(cameraId);
        mCameraDrawer.setCameraId(cameraId);
        mCameraDrawer.setCameraId(cameraId);
        final Point previewSize=mCamera.getPreviewSize();
        dataWidth = previewSize.x;
        dataHeight = previewSize.y;

        SurfaceTexture texture = mCameraDrawer.getTexture();
        texture.setOnFrameAvailableListener(this);
        mCamera.setPreviewTexture(texture);
        mCamera.preview();
    }

    /**
     * 切换摄像头
     */
    public void switchCamera(){
        cameraId = cameraId ==0 ? 1 :0;
        open(cameraId);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraDrawer.onSurfaceCreated(gl,config);
        if (!isSetParam){
            open(cameraId);
            stickerInit();
        }
        mCameraDrawer.setPreviewSize(dataWidth,dataHeight);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCameraDrawer.onSurfaceChanged(gl,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (isSetParam){
            mCameraDrawer.onDrawFrame(gl);
        }

    }
    private void stickerInit() {
        if (!isSetParam && dataWidth > 0 && dataHeight > 0) {
            isSetParam = true;
        }
    }
    /**
     * 每次Activity onResume时被调用,第一次不会打开相机
     */
    @Override
    public void onResume() {
        super.onResume();
        if (isSetParam) {
            open(cameraId);
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.requestRender();
    }
}
