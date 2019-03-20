package com.wys.lib.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;

/**
 * Created by yas on 2019/3/19
 * Describe:相机的控制类接口
 */
public interface ICamera {
    /**open the camera*/
    void open(int cameraId);

    void setPreviewTexture(SurfaceTexture texture);
    /**set the camera config*/
    void setConfig(Config config);

    void preview();

    Point getPreviewSize();
    Point getPictureSize();

    /**close the camera*/
    boolean close();

    class Config{
        //默认宽高比
        public float rate=1.778f;
        public int minPreviewWidth;
        public int minPictureWidth;
    }
}
