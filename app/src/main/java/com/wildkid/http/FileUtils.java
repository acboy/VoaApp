package com.wildkid.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by admin2 on 2017/7/1.
 */

public class FileUtils {
    private static final String IMG_DIR="/images";
    private static String dir;
    public static void init(Context context){
        dir = context.getFilesDir()+IMG_DIR;
    }

    public static void saveImg(String url, Bitmap bitmap){
        if(dir==null){
            throw new RuntimeException("必须调用FileUtils.init()方法来初始化!");
        }

        File dirFile= new File(dir);
        if(!dirFile.exists())
            dirFile.mkdir(); //创建目录

        File imgFile = new File(dirFile,md5(url));
        try {
            FileOutputStream fos= new FileOutputStream(imgFile);

            //将图片Bitmap写入到文件输出流中
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getImg(String url){
        File imageFile = new File(dir,md5(url));
        if(imageFile.exists()){
            //将图片文件转成图片对象(Bitmap)
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        }

        return null;
    }

    public static String md5(String url){
        StringBuilder result=new StringBuilder();
        try {
            MessageDigest md= MessageDigest.getInstance("MD5");
            md.update(url.getBytes());
            //获取针对url字节数据的md5算法之后的字节数据
            byte[] digest = md.digest();
            int n=0;
            for (byte b:digest) {
                n = b & 0xff;
                if(n<10)
                    result.append("0");

                //把int数值转成16进度的字符
                result.append(Integer.toHexString(n));
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}

