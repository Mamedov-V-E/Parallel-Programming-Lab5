import akka.actor.ActorSystem;
import akka.http.javadsl.Http;

import java.io.IOException;

public class AkkaApp {
    public static void main(String[] args) throws IOException {
        System.out.println("start!");
        ActorSystem system = ActorSystem.create("routes");
        final Http http = Http.get(system);
    }
}
