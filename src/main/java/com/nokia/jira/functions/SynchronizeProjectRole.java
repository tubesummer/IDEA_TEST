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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.Get;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.coyote.http11.filters.VoidInputFilter;
import org.apache.http.HttpResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.nokia.BaseHttpTools.utils.HttpMethod;
import com.nokia.jira.utils.ConfigurationTool;
import com.nokia.jira.utils.CreateExcelFile;
import com.nokia.jira.utils.ProjectRolesUtil;

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
	public static Map<String, String> getRolesByProjectKey(String JiraHost,String userName,String password,String projectKey){
		
		Map<String, String> projectRolesList = new HashMap<String,String>();
		String key = null;
		String value = null;
		
		String restUrl = JiraHost+String.format(GetRoleFromProject, projectKey);
		
		GetMethod response = HttpMethod.httpGet(userName, password, restUrl);
		
		if (response!=null && response.getStatusLine().getStatusCode()>=200 && response.getStatusLine().getStatusCode()<=300) {
			try {
				JSONObject jsonObject = new JSONObject(HttpMethod.convertResponseAsString(response));
				
				Iterator<String> iterator = jsonObject.keys();
				
				while (iterator.hasNext()) {
					key = iterator.next().toString();
					value = jsonObject.getString(key);
					projectRolesList.put(key, value);		
					
				}				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("获取的返回值不是JSON格式！！");
			}finally {
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
	 * 获取具体某个项目中具体某个role的
	 * @param jiraHost
	 * @param restUrl
	 * @return
	 */
	public static Set<String> getUsersFromProjectRole(String jiraHost,String userName,String password, String projectKey, String roleName) {
		
		Set<String> usersOfTheRole = new HashSet<String>();
		//获取该role在当前系统中的ID
		String roleId = ProjectRolesUtil.getRoleIdByRoleName(jiraHost, userName, password, roleName);
		
		String restURL = jiraHost+"/rest/api/2/project/"+projectKey+"/role/"+roleId;
		
		GetMethod response = HttpMethod.httpGet(userName, password, restURL);
		
		if (response!=null && response.getStatusLine().getStatusCode()>=200 && response.getStatusLine().getStatusCode()<=300) {
			try {
				String strResponse = HttpMethod.convertResponseAsString(response);
				
				JSONObject jsonObject = new JSONObject(strResponse);
				
				if (jsonObject.getString("actors") != "[]") {
					
					JSONArray jsonArray = jsonObject.getJSONArray("actors");
					
					for (int i = 0; i < jsonArray.length(); i++) {
						
						usersOfTheRole.add(jsonArray.getJSONObject(i).getString("name"));
					}
				}
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				response.releaseConnection();
			}
		}
		return usersOfTheRole;
	}
	
	
	/**
	 * 获取项目中每个role中users和groups
	 * @param projectRoelsList 项目中的roles列表
	 * @return
	 */
	public static Map<String, List<Map<String, String>>> getAllUsersFromProjectRoles(String userName,String password,Map<String, String> projectRoelsList){
		
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
			GetMethod response = HttpMethod.httpGet(userName, password, restURLOfprojectRole);
			
			if (response!=null && response.getStatusLine().getStatusCode()>=200 && response.getStatusLine().getStatusCode()<=300) {
				
				try {
					//将http请求返回的内容转换成JSON对象
					JSONObject jsonObject = new JSONObject(HttpMethod.convertResponseAsString(response));
					//判断当前的role中是否为空
					if (jsonObject.getString("actors")!="[]") {
						//当前role中不为空的情况下解析role中users 和groups
						JSONArray jsonArray = jsonObject.getJSONArray("actors");
						Map<String, String> role = null;
						//遍历返回的对象
						for (int i = 0; i < jsonArray.length(); i++) {
							role = new HashMap<String,String>();
							JSONObject roleJsonObject = jsonArray.getJSONObject(i);
							role.put("User Name", roleJsonObject.getString("name"));
							role.put("Type", roleJsonObject.getString("type"));
							users.add(role);
							
						}						
					}					
				} catch (JSONException e) {
					e.printStackTrace();
				}finally {
					//释放请求资源
					if (response != null) {
						response.releaseConnection();
					}
				}
			}else{
				System.out.println("请求发送失败！"+response.getStatusLine().getStatusCode());
			}
			if (users.size() != 0) {
				allUsersFromProjectRoles.put(mEntry.getKey(), users);
			}
		}		
		return allUsersFromProjectRoles;
	}
	
	/**
	 * 将某个环境中某个项目的roles同步到指定环境的指定project role中
	 * @param sourceJIRAHOST 源环境
	 * @param sourceProjectKey 源项目
	 * @param destinationJITAHOST 目标环境
	 * @param destinationProjectKey 目标项目
	 */
	public static void addUsersToProjectRoles(String sourceJIRAHOST,String sourceUserName,String sourcePassword,String sourceProjectKey,
			String destinationJITAHOST,String destinationUserName,String destinationPassword,String destinationProjectKey){
		
		//获取目标项目中的所有的project roles及其rest URL列表
		Map<String, String> DestinationProjectRoles = getRolesByProjectKey(destinationJITAHOST,sourceUserName,sourcePassword,destinationProjectKey);
		
		//获取源项目中所有的role及其中的user list
		Map<String, List<Map<String, String>>> users = getAllUsersFromProjectRoles(sourceUserName,sourcePassword,getRolesByProjectKey(sourceJIRAHOST,sourceUserName,sourcePassword, sourceProjectKey));
		
		//遍历参数中传入的project role的用户列表
		Set<Entry<String, List<Map<String, String>>>> usersSet = users.entrySet();
		Iterator<Entry<String, List<Map<String, String>>>> usersIterator = usersSet.iterator();
		String roleName = null;
		List<Map<String, String>> usersFromRole = null;
		String destinationRestUrl = null;
		while (usersIterator.hasNext()) {
			
			Entry<String, List<Map<String, String>>> mEntry = usersIterator.next();
			//获取需要遍历的user list集合
			usersFromRole = mEntry.getValue();
//			System.out.println(usersFromRole);
			//获取role Name
			roleName = mEntry.getKey();
			
			destinationRestUrl = DestinationProjectRoles.get(roleName);
			
			System.out.println("Project Role Name :"+roleName);
			
			//获取目标项目中该role的user list
			Set<String> destinationRoleusers = getUsersFromProjectRole(destinationJITAHOST, destinationUserName, destinationPassword, destinationProjectKey, roleName);
//			System.out.println(destinationRoleusers);
			
			//如果存在用户遍历加入
			String type = null;
			String usernameOrGroupname = null;
			String postJSONString = null;
			for (int i = 0; i < usersFromRole.size(); i++) {
				Map<String, String> userOrGroup = usersFromRole.get(i);
//				System.out.println(userOrGroup);
				
				usernameOrGroupname = userOrGroup.get("User Name");
				
				if (!destinationRoleusers.contains(usernameOrGroupname)) {
					//判断存放的是user还是group
					type = userOrGroup.get("Type");
					
					//拼接postJasonString
					if ("atlassian-user-role-actor".equals(type)) {
						postJSONString = "{ \"user\" : [\""+usernameOrGroupname.trim().toLowerCase()+"\"] }";	
					}else if ("atlassian-group-role-actor".equals(type)) {
						postJSONString = "{ \"group\" : [\""+usernameOrGroupname.trim().toLowerCase()+"\"] }";
					}
					System.out.println("postJSONString----->"+postJSONString);
//					System.out.println(destinationRestUrl);
					
					
					
					if (postJSONString != null && destinationRestUrl != null) {
												
						
						HttpResponse httpResponse = HttpMethod.getPostMethodWithAuthorPostJson(destinationUserName, destinationPassword, destinationRestUrl, postJSONString);
						
						System.out.println(usernameOrGroupname+"加入的状态:  "+httpResponse.getStatusLine().getStatusCode());
					}
				}
			}
		}
	}

	
	/**
	 * 将某个项目的所有project role 的users 导入到Excel中
	 * @param fileDir  导入文件地址
	 * @param users  该项目中所有
	 */
	public static void exportProjectRolesUserListToExcel(String fileDir, Map<String, List<Map<String, String>>> users){
		
		Set<Map.Entry<String, List<Map<String, String>>>> userSet = users.entrySet();
		
		Iterator<Entry<String, List<Map<String, String>>>> userIterator = userSet.iterator();
		
		List<String> sheetNames = new ArrayList<>();
		
		while (userIterator.hasNext()) {
			
			Entry<String, List<Map<String, String>>> mEntry = userIterator.next();
			
			String sheetName = mEntry.getKey();
			
			mEntry.getValue();
			
			sheetNames.add(sheetName);
		}
		
		String[] titleRow = {"User Name","Type"};
		
		
		CreateExcelFile.createExcelXls(fileDir, sheetNames, titleRow);
		
		for (Iterator iterator = sheetNames.iterator(); iterator.hasNext();) {
			String role = (String) iterator.next();
			
			try {
				CreateExcelFile.writeToExcelXls(fileDir, role, users.get(role));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	
	
	
	public static void main(String[] args) throws Exception {	 
			 
//		addUsersToProjectRoles(JIRA_Prod, CDCADMIN, CDC_PWD, "SAR", JIRA_Prod, CDCADMIN, CDC_PWD, "IXR");
		
		//get all users from every role of 7705 project
		
		exportProjectRolesUserListToExcel("d:\\SWT users.xls", getAllUsersFromProjectRoles(CDCADMIN, CDC_PWD, getRolesByProjectKey(JIRA_INT, CDCADMIN, CDC_PWD, "SWT")));
		
			
	}
}
