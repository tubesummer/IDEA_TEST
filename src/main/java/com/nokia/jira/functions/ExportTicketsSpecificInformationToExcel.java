/**
 * 
 */
package com.nokia.jira.functions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.nokia.BaseHttpTools.utils.HttpMethod;
import com.nokia.jira.utils.ConfigurationTool;
import com.nokia.jira.utils.CreateExcelFile;
/**
 * @author bpan
 * created 2018��2��27��
 */
public class ExportTicketsSpecificInformationToExcel {
	
	public static final String CDCADMIN = ConfigurationTool.INSTANCE.getCDCADMIN();
	public static final String CDCADMIN_PWD = ConfigurationTool.INSTANCE.getCDCPWD();
	
	public static final String JIRA_Prod = ConfigurationTool.INSTANCE.getJIRAProd();
	
	public static final String SEARCH_Jql = "/rest/api/2/search?jql=";
	
	
	public static void main(String[] args) {
		
		
		
		String JQL = "project=\'Internal%20Auditors\'%20AND%20(status=Validated%20OR%20status=Open)";
		String RestrictedFields = "&fields=customfield_11953,summary,status,reporter";
		String MaxResult = "&maxResults=1000";
		//����Http ����
		GetMethod response = HttpMethod.httpGet(CDCADMIN, CDCADMIN_PWD, JIRA_Prod+SEARCH_Jql+JQL+RestrictedFields+MaxResult);
		
		
		if (response!=null && response.getStatusLine().getStatusCode()>=200 && response.getStatusLine().getStatusCode()<=300) {
			//��ȡ��Ӧ�����Ϊ��
			try {
				//����Ӧ�Ľ��ת����string
				String responsebody = response.getResponseBodyAsString();
				//����Ӧ�Ľ��װ����Jason��ʽ
				JSONObject jsonobject1 = new JSONObject(responsebody);
				//����Jason���� ������Ϣд��Excel�ļ���
				JSONArray tickets = jsonobject1.getJSONArray("issues");
								
				//����Excel�ı�ͷ����
				String row[] = {"Key","Summary","Status","Reporter","Reporter Email Address","Verifying Lead Auditor","Verifying Lead Auditor Emailaddress"};
				
				//����Excel�ļ�
				String fileDir = "d:\\Verifying Lead Auditor List.xls";
				List<String> sheetName = new ArrayList<>();
				sheetName.add("Verifying Lead Auditor List");
				
				if (!CreateExcelFile.fileExist(fileDir)) {
					//���ȴ���Excel �ļ���sheet
					CreateExcelFile.createExcelXls(fileDir, sheetName, row);
					List<Map<String,String>> AuditorsList = new ArrayList<Map<String,String>>();
					
					Map<String, String> ticket = null;
					JSONObject ticketObject = null;
					JSONObject fieldsValue = null;
					String key = null; //ticket key
					String summary = null; //ticket summary
					String status = null; //ticket status
					String Reporter = null;
					String ReporterEmailAddress = null;
					String VLA = null; //Verifying Lead Auditor displayname
					String VLAE = null;	//Verifying Lead Auditor emailAddress 	
					for (int i = 0; i < tickets.length(); i++) {
						ticket = new HashMap<String,String>();
						ticketObject = new JSONObject(tickets.get(i).toString());
						key = ticketObject.getString("key");
						fieldsValue = ticketObject.getJSONObject("fields");
						
						summary = fieldsValue.getString("summary");
						
						Reporter = fieldsValue.getJSONObject("reporter").getString("displayName");
						ReporterEmailAddress = fieldsValue.getJSONObject("reporter").getString("emailAddress");
						
						if (fieldsValue.getString("customfield_11953")!="null") {
							VLA = fieldsValue.getJSONObject("customfield_11953").getString("displayName");
							VLAE = fieldsValue.getJSONObject("customfield_11953").getString("emailAddress");
						}else{
							VLA = "NULL";
							VLAE = "NULL";
						}
						status = fieldsValue.getJSONObject("status").getString("name");
						ticket.put("Key", key);
						ticket.put("Summary", summary);
						ticket.put("Verifying Lead Auditor", VLA);
						ticket.put("Verifying Lead Auditor Emailaddress", VLAE);
						ticket.put("Status", status);
						ticket.put("Reporter", Reporter);
						ticket.put("Reporter Email Address", ReporterEmailAddress);
						AuditorsList.add(ticket);
					}
					
//					System.out.println(AuditorsList.size());
					try {
						CreateExcelFile.writeToExcelXls(fileDir, sheetName.get(0), AuditorsList);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					
					System.out.println("�ļ��Ѵ��ڣ�����");
				}
				
			} catch (IOException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				if (response!=null) {
					//�ͷ�http������Դ
					response.releaseConnection();
				}
			}
		}
	}
}
