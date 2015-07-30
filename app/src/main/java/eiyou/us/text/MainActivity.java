package eiyou.us.text;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import eiyou.us.text.database.Db;
import eiyou.us.text.image.ImageLoader;
import eiyou.us.text.pullToRefresh.RefreshableView;
import eiyou.us.text.utils.Utils;
import eiyou.us.text.video.VideoMainActivity;

public class MainActivity extends Activity {
    private ImageView adImageView,tempImageView,userAvatarImageView;
    private Button nextAdButton;
    private ImageLoader imageLoader;
    private RefreshableView refreshableView;
    private ListView listView;
    private String URL="http://eiyou.us/mooc/list_json.txt";
    private String videoUrl;
    Db db;
    SQLiteDatabase dbwrite,dbread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isLogin();
        init();
        setAdImageView();
        new NewsAsyncTask().execute(URL);
        refreshListView();
        event();
    }

    private void isLogin() {
        BmobUser bmobUser = BmobUser.getCurrentUser(this);
        if(bmobUser != null){
            // 允许用户使用应用
        }else{
            //缓存用户对象为空时， 打开用户注册界面…
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
    }

    private void event() {
        userAvatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
    }


    //刷新下拉列表
    private void refreshListView() {
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    new NewsAsyncTask().execute(URL);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                refreshableView.finishRefreshing();
            }
        }, 0);
    }

    //加载广告域
    private void setAdImageView() {
        imageLoader=new ImageLoader();
        //预加载
        imageLoader.showImageByAsyncTask(tempImageView,"http://eiyou.us/mooc/ad/ad1.jpg");
        imageLoader.showImageByAsyncTask(tempImageView,"http://eiyou.us/mooc/ad/ad2.jpg");
        imageLoader.showImageByAsyncTask(tempImageView,"http://eiyou.us/mooc/ad/ad3.jpg");
        imageLoader.showImageByAsyncTask(tempImageView, "http://eiyou.us/mooc/ad/ad0.jpg");

        Utils.sharedPreferences.putInt(getApplicationContext(),"which_ad",0);
        nextAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int which_ad = Utils.sharedPreferences.getInt("which_ad", 0) % 4;
                Utils.sharedPreferences.putInt(getApplicationContext(), "which_ad", Utils.sharedPreferences.getInt("which_ad", 0) + 1);
                if (which_ad == 0) {
                    imageLoader.showImageByAsyncTask(adImageView, "http://eiyou.us/mooc/ad/ad1.jpg");
                } else if (which_ad == 1) {
                    imageLoader.showImageByAsyncTask(adImageView, "http://eiyou.us/mooc/ad/ad2.jpg");
                } else if (which_ad == 2) {
                    imageLoader.showImageByAsyncTask(adImageView, "http://eiyou.us/mooc/ad/ad3.jpg");
                } else if (which_ad == 3) {
                    imageLoader.showImageByAsyncTask(adImageView, "http://eiyou.us/mooc/ad/ad0.jpg");
                } else {
                    imageLoader.showImageByAsyncTask(adImageView, "http://eiyou.us/mooc/ad/ad0.jpg");
                }
                if (Utils.sharedPreferences.getInt("which_ad", 0) >= 232323232) {
                    Utils.sharedPreferences.putInt(getApplicationContext(), "which_ad", 0);
                }
            }
        });
    }

    //异步加载课程列表
    class NewsAsyncTask extends AsyncTask<String,Void,List<NewsBean>>{

        @Override
        protected List<NewsBean> doInBackground(String... params) {
            return getJsonData(params[0]);
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeans) {
            super.onPostExecute(newsBeans);
            //到本地数据库操作
//            ContentValues values=new ContentValues();
//            values.put("pic",);
//            dbwrite.insert("list",null,values);

            NewsAdapter adapter=new NewsAdapter(getApplicationContext(),newsBeans);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(getApplicationContext(), DetailsActivity.class).putExtra("videoUrl", videoUrl));
                }
            });
        }
    }
    private List<NewsBean> getJsonData(String url){
        List<NewsBean> newsBeanList=new ArrayList<>();
        try{
            String jsonString= readStream(new URL(url).openStream());
            Log.d("json",jsonString);
            JSONObject jsonObject;
            NewsBean newsBean;
            jsonObject=new JSONObject(jsonString);
            JSONArray jsonArray=jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject=jsonArray.getJSONObject(i);
                newsBean=new NewsBean();
                newsBean.newsIconUrl=jsonObject.getString("picSmall");
                newsBean.newsTitle=jsonObject.getString("title");
                newsBean.newsContent=jsonObject.getString("content");
                videoUrl=newsBean.videoUrl=jsonObject.getString("video");
                newsBeanList.add(newsBean);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return newsBeanList;
    }
    private String readStream(InputStream is){
        InputStreamReader isr;
        String result="";
        try{
            String line="";
            isr=new InputStreamReader(is,"utf-8");
            BufferedReader br=new BufferedReader(isr);
            while ((line=br.readLine())!=null){
                result+=line;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private void init() {
        listView=(ListView)findViewById(R.id.lv_list);
        adImageView=(ImageView)findViewById(R.id.iv_ad);
        nextAdButton=(Button)findViewById(R.id.b_next_ad);
        tempImageView=(ImageView)findViewById(R.id.iv_temp);
        refreshableView=(RefreshableView)findViewById(R.id.rv_refresh);
        userAvatarImageView=(ImageView)findViewById(R.id.iv_user_avatar);
        Bmob.initialize(this, "b683205e58831f338c406aa5ef6a5fe3");
        db=new Db(this);
        dbwrite=db.getWritableDatabase();
    }
}
