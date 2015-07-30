package eiyou.us.text.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Au on 24-Jul-15.
 */
public class Utils {
    public static class sharedPreferences extends Activity {
        static SharedPreferences openTimes;
        public static void putInt(Context context,String key, int value) {
            openTimes=context.getSharedPreferences("openTimes", 0);
            SharedPreferences.Editor editor = openTimes.edit();
            editor.putInt(key, value);
            editor.commit();
        }
        public static int getInt(String key, int value) {
            return openTimes.getInt(key, value);
        }

    }
    public static class toast{
        public static void show(Context context, String message)
        {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}



