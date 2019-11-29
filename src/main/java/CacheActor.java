import akka.actor.AbstractActor;

public class CacheActor extends AbstractActor {


    public Receive createReceive() {
        return receiveBuilder().create()
                .match()
                .build();
    }
}
