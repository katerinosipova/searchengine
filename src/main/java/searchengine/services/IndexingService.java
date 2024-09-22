package searchengine.services;

import searchengine.dto.statistics.IndexingResponse;
import searchengine.exceptions.IndexingAlreadyStartedException;

public interface IndexingService {

     IndexingResponse startIndexing();
     IndexingResponse stopIndexing ();

}
