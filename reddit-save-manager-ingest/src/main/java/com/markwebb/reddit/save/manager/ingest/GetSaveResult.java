package com.markwebb.reddit.save.manager.ingest;

public class GetSaveResult {

	private String firstThing, lastThing, content;
	private int length;
	private boolean moreData;
	
	public GetSaveResult(String firstThing, String lastThing, String content, int length, boolean moreData) {
		super();
		this.firstThing = firstThing;
		this.lastThing = lastThing;
		this.content = content;
		this.length = length;
		this.moreData = moreData;
	}

	public boolean isMoreData() {
		return moreData;
	}

	public void setMoreData(boolean moreData) {
		this.moreData = moreData;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getFirstThing() {
		return firstThing;
	}

	public void setFirstThing(String firstThing) {
		this.firstThing = firstThing;
	}

	public String getLastThing() {
		return lastThing;
	}

	public void setLastThing(String lastThing) {
		this.lastThing = lastThing;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
