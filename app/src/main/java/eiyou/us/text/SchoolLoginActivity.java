package eiyou.us.text;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;
import eiyou.us.text.communication.MyUser;
import eiyou.us.text.utils.Utils;

public class SchoolLoginActivity extends Activity {
    TextView schoolTextView,schoolIdTextView,schoolPwdTextview;
    String school,schoolId,schoolPwd;
    BmobUser bmobUser;
    MyUser myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_login);
        init();
        event();
    }

    private void init() {
        schoolTextView=(EditText)findViewById(R.id.ed_school);
        schoolIdTextView=(EditText)findViewById(R.id.ed_school_id);
        schoolPwdTextview=(EditText)findViewById(R.id.ed_school_pwd);
    }

    private void getEdit() {
        school=schoolTextView.getText().toString();
        schoolId=schoolIdTextView.getText().toString();
        schoolPwd=schoolPwdTextview.getText().toString();
    }

    private void event() {
        //返回按钮
        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //确定按钮
        findViewById(R.id.tv_ensure_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEdit();
                myUser.setSchool(school);
                myUser.setSchoolId(schoolId);
                myUser.setSchoolPwd(schoolPwd);
                myUser.update(getApplicationContext(), bmobUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Utils.toast.show(getApplicationContext(),"登录教务系统成功！");
                        startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("schoolId",schoolId));
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Utils.toast.show(getApplicationContext(),"教务系统登录失败！"+s);
                    }
                });
            }
        });
    }


}
