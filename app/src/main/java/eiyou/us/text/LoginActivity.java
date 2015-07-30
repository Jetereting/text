package eiyou.us.text;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import eiyou.us.text.utils.Utils;

public class LoginActivity extends Activity {
    EditText userNameEditText,userPasswordEditText;
    String usrName,userPassword;
    Button loginButton;
    TextView forgetPsdTextView,newUserTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        getStringFromEditText();
        event();
    }

    private void event() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(usrName,userPassword);
            }
        });
        forgetPsdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        newUserTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });
    }

    private void getStringFromEditText() {
        usrName=userNameEditText.getText().toString();
        userPassword=userPasswordEditText.getText().toString();
    }

    /**
     * 登陆用户
     */
    private void login(String userName,String userPassword) {
        final BmobUser bu2 = new BmobUser();
        bu2.setUsername(userName);
        bu2.setPassword(userPassword);
        bu2.login(this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Utils.toast.show(getApplicationContext(),bu2.getUsername() + "登陆成功");
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                Utils.toast.show(getApplicationContext(),"登陆失败:" + msg);
            }
        });
    }
    private void init() {
        userNameEditText=(EditText)findViewById(R.id.ed_user_name);
        userPasswordEditText=(EditText)findViewById(R.id.ed_user_psd);
        loginButton=(Button)findViewById(R.id.b_login);
        forgetPsdTextView=(TextView)findViewById(R.id.tv_forget_psd);
        newUserTextView=(TextView)findViewById(R.id.tv_new_user);
    }
}
