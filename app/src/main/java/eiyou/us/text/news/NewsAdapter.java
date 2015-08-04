package eiyou.us.text.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eiyou.us.text.R;
import eiyou.us.text.image.ImageLoader;

/**
 * Created by Au on 22-Jul-15.
 */
public class NewsAdapter extends BaseAdapter {
    private List<NewsBean> list;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;

    public NewsAdapter(Context context,List<NewsBean> data){
        list=data;
        inflater=LayoutInflater.from(context);
        imageLoader=new ImageLoader();
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
        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.main_item_layout,null);
            viewHolder.ivIcon=(ImageView)convertView.findViewById(R.id.iv_icon);
            viewHolder.tvTitle=(TextView)convertView.findViewById(R.id.tv_title);
            viewHolder.tvContent=(TextView)convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        //异步加载图片
        viewHolder.ivIcon.setImageResource(R.mipmap.ic_launcher);
        String iconUrl=list.get(position).newsIconUrl;
        viewHolder.ivIcon.setTag(iconUrl);
        imageLoader.showImageByAsyncTask(viewHolder.ivIcon,iconUrl);

        viewHolder.tvTitle.setText(list.get(position).newsTitle);
        viewHolder.tvContent.setText(list.get(position).newsContent);
        return convertView;
    }
    class ViewHolder{
        public TextView tvTitle,tvContent;
        public ImageView ivIcon;
    }
}
