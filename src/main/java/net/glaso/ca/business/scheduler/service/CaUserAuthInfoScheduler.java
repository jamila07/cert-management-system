package net.glaso.ca.business.scheduler.service;

import net.glaso.ca.business.user.dao.UserDao;
import net.glaso.ca.business.user.vo.AppliedUserInfoVo;
import net.glaso.ca.framework.init.CaSettings;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class CaUserAuthInfoScheduler extends CaScheduler {

	private Logger logger = Logger.getLogger( CaUserAuthInfoScheduler.class );

	private final UserDao userDao;

	@Autowired
	public CaUserAuthInfoScheduler( ThreadPoolTaskScheduler taskScheduler, UserDao userDao ) {
		super( taskScheduler );
		this.userDao = userDao;
	}

	@Override
	public Runnable runner() {
		return new Thread(() -> {

			logger.info( "start update user's sign up info.");

			List<AppliedUserInfoVo> volist = userDao.selectAppliedUserMailList( 1 );
			long currentTime = new Date().getTime();
			List<Integer> mailSeqIdList = new ArrayList<>();
			List<Integer> userSeqIdList = new ArrayList<>();
			HashMap<String, Object> paramMap = new HashMap<>();

			for ( AppliedUserInfoVo vo : volist ) {
				if ( vo.getAppliedUserMailVo().getExpiredDate().getTime() < currentTime ) {
					userSeqIdList.add( vo.getSeqId() );
					mailSeqIdList.add( vo.getAppliedUserMailVo().getSeqId() );
				}
			}

			if ( mailSeqIdList.isEmpty() ) return;

			paramMap.put( "updatedUserList", userSeqIdList );
			paramMap.put( "updatedMailList", mailSeqIdList );
			paramMap.put( "state", 4 );
			paramMap.put( "activatedState", 2 );

			userDao.updateAppliedUserMailActStateMultiple( paramMap );
			userDao.updateAppliedUserInfoStateMultiple( paramMap );
		});
	}

	@Override
	public Trigger getTrigger() {
		return new CronTrigger( new StringBuffer().append( "0 0 0/" )
				.append( CaSettings.getInstance().get( "checkUserAuthInfoTime" ) )
				.append( " * * ?").toString() );
	}

	@Override
	public boolean isEnabled() {
		if ( CaSettings.getInstance().get( "checkUserAuthInfoTrigger" ).equals( "on" ) ) {
			return true;
		} else return false;
	}
}
