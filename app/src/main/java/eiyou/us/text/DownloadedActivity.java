package eiyou.us.text;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eiyou.us.text.download.ShowDownload;
import eiyou.us.text.download.ViewHolder;
import eiyou.us.text.utils.Utils;
import eiyou.us.text.yuyin.Config;
import eiyou.us.text.yuyin.Constants;

public class DownloadedActivity extends Activity {
    ListView listView;
    ShowDownload showDownload;
    List<String> list,pathList;
    String videoPath;
    MyAdapter adapter;
    //语音
    private BaiduASRDigitalDialog mDialog = null;
    private DialogRecognitionListener mRecognitionListener;
    private int mCurrentTheme = Config.DIALOG_THEME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded);
        init();
        event();
    }

    private void event() {
        //获取SD卡内容
        String videoPath= Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/.us.mooc/";
        list=showDownload.show(videoPath);
        pathList=showDownload.showPath(videoPath);
        //listView添加适配器
        adapter=new MyAdapter();
        listView.setAdapter(adapter);

        //返回
        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    private void init() {
        listView=(ListView)findViewById(R.id.list);
        showDownload=new ShowDownload();
    }

    class MyAdapter extends BaseAdapter{
        private LayoutInflater inflater;
        MyAdapter(){
            inflater = LayoutInflater.from(getApplicationContext());
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
                convertView = inflater.inflate(R.layout.downloaded_list_item, null);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.textView.setText(list.get(position));

            //相应视频点击事件
            final int tempPosition = position;
            viewHolder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View position) {
                    videoPath = pathList.get(tempPosition);
                    startActivity(new Intent(getApplicationContext(),DetailsActivity.class).putExtra("videoPath",videoPath));
                }
            });
            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(DownloadedActivity.this).setTitle("确认删除？")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //执行删除操作
                                    File file=new File(Environment.getExternalStorageDirectory()
                                            .getAbsolutePath() + "/.us.mooc/"+ finalViewHolder.textView.getText().toString());
                                    if(file.exists()){
                                        file.delete();
                                    }
//                                    startActivity(new Intent(getApplicationContext(),DetailsActivity.class).putExtra("videoName1",finalViewHolder.textView.getText().toString()));
                                    startActivity(new Intent(getApplicationContext(),DetailsActivity.class).putExtra("videoNameFromDown","aaaaaaaaaaaaaaaaaaaaaaaaaaa"));
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    return true;
                }
            });
            return convertView;
        }
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
            mDialog = new BaiduASRDigitalDialog(DownloadedActivity.this, params);
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
            finish();
        }
        return true;
    }

}
