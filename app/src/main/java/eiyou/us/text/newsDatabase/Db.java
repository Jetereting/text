package eiyou.us.text.newsDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Au on 29-Jul-15.
 */
public class Db extends SQLiteOpenHelper {
    private static final String SQL_CREATE = "create table if not exists news(_id integer primary key autoincrement,"
            + "pic blob,title text,content text,videoUrl text)";
    private static final String SQL_DROP = "drop table if exists news";
    public Db(Context context) {
        super(context,"news",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }
}
