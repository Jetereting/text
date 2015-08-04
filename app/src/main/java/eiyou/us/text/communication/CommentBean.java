package eiyou.us.text.communication;

import java.io.File;

/**
 * Created by Au on 8/2/2015.
 */
public class CommentBean {
    public int userAvatarId;
    public String userName;
    public String userComment;
    public String time;
    public CommentBean(int userAvatarId,String userName,String userComment,String time){
        this.userAvatarId=userAvatarId;
        this.userComment=userComment;
        this.userName=userName;
        this.time=time;
    }
}
