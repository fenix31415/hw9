package actor;

import akka.actor.UntypedActor;
import model.SearchRequest;
import searcher.ISearcher;

// Child-actor. Только выполняет запрос к поисковику и возвращает результат
public class SearchActor extends UntypedActor {
    // Храним реазизацию поиска (aka bridge),
    //   так актор может искать у разных систем (у которых может быть разный API и реализация)
    private final ISearcher searcher;

    public SearchActor(ISearcher searcher) {
        this.searcher = searcher;
    }

    @Override
    public void onReceive(Object o) {
        if (o instanceof SearchRequest searchRequest) {
            // При получении запроса мы обращаемся к поисковику (searcher.query(..)),
            //   а результат посылаем родителю
            // Затем останавливаем актора, так как он свою работу сделал и больше не нужен
            getSender().tell(searcher.query(searchRequest), self());
            getContext().stop(self());
        }
    }
}
