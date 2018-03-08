/**
 * 
 */
package com.nokia.jira.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;


/**
 * @author bpan
 *
 * created 2018Äê2ÔÂ27ÈÕ
 */
public class JIRAAuthenticationInformation {
	
	private static String USERNAME;
	private static String PASSWORD;
	private static String JIRA_Prod;
	
	
	/**
	 * 
	 */
	public JIRAAuthenticationInformation() {
		// TODO Auto-generated constructor stub
		try {
			
			InputStream inputStream = new BufferedInputStream(new FileInputStream("/src/resources/AuthenticationInformation.properties"));
			Properties properties = new Properties();
			properties.load(inputStream);
			USERNAME = properties.getProperty("JIRA.UserName");
			PASSWORD = properties.getProperty("JIRA.password");
			JIRA_Prod = properties.getProperty("JIRA.ProdHost");
			
			
		} catch (Exception e) {
			// The exceprion message is proposed to be thrown to the caller for processing
			throw new RuntimeException("Read database configuration file exception!",e);
		}
	}
	
	
	public static void main(String[] args) {
		
		JIRAAuthenticationInformation jiraAuthenticationInformatica = new JIRAAuthenticationInformation();
		
		System.out.println(jiraAuthenticationInformatica.JIRA_Prod);
		
		
		
	}
	

}
