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
 * created 2018年3月2日
 */
public class LdapCheck {

	/*
	 * 
	 * 设置我们的连接Ldap服务的属性
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
	 * 初始化ldap
	 * 
	 * @author guxing
	 * 
	 */
	public static void initLdap() {
		// ad服务器
		// 配置属性值
		Hashtable<String, String> HashEnv = new Hashtable<String, String>();

		HashEnv.put(Context.INITIAL_CONTEXT_FACTORY, ctxFactory);
		HashEnv.put(Context.PROVIDER_URL, ldapUrl);
		HashEnv.put(Context.SECURITY_PRINCIPAL, userName);
		HashEnv.put(Context.SECURITY_CREDENTIALS,passwd);
		HashEnv.put(Context.SECURITY_AUTHENTICATION, "simple");

		try {
			// 初始化Ldap成功
			ctx = new InitialLdapContext(HashEnv, null);
			System.out.println("初始化ldap成功！");
		} catch (NamingException e) {
			e.printStackTrace();
			System.err.println("Throw Exception : " + e);
		}
	}
	

	/**
	 * 关闭ldap
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
			// 域节点
			String searchBase = "DC=Users,DC=Alcatel";
			// LDAP搜索过滤器类
			// cn=*name*模糊查询 cn=name 精确查询
			// String searchFilter = "(objectClass="+type+")";
			//String searchFilter = "(&(objectClass=" + type + ")(" + "mail=" + searchMail + "))";
			//String searchFilter = "(&(objectClass=" + type + ")(" + "mail=" + searchMail + ")(" + "name=" + searchCsl + "))";
			String searchFilter=null; 
			//如果含有“@”和“.com”字符，则进入mail查询，如果字符串里没有“ ”（空格符），进入csl查询，有则进入fullname查询
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
			// 创建搜索控制器
			SearchControls searchCtls = new SearchControls();
			// 设置搜索范围
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			// String returnedAtts[] = { "memberOf" }; // 定制返回属性
			// searchCtls.setReturningAttributes(returnedAtts); // 设置返回属性集
			// 不设置则返回所有属性
			// 根据设置的域节点、过滤器类和搜索控制器搜索LDAP得到结果
			NamingEnumeration answer = ctx.search(searchBase, searchFilter, searchCtls);

			// 初始化搜索结果数为0
			int totalResults = 0;// Specify the attributes to return
			int rows = 0;
			while (answer.hasMoreElements()) {// 遍历结果集
				SearchResult sr = (SearchResult) answer.next();// 得到符合搜索条件的DN
				++rows;
				String dn = sr.getName();
				// System.out.println(dn);

				Attributes Attrs = sr.getAttributes();// 得到符合条件的属性集
				// 如果结果集不为空，取出我们需要的数据

				if (Attrs != null) {
					// 创建一个Hashmap用来存放结果集
					Map<String, String> map = new HashMap<String, String>();
					try {
						// 遍历结果集将我们的数据放到map中
						for (NamingEnumeration ne = Attrs.getAll(); ne.hasMore();) {
							Attribute Attr = (Attribute) ne.next();// 得到下一个属性
							// 打印出我们的属性值
							String kId = Attr.getID().toString();
							//System.out.println(" AttributeID=属性名：" + kId);
							// 读取属性值
							for (NamingEnumeration e = Attr.getAll(); e.hasMore(); totalResults++) {
								company = e.next().toString();
								map.put(kId, company);
								 //System.out.println(" AttributeValues=属性值：" +company);
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
			System.out.println("字符串长度："+(searchInfo.length()));
			System.out.println("************************************************");
			System.out.println("Number: " + totalResults);
			System.out.println("总共用户数：" + rows);
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
