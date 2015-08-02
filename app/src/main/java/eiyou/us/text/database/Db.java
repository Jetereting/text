package eiyou.us.text.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Au on 29-Jul-15.
 */
public class Db extends SQLiteOpenHelper {
    public Db(Context context) {
        super(context,"db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table [list](\"+\"" +
                "[_id] integer primary key AUTOINCREMENT,\"+\"" +
                "[pic] binary,\"+\"" +
                "[title] text,\"+\"" +
                "[content] text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
