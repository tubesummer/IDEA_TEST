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
	public Map<String, String> getRolesByProjectKey(String JiraHost,String projectKey){
		
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
	 * ��ȡ��Ŀ��ÿ��role��users��groups
	 * @param projectRoelsList ��Ŀ�е�roles�б�
	 * @return
	 */
	public Map<String, List<Map<String, String>>> getAllUsersFromProjectRoles(Map<String, String> projectRoelsList){
		
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
							role.put(roleJsonObject.getString("type"), roleJsonObject.getString("name"));
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
