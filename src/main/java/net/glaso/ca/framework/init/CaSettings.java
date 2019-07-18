package net.glaso.ca.framework.init;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CaSettings {
		
	private static Map<String, String> configMap = new HashMap<String, String>();

	private final static String caSettingsFName = "conf/ca-settings.properties";
	
	public static void init() throws IOException {
		InputStream input = null;
		Properties prop = new Properties();
		
		File f = new File( CaSettings.class.getClassLoader().getResource( caSettingsFName ).getFile() );
		input = new BufferedInputStream( new FileInputStream( f ) );
		
		prop.load( input );
		
		CaSettings.getInstance().put( "organization", prop.getProperty( "organization" ) );
		CaSettings.getInstance().put( "country", prop.getProperty( "country" ) );
		CaSettings.getInstance().put( "rootCaOrganizationUnit", prop.getProperty( "root_ca_organization_unit" ) );
		CaSettings.getInstance().put( "rootCaCn", prop.getProperty( "root_ca_cn" ) );
		
		CaSettings.getInstance().put( "rootCertValidity", prop.getProperty( "root_ca_cert_validity" ) );
		CaSettings.getInstance().put( "interCertValidity", prop.getProperty( "intermediate_ca_cert_validity" ) );
		CaSettings.getInstance().put( "entityCertValidity", prop.getProperty( "end_entity_cert_validity" ) );

		CaSettings.getInstance().put( "checkUserAuthInfoTrigger", prop.getProperty( "check_user_auth_info_trigger" ) );
		CaSettings.getInstance().put( "checkUserAuthInfoTime", prop.getProperty( "check_user_auth_info_time" ) );

		System.out.println(" ----- LOAD DEFAULT SETTING ----- ");
		for ( Map.Entry< String, String> entry : CaSettings.getInstance().entrySet() ) {
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
