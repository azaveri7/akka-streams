import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletionStage;

public class ExploringMaterializedViewsWithReduceMethod {

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(), "actorSystem");
        Random random = new Random();
        Source<Integer, NotUsed> source = Source.range(1, 100)
                .map(x -> random.nextInt(1000) + 1);
        Flow<Integer, Integer, NotUsed> greaterThan200Filter =
                Flow.of(Integer.class).filter(x -> x > 200);
        Flow<Integer, Integer, NotUsed> evenNumberFilter =
                Flow.of(Integer.class).filter(x -> x % 2 == 0);

        Sink<Integer, CompletionStage<Integer>> sinkWithSum = Sink
                .reduce((firstValue, secondValue) -> {
                    System.out.println("secondValue: " + secondValue);
                    return firstValue + secondValue;
                });
        CompletionStage<Integer> result =
                source
                        .via(greaterThan200Filter)
                        .via(evenNumberFilter)
                        .toMat(sinkWithSum, Keep.right())
                        .run(actorSystem);

        result.whenComplete((value, throwable) -> {
            if(Objects.isNull(throwable)){
                System.out.println("The graph's materialized value is " + value);
            } else {
                System.out.println("Something is wrong " + value);
            }
            actorSystem.terminate();
        });
    }
}
