/**
 * 
 */
package com.nokia.jira.functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.nokia.BaseHttpTools.utils.HttpMethod;
import com.nokia.jira.utils.ConfigurationTool;

/**
 * @author bpan
 *
 * created 2018年3月1日
 */
public class SynchronizeProjectRole {
	
	
	private static String JIRA_Prod = ConfigurationTool.INSTANCE.getJIRAProd();
	private static String JIRA_INT = ConfigurationTool.INSTANCE.getJIRAINT();
	private static String CDCADMIN = ConfigurationTool.INSTANCE.getCDCADMIN();
	private static String CDC_PWD = ConfigurationTool.INSTANCE.getCDCPWD();
	private static String GetRoleFromProject = "/rest/api/2/project/%s/role";
	
	/**
	 * 根据project key获取所有project role的rest API
	 * @param projectKey
	 * @return Map 集合
	 */
	public Map<String, String> getRolesByProjectKey(String JiraHost,String projectKey){
		
		Map<String, String> projectRolesList = new HashMap<String,String>();
		String key = null;
		String value = null;
		
		String restUrl = JiraHost+String.format(GetRoleFromProject, projectKey);
		
		GetMethod response = HttpMethod.httpGet(CDCADMIN, CDC_PWD, restUrl);
		
		if (response!=null && response.getStatusLine().getStatusCode()>=200 && response.getStatusLine().getStatusCode()<=300) {
			
			InputStream responseStream = null;
			try {
				//将响应结果转换成输入流
				responseStream = response.getResponseBodyAsStream();
				//读取输入流
				BufferedReader bReader = new BufferedReader(new InputStreamReader(responseStream));
				//字符串操作类
				StringBuffer resBuffer = new StringBuffer();
				String resTemp = "";
				while((resTemp = bReader.readLine()) != null){
					
					resBuffer.append(resTemp);
				}			
				
				JSONObject jsonObject = new JSONObject(resBuffer.toString());
				
				Iterator iterator = jsonObject.keys();
				
				while (iterator.hasNext()) {
					key = iterator.next().toString();
					value = jsonObject.getString(key);
					projectRolesList.put(key, value);		
					
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("获取数据失败！！！");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("获取的返回值不是JSON格式！！");
			}finally {
				//关闭流释放资源
				if (responseStream != null) {
					try {
						responseStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				//释放Http请求资源
				if (response != null) {
					
					response.releaseConnection();
				}
			}
			
		}else {
			System.out.println("请求发送失败！！"+response.getStatusCode());
		}	
		
		return projectRolesList;		
	}
	
	/**
	 * 获取项目中每个role中users和groups
	 * @param projectRoelsList 项目中的roles列表
	 * @return
	 */
	public Map<String, List<Map<String, String>>> getAllUsersFromProjectRoles(Map<String, String> projectRoelsList){
		
		Map<String, List<Map<String, String>>> allUsersFromProjectRoles = new HashMap<>();
		//存放用户列表
		List<Map<String, String>> users = null;
		//遍历所有的project role
		Set<Map.Entry<String, String>> set = projectRoelsList.entrySet();
		//获取Iterator对象，并用此对象的方法遍历键值对象
		Iterator<Entry<String, String>> iterator = set.iterator();
		//遍历map集合中的键，值对象
		while (iterator.hasNext()) {
			users = new ArrayList<Map<String,String>>();
			Map.Entry<String, String> mEntry = iterator.next();
			
			String restURLOfprojectRole = mEntry.getValue();
			//根据获取的role的restURL发送http请求，获取这个role中所有的users和groups
			GetMethod response = HttpMethod.httpGet(CDCADMIN, CDC_PWD, restURLOfprojectRole);
			
			if (response!=null && response.getStatusLine().getStatusCode()>=200 && response.getStatusLine().getStatusCode()<=300) {
				
				InputStream resStream = null;
				try {
					//将响应的对象转换成输入流
					resStream = response.getResponseBodyAsStream();
					//将输入流放入缓冲区
					BufferedReader resReader = new BufferedReader(new InputStreamReader(resStream));
					StringBuffer resBuffer = new StringBuffer();
					
					String resTemp = "";
					while ((resTemp=resReader.readLine()) != null) {
						resBuffer.append(resTemp);						
					}
					//将http请求放回的内容转换成JSON对象
					JSONObject jsonObject = new JSONObject(resBuffer.toString());
					//判断当前的role中是否为空
					if (jsonObject.getString("actors")!="[]") {
						//当前role中不为空的情况下解析role中users 和groups
						JSONArray jsonArray = jsonObject.getJSONArray("actors");
						Map<String, String> role = null;
						//遍历返回的对象
						for (int i = 0; i < jsonArray.length(); i++) {
							role = new HashMap<String,String>();
							JSONObject roleJsonObject = jsonArray.getJSONObject(i);
							role.put(roleJsonObject.getString("type"), roleJsonObject.getString("name"));
							users.add(role);							
						}						
					}					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}finally {
					//关闭输入流
					if (resStream != null) {
						try {
							resStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					//释放请求资源
					if (response != null) {
						response.releaseConnection();
					}
				}
			}else{
				System.out.println("请求发送失败！"+response.getStatusLine().getStatusCode());
			}			
			allUsersFromProjectRoles.put(mEntry.getKey(), users);
		}		
		return allUsersFromProjectRoles;
	}
	
	
	public boolean addUsersToProjectRoles(Map<String, List<Map<String, String>>> users,String destinationProjectKey){
		
		boolean flag = false;
		
		Set<Entry<String, List<Map<String, String>>>> usersSet = users.entrySet();
		Iterator<Entry<String, List<Map<String, String>>>> usersIterator = usersSet.iterator();
		String role = null;
		while (usersIterator.hasNext()) {
			role = usersIterator.next().getKey();
			
		}
		
		
		
		return flag;
		
	}
	
	
	
	public static void main(String[] args) {
		
		 SynchronizeProjectRole synchronizeProjectRole = new SynchronizeProjectRole();
		 
		 Map<String, String> projectRolesList = synchronizeProjectRole.getRolesByProjectKey(JIRA_INT,"RDTH");
		 
		 Map<String, List<Map<String, String>>> allUsersFromProjectRoles = synchronizeProjectRole.getAllUsersFromProjectRoles(projectRolesList);
		 
		 Set<Map.Entry<String, List<Map<String, String>>>> sEntries = allUsersFromProjectRoles.entrySet();
		 
		 Iterator<Entry<String, List<Map<String, String>>>> iterator = sEntries.iterator();
		 
		 while (iterator.hasNext()) {
			
			 Map.Entry<String, List<Map<String, String>>> mEntry = iterator.next();
			 
			 String key = mEntry.getKey();
			 System.out.println("Project Role:  "+key);
			 
			 List<Map<String, String>> users = mEntry.getValue();
			 
			 System.out.println(users);
			 
			 
			 for (int i = 0; i < users.size(); i++) {
				
				 Map<String, String> user = users.get(i);
				 
				 System.out.println(user);
				 
				 
			}
		}
		

	}
	
	

}
