package eiyou.us.text;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;

public class UserInfoActivity extends Activity {

    ImageView userAvatarImageView;
    TextView userNameTextView,userSignTextView,userJobTextView;
    BmobUser bmobUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        init();
        event();
    }

    private void event() {
        if(bmobUser!=null){
            userNameTextView.setText("用户名："+bmobUser.getUsername());
        }
    }

    private void init() {
        userAvatarImageView=(ImageView)findViewById(R.id.iv_user_avatar);
        userNameTextView=(TextView)findViewById(R.id.tv_user_name);
        userSignTextView=(TextView)findViewById(R.id.tv_user_sign);
        userJobTextView=(TextView)findViewById(R.id.tv_user_job);
        bmobUser = BmobUser.getCurrentUser(this);
    }
}
