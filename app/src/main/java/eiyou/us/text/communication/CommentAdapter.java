package eiyou.us.text.communication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eiyou.us.text.R;

/**
 * Created by Au on 8/2/2015.
 */
public class CommentAdapter extends BaseAdapter {
    private List<CommentBean> list;
    private LayoutInflater inflater;

    public CommentAdapter(Context context,List<CommentBean> commentBeanList){
        this.list=commentBeanList;
        inflater=LayoutInflater.from(context);
    }

    class ViewHolder{
        ImageView userAvatarImageView;
        TextView userNameTextView,userCommentTextView,timeTextView;
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
        if(convertView==null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.comment_item_layout, null);
            viewHolder.userAvatarImageView = (ImageView) convertView.findViewById(R.id.iv_user_avatar);
            viewHolder.userNameTextView = (TextView) convertView.findViewById(R.id.tv_user_name);
            viewHolder.userCommentTextView = (TextView) convertView.findViewById(R.id.tv_user_comment);
            viewHolder.timeTextView=(TextView)convertView.findViewById(R.id.tv_time);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.userAvatarImageView.setImageResource(list.get(position).userAvatarId);
        viewHolder.userNameTextView.setText(list.get(position).userName);
        viewHolder.userCommentTextView.setText(list.get(position).userComment);
        viewHolder.timeTextView.setText(list.get(position).time);
        return convertView;
    }
}
