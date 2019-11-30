import akka.actor.AbstractActor;

import java.util.HashMap;
import java.util.Map;

public class CacheActor extends AbstractActor {
    private Map<String, Long> cache = new HashMap<>();

    public Receive createReceive() {
        return receiveBuilder().create()
                .match()
                .build();
    }
}
