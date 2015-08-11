package eiyou.us.text;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import eiyou.us.text.download.ShowDownload;

public class DownloadedActivity extends Activity {
    ListView listView;
    ShowDownload showDownload;
    List<String> list,pathList;
    String videoPath;

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
        MyAdapter adapter=new MyAdapter();
        listView.setAdapter(adapter);

        //返回
        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                    startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(videoPath),"video/mp4"));
                }
            });
            return convertView;
        }
    }
    class ViewHolder{
        TextView textView;
    }
}
