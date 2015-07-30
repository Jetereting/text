package eiyou.us.text;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.bmob.v3.listener.SaveListener;
import eiyou.us.text.communication.MyUser;
import eiyou.us.text.utils.Utils;

public class RegisterActivity extends Activity {
    EditText userNameEditText,userPasswordEditText,userPasswordAgainEditText;
    String userName="asdf",userPassword="23",userPasswordAgain="23";
    Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        event();
    }

    private void event() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStringFromEditText();
                if(userPassword.equals(userPasswordAgain)){
                    signUp(userName,userPassword);
                }else {
                    Utils.toast.show(getApplicationContext(),"两次密码输入不一致哦");
                }
            }
        });
    }

    private void init() {
        userNameEditText=(EditText)findViewById(R.id.ed_user_name0);
        userPasswordEditText=(EditText)findViewById(R.id.ed_user_psd0);
        userPasswordAgainEditText=(EditText)findViewById(R.id.ed_user_psd_again0);
        registerButton=(Button)findViewById(R.id.b_register);
    }

    /**
     * 注册用户
     */
    private void signUp(String userName,String userPassword) {
        final MyUser myUser = new MyUser();
        myUser.setUsername(userName);
        myUser.setPassword(userPassword);
        myUser.setAge(18);
        myUser.signUp(this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Utils.toast.show(getApplicationContext(),"注册成功:" + myUser.getUsername() + "-"
                        + myUser.getObjectId() + "-" + myUser.getCreatedAt()
                        + "-" + myUser.getSessionToken() + ",是否验证：" + myUser.getEmailVerified());
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                Utils.toast.show(getApplicationContext(),"注册失败，另起个账号名试试:" + msg);
            }
        });
    }

    public void getStringFromEditText() {
        userName=userNameEditText.getText().toString();
        userPassword=userPasswordEditText.getText().toString();
        userPasswordAgain=userPasswordAgainEditText.getText().toString();
    }
}
