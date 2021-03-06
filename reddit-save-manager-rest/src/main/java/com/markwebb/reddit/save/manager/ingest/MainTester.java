package com.markwebb.reddit.save.manager.ingest;

import com.markwebb.reddit.save.manager.ingest.mongo.MongoRedditDAO;
import org.apache.http.client.ClientProtocolException;

import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

public class MainTester {

    private static final int QUERY_PAUSE_TIME = 10;

	public static void main(String[] args) throws ClientProtocolException, IOException, InterruptedException {

		System.out.println("Enter your username: ");
		Scanner scanner = new Scanner(System.in);
		String username = scanner.nextLine();
		
		Console console = System.console();
		char[] password = console.readPassword("%s", "Enter your reddit.com password:");
		
		System.out.println("Enter your clientId: ");
		String clientId = scanner.nextLine();
		
		System.out.println("Enter your client secret: ");
		String clientSecret = scanner.nextLine();
		
		System.out.println("Username = [" + username + "]");
		System.out.println("Password = [" + new String(password) + "]");
		System.out.println("Client = [" + clientId + "]");
		System.out.println("Client Secret = [" + clientSecret + "]");
		
		RedditOAuth oAuth = new RedditOAuth(clientId, clientSecret, username, new String(password));
		
		Reddit reddit = new Reddit();
		GetSaveResult saves = reddit.getAllSaves(username, oAuth.getAccessToken());
//		System.out.println(saves.getContent());
		Thread.sleep(QUERY_PAUSE_TIME * 1000 );
		
		while( saves.isMoreData() ){
			System.out.println("BEFORE: [" + saves.getFirstThing() + "], AFTER: [" + saves.getLastThing() + "]");
			MongoRedditDAO dao = new MongoRedditDAO("127.0.0.1", 27017);
			dao.addSavedRecords(saves.getContent());
			
			saves = reddit.getAllSaves(username, oAuth.getAccessToken(), saves.getLastThing(), saves.getFirstThing(), 100);
			
			Thread.sleep(QUERY_PAUSE_TIME * 1000 );
		}
		
		scanner.close();
	}
}
