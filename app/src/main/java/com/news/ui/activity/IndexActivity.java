package com.news.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.news.adapter.NewsFragmentAdapter;
import com.news.app.Constants;
import com.news.ui.fragment.NewsFragment;
import com.news.model.NewsChannelEntity;
import com.news.util.net.HttpClientUtil;
import com.news.util.net.HttpDataCallBack;
import com.news.util.net.NetUtils;
import com.news.net.R;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IndexActivity extends AppCompatActivity {
    private String TAG="IndexActivity";
    @Bind(R.id.tabs)
    public TabLayout tabLayout;
    @Bind(R.id.viewpager)
    public ViewPager viewPager;
    @Bind(R.id.toolbar)
    public Toolbar toolbar;
    @Bind(R.id.fab)
    public FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);


        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.icon_left_menu));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("新闻");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initData();
    }

    /**
     * @desc:
     * @author：Administrator on 2015/12/21 16:40
     */
    public void initData(){
        Logger.init("IndexActivity").setLogLevel(LogLevel.FULL);
        String url=Constants.API_NEWS;
        String datetime=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        final Map<String,Object> param=new HashMap<>();
        param.put("showapi_appid", "12041");
        param.put("showapi_sign", "67f7892db890407f95cdf39f870b1234");
        param.put("showapi_timestamp", datetime);
        url=Constants.API_NEWS_CHANNEL;
        httpResquest(url, param);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupViewPager(ViewPager viewPager,List<NewsChannelEntity.ChannelEntity.ChannelList> channelList) {
        NewsFragmentAdapter adapter = new NewsFragmentAdapter(getSupportFragmentManager());
        if(!channelList.isEmpty()){
             for(int i=0;i<channelList.size();i++){
                 NewsFragment newsFragment=new NewsFragment();
                 Bundle ags=new Bundle(2);
                 ags.putString("name",channelList.get(i).getName());
                 ags.putString("channelId",channelList.get(i).getChannelId());
                 newsFragment.setArguments(ags);
                 adapter.addFragment(newsFragment, channelList.get(i).getName());
             }
        }
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(channelList.size());//预加载
    }

    private void httpResquest(String url, Map<String, Object> param) {
        NetUtils.httpResquest(this, url, param, Constants.HTTP_GET, new HttpDataCallBack() {
            @Override
            public void onStart() {
                //Log.i(TAG, "http start...");
            }

            @Override
            public void processData(Object paramObject, boolean paramBoolean) {
                Log.i(TAG, "json:" + paramObject.toString());
                NewsChannelEntity newsChannelEntity=JSON.parseObject(paramObject.toString(),NewsChannelEntity.class);
                setupViewPager(viewPager,newsChannelEntity.getShowapi_res_body().getChannelList());
            }

            @Override
            public void onFinish() {
                // Log.i(TAG, "http end...");
            }

            @Override
            public void onFailed() {
                Log.i(TAG, "http onFail...");
            }
        });
    }

    /**
     * @desc:httpClient
     * @author：Administrator on 2015/12/22 11:17
     */
    @Deprecated
    private void httpTask(String url, Map<String, Object> param) {
        try {
         HttpClientUtil.Response response= HttpClientUtil.sendGetHeaderRequest(url, param, null);
         Log.i(TAG, "httpclient:" + response.getStatusCode());
         Log.i(TAG,"httpclient:"+response.getResponseText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
