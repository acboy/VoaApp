package com.wildkid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.wildkid.bean.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin2 on 2017/7/1.
 */

public class DBHelper {
    private DBConn conn;
    public DBHelper(Context context){
        conn = new DBConn(context);
    }

    //从数据库的表查询所有人员信息
    public List<Person> queryAllPerson(){
        SQLiteDatabase db = conn.getReadableDatabase();

        Cursor cursor = db.rawQuery("select _id,name,age,level from person",null);

        List<Person> list= new ArrayList<>();
        Person person = null;
        while (cursor.moveToNext()){
            long id= cursor.getLong(0);
            String name= cursor.getString(1);
            int age= cursor.getInt(2);
            float level = cursor.getFloat(3);

            person = new Person(id,name,age,level);
            list.add(person);
        }
        cursor.close();
        db.close();
        return list;
    }

    public void addPerson(String name, int age, float level) {
        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues values= new ContentValues();
        values.put("name",name);
        values.put("age",age);
        values.put("level",level);

        db.insert("person",null,values);

        db.close(); //最后关闭数据库连接
    }

    public void saveJson(String url,String json){
        SQLiteDatabase db = conn.getWritableDatabase();
        //先删除已存在的json数据
        db.execSQL("delete from t_json where url=?",new Object[]{url});

        db.execSQL("insert into t_json(url,json) values(?,?)",
                new Object[]{url,json});
        db.close();
    }


    public String getJson(String url){
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("select json from t_json where url=?",
                new String[]{url});
        //判断是否查询到数据
        if(cursor.moveToNext()) {
            String json = cursor.getString(0);
            return json;
        }
        return null;
    }

    public void saveCollect(String title,String picUrl,String create_time){
        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues values= new ContentValues();
        values.put("title",title);
        values.put("pic",picUrl);
        values.put("create_time",create_time);

        db.insert("t_collect",null,values);

        db.close(); //最后关闭数据库连接
    }
}

