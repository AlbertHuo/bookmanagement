package com.practice.albert.tool;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by DELL on 2018/11/01.
 */

public class DialogTool {
    public static boolean isShow = false; //false表示上线模式，true表示开发模式
    public static int SYSTEM = 0;
    public static int IO = 1;
    public static int NETWORK = 2;
    private static String[] mError = {"系统异常，请稍后再试","读写失败，请清理内存空间后再试","网络连接失败，请检查网络是否开启"};

    public static void showError(Context ctx, int type, String title, Exception e){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        if(isShow == true){
            String desc = String.format("%s\n异常描述：%s",mError[type],e.getMessage());
            builder.setMessage(desc);
        }else{
            builder.setMessage(mError[type]);
        }
        builder.setPositiveButton("确定",null);
        builder.create().show();
    }

    public static void showError(Context ctx, int type, String title,int code, Exception e){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        if(isShow == true){
            String desc = String.format("%s\n异常描述：%s",mError[type],code,e.getMessage());
            builder.setMessage(desc);
        }else{
            builder.setMessage(mError[type]);
        }
        builder.setPositiveButton("确定",null);
        builder.create().show();
    }
}
