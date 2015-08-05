package eiyou.us.text.download.db;

import java.util.List;


import eiyou.us.text.download.entities.ThreadInfo;

public interface ThreadDao {
	public void insertThread(ThreadInfo info);

	public void deleteThread(String url, int thread_id);

	public void updateThread(String url, int thread_id, int finished);

	public List<ThreadInfo> getThreads(String url);

	public boolean isExists(String url, int thread_id);
}
