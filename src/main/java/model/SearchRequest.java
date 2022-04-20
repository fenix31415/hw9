package model;

// Запрос. Содержит ID поисковика, чтобы потом мы знали, откуда пришел ответ,
//   а также максимальное количество ответов (в этой задаче оно всегда 5, но наверное мы захотим варьировать)
//   и непосредственно саму строку запроса
public class SearchRequest {
    private final int searchSystemID;
    private final String query;
    private final int maxEntriesCount;

    public SearchRequest(int searchSystemID, String query, int maxEntriesCount) {
        this.searchSystemID = searchSystemID;
        this.query = query;
        this.maxEntriesCount = maxEntriesCount;
    }

    public int getSearchSystemID() {
        return searchSystemID;
    }

    public String getQueryText() {
        return query;
    }

    public int getMaxEntriesCount() {
        return maxEntriesCount;
    }
}
