import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class ExploringFlowWithGrouped {


    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(), "actorSystem");

        Source<Integer, NotUsed> numbers = Source.range(1, 200);

        Flow<Integer, Integer, NotUsed> filterFlow =
                Flow.of(Integer.class)
                        .filter(x -> x % 17 == 0);

        Flow<Integer, Integer, NotUsed> mapConcatFlow =
                Flow.of(Integer.class)
                        .mapConcat( value -> {
                            List<Integer> results = Arrays.asList(value, value + 1, value + 2);
                            return results;
                        });
        Flow<Integer, List<Integer>, NotUsed> groupedFlow = Flow.of(Integer.class)
                .grouped(3);
        Sink<List<Integer>, CompletionStage<Done>>
                printSink = Sink.foreach(System.out::println);

        //numbers.via(filterFlow).to(printSink).run(actorSystem);
        numbers.via(filterFlow).via(mapConcatFlow).via(groupedFlow).to(printSink).run(actorSystem);
    }
}
