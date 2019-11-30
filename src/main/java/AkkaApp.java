import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

public class AkkaApp {
    public static final String ACTOR_SYSTEM_NAME = "routes";
    public static final String HOST_NAME = "localhost";
    public static final int PORT_NUMBER = 8080;

    public static void main(String[] args) throws IOException {
        System.out.println("start!");
        ActorSystem system = ActorSystem.create(ACTOR_SYSTEM_NAME);
        ActorRef cacheActor = system.actorOf(Props.create(CacheActor.class));

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                FlowFactory.createFlow(http, system, cacheActor, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(HOST_NAME, PORT_NUMBER),
                materializer
        );
        System.out.println("Server online at http://" + HOST_NAME + ":" + PORT_NUMBER + "\nPress RETURN to stop...");
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }
}
