package com.wildkid.voaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by admin2 on 2017/7/1.
 */

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    ViewPager viewPager;
    List<View> views;
    WelcomePagerAdapter adapter;

    Button startAppBtn;
    RadioGroup navLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide(); //动作栏隐藏

        startAppBtn= (Button) findViewById(R.id.startAppBtn);
        startAppBtn.setOnClickListener(this);

        navLayout= (RadioGroup) findViewById(R.id.navLayout);

        viewPager= (ViewPager) findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==views.size()-1){
                    startAppBtn.setVisibility(View.VISIBLE);
                }else{
                    startAppBtn.setVisibility(View.GONE);
                }

                navLayout.check(
                        navLayout.getChildAt(position).getId());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initViews();
    }

    @Override
    public void onClick(View v) {
        //启动登录页面
        startActivity(new Intent(getApplicationContext(),
                MainActivity.class));

        finish();
    }

    private void initViews() {
        views=new ArrayList<>();
        View view =null;
        ImageView welcomeIv = null;
        for (int i = 0; i < 5; i++) {
            view = getLayoutInflater()
                    .inflate(R.layout.item_viewpager_welcome,null);
            welcomeIv= (ImageView) view.findViewById(R.id.welcomeIv);

            int resId=getResources()
                    .getIdentifier("help"+i,"drawable",getPackageName());
            welcomeIv.setImageBitmap(scaleImageRes(resId));

            views.add(view);
        }

        adapter=new WelcomePagerAdapter();
        viewPager.setAdapter(adapter);
    }



    private Bitmap scaleImageRes(int resId){
        BitmapFactory.Options options=
                new BitmapFactory.Options();
        options.inSampleSize=2;

        return BitmapFactory.decodeResource(getResources(),resId,options);
    }




    class WelcomePagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //向ViewPager容器中添加View
            container.addView(views.get(position));
            return views.get(position); //将当前面页作为item数据返回
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //将指定位置的页面从ViewPager容器中移除
            container.removeView(views.get(position));
        }
    }
}

