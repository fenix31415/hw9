package actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.util.Timeout;
import model.SearchRequest;
import model.SearchResult;
import model.SearchResultSingle;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import searcher.SearcherStub;

import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;

public class SearchActorTest {
    private ActorSystem system;

    @Before
    public void setUp() {
        system = ActorSystem.create("ChildActorTest");
    }

    @After
    public void tearDown() {
        system.terminate();
    }

    @Test
    public void testSearchActor() {
        final int maxEntriesCount = 5;

        ActorRef childActor = system.actorOf(Props.create(SearchActor.class,
                new SearcherStub(100, "Yandex")));

        SearchResultSingle response = (SearchResultSingle) ask(childActor,
                new SearchRequest(0, "test", maxEntriesCount), Timeout.apply(5, TimeUnit.SECONDS))
                .toCompletableFuture().join();

        assertEquals(maxEntriesCount, response.getSearchResults().size());
    }

    @Test(expected = java.util.concurrent.CompletionException.class)
    public void testSearchActorTimelimit() {
        ActorRef childActor = system.actorOf(Props.create(SearchActor.class,
                new SearcherStub(2000, "Yandex")));

        ask(childActor, new SearchRequest(0, "test", 5), Timeout.apply(1, TimeUnit.SECONDS)).toCompletableFuture().join();
    }
}
