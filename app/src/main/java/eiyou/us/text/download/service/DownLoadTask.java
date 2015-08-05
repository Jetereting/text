package eiyou.us.text.download.service;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.content.Intent;

import eiyou.us.text.download.db.ThreadDao;
import eiyou.us.text.download.db.ThreadDaoImp;
import eiyou.us.text.download.entities.FileInfo;
import eiyou.us.text.download.entities.ThreadInfo;

public class DownLoadTask {
	private Context context;
	private FileInfo mFileInfo;
	private ThreadDao mDao;
	private int finished = 0;
	public static boolean mIsPause = false;

	public DownLoadTask(Context context, FileInfo mFileInfo) {
		this.context = context;
		this.mFileInfo = mFileInfo;
		mDao = new ThreadDaoImp(context);
	}

	public void download() {
		// 读取数据库线程信息
		List<ThreadInfo> infos = mDao.getThreads(mFileInfo.getUrl());
		ThreadInfo info;
		if (infos.size() == 0) {
			info = new ThreadInfo(0, mFileInfo.getUrl(), 0,
					mFileInfo.getLength(), 0);
		} else {
			info = infos.get(0);
		}
		// 创建子线程进行下载
		new DownLoadThread(info).start();
	}

	class DownLoadThread extends Thread {
		private ThreadInfo info;

		public DownLoadThread(ThreadInfo info) {
			this.info = info;
		}

		@Override
		public void run() {
			// 向数据库插入线程信息
			if (!mDao.isExists(info.getUrl(), info.getId())) {
				mDao.insertThread(info);
			}
			HttpURLConnection conn = null;
			RandomAccessFile raf = null;
			InputStream in = null;
			try {
				URL url = new URL(info.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(3000);
				conn.setDoInput(true);
				conn.setRequestMethod("GET");
				// 设置下载位置
				int start = info.getStart() + info.getFinished();
				conn.setRequestProperty("Range",
						"bytes=" + start + "-" + info.getEnd());
				// 设置文件写入位置
				File file = new File(DownLoadService.DOWNLOAD_PATH,
						mFileInfo.getFilename());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start);
				Intent intent = new Intent(DownLoadService.ACTION_UPDATE);
				finished += info.getFinished();
				// 开始下载
				if (conn.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
					// 读取数据

					in = conn.getInputStream();
					int len = -1;
					byte[] b = new byte[1024 * 4];
					long time = System.currentTimeMillis();
					while ((len = in.read(b)) != -1) {
						// 写入文件
						raf.write(b, 0, len);
						// 把下载进度通过广播发送给Activity
						finished += len;
						if (System.currentTimeMillis() - time > 500) {
							time = System.currentTimeMillis();
							intent.putExtra("finished", finished * 100
									/ mFileInfo.getLength());
							context.sendBroadcast(intent);
						}
						// 在下载暂停时，保存下载进度
						if (mIsPause) {
							mDao.updateThread(info.getUrl(), info.getId(),
									finished);
							return;
						}
					}
					// 删除线程信息
					mDao.deleteThread(info.getUrl(), info.getId());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					raf.close();
					in.close();
					conn.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
