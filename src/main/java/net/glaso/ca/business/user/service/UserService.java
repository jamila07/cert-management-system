package net.glaso.ca.business.user.service;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.glaso.ca.business.group.dao.GroupDao;
import net.glaso.ca.business.group.vo.GroupSolutionVo;
import net.glaso.ca.business.group.vo.GroupVo;
import net.glaso.ca.business.user.vo.AppliedUserInfoVo;
import net.glaso.ca.business.user.vo.UserVo;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import net.glaso.ca.business.common.CommonConstants;
import net.glaso.ca.business.user.dao.UserDao;
import net.glaso.ca.framework.utils.CaUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class UserService {
	
	@Resource
	private UserDao userDao;
	
	@Resource
	private GroupDao groupDao;
		
	public void registerAppliedUser( HttpServletRequest request ) throws JsonParseException, JsonMappingException, IOException, NoSuchAlgorithmException {
		JSONObject body = (JSONObject) request.getAttribute( "body" );
		AppliedUserInfoVo vo = AppliedUserInfoVo.deserialize( body );
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		
		vo.setAddDate( new Date() );
		vo.setState( 0 );
		vo.setPassword( CaUtils.convertByteArrayToHexString( messageDigest.digest( vo.getPassword().getBytes() ) ) );

		userDao.insertAplliedUser( vo );
	}
	
	public UserVo selectOneUser(UserVo vo ) {
		return userDao.selectOneUser( vo );
	}
	
	public List<Map<String, Object>> showList( HttpServletRequest request ) {
		List<Map<String, Object>> voMapList = userDao.selectUserList();
		
		for ( Map<String, Object> voMap : voMapList ) {
			voMap.put( "adddate", CommonConstants.dateFormat.format( (Date)voMap.get( "adddate" ) ) );
		}
		
		return voMapList;
	}
	
	public boolean chkOverlapUser( String userId ) {
		UserVo vo = new UserVo();
		
		vo.setId( userId );
		
		if ( userDao.selectUserOne4ChkOverlap( vo ) != null ) return false;
		if ( userDao.selectAppliedUserOne4ChkOverlap( vo ) != null ) return false;
		
		return true;
	}
	
	public Map<Integer, Map<String, List<String>>> showJoinedGroup( HttpServletRequest request, String userId ) {
		List<GroupVo> groupVoList = groupDao.selectJoinedGroupList( userId );
		
//		"data":{
//			1:{	"dev2":["magickms","magicdb"]},
//			2:{ "dev1":["sso", "tsa"]}
//		}
		Map<Integer, Map<String, List<String>>> groupInfo = new HashMap<>();
		Map<String, List<String>> solutionInfo;
		for ( GroupVo groupVo : groupVoList ) {
			solutionInfo = new HashMap<>();
			
			for ( GroupSolutionVo groupSoltuionVo : groupVo.getGroupSolutionVo() ) {
				if ( groupInfo.get( groupVo.getId() ) == null ) {
					List<String> solutionNameList = new ArrayList<>();
					solutionNameList.add( groupSoltuionVo.getSolutionName() );
					
					solutionInfo.put( groupVo.getName(), solutionNameList );
					groupInfo.put( groupVo.getId(), solutionInfo );
				} else {
					groupInfo.get( groupVo.getId() ).get( groupVo.getName() ).add( groupSoltuionVo.getSolutionName() );
				}
			}
		}
		
		return groupInfo;
	}
}
