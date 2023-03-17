import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.concurrent.CompletionStage;

public class ExploreLogging {

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(), "actorSystem");
        Source<Integer, NotUsed> source = Source.range(1, 10);
        Flow<Integer, Integer, NotUsed> flow = Flow.of(Integer.class)
                .log("flow input")
                .map(x -> x * 2)
                .log("flow output");
        Sink<Integer, CompletionStage<Done>> sink = Sink.foreach(System.out::println);
        source.via(flow).to(sink).run(actorSystem);
    }
}
