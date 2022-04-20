package model;

import java.util.*;

// Результат обращения ко всем поисковикам
// Можно было вместо List<String> хранить SearchResultSingle (т.е. {ID, List<String>}),
//   но мы ID и так знаем, когда спрашиваем актора
public class SearchResult {
    private final List<List<String>> searchResults;

    public SearchResult(int totalSearchers) {
        searchResults = new ArrayList<>(Collections.nCopies(totalSearchers, new ArrayList<>()));
    }

    public void addSingleResult(int searcherID, SearchResultSingle searchResult) {
        searchResults.set(searcherID, searchResult.getSearchResults());
    }

    public List<List<String>> getSearchResults() {
        return searchResults;
    }
}
