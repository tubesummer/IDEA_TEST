/**
 * 
 */
package com.nokia.jira.utils;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import com.nokia.jira.entity.JiraUser;

/**
 * @author bpan
 *
 * created 2018��3��2��
 */
public class LdapCheck {

	/*
	 * 
	 * �������ǵ�����Ldap���������
	 * 
	 * ctxFactory = "com.sun.jndi.ldap.LdapCtxFactory"; ldapUrl =
	 * "ldap://destgsu0615.de.alcatel-lucent.com:3000"; userName =
	 * "uid=alfresco_gerard,dc=alfresco_gerard,dc=apps,dc=alcatel" passwd =
	 * "94frEsUN";
	 */
	
	static  LdapContext ctx = null;
	static String ctxFactory = "com.sun.jndi.ldap.LdapCtxFactory";
	static String ldapUrl = "ldap://destgsu0615.de.alcatel-lucent.com:3000";
	static String userName = "uid=alfresco_gerard,dc=alfresco_gerard,dc=apps,dc=alcatel";
	static String passwd = "94frEsUN";
	

	/**
	 * ��ʼ��ldap
	 * 
	 * @author guxing
	 * 
	 */
	public static void initLdap() {
		// ad������
		// ��������ֵ
		Hashtable<String, String> HashEnv = new Hashtable<String, String>();

		HashEnv.put(Context.INITIAL_CONTEXT_FACTORY, ctxFactory);
		HashEnv.put(Context.PROVIDER_URL, ldapUrl);
		HashEnv.put(Context.SECURITY_PRINCIPAL, userName);
		HashEnv.put(Context.SECURITY_CREDENTIALS,passwd);
		HashEnv.put(Context.SECURITY_AUTHENTICATION, "simple");

		try {
			// ��ʼ��Ldap�ɹ�
			ctx = new InitialLdapContext(HashEnv, null);
			System.out.println("��ʼ��ldap�ɹ���");
		} catch (NamingException e) {
			e.printStackTrace();
			System.err.println("Throw Exception : " + e);
		}
	}
	

	/**
	 * �ر�ldap
	 * @return 
	 */
	public static void closeLdap() {
		try {
			ctx.close();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static JiraUser GetADInfo(String type,String searchInfo) {

		
		String company = "";
//		String result = "";
		try {
			// ��ڵ�
			String searchBase = "DC=Users,DC=Alcatel";
			// LDAP������������
			// cn=*name*ģ����ѯ cn=name ��ȷ��ѯ
			// String searchFilter = "(objectClass="+type+")";
			//String searchFilter = "(&(objectClass=" + type + ")(" + "mail=" + searchMail + "))";
			//String searchFilter = "(&(objectClass=" + type + ")(" + "mail=" + searchMail + ")(" + "name=" + searchCsl + "))";
			String searchFilter=null; 
			//������С�@���͡�.com���ַ��������mail��ѯ������ַ�����û�С� �����ո����������csl��ѯ���������fullname��ѯ
			if(searchInfo.indexOf("@")!=-1 && searchInfo.indexOf(".com")!=-1){
				
				    searchFilter = "(&(objectClass=" + type + ")(" + "mail=" + searchInfo + "))";
				    
			}else if(searchInfo.indexOf(" ") == -1 && searchInfo.charAt(0) < 'A' || searchInfo.charAt(0) > 'Z'){
				
				    searchFilter  = "(&(objectClass=" + type + ")(" + "uid=" + searchInfo + "))";
				    
			}else if(searchInfo.length()>=4){
				
				if(searchInfo.trim().length()==9 
					&& searchInfo.indexOf(" ") == -1 
					&& searchInfo.indexOf("-") ==-1 
					&& searchInfo.charAt(3) >= '0' 
					&& searchInfo.charAt(3) <= '9')
				{
					
					searchFilter = "(&(objectClass=" + type + ")(" + "hrid=" + searchInfo + "))";
					
				}else{
					
					searchFilter = "(&(objectClass=" + type + ")(" + "cn=" + searchInfo + "))";
					
				}
			}else{
				
					searchFilter = "(&(objectClass=" + type + ")(" + "cn=" + searchInfo + "))";
					
		    }
			//searchFilter = "(&(objectClass=" + type + ")(" + "cn=" + searchInfo + "))";
			//String  searchFilter= "(&(objectClass=" + type + ")(" + "fullName=" + searchCsl + "))";
			// ��������������
			SearchControls searchCtls = new SearchControls();
			// ����������Χ
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			// String returnedAtts[] = { "memberOf" }; // ���Ʒ�������
			// searchCtls.setReturningAttributes(returnedAtts); // ���÷������Լ�
			// �������򷵻���������
			// �������õ���ڵ㡢�����������������������LDAP�õ����
			NamingEnumeration answer = ctx.search(searchBase, searchFilter, searchCtls);

			// ��ʼ�����������Ϊ0
			int totalResults = 0;// Specify the attributes to return
			int rows = 0;
			while (answer.hasMoreElements()) {// ���������
				SearchResult sr = (SearchResult) answer.next();// �õ���������������DN
				++rows;
				String dn = sr.getName();
				// System.out.println(dn);

				Attributes Attrs = sr.getAttributes();// �õ��������������Լ�
				// ����������Ϊ�գ�ȡ��������Ҫ������

				if (Attrs != null) {
					// ����һ��Hashmap������Ž����
					Map<String, String> map = new HashMap<String, String>();
					try {
						// ��������������ǵ����ݷŵ�map��
						for (NamingEnumeration ne = Attrs.getAll(); ne.hasMore();) {
							Attribute Attr = (Attribute) ne.next();// �õ���һ������
							// ��ӡ�����ǵ�����ֵ
							String kId = Attr.getID().toString();
							//System.out.println(" AttributeID=��������" + kId);
							// ��ȡ����ֵ
							for (NamingEnumeration e = Attr.getAll(); e.hasMore(); totalResults++) {
								company = e.next().toString();
								map.put(kId, company);
								 //System.out.println(" AttributeValues=����ֵ��" +company);
							}
							// System.out.println(" ---------------");

						}
                       
						
						JiraUser ju=new JiraUser();
                        ju.setCsl(map.get("uid"));
                        ju.setFullName(map.get("alcatelCIL"));
                        ju.setEmail(map.get("mail"));
                        ju.setInputInfo(searchInfo);
                        return ju;

					} catch (NamingException e) {
						System.err.println("Throw Exception : " + e);
					}
				} // if
			} // while
			System.out.println("�ַ������ȣ�"+(searchInfo.length()));
			System.out.println("************************************************");
			System.out.println("Number: " + totalResults);
			System.out.println("�ܹ��û�����" + rows);
		} catch (NamingException e) {
			e.printStackTrace();
			System.err.println("Throw Exception : " + e);
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		LdapCheck lCheck = new LdapCheck();
		lCheck.initLdap();
		JiraUser jiraUser = lCheck.GetADInfo("inetorgperson", "KUMAR J Senthil");
		
		System.out.println(jiraUser);
	}

}
