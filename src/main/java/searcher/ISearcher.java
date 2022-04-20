package searcher;

import model.SearchRequest;
import model.SearchResultSingle;

// Интерфейс для поисковиков
// Чтобы добавить новый поисковик, нужно лишь добавить класс и реализовать этот интерфейс
public interface ISearcher {
    SearchResultSingle query(SearchRequest searchRequest);
    String getName();
}
