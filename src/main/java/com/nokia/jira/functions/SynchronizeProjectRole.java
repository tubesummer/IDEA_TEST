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
 * created 2018��3��1��
 */
public class SynchronizeProjectRole {
	
	
	private static String JIRA_Prod = ConfigurationTool.INSTANCE.getJIRAProd();
	private static String JIRA_INT = ConfigurationTool.INSTANCE.getJIRAINT();
	private static String CDCADMIN = ConfigurationTool.INSTANCE.getCDCADMIN();
	private static String CDC_PWD = ConfigurationTool.INSTANCE.getCDCPWD();
	private static String GetRoleFromProject = "/rest/api/2/project/%s/role";
	
	/**
	 * ����project key��ȡ����project role��rest API
	 * @param projectKey
	 * @return Map ����
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
				System.out.println("��ȡ�ķ���ֵ����JSON��ʽ����");
			}finally {
				//�ͷ�Http������Դ
				if (response != null) {
					
					response.releaseConnection();
				}
			}
			
		}else {
			System.out.println("������ʧ�ܣ���"+response.getStatusCode());
		}	
		
		return projectRolesList;		
	}
	
	
	/**
	 * ��ȡ����ĳ����Ŀ�о���ĳ��role��
	 * @param jiraHost
	 * @param restUrl
	 * @return
	 */
	public static Set<String> getUsersFromProjectRole(String jiraHost,String userName,String password, String projectKey, String roleName) {
		
		Set<String> usersOfTheRole = new HashSet<String>();
		//��ȡ��role�ڵ�ǰϵͳ�е�ID
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
	 * ��ȡ��Ŀ��ÿ��role��users��groups
	 * @param projectRoelsList ��Ŀ�е�roles�б�
	 * @return
	 */
	public static Map<String, List<Map<String, String>>> getAllUsersFromProjectRoles(String userName,String password,Map<String, String> projectRoelsList){
		
		Map<String, List<Map<String, String>>> allUsersFromProjectRoles = new HashMap<>();
		//����û��б�
		List<Map<String, String>> users = null;
		//�������е�project role
		Set<Map.Entry<String, String>> set = projectRoelsList.entrySet();
		//��ȡIterator���󣬲��ô˶���ķ���������ֵ����
		Iterator<Entry<String, String>> iterator = set.iterator();
		//����map�����еļ���ֵ����
		while (iterator.hasNext()) {
			users = new ArrayList<Map<String,String>>();
			Map.Entry<String, String> mEntry = iterator.next();
			
			String restURLOfprojectRole = mEntry.getValue();
			//���ݻ�ȡ��role��restURL����http���󣬻�ȡ���role�����е�users��groups
			GetMethod response = HttpMethod.httpGet(userName, password, restURLOfprojectRole);
			
			if (response!=null && response.getStatusLine().getStatusCode()>=200 && response.getStatusLine().getStatusCode()<=300) {
				
				try {
					//��http���󷵻ص�����ת����JSON����
					JSONObject jsonObject = new JSONObject(HttpMethod.convertResponseAsString(response));
					//�жϵ�ǰ��role���Ƿ�Ϊ��
					if (jsonObject.getString("actors")!="[]") {
						//��ǰrole�в�Ϊ�յ�����½���role��users ��groups
						JSONArray jsonArray = jsonObject.getJSONArray("actors");
						Map<String, String> role = null;
						//�������صĶ���
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
					//�ͷ�������Դ
					if (response != null) {
						response.releaseConnection();
					}
				}
			}else{
				System.out.println("������ʧ�ܣ�"+response.getStatusLine().getStatusCode());
			}
			if (users.size() != 0) {
				allUsersFromProjectRoles.put(mEntry.getKey(), users);
			}
		}		
		return allUsersFromProjectRoles;
	}
	
	/**
	 * ��ĳ��������ĳ����Ŀ��rolesͬ����ָ��������ָ��project role��
	 * @param sourceJIRAHOST Դ����
	 * @param sourceProjectKey Դ��Ŀ
	 * @param destinationJITAHOST Ŀ�껷��
	 * @param destinationProjectKey Ŀ����Ŀ
	 */
	public static void addUsersToProjectRoles(String sourceJIRAHOST,String sourceUserName,String sourcePassword,String sourceProjectKey,
			String destinationJITAHOST,String destinationUserName,String destinationPassword,String destinationProjectKey){
		
		//��ȡĿ����Ŀ�е����е�project roles����rest URL�б�
		Map<String, String> DestinationProjectRoles = getRolesByProjectKey(destinationJITAHOST,sourceUserName,sourcePassword,destinationProjectKey);
		
		//��ȡԴ��Ŀ�����е�role�����е�user list
		Map<String, List<Map<String, String>>> users = getAllUsersFromProjectRoles(sourceUserName,sourcePassword,getRolesByProjectKey(sourceJIRAHOST,sourceUserName,sourcePassword, sourceProjectKey));
		
		//���������д����project role���û��б�
		Set<Entry<String, List<Map<String, String>>>> usersSet = users.entrySet();
		Iterator<Entry<String, List<Map<String, String>>>> usersIterator = usersSet.iterator();
		String roleName = null;
		List<Map<String, String>> usersFromRole = null;
		String destinationRestUrl = null;
		while (usersIterator.hasNext()) {
			
			Entry<String, List<Map<String, String>>> mEntry = usersIterator.next();
			//��ȡ��Ҫ������user list����
			usersFromRole = mEntry.getValue();
//			System.out.println(usersFromRole);
			//��ȡrole Name
			roleName = mEntry.getKey();
			
			destinationRestUrl = DestinationProjectRoles.get(roleName);
			
			System.out.println("Project Role Name :"+roleName);
			
			//��ȡĿ����Ŀ�и�role��user list
			Set<String> destinationRoleusers = getUsersFromProjectRole(destinationJITAHOST, destinationUserName, destinationPassword, destinationProjectKey, roleName);
//			System.out.println(destinationRoleusers);
			
			//��������û���������
			String type = null;
			String usernameOrGroupname = null;
			String postJSONString = null;
			for (int i = 0; i < usersFromRole.size(); i++) {
				Map<String, String> userOrGroup = usersFromRole.get(i);
//				System.out.println(userOrGroup);
				
				usernameOrGroupname = userOrGroup.get("User Name");
				
				if (!destinationRoleusers.contains(usernameOrGroupname)) {
					//�жϴ�ŵ���user����group
					type = userOrGroup.get("Type");
					
					//ƴ��postJasonString
					if ("atlassian-user-role-actor".equals(type)) {
						postJSONString = "{ \"user\" : [\""+usernameOrGroupname.trim().toLowerCase()+"\"] }";	
					}else if ("atlassian-group-role-actor".equals(type)) {
						postJSONString = "{ \"group\" : [\""+usernameOrGroupname.trim().toLowerCase()+"\"] }";
					}
					System.out.println("postJSONString----->"+postJSONString);
//					System.out.println(destinationRestUrl);
					
					
					
					if (postJSONString != null && destinationRestUrl != null) {
												
						
						HttpResponse httpResponse = HttpMethod.getPostMethodWithAuthorPostJson(destinationUserName, destinationPassword, destinationRestUrl, postJSONString);
						
						System.out.println(usernameOrGroupname+"�����״̬:  "+httpResponse.getStatusLine().getStatusCode());
					}
				}
			}
		}
	}

	
	/**
	 * ��ĳ����Ŀ������project role ��users ���뵽Excel��
	 * @param fileDir  �����ļ���ַ
	 * @param users  ����Ŀ������
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
