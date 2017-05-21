package com.markwebb.reddit.save.manager.rest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * vert.x based server that exposes a REST endpoint for exposing the data stored
 * in the Mongo database.
 */
public class RedditSaveManagerRestServer extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(RedditSaveManagerRestServer.class);

    @Override
    public void start(Future<Void> fut) {

        Router router = Router.router(vertx);

        router.get("/reddit-manager/subreddits").handler(this::getSubreddits);
        router.get("/reddit-manager/:subreddit/saved").handler(this::getSavesForSubreddit);
        router.get("/reddit-manager/message/:message").handler(this::getMessage);
        router.get("/reddit-manager/subreddit/count/").handler(this::getMessageCountPerSubreddit);
        router.get("/reddit-manager/posts").handler(this::getPostsPerSubreddit);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private void getPostsPerSubreddit(RoutingContext routingContext) {

        JsonObject mongoconfig = new JsonObject().put("connection_string", "mongodb://localhost:27017").put("db_name", "reddit");
        MongoClient mongoClient = MongoClient.createShared(vertx, mongoconfig);

        //db.messages.find({}, {_id:1, subreddit:1, title:1, permalink:1} ).sort( {subreddit:1} )
        FindOptions findOptions = new FindOptions();
        findOptions.setFields(new JsonObject().put("_id", 1).put("subreddit",1).put("permalink",1).put("title",1));
        findOptions.setSort(new JsonObject().put("subreddit",1));
        mongoClient.findWithOptions("messages", new JsonObject(), findOptions, result -> {
            if (result.failed()) {
                result.cause().printStackTrace();
            }

            JsonObject flare = new JsonObject();
            JsonArray flareChildren = new JsonArray();
            flare.put("name", "flare");
            flare.put("children", flareChildren);


            /*
             * 1. Iterate over the results
             * 2. Please the results in a Map<String, List<Result>>
             * 3. Using the map from #2, create a JSON document
             */
            SubredditPostMap map = new SubredditPostMap();

            for( JsonObject object : result.result() ){
                map.addEntry(object.getString("subreddit"), object.getString("title"), object.getString("permalink"));
            }

            Set<String> subreddits = map.getSubreddits();
            logger.info("SUBREDDITS --> " + subreddits);
            for( String subreddit : subreddits ){
                List<SubredditPost> posts = map.getPosts(subreddit);

                JsonObject record = new JsonObject();
                record.put("name", subreddit);
                JsonArray array = new JsonArray();
                for( SubredditPost post : posts ){
                    array.add( new JsonObject().put("name", post.getTitle()).put("permalink", post.getPermalink()));
                }
                record.put("children", array);

                flareChildren.add(record);
            }

            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .putHeader("Access-Control-Allow-Origin", "*")
                    .end(flare.encodePrettily());
        });
    }

    private void getMessageCountPerSubreddit(RoutingContext routingContext) {

        /*
         * db.messages.aggregate( [ {$unwind: "$subreddit" }, {$group: {_id:
         * {$toLower: '$subreddit'},count: { $sum: 1 }}}, {$sort : { count : -1}
         * } ] ); );
         */

        JsonObject mongoconfig = new JsonObject().put("connection_string", "mongodb://localhost:27017").put("db_name", "reddit");

        MongoClient mongoClient = MongoClient.createShared(vertx, mongoconfig);

        JsonArray array = new JsonArray();
        array.add(new JsonObject().put("$unwind", "$subreddit"));
        array.add(new JsonObject().put("$group", new JsonObject().put("_id", new JsonObject().put("$toLower", "$subreddit")).put("count", new JsonObject().put("$sum", 1))));
        array.add(new JsonObject().put("$sort", new JsonObject().put("count", -1)));

        JsonObject command = new JsonObject().put("aggregate", "messages").put("pipeline", array);

        mongoClient.runCommand("aggregate", command, res -> {
            if (res.succeeded()) {
                JsonArray resArr = res.result().getJsonArray("result");
                for (int i = 0; i < resArr.size(); i++) {
                    JsonObject obj = resArr.getJsonObject(i);
                    logger.debug(obj.toString());
                }

                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .putHeader("Access-Control-Allow-Origin", "*")
                        .end(resArr.encodePrettily());
            } else {
                res.cause().printStackTrace();
            }
        });
    }

    private void getSubreddits(RoutingContext routingContext) {

        logger.debug("Looking up subreddits");

        JsonObject mongoconfig = new JsonObject().put("connection_string", "mongodb://localhost:27017").put("db_name", "reddit");

        MongoClient mongoClient = MongoClient.createShared(vertx, mongoconfig);
        mongoClient.distinct("messages", "subreddit", String.class.getName(), distinct -> {
            if (distinct.failed()) {
                distinct.cause().printStackTrace();
            }

            routingContext.response().putHeader("content-type", "application/json").end(new JsonObject().put("subreddits", distinct.result()).encodePrettily());
        });
    }

    private void getSavesForSubreddit(RoutingContext routingContext) {

        String subreddit = routingContext.request().getParam("subreddit");
        logger.debug("Looking up messages for subreddit [{}]", subreddit);

        JsonObject mongoconfig = new JsonObject().put("connection_string", "mongodb://localhost:27017").put("db_name", "reddit");

        MongoClient mongoClient = MongoClient.createShared(vertx, mongoconfig);
        mongoClient.find("messages", new JsonObject().put("subreddit", subreddit), res -> {
            routingContext.response().putHeader("content-type", "application/json").end(res.result().toString());
        });
    }

    private void getMessage(RoutingContext routingContext) {

        String messageId = routingContext.request().getParam("message");
        logger.debug("Looking up message ID [{}]", messageId);
        JsonObject mongoconfig = new JsonObject().put("connection_string", "mongodb://localhost:27017").put("db_name", "reddit");

        MongoClient mongoClient = MongoClient.createShared(vertx, mongoconfig);
        mongoClient.findOne("messages", new JsonObject().put("id", messageId), new JsonObject(), res -> {
            routingContext.response().putHeader("content-type", "application/json").end(res.result().toString());
        });
    }
}
