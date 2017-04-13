package com.markwebb.reddit.save.manager.ingest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONValue;

public class RedditOAuth extends RedditWorker {

	private String clientId;
	private String clientSecret;
	private String username;
	private String password;
	
	public RedditOAuth(String clientId, String clientSecret, String username, String password) {
		super();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.username = username;
		this.password = password;
	}

	public String getAccessToken() throws ClientProtocolException, IOException{
		
		HttpClientContext context = HttpClientContext.create();
		
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(new AuthScope("ssl.reddit.com", 443),
                    new UsernamePasswordCredentials(getClientId(), getClientSecret()));
		context.setCredentialsProvider(credentialsProvider);
		
        CloseableHttpClient httpclient = HttpClients.createDefault();
        
        try {
            HttpPost httppost = new HttpPost("https://ssl.reddit.com/api/v1/access_token");

            List <NameValuePair> nvps = new ArrayList <NameValuePair>(3);
            nvps.add(new BasicNameValuePair("username", getUsername()));
            nvps.add(new BasicNameValuePair("password", getPassword()));
            nvps.add(new BasicNameValuePair("grant_type", "password"));

            httppost.setEntity(new UrlEncodedFormEntity(nvps));
            httppost.addHeader("User-Agent", "java:MyRedditCatalog:v1.0 by /u/elihusmails");
            httppost.setHeader("Accept","any;");


            HttpResponse response = httpclient.execute(httppost, context);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                 BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                 StringBuilder content = new StringBuilder();
                 String line;
                 while (null != (line = br.readLine())) {
                     content.append(line);
                 }
                 System.out.println(content.toString());
                 Map<?, ?> json = (Map<?, ?>) JSONValue.parse(content.toString());
                 if (json.containsKey("access_token")) {
                    return (String) (json.get("access_token"));
                 }
            }
            EntityUtils.consume(entity);
        } finally {
            httpclient.close();
        }
        return null;
	}

	@SuppressWarnings("rawtypes")
	public String getUsername(String accessToken) throws ClientProtocolException, IOException {
		String content = internalWorker("https://oauth.reddit.com/api/v1/me", accessToken);
		System.out.println("Content::" + content);
		Map json = (Map) JSONValue.parse(content);
		if (json.containsKey("name")) {
			return (String) (json.get("name"));
		}
		
		return null;
	}
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
