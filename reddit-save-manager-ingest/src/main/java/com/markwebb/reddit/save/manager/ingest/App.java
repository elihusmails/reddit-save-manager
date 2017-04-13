package com.markwebb.reddit.save.manager.ingest;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class App 
{
    public static void main( String[] args ) throws ClientProtocolException, IOException, InterruptedException 
    {
        RedditOAuth r = new RedditOAuth(
        		"L4eShP3PvdHgEA", 
        		"MYVZqerV3XsyVJQgcKxF8Vx-4uM",
        		"elihusmails",
        		"fukin.eediot");
        
        String token = r.getAccessToken();
        Thread.sleep(3000);
//        String username = r.getUsername(token);
//        Thread.sleep(2000);
//        System.out.println("USERNAME ==> " + username);

        Reddit reddit = new Reddit();
        String saves = reddit.getAllSaves("elihusmails", token);
        System.out.println(saves);
    }
}
