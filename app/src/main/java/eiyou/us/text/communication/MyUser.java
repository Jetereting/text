package eiyou.us.text.communication;

import cn.bmob.v3.BmobUser;

public class MyUser extends BmobUser {
	
	/**  
	 *  
	 */  
	
	private static final long serialVersionUID = 1L;
	
	private String instruction,hobby;

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}
}
