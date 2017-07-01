package com.wildkid.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin2 on 2017/7/1.
 */

public class HttpUtils {

    public static byte[] get(String url) throws IOException{
        HttpURLConnection conn =
                (HttpURLConnection) new URL(url).openConnection();

        InputStream is = conn.getInputStream();
        if(conn.getResponseCode() ==200){
            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();

            byte[] buffer = new byte[10*1024];
            int len=-1;
            while((len=is.read(buffer))!=-1){

                baos.write(buffer,0,len);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //下载完成
            is.close();
            conn.disconnect();

            return baos.toByteArray();
        }

        return null;
    }

    public static String getJson(String url) throws IOException{
        return new String(get(url),"utf-8");
    }

    public static Bitmap getBitmap(String url) throws IOException{
        byte[] bytes= get(url); //下载网络数据

        //将字节数组转成图片对象(Bitmap对象)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}

