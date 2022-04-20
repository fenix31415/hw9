package actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.util.Timeout;
import model.SearchResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import searcher.ISearcher;
import searcher.SearcherStub;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;
import static org.junit.Assert.assertEquals;

public class AggregateActorTest {
    private ActorSystem system;

    @Before
    public void setUp() {
        system = ActorSystem.create("MasterActorTest");
    }

    @After
    public void tearDown() {
        system.terminate();
    }

    @Test
    public void testAggregateActorTimeLimit() {
        final List<ISearcher> SEARCHERS = List.of(
                new SearcherStub(500, "Yandex"),
                new SearcherStub(600, "google"),
                new SearcherStub(10000, "bing")
        );
        final int maxEntriesCount = 5;

        ActorRef aggregateActor = system.actorOf(Props.create(AggregateActor.class,SEARCHERS, maxEntriesCount));

        SearchResult response = (SearchResult) ask(
                aggregateActor,
                "test",
                Timeout.apply(5, TimeUnit.SECONDS)
        ).toCompletableFuture().join();

        assertEquals(maxEntriesCount, response.getSearchResults().get(0).size());
        assertEquals(maxEntriesCount, response.getSearchResults().get(1).size());
        assertEquals(0, response.getSearchResults().get(2).size());
    }

    @Test
    public void testAggregateActor() {
        final List<ISearcher> SEARCHERS = List.of(
                new SearcherStub(500, "Yandex"),
                new SearcherStub(600, "google"),
                new SearcherStub(700, "bing")
        );
        final int maxEntriesCount = 5;

        ActorRef aggregateActor = system.actorOf(Props.create(AggregateActor.class,SEARCHERS, maxEntriesCount));

        SearchResult response = (SearchResult) ask(
                aggregateActor,
                "test",
                Timeout.apply(5, TimeUnit.SECONDS)
        ).toCompletableFuture().join();

        assertEquals(maxEntriesCount, response.getSearchResults().get(0).size());
        assertEquals(maxEntriesCount, response.getSearchResults().get(1).size());
        assertEquals(maxEntriesCount, response.getSearchResults().get(2).size());
    }
}
