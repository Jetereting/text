package eiyou.us.text;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import eiyou.us.text.video.VideoMainActivity;

public class MainActivity extends Activity {

    private ViewPager viewPager;
    private ListView listView;
    private Bitmap bitmap[];
    private String URL="http://eiyou.us/mooc/list_json.txt";
    private String videoUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setAdViewPager();
        new NewsAsyncTask().execute(URL);
    }

    //加载广告域
    public void setAdViewPager(){
        viewPager.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return false;
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
            NewsAdapter adapter=new NewsAdapter(getApplicationContext(),newsBeans);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(getApplicationContext(),VideoMainActivity.class).putExtra("videoUrl",videoUrl));
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
                newsBean.newsTitle=jsonObject.getString("name");
                newsBean.newsContent=jsonObject.getString("description");
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
        viewPager=(ViewPager)findViewById(R.id.vp_ad);
    }
}
