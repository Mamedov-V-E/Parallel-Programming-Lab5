import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.*;
import akka.http.scaladsl.model.StatusCode;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.actor.TypedActor.self;


public class FlowFactory {
    private static final String SITE_PARAMETER_NAME = "testUrl";
    private static final String COUNT_PARAMETER_NAME = "count";
    private static final int MAX_SIMULTANEOUS_REQUESTS = 10;
    private static final Duration TIMOUT_MILLIS = Duration.ofMillis(10000);
    private static final int NANO_TO_MILLIS_FACTOR = 100

    public static Flow<HttpRequest, HttpResponse, NotUsed> createFlow(
            Http http,
            ActorSystem actorSystem,
            ActorRef cacheActor,
            ActorMaterializer materializer) {
        return Flow.of(HttpRequest.class).map(r -> {
            Query q = r.getUri().query();
            String site = q.get("testUrl").get();
            Integer count = Integer.parseInt(q.get("count").get());

            return new TestConnectionRequest(site, count);
        }).mapAsync(MAX_SIMULTANEOUS_REQUESTS, (r) ->
                Patterns.ask(cacheActor, new CheckCachedMessage(r.getSite()), TIMOUT_MILLIS)
                        .thenCompose(result ->
                                result.getClass() == String.class
                                        ? TestConnection(r, materializer)
                                        : CompletableFuture.completedFuture((CacheMessage)result)))
                .map(result -> {
                    cacheActor.tell(result, self());

                    return HttpResponse
                            .create()
                            .withStatus(StatusCodes.OK)
                            .withEntity(
                                    HttpEntities.create(
                                            result.getSite() + ' ' + result. getAverageTime()
                                    )
                            );
                });
    }

    private static CompletionStage<CacheMessage> TestConnection (TestConnectionRequest r, Materializer materializer) {
        return Source
                .from(Collections.singletonList(r))
                .toMat(TestSink(), Keep.right())
                .run(materializer)
                .thenApply(sumTime -> new CacheMessage(
                        r.getSite(),
                        sumTime / r.getCount()));
    }

    private static Sink<TestConnectionRequest, CompletionStage<Long>> TestSink() {
        return Flow.<TestConnectionRequest>create()
                .mapConcat(r -> Collections.nCopies(r.getCount(), r.getSite()))
                .mapAsync(MAX_SIMULTANEOUS_REQUESTS, site -> {
                    long startTime = System.nanoTime();
                    AsyncHttpClient httpClient = Dsl.asyncHttpClient();

                    return httpClient
                            .prepareGet(site)
                            .execute()
                            .toCompletableFuture()
                            .thenApply(response -> System.nanoTime() - startTime);

                })
                .toMat(Sink.fold(0L, Long::sum), Keep.right());
    }

}
