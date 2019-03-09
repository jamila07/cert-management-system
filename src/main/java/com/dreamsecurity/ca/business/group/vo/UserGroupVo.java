package com.dreamsecurity.ca.business.group.vo;

import java.util.Date;

public class UserGroupVo {
	private String userId;
	private int groupId;
	private Date joinDate;
	private int userAuthority;
	private int state;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public Date getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
	public int getUserAuthority() {
		return userAuthority;
	}
	public void setUserAuthority(int userAuthority) {
		this.userAuthority = userAuthority;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
}
