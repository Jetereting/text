package eiyou.us.text.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.io.File;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Au on 24-Jul-15.
 */
public class Utils {
    public static class sharedPreferences extends Activity {
        static SharedPreferences openTimes,whichClass;
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

    public static class FileUtils {
        private static final String DOWNLOAD_DIR = "download";

        public static final File getDownloadDir(Context context) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return new File(context.getExternalCacheDir(), DOWNLOAD_DIR);
            }
            return new File(context.getCacheDir(), DOWNLOAD_DIR);
        }

        public static final String getPrefix(@NonNull String fileName) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }

        public static final String getSuffix(@NonNull String fileName) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
    }

}



