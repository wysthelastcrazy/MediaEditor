package com.wys.lib.utils;

import android.hardware.Camera;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yas on 2019/3/19
 * Describe:
 */
public class CameraUtil {
    /**
     * 获取设备支持的最佳尺寸
     * @param list
     * @param rate
     * @param minWidth
     * @return
     */
    public static Camera.Size getBestSupportSize(List<Camera.Size> list,float rate,int minWidth){
        Collections.sort(list,sizeComparator);
        int i=0;
        for (Camera.Size size:list){
            if (size.height>=minWidth&&equalRate(size,rate)){
                break;
            }
            i++;
        }
        if (i==list.size()){
            i=0;
        }
        return list.get(i);
    }
    private static boolean equalRate(Camera.Size s, float rate){
        float r = (float)(s.width)/(float)(s.height);
        if(Math.abs(r - rate) <= 0.03) {
            return true;
        }else{
            return false;
        }
    }

    private static Comparator<Camera.Size> sizeComparator=new Comparator<Camera.Size>(){
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if(lhs.height == rhs.height){
                return 0;
            }else if(lhs.height > rhs.height){
                return 1;
            }else{
                return -1;
            }
        }
    };
}
