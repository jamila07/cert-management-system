package com.dreamsecurity.ca.business.group.vo;

import java.util.Date;
import java.util.List;

import com.dreamsecurity.ca.business.common.domain.Criteria;
import com.dreamsecurity.ca.business.user.vo.UserVo;

public class GroupVo {
	private int id;
	private String name;
	private String altName;
	private Date createDate;
	private String creator;
	private int state;
	private String description;
	private List<GroupSolutionVo> groupSolutionVo;
	private Criteria cri;
	
	// JOIN
	private UserVo userVo;
	
	// JSP to Controller
	private String groupSolutionName;
	
	public UserVo getUserVo() {
		return userVo;
	}
	public void setUserVo(UserVo userVo) {
		this.userVo = userVo;
	}
	public String getGroupSolutionName() {
		return groupSolutionName;
	}
	public void setGroupSolutionName(String groupSolutionName) {
		this.groupSolutionName = groupSolutionName;
	}
	public Criteria getCri() {
		return cri;
	}
	public void setCri(Criteria cri) {
		this.cri = cri;
	}
	public List<GroupSolutionVo> getGroupSolutionVo() {
		return groupSolutionVo;
	}
	public void setGroupSolutionVo(List<GroupSolutionVo> groupSolutionVo) {
		this.groupSolutionVo = groupSolutionVo;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAltName() {
		return altName;
	}
	public void setAltName(String altName) {
		this.altName = altName;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}	
