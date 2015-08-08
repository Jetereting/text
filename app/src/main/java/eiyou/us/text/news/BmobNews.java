package eiyou.us.text.news;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Au on 8/8/2015.
 */
public class BmobNews extends BmobObject {
    public BmobFile getNewsIcon() {
        return newsIcon;
    }

    public void setNewsIcon(BmobFile newsIcon) {
        this.newsIcon = newsIcon;
    }

    BmobFile newsIcon;
    String newsTitle;
    String newsContent;
    String newsVideoUrl;

    public String getNewsVideoUrl() {
        return newsVideoUrl;
    }

    public void setNewsVideoUrl(String newsVideoUrl) {
        this.newsVideoUrl = newsVideoUrl;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }






}
