package actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import model.SearchRequest;
import model.SearchResult;
import model.SearchResultSingle;
import scala.concurrent.duration.Duration;
import searcher.ISearcher;

import java.util.List;
import java.util.concurrent.TimeUnit;

// Master-actor. Создает Child-actor-ов, дожидается результатов и возвращает.
public class AggregateActor extends UntypedActor {
    private final List<ISearcher> searchers;
    private final SearchResult response;
    private final int maxEntriesCount;

    private ActorRef requestSender;
    private int receivedResults;

    public AggregateActor(List<ISearcher> searchers, int maxEntriesCount) {
        this.maxEntriesCount = maxEntriesCount;
        this.searchers = searchers;
        this.response = new SearchResult(searchers.size());
        this.receivedResults = 0;
    }

    @Override
    public void onReceive(Object o) {
        if (o instanceof String query) {
            // При запросе запомним, куда посылать ответ
            // Далее создадим столько child-actor-ов, сколько поисковиков в массиве и попросим их выполнить запрос
            // Также установим таймер, по истечении которого нам придет сообщение, что поисковики работают долго
            requestSender = getSender();

            for (int i = 0; i < searchers.size(); ++i) {
                ISearcher searcher = searchers.get(i);
                getContext().actorOf(Props.create(SearchActor.class, searcher))
                        .tell(new SearchRequest(i, query, maxEntriesCount), self());
            }

            getContext().setReceiveTimeout(Duration.create(1, TimeUnit.SECONDS));
        } else if (o instanceof SearchResultSingle resultSingle) {
            // При сообщении от child-actor-а запомним результат
            response.addSingleResult(resultSingle.getSearchSystem(), resultSingle);
            ++receivedResults;

            //   и, когда все получили, посылаем результат и останавливаемся
            if (searchers.size() == receivedResults) {
                requestSender.tell(response, self());
                getContext().stop(self());
            }
        } else if (o instanceof ReceiveTimeout) {
            // При истечении времени посылаем то, чего дождались, и выключаемся
            requestSender.tell(response, self());
            getContext().stop(self());
        }
    }
}
