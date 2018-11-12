package com.practice.albert.tool;

import android.util.Log;

/**
 * Created by DELL on 2018/11/01.
 */

public class LogTool {
    public static boolean isShow = true; //false表示上线模式，true表示开发模式

    public static void v(String tag, String msg){
        if(isShow){
            Log.v(tag,msg);
        }
    }

    public static void d(String tag, String msg){
        if(isShow){
            Log.d(tag,msg);
        }
    }

    public static void i(String tag, String msg){
        if(isShow){
            Log.i(tag,msg);
        }
    }

    public static void w(String tag, String msg){
        if(isShow){
            Log.w(tag,msg);
        }
    }

    public static void e(String tag, String msg){
        if(isShow){
            Log.e(tag,msg);
        }
    }

    public static void wtf(String tag, String msg){
        if(isShow){
            Log.wtf(tag,msg);
        }
    }
}
