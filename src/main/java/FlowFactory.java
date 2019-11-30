import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import javafx.util.Pair;
import scala.concurrent.Future;

import java.util.regex.Pattern;

public class FlowFactory {
    private static final String SITE_PARAMETER_NAME = "testUrl";
    private static final String COUNT_PARAMETER_NAME = "count";
    private static final int MAX_SIMULTANEOUS_REQUESTS = 10;
    private static final long TIMOUT_MILLIS = 10000;

    public static Flow<HttpRequest, HttpResponse, NotUsed> createFlow(
            Http http,
            ActorSystem actorSystem,
            ActorRef cacheActor,
            ActorMaterializer materializer) {
        Flow.of(HttpRequest.class).map(r -> {
            Query q = r.getUri().query();
            return new Pair(q.get("testUrl").get(), Long.parseLong(q.get("count").get()));
        }).mapAsync(MAX_SIMULTANEOUS_REQUESTS, p ->
                Patterns.ask(cacheActor, new CheckCachedMessage(p.getKey().toString()), TIMOUT_MILLIS).the)
    }
}
