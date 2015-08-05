package eiyou.us.text.download.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import eiyou.us.text.download.entities.ThreadInfo;


public class ThreadDaoImp implements ThreadDao {
	private DbHelper helper;

	public ThreadDaoImp(Context context) {
		helper = new DbHelper(context);
	}

	@Override
	public void insertThread(ThreadInfo info) {
		SQLiteDatabase db = helper.getReadableDatabase();
		db.execSQL(
				"insert into infos(thread_id,url,start,end,finished)values(?,?,?,?,?)",
				new Object[] { info.getId(), info.getUrl(), info.getStart(),
						info.getEnd(), info.getFinished() });
		db.close();
	}

	@Override
	public void deleteThread(String url, int thread_id) {
		SQLiteDatabase db = helper.getReadableDatabase();
		db.execSQL("delete from infos where url=? and thread_id=?",
				new Object[] { url, thread_id });
		db.close();
	}

	@Override
	public void updateThread(String url, int thread_id, int finished) {
		SQLiteDatabase db = helper.getReadableDatabase();
		db.execSQL("update infos set finished=? where url=? and thread_id=?",
				new Object[] { finished, url, thread_id });
		db.close();
	}

	@Override
	public List<ThreadInfo> getThreads(String url) {
		List<ThreadInfo> list=new ArrayList<ThreadInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from infos where url=?",
				new String[] { url });
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				ThreadInfo info=new ThreadInfo();
				info.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
				info.setUrl(cursor.getString(cursor.getColumnIndex("url")));
				info.setStart(cursor.getInt(cursor.getColumnIndex("start")));
				info.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
				info.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
				list.add(info);
			}
		}
		
		db.close();
		return list;
	}

	@Override
	public boolean isExists(String url, int thread_id) {
		SQLiteDatabase db = helper.getReadableDatabase();
		boolean isexists=false;
		Cursor cursor = db.rawQuery("select * from infos where url=? and thread_id=?",
				new String[] { url,thread_id+""});
		if (cursor!=null) {
			isexists=cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return false;
	}

}
