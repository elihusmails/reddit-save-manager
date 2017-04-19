package com.markwebb.reddit.save.manager.ingest.mongo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.MongoClient;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoRedditDAO {

	public static final String MONGO_DATABASE = "reddit";
	public static final String MONGO_COLLECTION = "messages";
	
	private MongoClient mongoClient;
	private MongoCollection<Document> collection;
	
	public MongoRedditDAO( String host, int port ){
		
		mongoClient = new MongoClient(host,port);
		MongoDatabase db = mongoClient.getDatabase(MONGO_DATABASE);
		collection = db.getCollection(MONGO_COLLECTION);
	}
	
	@SuppressWarnings("unchecked")
	public void addSavedRecords( String jsonData ){
		Map<?, ?> json = (Map<?, ?>) JSONValue.parse(jsonData);
		
		JSONObject data = (JSONObject)json.get("data");
		JSONArray children = (JSONArray)data.get("children");
		Iterator<JSONObject> iterator = children.iterator();
        while (iterator.hasNext()) {
        	JSONObject child = iterator.next();
        	JSONObject childData = (JSONObject)child.get("data");
        	addSavedRecord(childData.toJSONString());
        }
	}
	
	public void addSavedRecord( String json ){
		
		Document dbObject = Document.parse(json);
		collection.insertOne(dbObject);
	}
	
	public List<Document> getDocuments(){
		
		return new ArrayList<Document>();
	}
	
	public List<String> getSubreddits(){
		
		DistinctIterable<String> subreddits = collection.distinct("subreddit", String.class);
		return StreamSupport.stream(subreddits.spliterator(), false)
                .collect(Collectors.toList());
	}
}
