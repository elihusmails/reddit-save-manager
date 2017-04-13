package com.markwebb.reddit.save.manager.rest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class RedditSaveManagerRestServer extends AbstractVerticle {
	
	@Override
	public void start(Future<Void> fut) {

		Router router = Router.router(vertx);
		
		router.get("/reddit-manager/subreddits").handler(this::getSubreddits);
		router.get("/reddit-manager/:subreddit/saved").handler(this::getSavesForSubreddit);
		router.get("/reddit-manager/:message").handler(this::getMessage);
		
		vertx
			.createHttpServer()
			.requestHandler(router::accept)
			.listen(8080);
	}
	
	private void getSubreddits(RoutingContext routingContext){
		
		routingContext
			.response()
			.putHeader("content-type", "application/json")
			.end("subreddits");
			
	}
	
	private void getSavesForSubreddit(RoutingContext routingContext){
		
		String subreddit = routingContext.request().getParam("subreddit");
		
		routingContext
			.response()
			.putHeader("content-type", "application/json")
			.end("Looking up saves for " + subreddit);
	}
	
	private void getMessage(RoutingContext routingContext){
		
		String messageId = routingContext.request().getParam("message");
		
		routingContext
			.response()
			.putHeader("content-type", "application/json")
			.end("Looking up message id " + messageId);
	}
}
