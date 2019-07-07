package net.glaso.ca.business.group.vo;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import net.glaso.ca.business.common.domain.Criteria;
import net.glaso.ca.business.user.vo.UserVo;

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

	public GroupVo() {}

	private GroupVo ( String an, String d, String gsn, String n ) {
		this.altName = an;
		this.description = n;
		this.groupSolutionName = gsn;
		this.name = n;
	}

	public static GroupVo deserialize( JSONObject body) {
		validation( body, "altName" );
		validation( body, "description" );
		validation( body, "groupSolutionName" );
		validation( body, "name" );

		return new GroupVo( body.getString( "altName"),
				body.getString( "description" ),
				body.getString( "groupSolutionName" ),
				body.getString( "name" ) );
	}

	private static boolean validation( JSONObject body, String variable ) {
		if ( body.has( variable ) ) return true;
		else throw new IllegalArgumentException( variable + " is null.");
	}

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
