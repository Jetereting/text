package eiyou.us.text;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

import java.util.ArrayList;

import cn.bmob.v3.listener.SaveListener;
import eiyou.us.text.communication.MyUser;
import eiyou.us.text.utils.Utils;
import eiyou.us.text.yuyin.Config;
import eiyou.us.text.yuyin.Constants;

public class RegisterActivity extends Activity {
    EditText userNameEditText,userPasswordEditText,userPasswordAgainEditText;
    String userName,userPassword,userPasswordAgain;
    Button registerButton;
    //语音
    private BaiduASRDigitalDialog mDialog = null;
    private DialogRecognitionListener mRecognitionListener;
    private int mCurrentTheme = Config.DIALOG_THEME;
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
                if (userPassword.equals(userPasswordAgain)) {
                    signUp(userName, userPassword);
                } else {
                    Utils.toast.show(getApplicationContext(), "两次密码输入不一致哦");
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
        myUser.signUp(this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
//                Utils.toast.show(getApplicationContext(),"注册成功:" + myUser.getUsername() + "-"
//                        + myUser.getObjectId() + "-" + myUser.getCreatedAt()
//                        + "-" + myUser.getSessionToken() + ",是否验证：" + myUser.getEmailVerified());
                Utils.toast.show(getApplicationContext(),"注册成功");

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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
            mDialog = new BaiduASRDigitalDialog(RegisterActivity.this, params);
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
