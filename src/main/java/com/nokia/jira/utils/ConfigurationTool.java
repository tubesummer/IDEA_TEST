/**
 * 
 */
package com.nokia.jira.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * @author bpan
 *
 * created 2018��3��5��
 * 
 * ö�ٵķ�ʽʵ�ֵ���ģʽ���е����£�
 * 1���������л�
 * 2����ֻ֤��һ��ʵ��
 * 3���̰߳�ȫ
 */
public enum ConfigurationTool {
	
	INSTANCE;	
	private InputStream inputStream;
	private Properties properties;
	/**
	 * 
	 */
	private ConfigurationTool() {
		// TODO Auto-generated constructor stub
		inputStream = ConfigurationTool.class.getResourceAsStream("/AuthenticationInformation.properties");
		properties = new Properties();
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	public String getCDCADMIN() {
		
		return properties.getProperty("JIRA.UserName");
		
	}
	
	public String getCDCPWD(){
		return properties.getProperty("JIRA.password");
	}
	
	public String getJIRAProd(){
		return properties.getProperty("JIRA.ProdHost");
	}
	
	public String getJIRAINT(){
		return properties.getProperty("JIRA.INTHost");
	}
	
	public String getJIRAQA(){
		return properties.getProperty("JIRA.QAHost");
	}
	
	
	public String getJIRADEV(){
		return properties.getProperty("JIRA.DEVHost");
	}
	
	public void close(){
		
		if (inputStream !=null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
