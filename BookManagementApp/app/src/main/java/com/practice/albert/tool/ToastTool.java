package com.practice.albert.tool;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by DELL on 2018/11/01.
 */

public class ToastTool {
    public static boolean isShow = true; //false表示上线模式，true表示开发模式
    public static boolean isQuit = false; //false表示显示，true表示不显示

    public static void showShort(Context ctx,String msg){
        if(isShow){
            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showLong(Context ctx,String msg){
        if(isShow){
            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
        }
    }

    public static void showToast(Context ctx,String msg){
        if(!isQuit){
            Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
        }
    }
}

