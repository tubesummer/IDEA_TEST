package com.nokia.jira.functions;
//将group中user导入到Excel表中，相关ticket：RDTH-8683


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import com.nokia.BaseHttpTools.utils.BaseHttpDomain;
import com.nokia.jira.entity.JiraUser;
import com.nokia.jira.utils.ConfigurationTool;




public class ExtractGroupMembersToExcelFile {	
	
	
	
	public static final String CDCADMIN = ConfigurationTool.INSTANCE.getCDCADMIN();
	public static final String CDCADMIN_PWD = ConfigurationTool.INSTANCE.getCDCPWD();
	
	public static final String JIRA_Prod = ConfigurationTool.INSTANCE.getJIRAProd();
	
	
	public static final String MenbersOfGroup = "/rest/api/2/group/member?groupname=";
	
	
	
	public static void main(String[] args) throws IOException {		
		
		String groupName = "Xhaul%20NSP";
		
		String restURL = JIRA_Prod+MenbersOfGroup+groupName;		
		
		//Set<String> AllGroups = new TreeSet<String>();		
		GetMethod response = getMethod(CDCADMIN, CDCADMIN_PWD, restURL);		
		
		List<Map<String, String>> allUsers = new ArrayList<JiraUser>();
		
		
		if (response != null) {
						
			if(response.getStatusLine().getStatusCode()>=200 && response.getStatusLine().getStatusCode()<=300){
				
				try {
					String responsebody = response.getResponseBodyAsString();
					
					JSONObject jsonobject = new JSONObject(responsebody);
					
					int TotalNumberOfUsers = new Integer(jsonobject.get("total").toString());					
							
					int totalCycle = TotalNumberOfUsers/50;
					
					for(int i = 0; i< (totalCycle+1); i++){
						
						restURL =JIRA_Prod+MenbersOfGroup+groupName+"&startAt="+(i*50);
						response = getMethod(CDCADMIN, CDCADMIN_PWD, restURL);						
						
						if(response.getStatusLine().getStatusCode()>=200 && response.getStatusLine().getStatusCode()<=300){
							try {
								responsebody = response.getResponseBodyAsString();
								
								jsonobject = new JSONObject(responsebody);
								
								JSONArray users = jsonobject.getJSONArray("values");
								
								System.out.println("total number of the users---->"+users.length());
								
								for (int j = 0; j < users.length(); j++) {
									
									JSONObject userInfo = (JSONObject)users.get(j);
									
									JiraUser user = new JiraUser(userInfo.get("name").toString(), userInfo.get("displayName").toString(), userInfo.get("emailAddress").toString());
									
									System.out.println("user info----->"+user.toString());
									
									
									allUsers.add(user);				
									
								}
								
								
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}finally {
								if(response !=null){
									response.releaseConnection();
								}
							}	
						}		
						
					}			
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					if(response !=null){
						response.releaseConnection();
					}
					
					System.out.println(allUsers.size());
				}			
				
			}		
			
		}
		
		
		GenerateExcelFile.CreateExcelFile(groupName, "", allUsers);
        
		
		
	}
	
	
	
	


	/**
	 * 
	 * @param userName
	 * @param password
	 * @param restUrl
	 * @return
	 */
	  public static GetMethod getMethod(String userName, String password, String restUrl) {
	      	GetMethod response = getMethodUseProxyAndBasicAuthorByJson(userName, password, restUrl);
				return response;
	  }

	/**
	 * 
	 * @param AuthorName
	 * @param AuthorPassword
	 * @param url
	 * @return
	 */

	public static GetMethod getMethodUseProxyAndBasicAuthorByJson(String AuthorName,
		String AuthorPassword,String url){
		BaseHttpDomain baseHttpDomain = new BaseHttpDomain();
		GetMethod method = baseHttpDomain.getGetMethodByJson(url);
		
		try {
			baseHttpDomain.getClientUseProxyAndBasicAuthor(AuthorName, 
					AuthorPassword).executeMethod(method);
			
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
	
		return method;
	}
	
	
}



class User{
	
	private String userName;
	
	private String fullName;
	
	private String emailAdress;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmailAdress() {
		return emailAdress;
	}

	public void setEmailAdress(String emailAdress) {
		this.emailAdress = emailAdress;
	}
	

	public User(String userName, String fullName, String emailAdress) {
		super();
		this.userName = userName;
		this.fullName = fullName;
		this.emailAdress = emailAdress;
	}

	@Override
	public String toString() {
		return "user [userName=" + userName + ", fullName=" + fullName + ", emailAdress=" + emailAdress + "]";
	}	
}




