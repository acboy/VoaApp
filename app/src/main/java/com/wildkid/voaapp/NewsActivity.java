package com.wildkid.voaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.wildkid.bean.News;
import com.wildkid.http.HttpUtils;
import com.wildkid.http.ImageTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    ListView lv;
    NewsAdapter adapter;  //适配器
    List<News> datas; //数据源

    LinearLayout loadingLayout;

    MyApplication app;

    SwipeRefreshLayout refreshLayout;
    boolean isRefreshing=false;
    boolean isLoading=false;
    String maxId="0";

    ProgressDialog pd;

    SlidingPaneLayout slidingPane;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (MyApplication) getApplication();

        setContentView(R.layout.activity_news);
        slidingPane= (SlidingPaneLayout) findViewById(R.id.slidingPane);

        setTitle("最新");

        //显示标题栏上的图标
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        actionBar.setHomeAsUpIndicator(R.drawable.icon);

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(Color.RED,Color.GREEN,Color.BLUE);
        refreshLayout.setDistanceToTriggerSync(20);
        refreshLayout.setProgressViewOffset(true,10,40);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing=true;
                maxId="0";
                loadData();
            }
        });

        lv= (ListView) findViewById(R.id.newsListView);
        loadingLayout= (LinearLayout) findViewById(R.id.loadingLayout);

        //设置lv空数据时的显示视图
        lv.setEmptyView(loadingLayout);

        datas=new ArrayList<>();
        adapter = new NewsAdapter(); //在NewsAdapter中可以直接访问datas
        lv.setAdapter(adapter);

        //设置ListView中item点击事件监听
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //setTitle("voaId:"+id);

                //打开详情页面,并将voaid传过去
                Intent intent= new Intent(getApplicationContext(),
                        NewsItemActivity.class);
                intent.putExtra("voaId",id);
                intent.putExtra("title",
                        datas.get(position).getTitle());
                intent.putExtra("mp3",
                        datas.get(position).getSound());

                startActivity(intent);
            }
        });

        //设置ListView的滚动监听
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            boolean flag=false;//是否滚动到底部

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState==SCROLL_STATE_IDLE && flag==true){
                    //加载更多的数据
                    isLoading=true;
                    if(datas.size()>0) {
                        maxId = datas.get(datas.size() - 1).getVoaId();

                        if(pd==null) {
                            pd = new ProgressDialog(NewsActivity.this);
                            pd.setMessage("正在加载数据");
                        }
                        pd.show();

                        loadData(); //加载下一页数据
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem+visibleItemCount==totalItemCount){
                    flag=true;
                }else{
                    flag=false;
                }
            }
        });

        loadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            //点击当前窗口标题栏上的图标按钮事件
            if(!slidingPane.isOpen()){
                slidingPane.openPane();
            }else{
                slidingPane.closePane();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        //下载json数据
        String url="http://apps.iyuba.com/iyuba/titleApi3.jsp?maxid=%s&pages=1&pageNum=20&parentID=0&type=android&format=json";
        new NewsTask().execute(String.format(url,maxId));
    }

    class NewsTask extends AsyncTask<String,Void,List<News>>{

        @Override
        protected List<News> doInBackground(String... params) {
            String url= params[0]; //获取下载的任务(url地址)
            try {
                String json = null;
                //判断当前加载数据是否为下拉刷新
                if(isRefreshing){ //刷新
                    //从网络下载
                    json = HttpUtils.getJson(url);
                    //将下载的json数据存到数据库
                    app.db.saveJson(url, json);
                }else if(isLoading){ //加载更多
                    json = HttpUtils.getJson(url);
                } else{
                    //从数据库中读取json
                    json = app.db.getJson(url);
                    if (json == null) {
                        //从网络下载
                        json = HttpUtils.getJson(url);
                        //将下载的json数据存到数据库
                        app.db.saveJson(url, json);
                    }
                }

                //开始解析
                JSONObject obj= new JSONObject(json);
                //从json对象中读取到最核心的json数组
                JSONArray array = obj.getJSONArray("data");

                List<News> list = new ArrayList<>();
                News news= null;

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);

                    news = new News();
                    news.setVoaId(jsonObject.getString("VoaId"));
                    news.setTitle(jsonObject.getString("Title"));
                    news.setTitle_cn(jsonObject.getString("Title_cn"));
                    news.setDescCn(jsonObject.getString("DescCn"));
                    news.setCreatTime(jsonObject.getString("CreatTime"));
                    news.setPic(jsonObject.getString("Pic"));
                    news.setReadCount(jsonObject.getString("ReadCount"));
                    news.setSound(jsonObject.getString("Sound"));

                    list.add(news);
                }
                //解析完成
                return list;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<News> newses) {
            if(newses!=null) {
                //将数据显示在ListView中
                //setTitle("数据已下载完成:" + newses.size());

                if(isRefreshing){
                    datas.clear();//清除之前的旧数据
                }
                //将下载完成的数据添加到 数据源中
                datas.addAll(newses);

                //刷新适配器
                adapter.notifyDataSetChanged();

                if(isLoading)
                    pd.hide();

                isRefreshing=false;
                isLoading=false;

                //刷新结束
                refreshLayout.setRefreshing(isRefreshing);
            }
        }
    }

    class NewsAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public News getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return  Long.parseLong(getItem(position).getVoaId());
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            ViewHolder vh =null;

            if(convertView==null){ //没有可复用的item
                //创建新的itemView
                LayoutInflater inflater=
                        LayoutInflater.from(parent.getContext());

                convertView =
                        inflater.inflate(R.layout.item_news, parent, false);

                vh= new ViewHolder();
                vh.itemTitle = (TextView)
                        convertView.findViewById(R.id.item_title);
                vh.itemCountAndTime = (TextView)
                        convertView.findViewById(R.id.item_countAndTime);

                vh.itemPic= (ImageView)
                        convertView.findViewById(R.id.item_pic);

                vh.itemCollect= (ImageView)
                        convertView.findViewById(R.id.item_collect);

                //将VieHolder的对象作为itemView的标签使用
                convertView.setTag(vh);
            }else{ //有可复用的itemView
                vh= (ViewHolder) convertView.getTag();
            }

            //判断当前的数据是否被收藏
            vh.itemCollect.setImageResource(R.drawable.news_uncollect);

            vh.itemCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //将当前位置的数据存入到收藏表中(title,pic,create_time)
                    ((ImageView)v).setImageResource(R.drawable.news_collected);
                }
            });



            vh.itemTitle.setText(getItem(position).getTitle_cn());
            vh.itemCountAndTime.setText(getItem(position).getReadCount()+"阅读次数");

            String creatTime = getItem(position).getCreatTime();
            vh.itemCountAndTime.append("\n\n"+creatTime.substring(0,10));

            //图片的下载和显示
            String picURL= getItem(position).getPic();
            vh.itemPic.setTag(picURL); //将图片的地址作为图片控件的标签使用

            //显示默认的图片
            vh.itemPic.setImageResource(R.mipmap.ic_launcher);


            new ImageTask(new ImageTask.Callback() {
                @Override
                public void ok(String url, Bitmap bitmap) {
                    //图片下载完成,通过tag查找图片控件,判断图片控件是否被其它的图片地址占用
                    ImageView iv =
                            (ImageView) parent.findViewWithTag(url);

                    if(iv!=null){ //图片作为标签所绑定图片控件没有被其它图片占用
                        iv.setImageBitmap(bitmap);
                    }
                }
            }).execute(picURL);

            return convertView;
        }

        class ViewHolder{
            TextView itemTitle;
            ImageView itemPic;
            TextView itemCountAndTime;
            ImageView itemCollect;
        }
    }
}

