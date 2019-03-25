package com.wys.mediaeditor.util;

import android.os.Environment;
import android.widget.Toast;

import com.wys.mediaeditor.MainActivity;
import com.wys.mediaeditor.MyApp;

import java.io.File;

/**
 * Created by yas on 2019/3/25
 * Describe:
 */
public class FileUtils {



    public static String getBaseFolder() {
        String baseFolder = Environment.getExternalStorageDirectory() + "/Codec/";
        File f = new File(baseFolder);
        if (!f.exists()) {
            boolean b = f.mkdirs();
            if (!b) {
                baseFolder = MyApp.getContext().getExternalFilesDir(null).getAbsolutePath() + "/";
            }
        }
        return baseFolder;
    }
    //获取VideoPath
    public static String getPath(String path, String fileName) {
        String p = getBaseFolder() + path;
        File f = new File(p);
        if (!f.exists() && !f.mkdirs()) {
            return getBaseFolder() + fileName;
        }
        return p + fileName;
    }
}
