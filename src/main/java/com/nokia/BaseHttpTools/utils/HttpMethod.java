package com.nokia.BaseHttpTools.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;

public class HttpMethod {
	
	
	/**
	 * 将响应对象转换成String 字符串
	 * @param response
	 * @return
	 */
	public static String convertResponseAsString(GetMethod response){
		
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		StringBuffer stringBuffer = null;
		String tempString = null;
		try {
			inputStream = response.getResponseBodyAsStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			stringBuffer = new StringBuffer();
			while ((tempString = bufferedReader.readLine()) != null) {
				stringBuffer.append(tempString);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			
			if (bufferedReader !=null ) {
				
				try {
					bufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return stringBuffer.toString();
	}

    /**
	 * 获取http请求的响应结果
	 * @param userName
	 * @param password
	 * @param restURL  在字符串URL中空格使用“+”或者“20%”代替， 双引号要使用单引号代替并使用转移符\
	 * @return
	 */
	public static GetMethod httpGet(String userName, String passWord, String restUrl) {
	      	GetMethod response = getMethodUseProxyAndBasicAuthorByJson(userName, passWord, restUrl);
				return response;
	}
	  
	public static GetMethod getMethodUseProxyAndBasicAuthorByJson(String AuthorName, String AuthorPassword, String url) {
	  	  BaseHttpDomain baseHttpDomain=  new BaseHttpDomain();
	  	  GetMethod method = baseHttpDomain.getGetMethodByJson(url);
	      try {
	            baseHttpDomain.getClientUseProxyAndBasicAuthor(AuthorName,
	          		  AuthorPassword).executeMethod(method);
	      } catch (IOException e) {
	            e.printStackTrace();
	      }
	      return method;
	}
	
	//httpPost
	public static int httpPost(String userName, String passWord, String restUrl,String postJSONString) {
	      HttpResponse response = getPostMethodWithAuthorPostJson(userName, passWord, restUrl,postJSONString);
	      if (response != null) {
	          if (response.getStatusLine().getStatusCode() >= 200 
	        		  && response.getStatusLine().getStatusCode() <= 300) {
	              return 1;
	          } else return 0;
	      } else return 0;
	}

	public static HttpResponse getPostMethodWithAuthorPostJson(String AuthorName, String AuthorPassword, String url, String postJSONString) {
		  BaseHttpDomain baseHttpDomain =  new BaseHttpDomain();
	      HttpPost httpPost = baseHttpDomain.getDefaultPostMethodWithAuthorPostJson(AuthorName, AuthorPassword, url, postJSONString);
	      HttpResponse httpResponse = null;
	      try {
//	          httpResponse = baseHttpDomain.getDefaultHttpClient().execute(httpPost);
	    	  httpResponse = baseHttpDomain.getCloseableHttpClient().execute(httpPost);
	      }  catch (IOException e) {
	          e.printStackTrace();
	      }
	      return httpResponse;
	}
	
	 //httpDelete
	 public static int httpDelete(String userName, String passWord, String restUrl) {
			HttpResponse response = getDeleteMethod(userName, passWord, restUrl);
	        if (response != null) {
	            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 300) {
	                return 1;
	            } else return 0;
	        } else return 0;
	 }

	 public static HttpResponse getDeleteMethod(String AuthorName, String AuthorPassword, String url) {
	    	BaseHttpDomain baseHttpDomain=  new BaseHttpDomain();
	        HttpDelete httpDelete=null;
	        HttpResponse httpResponse=null;
	        try {
	            httpDelete = baseHttpDomain.getHttpDelete(url, AuthorName, AuthorPassword);
	            httpResponse = baseHttpDomain.getCloseableHttpClient().execute(httpDelete);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return httpResponse;
	 }
}
