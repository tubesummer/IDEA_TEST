package com.nokia.jira.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.tomcat.dbcp.pool.impl.GenericKeyedObjectPool.Config;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.nokia.BaseHttpTools.utils.HttpMethod;

public class ProjectRolesUtil {
	
	
	
	/**
	 * 获取当前系统中所有的project role 的name 和 id
	 * @param RestURL
	 * @return 当前系统中所有role的name 和 id的list 集合
	 */
	public static  List<Map<String, String>> getAllRolesOfCurrentJIRASystem(String userName,String passWord,String JiraHost){
		
		List<Map<String, String>> rolesInCurrentSystem = new ArrayList<>();
		
		GetMethod response = HttpMethod.httpGet(userName, passWord, JiraHost+"/rest/api/2/role");
		
		InputStream inputStream = null;
		
		if (response!=null && response.getStatusLine().getStatusCode()>=200 && response.getStatusLine().getStatusCode()<=300) {
			
			try {
				inputStream = response.getResponseBodyAsStream();
				
				BufferedReader resReader = new BufferedReader(new InputStreamReader(inputStream));
				
				StringBuffer resBuffer = new StringBuffer();
				
				String resTemp = "";
				while ((resTemp=resReader.readLine()) != null) {
					resBuffer.append(resTemp);						
				}
				
				JSONArray jsonArray = new JSONArray(resBuffer.toString());
				
				JSONObject jsonObject = null;
				
				Map<String, String> role = null;
				
				for (int i = 0; i < jsonArray.length(); i++) {
					
					jsonObject = jsonArray.getJSONObject(i);
					
					role = new HashMap<>();
					
					role.put(jsonObject.getString("name"), jsonObject.getString("id"));
					
					rolesInCurrentSystem.add(role);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				
				response.releaseConnection();
			}
		}
		return rolesInCurrentSystem;
	}
	
	
	
	/**
	 * 根据提供的Role Name获取Role ID
	 * @param JIRAHost  JIRAURL
	 * @param userName  用户名
	 * @param passWord  密码
	 * @param RoleName  
	 * @return  Role ID
	 */
	public static String getRoleIdByRoleName(String JIRAHost,String userName,String passWord,String RoleName){
		
		String id = null;
		
		List<Map<String, String>> allRoles = getAllRolesOfCurrentJIRASystem(userName, passWord, JIRAHost);
		
		for (int i = 0; i < allRoles.size() ; i++) {
			
			if (allRoles.get(i).get(RoleName)!=null) {
				id = allRoles.get(i).get(RoleName);
			}
		}
		return id;
	}
	
	public static void main(String[] args) {
		
		
//		System.out.println(getAllRolesOfCurrentJIRASystem(ConfigurationTool.INSTANCE.getCDCADMIN(), ConfigurationTool.INSTANCE.getCDCPWD(), 
//									ConfigurationTool.INSTANCE.getJIRAINT()));
		
		System.out.println(getRoleIdByRoleName(ConfigurationTool.INSTANCE.getJIRAINT(), ConfigurationTool.INSTANCE.getCDCADMIN(),
				ConfigurationTool.INSTANCE.getCDCPWD(), "2LS Users"));
		
//		System.out.println(getAllRolesOfCurrentJIRASystem(ConfigurationTool.INSTANCE.getCDCADMIN(), ConfigurationTool.INSTANCE.getCDCPWD(),
//				ConfigurationTool.INSTANCE.getJIRAINT()).size());
		
	}
	

}
