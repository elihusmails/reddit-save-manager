package com.markwebb.reddit.save.manager.ingest;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Reddit extends RedditWorker {

	public GetSaveResult getAllSaves(String username, String accessToken ) throws ClientProtocolException, IOException {
		return getAllSaves( username, accessToken, null, null, 100);
	}
	
	public GetSaveResult getAllSaves(String username, String accessToken, String after, String before, int limit ) throws ClientProtocolException, IOException {
		
		if( limit < 25 )
			limit = 25;		// This is the default as per the API -- https://www.reddit.com/dev/api#GET_user_{username}_saved
		
		StringBuffer url = new StringBuffer();
		url.append("https://oauth.reddit.com/user/");
		url.append(username);
		url.append("/saved?limit=100");
		if( after != null && before != null ){
			url.append("&after=" + after);
			url.append("&before=" + before);
		}
		
		System.out.println("URL = " + url);
		
		String content = internalWorker(url.toString(), accessToken);
//		System.out.println(content);
		Map<?, ?> json = (Map<?, ?>) JSONValue.parse(content);
		
		JSONObject data = (JSONObject)json.get("data");
		JSONArray children = (JSONArray)data.get("children");
		
		String newBefore = ((String)data.get("before"));
		String newAfter = ((String)data.get("after")).toString();
		
//		Iterator<String> iterator = (Iterator<String>)children.iterator();
//        while (iterator.hasNext()) {
//            System.out.println(iterator.next());
//        }
		
		System.out.println("Children length = " + children.size());
		return new GetSaveResult(newBefore, newAfter, content, children.size(), (children.size() == limit));
	}
}
