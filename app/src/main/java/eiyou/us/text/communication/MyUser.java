package eiyou.us.text.communication;

import cn.bmob.v3.BmobUser;

public class MyUser extends BmobUser {
	
	/**  
	 *  
	 */  
	
	private static final long serialVersionUID = 1L;
	
	private String instruction,hobby,school,schoolId,schoolPwd;

	public String getInstruction() {
		return instruction;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getSchoolPwd() {
		return schoolPwd;
	}

	public void setSchoolPwd(String schoolPwd) {
		this.schoolPwd = schoolPwd;
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
