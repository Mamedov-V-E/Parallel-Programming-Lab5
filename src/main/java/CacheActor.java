import akka.actor.AbstractActor;

import java.util.HashMap;
import java.util.Map;

public class CacheActor extends AbstractActor {
    private static final String NOT_CACHED_MESSAGE = "no cached result";
    private Map<String, Long> cache = new HashMap<>();

    public Receive createReceive() {
        return receiveBuilder().create()
                .match(CacheMessage.class, m ->
                        cache.put(m.getSite(), m.getAverageTime()))
                .match(CheckCachedMessage.class, m -> {
                    Long cashedTime = cache.get(m.getSite());
                    if (cashedTime != null) {
                        sender().tell(new CacheMessage(m.getSite(), cashedTime), self());
                    } else {
                        sender().tell(NOT_CACHED_MESSAGE, self());
                    }
                })
                .build();
    }
}
