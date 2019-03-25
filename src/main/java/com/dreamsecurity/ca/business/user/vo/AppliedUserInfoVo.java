package com.dreamsecurity.ca.business.user.vo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class AppliedUserInfoVo {
	@JsonInclude(Include.NON_NULL)
	private int seqId;
	
	private String userId;
	private String password;
	private String name;
	@JsonInclude(Include.NON_NULL)
	private Date addDate;
	private String departTeam;
	private String jobLevel;
	private String eMail;
	@JsonInclude(Include.NON_NULL)
	private boolean groupCreator;
	@JsonInclude(Include.NON_NULL)
	private int groupId;
	@JsonInclude(Include.NON_NULL)
	private String groupName;
	@JsonInclude(Include.NON_NULL)
	private String solutionName;
	@JsonInclude(Include.NON_NULL)
	private String groupDescription;
	@JsonInclude(Include.NON_NULL)
	private int state;
	
	private Map<String, Object> dynamicParam = new HashMap<String, Object>();
	
	@JsonAnySetter
	public void setDynamicParam( String key, Object value ) {
		dynamicParam.put( key, value );
	}
	
	public Map<String, Object> getDynamicParam() {
		return dynamicParam;
	}
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Date getAddDate() {
		return addDate;
	}
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
	public int getSeqId() {
		return seqId;
	}
	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public boolean getGroupCreator() {
		return groupCreator;
	}
	public void setGroupCreator(boolean groupCreator) {
		this.groupCreator = groupCreator;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public String getSolutionName() {
		return solutionName;
	}
	public void setSolutionName(String solutionName) {
		this.solutionName = solutionName;
	}
	public String getGroupDescription() {
		return groupDescription;
	}
	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
}
