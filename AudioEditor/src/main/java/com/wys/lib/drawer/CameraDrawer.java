package com.wys.lib.drawer;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.wys.lib.filter.AFilter;
import com.wys.lib.filter.CameraFilter;
import com.wys.lib.utils.EasyGlUtils;
import com.wys.lib.utils.MatrixUtils;
import com.wys.lib.utils.OpenGLUtil;
import com.wys.lib.video.video.TextureMovieEncoder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yas on 2019/3/19
 * Describe:管理图像绘制的类
 * 主要用于管理各种滤镜、画面旋转、视频编码录制等
 */
public class CameraDrawer implements GLSurfaceView.Renderer {

    private float[] OM;

    /**显示画面的filter*/
    private final AFilter showFilter;
    /**后台绘制的filter*/
    private final AFilter drawFilter;

    private SurfaceTexture mSurfaceTextrue;
    /**预览数据的宽高*/
    private int mPreviewWidth=0,mPreviewHeight=0;
    /**控件的宽高*/
    private int width=0,height=0;

    private int textureID;
    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];

    private TextureMovieEncoder videoEncoder;

    private boolean recordingEnabled;
    private int recordingStatus;
    private static final int RECORDING_OFF = 0;
    private static final int RECORDING_ON = 1;
    private static final int RECORDING_RESUMED=2;
    private static final int RECORDING_PAUSE=3;
    private static final int RECORDING_RESUME=4;
    private static final int RECORDING_PAUSED=5;

    private String savePath;

    private float[] SM = new float[16];


    public CameraDrawer(Resources resources){
        showFilter = new CameraFilter(resources);
        drawFilter = new CameraFilter(resources);

//        drawFilter = new
        OM = MatrixUtils.getOriginalMatrix();
        //矩阵上下翻转
        MatrixUtils.flip(OM,false,true);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        textureID = OpenGLUtil.createTextureID();
        mSurfaceTextrue = new SurfaceTexture(textureID);

        drawFilter.create();
        drawFilter.setTextureId(textureID);

        showFilter.create();
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width=width;
        this.height=height;

        //清除遗留的
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
        /**创建一个帧染缓冲区对象*/
        GLES20.glGenFramebuffers(1,fFrame,0);
        /**根据纹理数量 返回的纹理索引*/
        GLES20.glGenTextures(1, fTexture, 0);
       /* GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width,
                height);*/
        /**将生产的纹理名称和对应纹理进行绑定*/
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[0]);
        /**根据指定的参数 生产一个2D的纹理 调用该函数前  必须调用glBindTexture以指定要操作的纹理*/
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mPreviewWidth, mPreviewHeight,
                0,  GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        OpenGLUtil.useTexParameter();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);

        drawFilter.setSize(mPreviewWidth,mPreviewHeight);

        MatrixUtils.getShowMatrix(SM,mPreviewWidth, mPreviewHeight, width, height);
        showFilter.setMatrix(SM);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mSurfaceTextrue.updateTexImage();
        EasyGlUtils.bindFrameTexture(fFrame[0],fTexture[0]);
        GLES20.glViewport(0,0,mPreviewWidth,mPreviewHeight);
        drawFilter.draw();
        EasyGlUtils.unBindFrameBuffer();

        if (recordingEnabled){
            /**录制状态*/
            switch (recordingStatus) {
                case RECORDING_OFF:
                    videoEncoder=new TextureMovieEncoder();
                    videoEncoder.setPreviewSize(mPreviewWidth,mPreviewHeight);
                    videoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(savePath,mPreviewWidth,mPreviewHeight,
                            3500000, EGL14.eglGetCurrentContext(),
                            null));
                    recordingStatus = RECORDING_ON;
                    break;
                case RECORDING_RESUMED:
                    videoEncoder.updateSharedContext(EGL14.eglGetCurrentContext());
                    videoEncoder.resumeRecording();
                    break;
                case RECORDING_ON:
                case RECORDING_PAUSED:
                    break;
                case RECORDING_PAUSE:
                    videoEncoder.pauseRecording();
                    recordingStatus = RECORDING_PAUSED;
                    break;
                case RECORDING_RESUME:
                    videoEncoder.resumeRecording();
                    recordingStatus=RECORDING_ON;
                    break;

                default:
                    throw new RuntimeException("unknown recording status " + recordingStatus);
            }
        }else{
            switch (recordingStatus){
                case RECORDING_ON:
                case RECORDING_RESUMED:
                case RECORDING_PAUSE:
                case RECORDING_RESUME:
                case RECORDING_PAUSED:
                    videoEncoder.stopRecording();
                    recordingStatus = RECORDING_OFF;
                    break;
                case RECORDING_OFF:
                    break;
                default:
                    throw new RuntimeException("unknown recording status " + recordingStatus);

            }
        }

        /**绘制显示的filter*/
        GLES20.glViewport(0,0,width,height);
        showFilter.setTextureId(fTexture[0]);
        showFilter.draw();

        if (videoEncoder != null && recordingEnabled && recordingStatus == RECORDING_ON) {
            videoEncoder.setTextureId(fTexture[0]);
            videoEncoder.frameAvailable(mSurfaceTextrue);
        }

    }


    public SurfaceTexture getTexture() {
        return mSurfaceTextrue;
    }

    /**设置预览效果的size*/
    public void setPreviewSize(int width,int height){
        if (mPreviewWidth != width || mPreviewHeight != height){
            mPreviewWidth = width;
            mPreviewHeight = height;
        }
    }



    /**根据摄像头设置纹理映射坐标*/
    public void setCameraId(int id) {
        drawFilter.setFlag(id);
        showFilter.setFlag(id);
    }


    public void setSavePath(String path){
        this.savePath=path;
    }

    public void startRecord() {
        recordingEnabled=true;
    }
    public void stopRecord(){
        recordingEnabled=false;
    }
    public void onPause(boolean auto){
        if (auto){
            videoEncoder.pauseRecording();
            if (recordingStatus==RECORDING_ON){
                recordingStatus=RECORDING_PAUSED;
            }
            return;
        }
        if (recordingStatus==RECORDING_ON){
            recordingStatus=RECORDING_PAUSE;

        }
    }
    public void onResume(boolean auto) {
        if(auto){
            if(recordingStatus==RECORDING_PAUSED){
                recordingStatus=RECORDING_RESUME;
            }
            return;
        }
        if(recordingStatus==RECORDING_PAUSED){
            recordingStatus=RECORDING_RESUME;
        }
    }
}

