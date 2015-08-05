package eiyou.us.text.download.service;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import eiyou.us.text.download.entities.FileInfo;


public class DownLoadService extends Service {
	public static String DOWNLOAD_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/.us.mooc/";
	private String TAG = DownLoadService.class.getSimpleName();
	public final static String ACTION_START = "ACTION_START";
	public final static String ACTION_STOP = "ACTION_STOP";
	public final static String ACTION_UPDATE = "ACTION_UPDATE";
	public final static int MSG_INIT = 0;
	
	private DownLoadTask task=null;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		FileInfo fileInfo;
		if (intent.getAction().equals(ACTION_START)) {
			fileInfo = (FileInfo) intent.getSerializableExtra("fileinfo");
			new InitThread(fileInfo).start();
			Log.e(TAG, "start:" + fileInfo);
		} else if (intent.getAction().equals(ACTION_STOP)) {
			fileInfo = (FileInfo) intent.getSerializableExtra("fileinfo");
			Log.e(TAG, "stop:" + fileInfo);
			if (task!=null) {
				task.mIsPause=true;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_INIT:
               FileInfo fileInfo=(FileInfo) msg.obj;
               Log.e("MSG_INIT", fileInfo.toString());
               //启动下载任务
               task=new DownLoadTask(DownLoadService.this, fileInfo);
               task.download();
				break;

			default:
				break;
			}
		}
	};

	class InitThread extends Thread {
		private FileInfo info;

		public InitThread(FileInfo info) {
			this.info = info;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null;
			RandomAccessFile raf = null;
			try {
				URL url = new URL(info.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setDoInput(true);
				conn.setRequestMethod("GET");
				int len = -1;
				if (conn.getResponseCode() == HttpStatus.SC_OK) {
					len = conn.getContentLength();
				}
				if (len <= 0) {
					return;
				}
				File temp = new File(DOWNLOAD_PATH);
				if (!temp.exists()) {
					temp.mkdirs();
				}

				File file = new File(temp, info.getFilename());
				raf = new RandomAccessFile(file, "rwd");
				// 设置文件长度
				raf.setLength(len);
				info.setLength(len);
				Message msg = mHandler.obtainMessage(MSG_INIT, info);
				mHandler.sendMessage(msg);
			} catch (Exception e) {
			Log.e(TAG, e.toString());
			} finally {  
				try {
					raf.close();
					conn.disconnect();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
