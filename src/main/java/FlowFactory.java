import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;

public class FlowFactory {

    public static Flow<HttpRequest, HttpResponse, NotUsed>createFlow(Http http, ActorSystem actorSystem,)
}
