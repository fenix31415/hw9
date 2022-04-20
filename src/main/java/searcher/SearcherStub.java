package searcher;

import model.SearchRequest;
import model.SearchResultSingle;

import java.util.ArrayList;
import java.util.List;

// Заглушка-поисковик. "Параметризуется" именем и задержкой ответа. Так мы получим разные поисковики.
public class SearcherStub implements ISearcher {
    private final int delayMS;
    private final String name;

    public SearcherStub(int delayMS, String name) {
        this.delayMS = delayMS;
        this.name = name;
    }

    // Генерация ответа
    List<String> findUrls(int maxEntriesCount) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < maxEntriesCount; i++) {
            list.add("https://" + name + ".ru/" + i);
        }
        return list;
    }

    // Проигнорируем сам текст запроса и просто вернем, после ожидания, какие-то сайты
    @Override
    public SearchResultSingle query(SearchRequest searchRequest) {
        try {
            Thread.sleep(delayMS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new SearchResultSingle(searchRequest.getSearchSystemID(), findUrls(searchRequest.getMaxEntriesCount()));
    }

    @Override
    public String getName() {
        return name;
    }
}
