package com.markwebb.reddit.save.manager.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class RedditSaveManagerRestServer extends AbstractVerticle {
	
	private static final Logger logger = LoggerFactory.getLogger(RedditSaveManagerRestServer.class);
	
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
	
	private void getSubreddits(RoutingContext routingContext) {

		logger.debug("Looking up subreddits");
		
		JsonObject mongoconfig = new JsonObject()
				.put("connection_string", "mongodb://localhost:27017")
				.put("db_name", "reddit");

		MongoClient mongoClient = MongoClient.createShared(vertx, mongoconfig);
		
		mongoClient.distinct("messages", "subreddit", String.class.getName(), distinct -> {
			if( distinct.failed()){
				distinct.cause().printStackTrace();
			}
			
			routingContext
				.response()
				.putHeader("content-type", "application/json")
				.end(new JsonObject().put("subreddits", distinct.result()).toString());
		});
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
		logger.debug("Looking up message ID [{}]", messageId);
		JsonObject mongoconfig = new JsonObject()
				.put("connection_string", "mongodb://localhost:27017")
				.put("db_name", "reddit");

		MongoClient mongoClient = MongoClient.createShared(vertx, mongoconfig);
		mongoClient.findOne("messages", 
				new JsonObject().put("id", messageId), 
				new JsonObject(), 
				res -> {
					routingContext
						.response()
						.putHeader("content-type", "application/json")
						.end(res.result().toString());
		});
	}
}
