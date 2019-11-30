import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import javafx.util.Pair;

public class FlowFactory {

    public static Flow<HttpRequest, HttpResponse, NotUsed> createFlow(
            Http http,
            ActorSystem actorSystem,
            ActorMaterializer materializer) {
        Flow.of(HttpRequest.class).map(r -> new Pair)
    }
}
