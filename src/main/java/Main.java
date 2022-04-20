import actor.AggregateActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import model.SearchResult;
import scala.concurrent.Await;
import scala.concurrent.Future;
import searcher.ISearcher;
import searcher.SearcherStub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final List<ISearcher> SEARCHERS = List.of(
            new SearcherStub(100, "Yandex"),
            new SearcherStub(200, "google"),
            new SearcherStub(300, "bing")
    );

    // Формируем читабельный ответ
    private static String ResultToString(SearchResult searchResult) {
        assert(searchResult.getSearchResults().size() == SEARCHERS.size());

        var searchResultList = searchResult.getSearchResults();
        StringBuilder result = new StringBuilder("[\n");
        for (int i = 0; i < SEARCHERS.size(); ++i) {
            result.append(SEARCHERS.get(i).getName()).append(":\n");
            StringBuilder entries = new StringBuilder();
            for (String entry : searchResultList.get(i)) {
                entries.append("      ").append(entry).append("\n");
            }
            result.append("  {\n")
                    .append("    entries : [\n")
                    .append(entries)
                    .append("    ]\n")
                    .append("  }\n");
        }
        return result.append("]").toString();
    }

    // На каждый запрос создается свой master-actor
    // Он дождется ответа и выведет его в консоль
    static void Request(ActorSystem system, String query) {
        ActorRef aggregator = system.actorOf(Props.create(AggregateActor.class,SEARCHERS, 5));

        // Здесь таймаут никогда не выйдет, так как Master-actor заботится о времени
        final Timeout timeout = Timeout.apply(10, TimeUnit.SECONDS);
        final Future<Object> future = Patterns.ask(aggregator, query, timeout);
        try {
            System.out.println(ResultToString((SearchResult) Await.result(future, timeout.duration())));
        } catch (final Exception e) {
            System.err.println("Cannot get result: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("search");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in));

        String query;
        while (true) {
            System.out.println("Enter search request:");
            try {
                query = in.readLine();
            } catch (IOException e) {
                System.out.println("Error: cannot read request line");
                query = "";
            }

            if (!query.equals("")) {
                Request(system, query);
            } else {
                break;
            }
        }

        system.terminate();
    }
}
