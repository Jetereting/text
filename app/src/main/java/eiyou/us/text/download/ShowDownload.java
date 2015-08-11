package eiyou.us.text.download;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Au on 2015/8/10.
 */
public class ShowDownload {
    public List<String> show(String videoPath){
        List<String> list =new ArrayList<>();

        File file=new File(videoPath);
        File[] files=file.listFiles();
        for(int i=0;i<files.length;i++){
            File f=files[i];
            list.add(f.getName());
        }
        return list;
    }
    public List<String> showPath(String videoPath){
        List<String> list =new ArrayList<>();

        File file=new File(videoPath);
        File[] files=file.listFiles();
        for(int i=0;i<files.length;i++){
            File f=files[i];
            list.add(f.getName());
        }
        return list;
    }
}
