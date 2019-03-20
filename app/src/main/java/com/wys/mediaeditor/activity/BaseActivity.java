package com.wys.mediaeditor.activity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yas on 2019/3/19
 * Describe:
 */
public class BaseActivity extends AppCompatActivity {

    private Map<Integer, IRequestPermissionsCallBack> permissionCallBacks = new HashMap<>();


    public boolean checkPermission(String permission) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return PackageManager.PERMISSION_GRANTED == checkSelfPermission(permission);

    }

    public boolean checkPermissions(String[] permission){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for(String s : permission){
            if(checkPermission(s)){
                continue;
            }else{
                return false;
            }
        }

        return true;
    }


    public void requestPermission(String permission,int requestCode,IRequestPermissionsCallBack callBack) {

        requestPermissions(new String[]{permission}, requestCode,callBack);
    }

    public void requestPermissions(String[] permission,int requestCode,IRequestPermissionsCallBack callBack){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (callBack!=null){
                callBack.succeed();
            }
            return;
        }
        if (checkPermissions(permission)) {
            if (callBack!=null){
                callBack.succeed();
            }
            return;
        } else {
            permissionCallBacks.put(requestCode,callBack);
        }
        requestPermissions(permission, requestCode);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        IRequestPermissionsCallBack callBack = permissionCallBacks.get(requestCode);
        if (grantResults.length>0&&PackageManager.PERMISSION_GRANTED == grantResults[0]) {
            if (callBack != null) {
                callBack.succeed();
            }
        }else{
            if (callBack != null) {
                callBack.fail();
            }
        }
    }
    public interface IRequestPermissionsCallBack{
        void succeed();
        void fail();
    }
}
