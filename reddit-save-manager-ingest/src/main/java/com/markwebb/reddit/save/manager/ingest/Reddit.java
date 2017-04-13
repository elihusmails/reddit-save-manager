package com.markwebb.reddit.save.manager.ingest;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Reddit extends RedditWorker {

	public String getSaves(String username, String accessToken ) throws ClientProtocolException, IOException{
		String content = internalWorker("https://oauth.reddit.com/user/" + username + "/saved", accessToken);
		return content;
	}
	
	public String getAllSaves(String username, String accessToken ) throws ClientProtocolException, IOException {
		
		String content = internalWorker("https://oauth.reddit.com/user/" + username + "/saved?limit=100", accessToken);
		
		Map<?, ?> json = (Map<?, ?>) JSONValue.parse(content);
		
		JSONObject data = (JSONObject)json.get("data");
		JSONArray children = (JSONArray)data.get("children");
		
		String before = ((String)data.get("before"));
		String after = ((String)data.get("after")).toString();
		
		Iterator<String> iterator = children.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
		
		return content;
	}
}
