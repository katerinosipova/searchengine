package searchengine.exceptions;

public class IndexingAlreadyStartedException extends Exception{
    public IndexingAlreadyStartedException (String message) {
        super(message);
    }
}
