package com.wildkid.voaapp;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.wildkid.bean.NewsItem;
import com.wildkid.http.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsItemActivity extends AppCompatActivity {
    private String url="http://apps.iyuba.com/iyuba/textNewApi.jsp?voaid=%d&format=json";

    private ListView lv;
    private List<NewsItem> datas;
    private NewsItemAdapter adapter;

    private String mp3URL;
    private String baseURL="http://static.iyuba.com/sounds/voa";

    MediaPlayer mPLayer; //媒体资源播放控件,可以播放mp3,mp4,3gp

    ImageView pauseBtn;
    SeekBar playProgressBar;
    TextView curTimeTv,totalTimeTv;

    int currentIndex=0; //当前播放段落的index位置,即ListView的item的索引位置


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = String.format(url,getIntent().getLongExtra("voaId",0));

        setTitle(getIntent().getStringExtra("title"));
        mp3URL=baseURL+getIntent().getStringExtra("mp3");

        setContentView(R.layout.activity_newsitem);
        pauseBtn = (ImageView) findViewById(R.id.pauseBtn);
        playProgressBar = (SeekBar) findViewById(R.id.playProgressBar);
        curTimeTv= (TextView) findViewById(R.id.curTimeTv);
        totalTimeTv = (TextView) findViewById(R.id.totalTimeTv);

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPLayer.isPlaying()){
                    mPLayer.pause();//暂停
                    pauseBtn.setImageResource(android.R.drawable.ic_media_play);
                }else{
                    mPLayer.start();//播放
                    new PlayingThread().start();
                    pauseBtn.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        //设置SeekBar的拖动事件监听
        playProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止拖动
                int progress= seekBar.getProgress();
                if(datas.size()>0){
                    //查找拖动的位置时间,是哪一个段落
                    for (int i = 0; i < datas.size(); i++) {
                        float timing= datas.get(i).getTiming()*1000;
                        float endTiming=datas.get(i).getEndTiming()*1000;

                        if(progress>=timing && progress<=endTiming){
                            currentIndex =i;
                            break;
                        }
                    }
                }

                if(mPLayer!=null){
                    mPLayer.seekTo(progress);
                }
            }
        });

        lv= (ListView) findViewById(R.id.newsitemListView);

        datas= new ArrayList<>();
        adapter = new NewsItemAdapter();

        lv.setAdapter(adapter);

        loadData();

        initPlayer();
    }

    private void initPlayer() {
        mPLayer = new MediaPlayer();
        try {
            mPLayer.setDataSource(mp3URL);
            mPLayer.prepareAsync();
            mPLayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //就绪已完成
                    mPLayer.start(); //开始播放
                    new PlayingThread().start();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPLayer!=null && mPLayer.isPlaying()){
            mPLayer.stop();
            mPLayer.release();
        }
    }

    private void loadData() {

        new NewsItemTask().execute(url);
    }

    private String formatTime(int ms){
        StringBuilder builder= new StringBuilder();
        int s=ms/1000;
        int m=s/60; //分钟
        s=s%60; //秒数

        if(m<10)
            builder.append("0");
        builder.append(m).append(":");

        if(s<10)
            builder.append("0");
        builder.append(s);

        return builder.toString();
    }

    class PlayingThread extends Thread{
        @Override
        public void run() {
            //获取播放mp3的总时长
            final int totalTime=mPLayer.getDuration();
            playProgressBar.setMax(totalTime);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    totalTimeTv.setText("/"+formatTime(totalTime));
                }
            });

            try{
                while(mPLayer!=null && mPLayer.isPlaying()){
                    final int progress= mPLayer.getCurrentPosition();
                    playProgressBar.setProgress(progress);

                    //子线程调用runOnUiThread(),将数据显示在UI上
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            curTimeTv.setText(formatTime(progress));
                        }
                    });

                    if(datas.size()>0 && progress >= datas.get(currentIndex).getEndTiming()*1000 ){

                        //即将播放下一段落
                        //刷新ListView,将当前显示播放段落的TextView显示蓝色,
                        //同时要同步滚动
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                currentIndex++;
                                if(currentIndex > datas.size()-1){
                                    return;
                                }
                                adapter.notifyDataSetChanged();
                                if(lv.getChildCount()>0){

                                    int offset=(lv.getHeight()
                                            -lv.getChildAt(lv.getChildCount()/2).getHeight())/4;

                                    //同步滚动
                                    lv.smoothScrollToPositionFromTop(currentIndex-1,offset);
                                }
                            }
                        });

                        Thread.sleep(500);
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class NewsItemTask extends AsyncTask<String,Void,List<NewsItem>>{

        @Override
        protected List<NewsItem> doInBackground(String... params) {
            String url= params[0];
            try {
                String json = HttpUtils.getJson(url);
                JSONObject obj = new JSONObject(json);
                JSONArray array = obj.getJSONArray("data");

                List<NewsItem> list=  new ArrayList<>();
                NewsItem nItem=null;

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObj = array.getJSONObject(i);

                    nItem=new NewsItem();

                    nItem.setSentence(jsonObj.getString("Sentence"));
                    nItem.setSentence_cn(jsonObj.getString("sentence_cn"));
                    nItem.setTiming((float) jsonObj.getDouble("Timing"));
                    nItem.setEndTiming((float) jsonObj.getDouble("EndTiming"));

                    list.add(nItem);
                }

                return list;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<NewsItem> newsItems) {
            if(newsItems!=null){
                //数据解析完成
                datas.addAll(newsItems);
                adapter.notifyDataSetChanged();
            }
        }
    }

    class NewsItemAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public NewsItem getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv=null;
            if(convertView==null){
                tv= (TextView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_newsitem,parent,false);
            }else{
                tv= (TextView) convertView;
            }

            tv.setText(getItem(position).getSentence());
            if(position==currentIndex){
                tv.setTextColor(Color.BLUE);
            }else{
                tv.setTextColor(Color.BLACK);
            }

            return tv;
        }
    }
}

