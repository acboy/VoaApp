package com.wildkid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin2 on 2017/7/1.
 */

public class DBConn extends SQLiteOpenHelper {
    public DBConn(Context context){
        super(context,"app.db",null,3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库中相关表,SQLite数据库是嵌入式小型的数据库,且没有固定的数据类型
        //创建表 person:
        // create table person(_id integer primary key,name,age,level)
        db.execSQL("create table person(_id integer primary key,name,age,level)"); //执行sql语句
        db.execSQL("create table t_json(url,json)");
        db.execSQL("create table t_collect(title,pic,create_time)");

        //向person表中添加2条数据
        db.execSQL("insert into person(name,age,level) values('disen',21,18.5)");

        ContentValues values= new ContentValues();
        values.put("name","jack"); //添加列名和列值
        values.put("age",22);
        values.put("level",19.8f);
        db.insert("person",null,values); //插入数据

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //版本升级时执行的方法
        if(newVersion>oldVersion && newVersion==3){
            db.execSQL("drop table if exists json");
            db.execSQL("create table t_json(url,json)");
            db.execSQL("create table t_collect(title,pic,create_time)");
        }
    }
}
