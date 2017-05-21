package com.markwebb.reddit.save.manager.ingest;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reddit extends RedditWorker {

	private static final Logger log = LoggerFactory.getLogger(Reddit.class);
	
	public GetSaveResult getAllSaves(String username, String accessToken ) throws ClientProtocolException, IOException {
		return getAllSaves( username, accessToken, null, null, 100);
	}
	
	public GetSaveResult getAllSaves(String username, String accessToken, String after, String before, int limit ) throws ClientProtocolException, IOException {
		
		log.info("BEFORE: [{}], AFTER: [{}], LIMIT [{}]", before, after, limit);
		
		if( limit < 25 )
			limit = 25;		// This is the default as per the API -- https://www.reddit.com/dev/api#GET_user_{username}_saved
		
		StringBuffer url = new StringBuffer();
		url.append("https://oauth.reddit.com/user/");
		url.append(username);
		url.append("/saved?limit=" + limit);
		
		if( after != null ){
			url.append("&after=" + after);
		}
		
		log.info("URL = " + url);
		
		String content = internalWorker(url.toString(), accessToken);
		log.info("Response:" + content);
		
		//TODO: Check for error condition
		
		Map<?, ?> json = (Map<?, ?>) JSONValue.parse(content);
		
		JSONObject data = (JSONObject)json.get("data");
		JSONArray children = (JSONArray)data.get("children");
		
		String newBefore = ((String)data.get("before"));
		String newAfter = ((String)data.get("after"));
		
		if( newBefore != null )
			newBefore = newBefore.toString();
		
		if( newAfter != null )
			newAfter = newAfter.toString();
		
//		Iterator<String> iterator = (Iterator<String>)children.iterator();
//        while (iterator.hasNext()) {
//            System.out.println(iterator.next());
//        }
		
		log.info("Children length = " + children.size());
		return new GetSaveResult(newBefore, newAfter, content, children.size(), (children.size() == limit));
	}
}
