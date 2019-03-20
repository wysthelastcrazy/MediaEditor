package com.wys.lib.camera;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.wys.lib.Constants;
import com.wys.lib.utils.CameraUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yas on 2019/3/19
 * Describe:相机的管理类 主要是Camera的一些设置
 * 包括预览和录制尺寸、闪光灯、曝光、聚焦、摄像头切换等
 */
public class CameraController implements ICamera{
    /**配置信息*/
    private Config mConfig;
    /**相机*/
    private Camera mCamera;
    /**预览的尺寸*/
    private Camera.Size preSize;
    /**实际的尺寸*/
    private Camera.Size picSize;

    private Point mPreSize;
    private Point mPicSize;

    public CameraController(){
        /**初始化一个默认的配置*/
        mConfig = new Config();
        mConfig.minPreviewWidth=720;
        mConfig.minPictureWidth=720;
        mConfig.rate=1.778f;
    }
    @Override
    public void open(int cameraId) {
        mCamera = Camera.open(cameraId);
        if (mCamera != null){
            /**选择当前设备允许的预览尺寸*/
            Camera.Parameters param=mCamera.getParameters();
            preSize = CameraUtil.getBestSupportSize(param.getSupportedPreviewSizes(),mConfig.rate,
                    mConfig.minPreviewWidth);
            picSize = CameraUtil.getBestSupportSize(param.getSupportedPictureSizes(),mConfig.rate,
                    mConfig.minPictureWidth);

            param.setPictureSize(picSize.width,picSize.height);
            param.setPreviewSize(preSize.width,preSize.height);

            mCamera.setParameters(param);
            Camera.Size pre=param.getPreviewSize();
            Camera.Size pic=param.getPictureSize();
            mPicSize=new Point(pic.height,pic.width);
            mPreSize=new Point(pre.height,pre.width);
        }
    }

    @Override
    public void setPreviewTexture(SurfaceTexture texture) {
        if (mCamera!=null){
            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setConfig(Config config) {
        this.mConfig=config;
    }

    @Override
    public void preview() {
        if (mCamera!=null){
            mCamera.startPreview();
        }
    }

    @Override
    public Point getPreviewSize() {
        return mPreSize;
    }

    @Override
    public Point getPictureSize() {
        return mPicSize;
    }

    @Override
    public boolean close() {
        if (mCamera!=null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return false;
    }

    /**
     * 手动聚焦
     *
     * @param point 触屏坐标 必须传入转换后的坐标
     */
    public void onFocus(Point point, Camera.AutoFocusCallback callback) {
        Camera.Parameters parameters = mCamera.getParameters();
        boolean supportFocus=true;
        boolean supportMetering=true;
        //不支持设置自定义聚焦，则使用自动聚焦，返回
        if (parameters.getMaxNumFocusAreas() <= 0) {
            supportFocus=false;
        }
        if (parameters.getMaxNumMeteringAreas() <= 0){
            supportMetering=false;
        }
        List<Camera.Area> areas = new ArrayList<Camera.Area>();
        List<Camera.Area> areas1 = new ArrayList<Camera.Area>();
        //再次进行转换
        point.x= (int) (((float)point.x)/ Constants.screenWidth*2000-1000);
        point.y= (int) (((float)point.y)/ Constants.screenHeight*2000-1000);

        int left = point.x - 300;
        int top = point.y - 300;
        int right = point.x + 300;
        int bottom = point.y + 300;
        left = left < -1000 ? -1000 : left;
        top = top < -1000 ? -1000 : top;
        right = right > 1000 ? 1000 : right;
        bottom = bottom > 1000 ? 1000 : bottom;
        areas.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
        areas1.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
        if(supportFocus){
            parameters.setFocusAreas(areas);
        }
        if(supportMetering){
            parameters.setMeteringAreas(areas1);
        }

        try {
            mCamera.setParameters(parameters);// 部分手机 会出Exception（红米）
            mCamera.autoFocus(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
