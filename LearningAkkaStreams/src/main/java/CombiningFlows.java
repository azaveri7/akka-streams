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

public class CombiningFlows {

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(), "actorSystem");
        Source<String, NotUsed> sentencesSource = Source.from(Arrays.asList("The sky is blue",
                "The moon is seen only at night","The planets orbit around the sun"));
        Flow<String, Integer, NotUsed> howManyWordsFlow = Flow.of(String.class)
                .map(sentence -> sentence.split(" ").length);
        // Source + Flow = Another source
        // new source becomes integer source
        Source<Integer, NotUsed> howManyWordsSource = sentencesSource.via(howManyWordsFlow);

        //Instead of writing this, we can directly write a new source in following way:
        Source<Integer, NotUsed> sentencesSource2 = Source.from(Arrays.asList("The sky is blue",
                "The moon is seen only at night","The planets orbit around the sun"))
                .map(sentence -> sentence.split(" ").length);

        Sink<Integer, CompletionStage<Done>> sink = Sink.ignore();

        // Flow + sink = Another Sink
        Sink<String, NotUsed> combinedSink = howManyWordsFlow.to(sink);
    }

}
