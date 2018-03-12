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
	public static Map<String, String> getRolesByProjectKey(String JiraHost,String projectKey){
		
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
				String resTemp = null;
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
	 * 获取具体某个项目中具体某个role的
	 * @param jiraHost
	 * @param restUrl
	 * @return
	 */
	public static Set<String> getUsersFromProjectRole(String jiraHost,String userName,String password, String projectKey, String roleName) {
		
		 Set<String> usersOfTheRole = new HashSet<String>();
		
		String roleId = ProjectRolesUtil.getRoleIdByRoleName(jiraHost, userName, password, roleName);
		
		String restURL = jiraHost+"/rest/api/2/project/"+projectKey+"/role/"+roleId;
		
		GetMethod response = HttpMethod.httpGet(userName, password, restURL);
		
		try {
			InputStream inputStream = response.getResponseBodyAsStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			response.releaseConnection();
		}
		
		
		
		return usersOfTheRole;
	}
	
	
	/**
	 * 获取项目中每个role中users和groups
	 * @param projectRoelsList 项目中的roles列表
	 * @return
	 */
	public static Map<String, List<Map<String, String>>> getAllUsersFromProjectRoles(Map<String, String> projectRoelsList){
		
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
							role.put("User Name", roleJsonObject.getString("name"));
							role.put("Type", roleJsonObject.getString("type"));
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
	
	/**
	 * 将某个环境中某个项目的roles同步到指定环境的指定project role中
	 * @param sourceJIRAHOST 源环境
	 * @param sourceProjectKey 源项目
	 * @param destinationJITAHOST 目标环境
	 * @param destinationProjectKey 目标项目
	 */
	public static void addUsersToProjectRoles(String sourceJIRAHOST,String sourceProjectKey,String destinationJITAHOST,String destinationProjectKey){
		
		//获取目标项目中的所有的project roles及其rest URL列表
		Map<String, String> DestinationProjectRoles = getRolesByProjectKey(destinationJITAHOST,destinationProjectKey);
		
		Map<String, List<Map<String, String>>> users = getAllUsersFromProjectRoles(getRolesByProjectKey(sourceJIRAHOST, sourceProjectKey));
		
		//遍历参数中传入的project role的用户列表
		Set<Entry<String, List<Map<String, String>>>> usersSet = users.entrySet();
		Iterator<Entry<String, List<Map<String, String>>>> usersIterator = usersSet.iterator();
		String role = null;
		List<Map<String, String>> usersFromRole = null;
		String destinationRestUrl = null;
		while (usersIterator.hasNext()) {
			
			Entry<String, List<Map<String, String>>> mEntry = usersIterator.next();
			
			usersFromRole = mEntry.getValue();
			//判断当前role中是否有用户
			if (usersFromRole.size() != 0) {
				role = mEntry.getKey();
				
				System.out.println(role);
				destinationRestUrl = DestinationProjectRoles.get(role);
				//如果存在用户遍历加入
				String type = null;
				String usernameOrGroupname = null;
				String postJSONString = null;
				for (int i = 0; i < usersFromRole.size(); i++) {
					Map<String, String> userOrGroup = usersFromRole.get(i);
					
					System.out.println(userOrGroup);
					//判断存放的是user还是group
					usernameOrGroupname = userOrGroup.get("User Name");
					
					type = userOrGroup.get("Type");
					
					
					System.out.println(type);
					
					System.out.println("atlassian-user-role-actor".equals(type));
					
					//拼接postJasonString
					if ("atlassian-user-role-actor".equals(type)) {
						postJSONString = "{ \"user\" : [\""+usernameOrGroupname+"\"] }";	
					}else if ("atlassian-group-role-actor".equals(type)) {
						postJSONString = "{ \"group\" : [\""+usernameOrGroupname+"\"] }";
					}
					System.out.println("postJSONString----->"+postJSONString);
					System.out.println(destinationRestUrl);
					
					if (postJSONString != null && destinationRestUrl != null) {
						HttpResponse httpResponse = HttpMethod.getPostMethodWithAuthorPostJson(CDCADMIN, CDC_PWD, destinationRestUrl, postJSONString);
						
						System.out.println(usernameOrGroupname+"加入的状态:  "+httpResponse.getStatusLine().getStatusCode());
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {	 
			 
//		addUsersToProjectRoles(JIRA_INT,"SWT", JIRA_INT, "RDTH");
		
		//get all users from every role of 7705 project
		/*Map<String, List<Map<String, String>>> users = getAllUsersFromProjectRoles(getRolesByProjectKey(JIRA_Prod, "IXR"));
		
		Set<Map.Entry<String, List<Map<String, String>>>> userSet = users.entrySet();
		
		Iterator<Entry<String, List<Map<String, String>>>> userIterator = userSet.iterator();
		
		List<String> sheetNames = new ArrayList<>();
		
		while (userIterator.hasNext()) {
			
			Entry<String, List<Map<String, String>>> mEntry = userIterator.next();
			
			String sheetName = mEntry.getKey();
			
			mEntry.getValue();
			
			sheetNames.add(sheetName);
		}
		
		
		System.out.println(sheetNames+" sheetName的大小："+sheetNames.size());
		
		for (Iterator sheetNamesIterator = sheetNames.iterator(); sheetNamesIterator.hasNext();) {
			
			String sheet = (String) sheetNamesIterator.next();
			
			
			if ( users.get(sheet).size() == 0) {
				
				sheetNamesIterator.remove();
				
			}
			
		}
		
		System.out.println(sheetNames+" sheetName的大小："+sheetNames.size());
		
		String[] titleRow = {"User Name","Type"};
		
		String filDir = "d:\\usersFromEveryRoleOf7250.xls";
		
		CreateExcelFile.createExcelXls(filDir, sheetNames, titleRow);
		
		for (Iterator iterator = sheetNames.iterator(); iterator.hasNext();) {
			String role = (String) iterator.next();
			
			CreateExcelFile.writeToExcelXls(filDir, role, users.get(role));
			
		}*/
		
//		addUsersToProjectRoles(JIRA_Prod, "SAR", JIRA_Prod, "IXR");
		
		
		
		List<String> teStrings = new ArrayList<>();
		teStrings.add("A");
		
		teStrings.add("B");
		
		teStrings.add("C");
		
		System.out.println(teStrings);
		
		System.out.println(teStrings.contains("C"));
		
	}
}
