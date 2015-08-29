package eiyou.us.text;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;
import eiyou.us.text.communication.MyUser;
import eiyou.us.text.utils.Utils;
import eiyou.us.text.yuyin.Config;
import eiyou.us.text.yuyin.Constants;

public class EditInfoActivity extends Activity {
    TextView cancelTextView,saveTextView;
    EditText userNameEditText,userInstructionEditText,userTelEditText,userMailEditText,userHobbyEditText;
    String userInstruction,userTel,userMail,userHobby;
    BmobUser bmobUser;
    MyUser myUser;
    //语音
    private BaiduASRDigitalDialog mDialog = null;
    private DialogRecognitionListener mRecognitionListener;
    private int mCurrentTheme = Config.DIALOG_THEME;

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
                startActivity(new Intent(getApplicationContext(), UserInfoActivity.class));
            }
        });
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInstruction = userInstructionEditText.getText().toString();
                userTel = userTelEditText.getText().toString();
                userMail = userMailEditText.getText().toString();
                userHobby = userHobbyEditText.getText().toString();

                myUser.setInstruction(userInstruction);
                myUser.setMobilePhoneNumber(userTel);
                myUser.setEmail(userMail);
                myUser.setHobby(userHobby);
                myUser.update(getApplicationContext(), bmobUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Utils.toast.show(getApplicationContext(), "更新成功！");
                        startActivity(new Intent(getApplicationContext(), UserInfoActivity.class));
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Utils.toast.show(getApplicationContext(), "更新失败！" + s);
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
        myUser=BmobUser.getCurrentUser(this,MyUser.class);
        userInstructionEditText.setText(myUser.getInstruction());
        userHobbyEditText.setText(myUser.getHobby());
        userTelEditText.setText(myUser.getMobilePhoneNumber());
        userMailEditText.setText(myUser.getEmail());
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
            mDialog = new BaiduASRDigitalDialog(EditInfoActivity.this, params);
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
