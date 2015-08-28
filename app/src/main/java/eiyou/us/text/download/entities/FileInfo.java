package eiyou.us.text.download.entities;

import java.io.Serializable;

public class FileInfo implements Serializable {
	private int id;
	private String url;
	private String filename;
	private int length;
	private int finished;
	private String videoIcon,videoContent;

	public String getVideoContent() {
		return videoContent;
	}

	public void setVideoContent(String videoContent) {
		this.videoContent = videoContent;
	}

	public String getVideoIcon() {
		return videoIcon;
	}

	public void setVideoIcon(String videoIcon) {
		this.videoIcon = videoIcon;
	}

	public FileInfo() {
		super();
	}

	public FileInfo(int id, String url, String filename,String videoIcon,String videoContent, int length,
			int finished) {
		super();
		this.id = id;
		this.url = url;
		this.filename = filename;
		this.length = length;
		this.finished = finished;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", url=" + url + ", filename=" + filename
				+ ", length=" + length + ", finished=" + finished + "]";
	}

}
