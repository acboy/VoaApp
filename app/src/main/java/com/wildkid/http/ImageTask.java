package com.wildkid.http;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by admin2 on 2017/7/1.
 */

public class ImageTask extends AsyncTask<String,Void,Bitmap> {
    public interface Callback{
        void ok(String url,Bitmap bitmap);
    }

    private Callback callback;
    private String url;
    public ImageTask(Callback callback){
        this.callback = callback;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        this.url= params[0];
        try {
            Bitmap bitmap =null;
            //先从本地取
            bitmap=FileUtils.getImg(url);
            if(bitmap!=null){
                return bitmap;
            }

            //从网络下载
            bitmap = HttpUtils.getBitmap(url);
            //要保存到本地中
            FileUtils.saveImg(url,bitmap);

            return bitmap; //返回图片对象
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap!=null && callback!=null){
            callback.ok(url,bitmap); //数据回传给任务的调用者
        }
    }
}

