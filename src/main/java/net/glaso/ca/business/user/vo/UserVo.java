package net.glaso.ca.business.user.vo;

import java.util.Date;

public class UserVo {
	private String id;
	private String name;
	private Date addDate;
	private String departTeam;
	private String jobLevel;
	private String eMail;
	private String password;
	private int state;
	private String sha256Pw;
	private String loginId;
	private String loginPw;
	
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getLoginPw() {
		return loginPw;
	}
	public void setLoginPw(String loginPw) {
		this.loginPw = loginPw;
	}
	public String getSha256Pw() {
		return sha256Pw;
	}
	public void setSha256Pw(String sha256Pw) {
		this.sha256Pw = sha256Pw;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getAddDate() {
		return addDate;
	}
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
	public String getDepartTeam() {
		return departTeam;
	}
	public void setDepartTeam(String departTeam) {
		this.departTeam = departTeam;
	}
	public String getJobLevel() {
		return jobLevel;
	}
	public void setJobLevel(String jobLevel) {
		this.jobLevel = jobLevel;
	}
	public String geteMail() {
		return eMail;
	}
	public void seteMail(String eMail) {
		this.eMail = eMail;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
}
