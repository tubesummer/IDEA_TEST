/**
 * 
 */
package com.nokia.jira.entity;

/**
 * @author bpan
 *
 * created 2018Äê2ÔÂ27ÈÕ
 */
public class JiraUser {
	private String csl;
	private String fullName;
	private String email;
	private String inputInfo;
	
	/**
	 * 
	 */
	public JiraUser() {
		// TODO Auto-generated constructor stub
	}
	
	
	public JiraUser(String csl, String fullName, String email) {
		super();
		this.csl = csl;
		this.fullName = fullName;
		this.email = email;
		
	}
	public String getCsl() {
		return csl;
	}
	public void setCsl(String csl) {
		this.csl = csl;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getInputInfo() {
		return inputInfo;
	}
	public void setInputInfo(String inputInfo) {
		this.inputInfo = inputInfo;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JiraUser [csl=" + csl + ", fullName=" + fullName + ", email=" + email + ", inputInfo=" + inputInfo
				+ "]";
	}
	
	
	
}
