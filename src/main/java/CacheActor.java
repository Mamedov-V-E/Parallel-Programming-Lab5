import akka.actor.AbstractActor;

import java.util.HashMap;
import java.util.Map;

public class CacheActor extends AbstractActor {
    private Map<String, Long> cache = new HashMap<>();

    public Receive createReceive() {
        return receiveBuilder().create()
                .match(CacheMessage.class, m ->
                        cache.put(m.getSite(), m.getAverageTime()))
                .match(CheckCachedMessage.class, m ->
                        )
                .build();
    }
}
