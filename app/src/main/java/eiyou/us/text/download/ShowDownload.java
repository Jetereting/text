package eiyou.us.text.download;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Au on 2015/8/10.
 */
public class ShowDownload {

    public List<String> show(String videoPath) {
        List<String> list = new ArrayList<>();

        File file = new File(videoPath);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                list.add(f.getName());
            }
        }

        return list;
    }

    public List<String> showPath(String videoPath) {
        List<String> list = new ArrayList<>();

        File file = new File(videoPath);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                list.add(f.getPath());
            }
        }
        return list;
    }
    public static Boolean isExist(String filePath,String fileName){
        boolean is=false;
        File file=new File(filePath);
        File[] files=file.listFiles();
        for(File file1 :files){
            if(fileName.equals(file1.getName())){
                is=true;
            }
        }
        return is;
    }
}
