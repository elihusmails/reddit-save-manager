package com.markwebb.reddit.save.manager.rest;

/**
 * Created by mark on 5/15/17.
 */
public class SubredditPost {
    private String title;
    private String permalink;

    public SubredditPost(String title, String permalink) {
        this.title = title;
        this.permalink = permalink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }
}