package com.dreamsecurity.ca.framework.listener;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.dreamsecurity.ca.framework.init.CaSettings;

public class ContextLoaderListener implements ServletContextListener {
	
	private static final Logger logger = Logger.getLogger(ContextLoaderListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info( "run caSetting init...");
		try {
			CaSettings.init();
		} catch (IOException e) {
			logger.error( "ca setting file not found.");
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
