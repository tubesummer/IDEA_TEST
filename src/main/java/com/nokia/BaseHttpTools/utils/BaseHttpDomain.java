package com.nokia.BaseHttpTools.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.UnsupportedEncodingException;


/**
 * @author bpan
 *
 */
public class BaseHttpDomain {
	
	private HttpClient client;
    private GetMethod method;
    private static HttpClientBuilder httpClientBuilder;
    private CloseableHttpClient closeableHttpClient;
    private HttpPost httpPost;
    private HttpPut httpPut;
    private HttpDelete httpDelete;
    
    public static HttpClientBuilder getHttpClientBuilder() {
    	httpClientBuilder = HttpClientBuilder.create();
        return httpClientBuilder;
    }
    
    public CloseableHttpClient getCloseableHttpClient(){
    	closeableHttpClient = getHttpClientBuilder().build();
    	return closeableHttpClient;
    }
    
    public GetMethod getGetMethod(String url) {
        method = new GetMethod(url);
        return method;
    }
    
    public GetMethod getGetMethodByJson(String url) {
        GetMethod method = new GetMethod(url);
        method.addRequestHeader("content-type", "application/json");
        method.addRequestHeader("Accept", "application/json");
        return method;
    }
    
    /**
     * 
     * @param url
     * @return
     */
    public PostMethod getPostMethod(String url) {
        PostMethod method = new PostMethod(url);
        return method;
    }
    
    public HttpPost getDefaultPostMethod(String url) {
        try {
            httpPost = new HttpPost(url);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return httpPost;
    }
    
    public HttpPost getDefaultPostMethodWithAuthorPostJson(String AuthorName, String AuthorPassword, String url, String postJSONString) {
        this.getDefaultPostMethod(url);
        String auth = new String(Base64.encodeBase64((AuthorName + ":" + AuthorPassword).getBytes()));
        httpPost.setHeader(HttpHander.TOKEN, HttpHander.CHECK);
        httpPost.setHeader(HttpHander.AUTHO, HttpHander.BASIC + auth);
        StringEntity params = null;
        try {
            params = new StringEntity(postJSONString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpPost.addHeader("content-type", "application/json");
        httpPost.addHeader("Accept", "application/json");
        httpPost.setEntity(params);
        return httpPost;
    }
    
    public HttpPut getDefaultPutMethod(String url) {
        try {
            httpPut = new HttpPut(url);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return httpPut;
    }
    
    public HttpPut getDefaultPutMethodWithAuthorPutJson(String AuthorName, String AuthorPassword, String url, String postJSONString) {
        this.getDefaultPutMethod(url);
        String auth = new String(Base64.encodeBase64((AuthorName + ":" + AuthorPassword).getBytes()));
        httpPut.setHeader(HttpHander.TOKEN, HttpHander.CHECK);
        httpPut.setHeader(HttpHander.AUTHO, HttpHander.BASIC + auth);
        StringEntity params = null;
        try {
            params = new StringEntity(postJSONString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpPut.addHeader("content-type", "application/json");
        httpPut.setEntity(params);
        return httpPut;
    }
    
    public HttpDelete getHttpDelete(String url, String authorName, String password){
    	httpDelete = new HttpDelete(url);
    	String auth = new String(Base64.encodeBase64((authorName + ":" + password).getBytes()));
    	httpDelete.setHeader(HttpHander.TOKEN,HttpHander.CHECK);
    	httpDelete.setHeader(HttpHander.AUTHO, HttpHander.BASIC + auth);

        httpDelete.addHeader("Content-Type", "application/json");
    	return httpDelete;
    }
    
    public HttpClient getClient() {
        return new HttpClient();
    }
    
    /**
     * 
     * @param AuthorName
     * @param AuthorPassword
     * @return
     */
    public HttpClient getClientUseProxyAndBasicAuthor(String AuthorName, String AuthorPassword) {
        //ProxyHost proxy = new ProxyHost("135.245.115.235", 80);
        client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);
        client.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(AuthorName, AuthorPassword));
        //client.getHostConfiguration().setProxyHost(proxy);
        return client;
    }
}