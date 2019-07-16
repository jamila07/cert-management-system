package net.glaso.ca.framework.init;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MailSettings {
		
	private static Map<String, String> configMap = new HashMap<String, String>();

	private final static String mailSettingsFName = "conf/mail-settings.properties";
	
	public static void init() throws IOException {
		InputStream input = null;
		Properties prop = new Properties();
		
		File f = new File( MailSettings.class.getClassLoader().getResource( mailSettingsFName ).getFile() );
		input = new BufferedInputStream( new FileInputStream( f ) );
		
		prop.load( input );

		MailSettings.getInstance().put( "host", prop.getProperty( "mail.host" ) );
		MailSettings.getInstance().put( "smtpHost", prop.getProperty( "mail.smtphost" ) );
		MailSettings.getInstance().put( "port", prop.getProperty( "mail.port" ) );
		MailSettings.getInstance().put( "userName", prop.getProperty( "mail.username" ) );
		MailSettings.getInstance().put( "password", prop.getProperty( "mail.password" ) );
		MailSettings.getInstance().put( "recipient", prop.getProperty( "mail.recipient" ) );

		System.out.println(" ----- LOAD DEFAULT SETTING ----- ");
		for ( Map.Entry< String, String> entry : MailSettings.getInstance().entrySet() ) {
			System.out.println( new StringBuffer().append( entry.getKey() )
					.append( " : " )
					.append( entry.getValue() ).toString() );
		}
	}
	
	public static Map<String, String> getInstance() {

		if( configMap == null ) {
			configMap = new HashMap<String, String>();
		}
		
		return configMap;
	}
}
