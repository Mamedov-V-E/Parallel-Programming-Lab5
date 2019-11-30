import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.http.javadsl.model.StatusCode;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import javafx.util.Pair;
import scala.concurrent.Future;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.actor.TypedActor.self;


public class FlowFactory {
    private static final String SITE_PARAMETER_NAME = "testUrl";
    private static final String COUNT_PARAMETER_NAME = "count";
    private static final int MAX_SIMULTANEOUS_REQUESTS = 10;
    private static final Duration TIMOUT_MILLIS = Duration.ofMillis(10000);

    public static Flow<HttpRequest, HttpResponse, NotUsed> createFlow(
            Http http,
            ActorSystem actorSystem,
            ActorRef cacheActor,
            ActorMaterializer materializer) {
        Flow.of(HttpRequest.class).map(r -> {
            Query q = r.getUri().query();
            String site = q.get("testUrl").get();
            Long count = Long.parseLong(q.get("count").get());

            return new Pair(site, count);
        }).mapAsync(MAX_SIMULTANEOUS_REQUESTS, (p) ->
                Patterns.ask(cacheActor, new CheckCachedMessage(p.getKey().toString()), TIMOUT_MILLIS)
                        .thenCompose(result ->
                                result.getClass() == String.class
                                        ? TestConnection(p.getKey().toString(), (Long)p.getValue())
                                        : CompletableFuture.completedFuture((CacheMessage)result)))
                .map(result -> {
                    cacheActor.tell(result, self());

                    return HttpResponse
                            .create()
                            .withStatus(StatusCode.OK)
                });
    }

    private static CompletionStage<CacheMessage> TestConnection (String site, Long count) {

    }

}
