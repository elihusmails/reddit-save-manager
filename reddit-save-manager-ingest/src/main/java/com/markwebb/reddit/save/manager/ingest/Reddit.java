package com.markwebb.reddit.save.manager.ingest;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class Reddit extends RedditWorker {

	public String getSaves(String username, String accessToken ) throws ClientProtocolException, IOException{
		String content = internalWorker("https://oauth.reddit.com/user/" + username + "/saved", accessToken);
		return content;
	}
	
	public String getAllSaves(String username, String accessToken ) throws ClientProtocolException, IOException {
		
		String content = internalWorker("https://oauth.reddit.com/user/" + username + "/saved?limit=100", accessToken);
		
		
		
		return content;
	}
}
