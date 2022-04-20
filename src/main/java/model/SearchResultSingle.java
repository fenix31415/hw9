package model;

import java.util.List;

// Результат запроса к поисковику. Список сайтов и ID поисковика
// Содержит только строки-имена сайтов, но это легко можно заменить на произвольный класс и хранить еще что-то
public class SearchResultSingle {
    private final int searchSystemID;
    private final List<String> searchResults;

    public SearchResultSingle(int searchSystemID, List<String> searchResults) {
        this.searchSystemID = searchSystemID;
        this.searchResults = searchResults;
    }

    public int getSearchSystem() {
        return searchSystemID;
    }

    public List<String> getSearchResults() {
        return searchResults;
    }
}
