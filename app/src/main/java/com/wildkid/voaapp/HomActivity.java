package com.wildkid.voaapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.wildkid.bean.Person;

import java.util.List;

/**
 * Created by admin2 on 2017/7/1.
 */

public class HomActivity extends AppCompatActivity {

    TextView contentTv;
    MyApplication app;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        app = (MyApplication) getApplication();

        contentTv = (TextView) findViewById(R.id.contentTv);

        //当前登录用户的名称显示在标题栏上
        setTitle(String.valueOf(app.appDatas.get("username")));

        //获取启动当前页面的intent中的数据
        int age = getIntent().getIntExtra("age",-1);
        float level= getIntent().getFloatExtra("level",0f);

        contentTv.setText("age:"+age+",level:"+level);

        loadData();
    }

    private void loadData() {
        contentTv.setText("");

        List<Person> persons = app.db.queryAllPerson();
        for(Person p: persons){
            contentTv.append("\n"+p.getName()+","+p.getAge());
        }
    }

    //添加按钮点击事件的处理方法
    public void add(View v){
        //向数据库的person表中插入一条记录
        app.db.addPerson("rose",(int)(Math.random()*21+10),17.5f);

        loadData();
    }
}

