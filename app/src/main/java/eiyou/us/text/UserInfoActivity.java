package eiyou.us.text;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import eiyou.us.text.communication.MyUser;
import eiyou.us.text.yuyin.Config;
import eiyou.us.text.yuyin.Constants;

public class UserInfoActivity extends Activity {

    ImageView userAvatarImageView;
    TextView userNameTextView,userInstructionTextView,userTelTextView,userMailTextView,userHobbyTextView,userInfoChangeTextView,backTextView,buttonBackTextView;
    BmobUser bmobUser;
    MyUser myUser;
    //语音
    private BaiduASRDigitalDialog mDialog = null;
    private DialogRecognitionListener mRecognitionListener;
    private int mCurrentTheme = Config.DIALOG_THEME;

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
                startActivity(new Intent(getApplicationContext(), EditInfoActivity.class));
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
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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
            mDialog = new BaiduASRDigitalDialog(UserInfoActivity.this, params);
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
