package eiyou.us.text;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

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
import eiyou.us.text.communication.MyUser;
import eiyou.us.text.image.ImageLoader;
import eiyou.us.text.news.NewsBean;
import eiyou.us.text.pullToRefresh.RefreshableView;
import eiyou.us.text.utils.Utils;
import eiyou.us.text.yuyin.Config;
import eiyou.us.text.yuyin.Constants;

public class MainActivity extends Activity {
    private ImageView adImageView, tempImageView, userAvatarImageView;
    private Button nextAdButton, userButton;
    private ImageLoader imageLoader;
    private RefreshableView refreshableView;
    private ListView listView;
    private String URL = "http://eiyou.us/mooc/list_json.txt";
    private String softURL = "http://eiyou.us/mooc/soft_list_json.txt";
    private String computerURl = "http://eiyou.us/mooc/computer_list_json.txt";
    private String videoUrl;
    private String videoName;
    private NewsAdapter adapter;
    private LinearLayout noConnectLinearLayout;
    private TextView schoolTextView, userName, userInstruction;
    //语音
    private BaiduASRDigitalDialog mDialog = null;
    private DialogRecognitionListener mRecognitionListener;
    private int mCurrentTheme = Config.DIALOG_THEME;

    private Intent intent;
    int whichClass=0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    BmobUser bmobUser;
    MyUser myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isLogin();
        init();
        if (isNetworkConnected(getApplicationContext())) {
            noConnectLinearLayout.setVisibility(View.GONE);
            if (myUser.getSchoolId() != null&&sharedPreferences.getInt("AllorSch",0)==1) {
                if (myUser.getSchoolId().indexOf("1408620") >= 0) {
                    new NewsAsyncTask().execute(softURL);
                    whichClass=20;
                }
                if (myUser.getSchoolId().indexOf("1408610") >= 0) {
                    new NewsAsyncTask().execute(computerURl);
                    whichClass=10;
                }
            } else {
                new NewsAsyncTask().execute(URL);
                whichClass=0;
            }
            setAdImageView();
//            new NewsAsyncTask().execute(softURL);
            refreshListView();
        } else {
            noConnectLinearLayout.setVisibility(View.VISIBLE);
        }
        event();
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private void isLogin() {
        bmobUser = BmobUser.getCurrentUser(this);
        if (bmobUser != null) {
            // 允许用户使用应用
        } else {
            //缓存用户对象为空时， 打开用户注册界面…
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }

    private void event() {
        //用户信息
        if (bmobUser != null) {
            userName.setText(myUser.getUsername());
            userInstruction.setText(myUser.getInstruction());
        }
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bmobUser != null) {
                    startActivity(new Intent(getApplicationContext(), UserInfoActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        });
        //学校课程
        schoolTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("AllorSch",1);
                editor.commit();
                if (bmobUser != null) {
                    if (myUser.getSchoolId() == null) {
                        Utils.toast.show(getApplicationContext(), "请先完成教务系统绑定");
                        startActivity(new Intent(getApplicationContext(), SchoolLoginActivity.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                } else {
                    Utils.toast.show(getApplicationContext(), "请先登录 ");
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        });
        //全部课程
        findViewById(R.id.all_learn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("AllorSch",0);
                editor.commit();
                whichClass=0;
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        //离线缓存
        findViewById(R.id.downloaded).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DownloadedActivity.class));
            }
        });
        //关于我们
        findViewById(R.id.about_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
            }
        });
    }


    //刷新下拉列表
    private void refreshListView() {
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkConnected(getApplicationContext())) {
                    noConnectLinearLayout.setVisibility(View.GONE);
                    switch (whichClass) {
                        case 0:
                            new NewsAsyncTask().execute(URL);
                            break;
                        case 10:
                            new NewsAsyncTask().execute(computerURl);
                            break;
                        case 20:
                            new NewsAsyncTask().execute(softURL);
                            break;
                    }
                    refreshListView();
                } else {
                    noConnectLinearLayout.setVisibility(View.VISIBLE);
                }
                try {
                    switch (whichClass) {
                        case 0:
                            new NewsAsyncTask().execute(URL);
                            break;
                        case 10:
                            new NewsAsyncTask().execute(computerURl);
                            break;
                        case 20:
                            new NewsAsyncTask().execute(softURL);
                            break;
                    }
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
        imageLoader = new ImageLoader();
        //预加载
        switch (whichClass) {
            case 0:
                imageLoader.showImageByAsyncTask(tempImageView, "http://eiyou.us/mooc/ad/ad1.jpg");
                imageLoader.showImageByAsyncTask(tempImageView, "http://eiyou.us/mooc/ad/ad2.jpg");
                imageLoader.showImageByAsyncTask(tempImageView, "http://eiyou.us/mooc/ad/ad3.jpg");
                imageLoader.showImageByAsyncTask(tempImageView, "http://eiyou.us/mooc/ad/ad0.jpg");

                Utils.sharedPreferences.putInt(getApplicationContext(), "which_ad", 0);
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
                });break;
            case 10:
                adImageView.setImageResource(R.drawable.computer_ad);
                nextAdButton.setVisibility(View.GONE);
                break;
            case 20:
                adImageView.setImageResource(R.drawable.soft_ad);
                nextAdButton.setVisibility(View.GONE);
                break;
        }
    }

    //异步加载课程列表
    class NewsAsyncTask extends AsyncTask<String, Void, List<NewsBean>> {

        @Override
        protected List<NewsBean> doInBackground(String... params) {
            return getJsonData(params[0]);
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeans) {
            super.onPostExecute(newsBeans);
            adapter = new NewsAdapter(getApplicationContext(), newsBeans);
            listView.setAdapter(adapter);
        }
    }

    private List<NewsBean> getJsonData(String url) {
        List<NewsBean> newsBeanList = new ArrayList<>();
        try {
            String jsonString = readStream(new URL(url).openStream());
            Log.d("json", jsonString);
            JSONObject jsonObject;
            NewsBean newsBean;
            jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                newsBean = new NewsBean();
                newsBean.newsIconUrl = jsonObject.getString("picSmall");
                newsBean.newsTitle = jsonObject.getString("title");
                newsBean.newsContent = jsonObject.getString("content");
                newsBean.videoUrl = jsonObject.getString("video");
                newsBeanList.add(newsBean);

                //bmob
//                BmobNews bmobNews = new BmobNews();
//                bmobNews.setNewsIcon(new BmobFile(new File(newsBean.newsIconUrl)));
//                bmobNews.setNewsIcon();
//                bmobNews.setNewsTitle(newsBean.newsTitle);
//                bmobNews.setNewsContent(newsBean.newsContent);
//                bmobNews.setNewsVideoUrl(newsBean.newsContent);
//                bmobNews.save(getApplicationContext(), new SaveListener() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onFailure(int i, String s) {
//                        Log.e("ee", s);
//                    }
//                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newsBeanList;
    }

    private String readStream(InputStream is) {
        InputStreamReader isr;
        String result = "";
        try {
            String line = "";
            isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void init() {
        userButton = (Button) findViewById(R.id.b_user);
        listView = (ListView) findViewById(R.id.lv_list);
        adImageView = (ImageView) findViewById(R.id.iv_ad);
        userName = (TextView) findViewById(R.id.tv_user_name);
        nextAdButton = (Button) findViewById(R.id.b_next_ad);
        tempImageView = (ImageView) findViewById(R.id.iv_temp);
        userInstruction = (TextView) findViewById(R.id.tv_user_instruction);
        refreshableView = (RefreshableView) findViewById(R.id.rv_refresh);
        userAvatarImageView = (ImageView) findViewById(R.id.iv_user_avatar);
        noConnectLinearLayout = (LinearLayout) findViewById(R.id.ll_no_connect);
        schoolTextView = (TextView) findViewById(R.id.tv_school);
        Bmob.initialize(this, "b683205e58831f338c406aa5ef6a5fe3");
        intent = this.getIntent();
        sharedPreferences = getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        myUser = BmobUser.getCurrentUser(this, MyUser.class);
        bmobUser = BmobUser.getCurrentUser(this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            mRecognitionListener = new DialogRecognitionListener() {
                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> rs = results != null ? results
                            .getStringArrayList(RESULTS_RECOGNITION) : null;
                    if (rs != null && rs.size() > 0) {
                        String heard = rs.get(0);
                        if (heard.indexOf("退出") >= 0) {
                            startActivity(new Intent(Intent.ACTION_MAIN).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addCategory(Intent.CATEGORY_HOME));
                        } else if (heard.indexOf("个人信息") >= 0) {
                            Log.e("ee", "个人信息");
                            startActivity(new Intent(getApplicationContext(), UserInfoActivity.class));
                        } else if (heard.indexOf("编辑") >= 0) {
                            startActivity(new Intent(getApplicationContext(), EditInfoActivity.class));
                        } else if(heard.indexOf("主页")>=0){
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        } else if(heard.indexOf("下载")>=0){
                            startActivity(new Intent(getApplicationContext(),DownloadedActivity.class));
                        } else if (heard.indexOf("我们")>=0){
                            startActivity(new Intent(getApplicationContext(),AboutUsActivity.class));
                        }
                    }

                }
            };
            mCurrentTheme = Config.DIALOG_THEME;
            if (mDialog != null) {
                mDialog.dismiss();
            }
            Bundle params = new Bundle();
            params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, Constants.API_KEY);
            params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, Constants.SECRET_KEY);
            params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, Config.DIALOG_THEME);
            mDialog = new BaiduASRDigitalDialog(MainActivity.this, params);
            mDialog.setDialogRecognitionListener(mRecognitionListener);
//                }
            mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP, Config.CURRENT_PROP);
            mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
                    Config.getCurrentLanguage());
            Log.e("DEBUG", "Config.PLAY_START_SOUND = " + Config.PLAY_START_SOUND);
            mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, Config.PLAY_START_SOUND);
            mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, Config.PLAY_END_SOUND);
            mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, Config.DIALOG_TIPS_SOUND);
            mDialog.show();
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this).setTitle("确认退出吗？")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 点击“确认”后的操作
                            Intent home = new Intent(Intent.ACTION_MAIN);
                            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            home.addCategory(Intent.CATEGORY_HOME);
                            startActivity(home);
                        }
                    })
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 点击“返回”后的操作,这里不设置没有任何操作
                        }
                    }).show();
        }
        return true;
    }


    public class NewsAdapter extends BaseAdapter {
        private List<NewsBean> list;
        private LayoutInflater inflater;
        private ImageLoader imageLoader;

        public NewsAdapter(Context context, List<NewsBean> data) {
            list = data;
            inflater = LayoutInflater.from(context);
            imageLoader = new ImageLoader();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.news_item_layout, null);
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.ll_ll);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //异步加载图片
            viewHolder.ivIcon.setImageResource(R.mipmap.ic_launcher);
            String iconUrl = list.get(position).newsIconUrl;
            viewHolder.ivIcon.setTag(iconUrl);
            imageLoader.showImageByAsyncTask(viewHolder.ivIcon, iconUrl);

            //bmob
//            BmobNews bmobNews=new BmobNews();
//            bmobNews.setNewsIcon(new BmobFile(new File(list.get(position).newsIconUrl)));
//            bmobNews.setNewsTitle(list.get(position).newsTitle);
//            bmobNews.setNewsContent(list.get(position).newsContent);
//            bmobNews.setNewsVideoUrl(list.get(position).videoUrl);
//            bmobNews.save(getApplicationContext());

            viewHolder.tvTitle.setText(list.get(position).newsTitle);
            viewHolder.tvContent.setText(list.get(position).newsContent);

            //相应课程点击事件
            final int tempPosition = position;
            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View position) {
                    videoName = list.get(tempPosition).newsTitle;
                    videoUrl = list.get(tempPosition).videoUrl;
                    startActivity(new Intent(getApplicationContext(), DetailsActivity.class).putExtra("videoUrl", videoUrl).putExtra("videoName", videoName));
                }
            });
            return convertView;
        }

        class ViewHolder {
            public TextView tvTitle, tvContent;
            public ImageView ivIcon;
            public LinearLayout linearLayout;
        }
    }

    public void onDestroy() {
        listView.setAdapter(null);
        super.onDestroy();
    }
}
