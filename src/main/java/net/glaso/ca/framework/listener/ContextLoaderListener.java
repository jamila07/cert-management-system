package net.glaso.ca.framework.listener;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.glaso.ca.framework.init.MailSettings;
import org.apache.log4j.Logger;

import net.glaso.ca.framework.init.CaSettings;

public class ContextLoaderListener implements ServletContextListener {
	
	private static final Logger logger = Logger.getLogger(ContextLoaderListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info( "run caSetting init...");
		try {
			CaSettings.init();

			MailSettings.init();

		} catch (IOException e) {
			logger.error( "setting files not found.");
			e.printStackTrace();
		}
	}
		
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("down down");
		logger.info("down down");
		logger.info("down down");
		logger.info("down down");
	}
}
