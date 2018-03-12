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
	public static Map<String, String> getRolesByProjectKey(String JiraHost,String projectKey){
		
		Map<String, String> projectRolesList = new HashMap<String,String>();
		String key = null;
		String value = null;
		
		String restUrl = JiraHost+String.format(GetRoleFromProject, projectKey);
		
		GetMethod response = HttpMethod.httpGet(CDCADMIN, CDC_PWD, restUrl);
		
		if (response!=null && response.getStatusLine().getStatusCode()>=200 && response.getStatusLine().getStatusCode()<=300) {
			
			InputStream responseStream = null;
			try {
				//����Ӧ���ת����������
				responseStream = response.getResponseBodyAsStream();
				//��ȡ������
				BufferedReader bReader = new BufferedReader(new InputStreamReader(responseStream));
				//�ַ���������
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
				System.out.println("��ȡ����ʧ�ܣ�����");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("��ȡ�ķ���ֵ����JSON��ʽ����");
			}finally {
				//�ر����ͷ���Դ
				if (responseStream != null) {
					try {
						responseStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
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
	 * ��ȡ��Ŀ��ÿ��role��users��groups
	 * @param projectRoelsList ��Ŀ�е�roles�б�
	 * @return
	 */
	public static Map<String, List<Map<String, String>>> getAllUsersFromProjectRoles(Map<String, String> projectRoelsList){
		
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
			GetMethod response = HttpMethod.httpGet(CDCADMIN, CDC_PWD, restURLOfprojectRole);
			
			if (response!=null && response.getStatusLine().getStatusCode()>=200 && response.getStatusLine().getStatusCode()<=300) {
				
				InputStream resStream = null;
				try {
					//����Ӧ�Ķ���ת����������
					resStream = response.getResponseBodyAsStream();
					//�����������뻺����
					BufferedReader resReader = new BufferedReader(new InputStreamReader(resStream));
					StringBuffer resBuffer = new StringBuffer();
					
					String resTemp = "";
					while ((resTemp=resReader.readLine()) != null) {
						resBuffer.append(resTemp);						
					}
					//��http����Żص�����ת����JSON����
					JSONObject jsonObject = new JSONObject(resBuffer.toString());
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
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}finally {
					//�ر�������
					if (resStream != null) {
						try {
							resStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					//�ͷ�������Դ
					if (response != null) {
						response.releaseConnection();
					}
				}
			}else{
				System.out.println("������ʧ�ܣ�"+response.getStatusLine().getStatusCode());
			}			
			allUsersFromProjectRoles.put(mEntry.getKey(), users);
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
	public static void addUsersToProjectRoles(String sourceJIRAHOST,String sourceProjectKey,String destinationJITAHOST,String destinationProjectKey){
		
		//��ȡĿ����Ŀ�е����е�project roles����rest URL�б�
		Map<String, String> DestinationProjectRoles = getRolesByProjectKey(destinationJITAHOST,destinationProjectKey);
		
		Map<String, List<Map<String, String>>> users = getAllUsersFromProjectRoles(getRolesByProjectKey(sourceJIRAHOST, sourceProjectKey));
		
		//���������д����project role���û��б�
		Set<Entry<String, List<Map<String, String>>>> usersSet = users.entrySet();
		Iterator<Entry<String, List<Map<String, String>>>> usersIterator = usersSet.iterator();
		String role = null;
		List<Map<String, String>> usersFromRole = null;
		String destinationRestUrl = null;
		while (usersIterator.hasNext()) {
			
			Entry<String, List<Map<String, String>>> mEntry = usersIterator.next();
			
			usersFromRole = mEntry.getValue();
			//�жϵ�ǰrole���Ƿ����û�
			if (usersFromRole.size() != 0) {
				role = mEntry.getKey();
				
				System.out.println(role);
				destinationRestUrl = DestinationProjectRoles.get(role);
				//��������û���������
				String type = null;
				String usernameOrGroupname = null;
				String postJSONString = null;
				for (int i = 0; i < usersFromRole.size(); i++) {
					Map<String, String> userOrGroup = usersFromRole.get(i);
					
					System.out.println(userOrGroup);
					//�жϴ�ŵ���user����group
					usernameOrGroupname = userOrGroup.get("User Name");
					
					type = userOrGroup.get("Type");
					
					
					System.out.println(type);
					
					System.out.println("atlassian-user-role-actor".equals(type));
					
					//ƴ��postJasonString
					if ("atlassian-user-role-actor".equals(type)) {
						postJSONString = "{ \"user\" : [\""+usernameOrGroupname+"\"] }";	
					}else if ("atlassian-group-role-actor".equals(type)) {
						postJSONString = "{ \"group\" : [\""+usernameOrGroupname+"\"] }";
					}
					System.out.println("postJSONString----->"+postJSONString);
					System.out.println(destinationRestUrl);
					
					if (postJSONString != null && destinationRestUrl != null) {
						HttpResponse httpResponse = HttpMethod.getPostMethodWithAuthorPostJson(CDCADMIN, CDC_PWD, destinationRestUrl, postJSONString);
						
						System.out.println(usernameOrGroupname+"�����״̬:  "+httpResponse.getStatusLine().getStatusCode());
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
		
		
		System.out.println(sheetNames+" sheetName�Ĵ�С��"+sheetNames.size());
		
		for (Iterator sheetNamesIterator = sheetNames.iterator(); sheetNamesIterator.hasNext();) {
			
			String sheet = (String) sheetNamesIterator.next();
			
			
			if ( users.get(sheet).size() == 0) {
				
				sheetNamesIterator.remove();
				
			}
			
		}
		
		System.out.println(sheetNames+" sheetName�Ĵ�С��"+sheetNames.size());
		
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
