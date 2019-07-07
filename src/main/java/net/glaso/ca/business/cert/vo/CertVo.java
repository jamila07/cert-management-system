package net.glaso.ca.business.cert.vo;

import java.util.Date;

import net.glaso.ca.business.group.vo.GroupVo;
import org.json.JSONObject;

public class CertVo {
	private int id;
	private int serialNumber;
	private byte[] file;
	private Date issuingRequestDate;
	private Date startDate;
	private Date endDate;
	private String issuer;
	private String subject;
	private byte[] subjectDn;
	private String description;
	private int keyId;
	private int type;
	private int ouType;

	// Join
	private KeyVo keyVo;
	private GroupVo groupVo;

	// JSP TO Controller : JSON
	private String citeName;
	private String citeLocality;
	private String citeProvince;
	private String citeDomain;
	private int groupId;
	private String groupSolutionName;

	public CertVo() {}

	private CertVo( String cd, String cl, String cn, String cp, String d, int gi, String gsn, int t ) {
		this.citeDomain = cd;
		this.citeLocality = cl;
		this.citeName = cn;
		this.citeProvince = cp;
		this.description = d;
		this.groupId = gi;
		this.groupSolutionName = gsn;
		this.type = t;
	}

	public static CertVo deserialize( JSONObject body ) {
		validation(body, "citeDomain");
		validation(body, "citeLocality");
		validation(body, "citeName");
		validation(body, "citeProvince");
		validation(body, "description");
		validation(body, "groupId");
		validation(body, "groupSolutionName");
		validation(body, "type");

		return new CertVo( body.getString( "citeDomain"),
				body.getString( "citeLocality"),
				body.getString( "citeName"),
				body.getString( "citeProvince"),
				body.getString( "description"),
				body.getInt( "groupId"),
				body.getString( "groupSolutionName"),
				body.getInt( "type") );
	}

	private static boolean validation( JSONObject body, String variable ) {
		if ( body.has( variable ) ) return true;
		else throw new IllegalArgumentException( variable + " is null.");
	}

	public GroupVo getGroupVo() {
		return groupVo;
	}
	public void setGroupVo(GroupVo groupVo) {
		this.groupVo = groupVo;
	}
	public String getGroupSolutionName() {
		return groupSolutionName;
	}
	public void setGroupSolutionName(String groupSolutionName) {
		this.groupSolutionName = groupSolutionName;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public String getCiteLocality() {
		return citeLocality;
	}
	public void setCiteLocality(String citeLocality) {
		this.citeLocality = citeLocality;
	}
	public String getCiteProvince() {
		return citeProvince;
	}
	public void setCiteProvince(String citeProvince) {
		this.citeProvince = citeProvince;
	}
	public String getCiteDomain() {
		return citeDomain;
	}
	public void setCiteDomain(String citeDomain) {
		this.citeDomain = citeDomain;
	}
	public int getOuType() {
		return ouType;
	}
	public void setOuType(int ouType) {
		this.ouType = ouType;
	}
	public KeyVo getKeyVo() {
		return keyVo;
	}
	public void setKeyVo(KeyVo keyVo) {
		this.keyVo = keyVo;
	}
	public String getCiteName() {
		return citeName;
	}
	public void setCiteName(String citeName) {
		this.citeName = citeName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}
	public Date getIssuingRequestDate() {
		return issuingRequestDate;
	}
	public void setIssuingRequestDate(Date issuingRequestDate) {
		this.issuingRequestDate = issuingRequestDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public byte[] getSubjectDn() {
		return subjectDn;
	}
	public void setSubjectDn(byte[] subjectDn) {
		this.subjectDn = subjectDn;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getKeyId() {
		return keyId;
	}
	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
