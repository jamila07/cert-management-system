package net.glaso.ca.business.user.vo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class AppliedUserInfoVo {
	private int seqId;
	private String userId;
	private String password;
	private String name;
	private Date addDate;
	private String departTeam;
	private String jobLevel;
	private String eMail;
	private boolean groupCreator;
	private int groupId;
	private String groupName;
	private String solutionName;
	private String groupDescription;
	private int state;

	private AppliedUserMailVo appliedUserMailVo;

	public AppliedUserInfoVo() {}
	
	private AppliedUserInfoVo( String dt, String em, String jl, String n, String p, String ui, boolean gc, int gi, String gd, String sn, String gn ) {
		this.departTeam = dt;
		this.eMail = em;
		this.jobLevel = jl;
		this.name = n;
		this.password = p;
		this.userId = ui;
		this.groupCreator = gc;
		this.groupId = gi;
		this.groupDescription = gd;
		this.solutionName = sn;
		this.groupName = gn;
	}
	
	public static AppliedUserInfoVo deserialize( JSONObject body ) {
		validation(body, "departTeam");
		validation(body, "eMail");
		validation(body, "groupCreator");
		validation(body, "jobLevel");
		validation(body, "name");
		validation(body, "password");
		validation(body, "userId");
		
		boolean groupCrator = false;;
		if ( body.getString( "groupCreator" ).equals( "true" ) ) groupCrator = true;
		
		return new AppliedUserInfoVo( body.getString( "departTeam" ),
				body.getString( "eMail" ), 
				body.getString( "jobLevel" ),
				body.getString( "name" ),
				body.getString( "password" ),
				body.getString( "userId" ),
				groupCrator,
				body.has( "groupId" ) ? body.getInt( "groupId" ) : 0,
				body.has( "groupDescription") ? body.getString( "groupDescription" ) : null,
				body.has( "solutionName") ? body.getString( "solutionName" ) : null,
				body.has( "groupName" ) ? body.getString( "groupName" ) : null );
				
	}
	
	private static boolean validation( JSONObject body, String variable ) {
		if ( body.has( variable ) ) return true;
		else throw new IllegalArgumentException( variable + " is null.");
	}

	public AppliedUserMailVo getAppliedUserMailVo() {
		return appliedUserMailVo;
	}
	public void setAppliedUserMailVo(AppliedUserMailVo appliedUserMailVo) {
		this.appliedUserMailVo = appliedUserMailVo;
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
