package com.markwebb.reddit.save.manager.ingest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SaveParseTest extends BaseTest {

	@Test
	public void test() throws IOException {

		String testData = readFile("src/test/resources/save-first-100.json");
		
		Map<?, ?> json = (Map<?, ?>) JSONValue.parse(testData);
		
		JSONObject data = (JSONObject)json.get("data");
		JSONArray children = (JSONArray)data.get("children");
		Assert.assertEquals(100, children.size());
		
		String before = ((String)data.get("before"));
		String after = ((String)data.get("after")).toString();
		
		assertEquals("t3_5qa6n3", after);
		assertNull(before);
		
		Iterator<JSONObject> iterator = children.iterator();
        while (iterator.hasNext()) {
        	
        	JSONObject child = iterator.next();
        	JSONObject childData = (JSONObject)child.get("data");
        	//System.out.println((String)childData.get("subreddit"));
        	System.out.println(childData);
        }
	}
}
