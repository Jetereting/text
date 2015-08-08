package eiyou.us.text;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;
import eiyou.us.text.communication.MyUser;

public class UserInfoActivity extends Activity {

    ImageView userAvatarImageView;
    TextView userNameTextView,userInstructionTextView,userTelTextView,userMailTextView,userHobbyTextView,userInfoChangeTextView,backTextView,buttonBackTextView;
    BmobUser bmobUser;
    MyUser myUser;

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
            userInstructionTextView.setText("个人说明："+myUser.getInstruction());
            userTelTextView.setText("联系方式："+bmobUser.getMobilePhoneNumber());
            userMailTextView.setText("个人邮箱："+bmobUser.getEmail());
            userHobbyTextView.setText("爱好： "+myUser.getHobby());
        }
        userInfoChangeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),EditInfoActivity.class));
            }
        });
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        buttonBackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser.logOut(getApplicationContext());   //清除缓存用户对象
                bmobUser = BmobUser.getCurrentUser(getApplicationContext()); // 现在的currentUser是null了
            }
        });
    }

    private void init() {
        userAvatarImageView=(ImageView)findViewById(R.id.iv_user_avatar);
        userNameTextView=(TextView)findViewById(R.id.tv_user_name);
        userInstructionTextView=(TextView)findViewById(R.id.tv_user_instruction);
        userTelTextView=(TextView)findViewById(R.id.tv_user_tel);
        userMailTextView=(TextView)findViewById(R.id.tv_user_mail);
        userHobbyTextView=(TextView)findViewById(R.id.tv_user_hobby);
        userInfoChangeTextView=(TextView)findViewById(R.id.tv_user_info_change);
        backTextView=(TextView)findViewById(R.id.tv_back);
        buttonBackTextView=(TextView)findViewById(R.id.tv_button_back);
        bmobUser = BmobUser.getCurrentUser(this);
        myUser=BmobUser.getCurrentUser(this,MyUser.class);
    }
}
