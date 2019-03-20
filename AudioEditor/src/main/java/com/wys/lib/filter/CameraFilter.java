package com.wys.lib.filter;

import android.content.res.Resources;


/**
 * Description:
 */
public class CameraFilter extends OesFilter {

    public CameraFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    public void setFlag(int flag) {
        super.setFlag(flag);
        float[] coord;
        if(getFlag()==1){    //前置摄像头 顺时针旋转90,并上下颠倒
            coord=new float[]{
                    1.0f, 1.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,
            };
        }else{               //后置摄像头 顺时针旋转90度
            coord=new float[]{
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 0.0f,
            };
        }
        mTextBuffer.clear();
        mTextBuffer.put(coord);
        mTextBuffer.position(0);
    }
}
