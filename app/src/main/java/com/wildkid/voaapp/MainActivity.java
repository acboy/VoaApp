package com.wildkid.voaapp;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText nameEt;
    private EditText passwdEt;
    private TextView errorMsgTv;

    private ImageView imgIv;

    private MyApplication app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取当前运行的程序对象
        app = (MyApplication) getApplication();

        //加载布局
        setContentView(R.layout.activity_main);

        //从布局中查找两个文本输入控件
        nameEt= (EditText) findViewById(R.id.nameEt);
        passwdEt = (EditText) findViewById(R.id.passwdEt);

        errorMsgTv = (TextView) findViewById(R.id.errorMsg);

        imgIv = (ImageView) findViewById(R.id.img);

        //从共享参数中读取之前存储的用户名
        String username = (String)app.read("username");

        //app级别数据会随app的退出而无效
        if(username!=null){
            nameEt.setText(username);
        }

        startImgAnim();
    }

    private void startImgAnim() {

        //属性动画: 在动画的一定时间内,
        //        从属性的开始值到结束值按均速来改变指定对象的属性值
        ValueAnimator anim= ObjectAnimator
                .ofFloat(imgIv,"rotationX",0,45);
        anim.setDuration(2000);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(ValueAnimator.INFINITE); //无限次

        anim.start();
    }

    //对应布局中: onClick属性值,即为"login"
    public void login(View v) {
//        Toast.makeText(getApplicationContext(), "--login--",
//                Toast.LENGTH_LONG).show();

        String name= nameEt.getText().toString();
        String passwd= passwdEt.getText().toString();

        String validStr=null;

        //判断name或passwd是否为空
        if(TextUtils.isEmpty(name)){
            validStr ="账号不能为空!";
            //nameEt.setError(validStr);//显示错误信息
        }else if(TextUtils.isEmpty(passwd)){
            validStr ="口令不能为空!";
        }

        if(validStr!=null){
            //将错误显示出来
            showValidError(validStr);
            return;
        }

        //判断用户名和口令是否正确
        if(name.equals("disen")
                && passwd.equals("888")){

            //将当前登录的用户名存入到app的数据中
            app.appDatas.put("username",name);

            app.save("username",name); //将数据存放到共享参数文件中

            //显示主页面(NewsActivity)
            Intent intent= new Intent(getApplicationContext(),
                    NewsActivity.class);
            intent.putExtra("age",(int)(Math.random()*21+10));
            intent.putExtra("level",28.5f);

            startActivity(intent);

            finish(); //关闭当前页面

        }else{
            showValidError("账号或口令错误,请重试!");
            passwdEt.setText(""); //清空数据
        }

    }

    private void showValidError(String validStr){
        errorMsgTv.setVisibility(View.VISIBLE);
        errorMsgTv.setText(validStr);

        // errorMsgTv.setAlpha();
        //errorMsgTv.setRotationY();
        //errorMsgTv.setScaleX();
        PropertyValuesHolder pvh1=
                PropertyValuesHolder.ofFloat("alpha",0,1);
        PropertyValuesHolder pvh2=
                PropertyValuesHolder.ofFloat("scaleX",0,1);
        PropertyValuesHolder pvh3=
                PropertyValuesHolder.ofFloat("scaleY",0,1);
//        PropertyValuesHolder pvh3=
//                PropertyValuesHolder.ofFloat("rotationY",0,180);

        ValueAnimator animator =
                ObjectAnimator.ofPropertyValuesHolder(errorMsgTv,pvh1,pvh2,pvh3);
        animator.setDuration(4000);
        animator.setRepeatCount(1); //重复执行一次
        animator.setRepeatMode(ValueAnimator.REVERSE); //反向执行

        animator.start();//启动动画
    }
}

