package cn.edu.zju.isst.ui.contact;

import java.io.Serializable;

import android.R.string;

public class ContactFilter  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 890650792358672364L;
	
	public int id;
	public String username;
	public String name;
	public int gender;
	public int grade;
	public int classId;
	public String major;
	public int cityId;
	public String company;
	//以下是现实的字符串
	public String genderString;
	public String cityString;
	public ContactFilter() {
		clear();
	}
	//清空条件
	public void clear() {
		id = 0;
		username = "";
		name = "";
		gender = 0;
		grade = 0;
		classId = 0;
		major = "";
		cityId = 0;
		company = "";
	}
}
