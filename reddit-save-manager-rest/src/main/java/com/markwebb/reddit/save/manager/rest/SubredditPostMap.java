package com.markwebb.reddit.save.manager.rest;

import java.util.*;

/**
 * Created by mark on 5/15/17.
 */
public class SubredditPostMap {

    private Map<String,List<SubredditPost>> map;

    public SubredditPostMap(){
        map = new HashMap<>();
    }

    public void addEntry( String subreddit, String title, String permalink ){

        List<SubredditPost> value = map.get(subreddit);

        if( value == null ) {
            SubredditPost post = new SubredditPost(title, permalink);
            value = new ArrayList<>();
            value.add(post);
            map.put(subreddit, value);
        } else {
            value.add(new SubredditPost(title,permalink));
            map.put(subreddit, value);
        }
    }

    public List<SubredditPost> getPosts(String subreddit){
        return map.get(subreddit);
    }

    public Set<String> getSubreddits(){
        return map.keySet();
    }
}
