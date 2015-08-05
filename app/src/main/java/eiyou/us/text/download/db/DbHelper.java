package eiyou.us.text.download.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	private static String DB_NAME = "down.db";
	private static int VERSION = 1;
	private static final String SQL_CREATE = "create table if not exists infos(_id integer primary key autoincrement,"
			+ "thread_id integer,url text,start integer,end integer,finished integer)";
	private static final String SQL_DROP = "drop table if exists infos";

	public DbHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
             db.execSQL(SQL_DROP);
             db.execSQL(SQL_CREATE);
	}

}
