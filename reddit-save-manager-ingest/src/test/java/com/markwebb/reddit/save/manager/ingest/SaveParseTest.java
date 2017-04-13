package com.markwebb.reddit.save.manager.ingest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

public class SaveParseTest extends BaseTest {

	@Test
	public void test() throws IOException {

		String testData = readFile("src/test/resources/save-first-100.json");
		
		Map<?, ?> json = (Map<?, ?>) JSONValue.parse(testData);
		
		JSONObject data = (JSONObject)json.get("data");
		JSONArray children = (JSONArray)data.get("children");
		assertEquals(100, children.size());
		
		String before = ((String)data.get("before"));
		String after = ((String)data.get("after")).toString();
		
		assertEquals("t3_5qa6n3", after);
		assertNull(before);
	}
}