package eiyou.us.text.communication;

import cn.bmob.v3.BmobUser;

public class MyUser extends BmobUser {
	
	/**  
	 *  
	 */  
	
	private static final long serialVersionUID = 1L;
	
	private String sign;

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	private String job;
}
