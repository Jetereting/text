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

public class EditInfoActivity extends Activity {
    TextView cancelTextView,saveTextView;
    EditText userNameEditText,userInstructionEditText,userTelEditText,userMailEditText,userHobbyEditText;
    String userInstruction,userTel,userMail,userHobby;
    BmobUser bmobUser;
    MyUser myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        init();
        even();
    }

    private void even() {
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UserInfoActivity.class));
            }
        });
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInstruction=userInstructionEditText.getText().toString();
                userTel=userTelEditText.getText().toString();
                userMail=userMailEditText.getText().toString();
                userHobby=userHobbyEditText.getText().toString();

                myUser.setInstruction(userInstruction);
                myUser.setMobilePhoneNumber(userTel);
                myUser.setEmail(userMail);
                myUser.setHobby(userHobby);
                myUser.update(getApplicationContext(), bmobUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Utils.toast.show(getApplicationContext(),"更新成功！");
                        startActivity(new Intent(getApplicationContext(), UserInfoActivity.class));
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Utils.toast.show(getApplicationContext(),"更新失败！"+s);
                    }
                });
            }
        });
    }

    private void init() {
        cancelTextView=(TextView)findViewById(R.id.tv_cancel);
        saveTextView=(TextView)findViewById(R.id.tv_save);
        userNameEditText=(EditText)findViewById(R.id.ed_user_name);
        userInstructionEditText=(EditText)findViewById(R.id.ed_user_instruction);
        userHobbyEditText=(EditText)findViewById(R.id.ed_user_hobby);
        userTelEditText=(EditText)findViewById(R.id.ed_user_tel);
        userMailEditText=(EditText)findViewById(R.id.ed_user_mail);
        myUser=new MyUser();
        bmobUser=BmobUser.getCurrentUser(this);
    }
}
