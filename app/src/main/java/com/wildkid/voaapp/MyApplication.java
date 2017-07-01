package com.wildkid.voaapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


import com.wildkid.db.DBHelper;
import com.wildkid.http.FileUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin2 on 2017/7/1.
 */

public class MyApplication extends Application {

    public Map<String,Object> appDatas; //存放app的共享的数据
    public DBHelper db;

    private SharedPreferences sp;
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Application(应用程序)

        db= new DBHelper(this);

        appDatas = new HashMap<>();

        //datas为共享参数文件名:
        //  /data/data/{package}/shared_fs/datas.xml
        sp=getSharedPreferences("datas", Context.MODE_PRIVATE);

        FileUtils.init(this);
    }

    public void save(String name,String value){
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(name,value);

        edit.commit();//提交存入的数据
    }

    public String read(String name){
        return sp.getString(name,null);
    }

}

