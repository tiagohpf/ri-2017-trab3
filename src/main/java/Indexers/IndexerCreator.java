package Indexers;

import Utils.Values;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IR, October 2017
 *
 * Assignment 2 
 *
 * @author Tiago Faria, 73714, tiagohpf@ua.pt
 * @author David dos Santos Ferreira, 72219, davidsantosferreira@ua.pt
 * 
 */

// Class Indexer that uses Stopwording filtering, Stemmer and create an Indexer.
public class IndexerCreator {
    // List of documents (tokens)
    private final Map<Integer, List<String>> terms;
    // Indexer. The Indexer has a list of documents like [term, docId: frequency] 
    private final Map<String, Values> indexer;
    
    /**
     * Constructor. An Indexer needs the documents and the filename to write
     * @param terms
     * @param file
     * @throws FileNotFoundException
     */
    public IndexerCreator(Map<Integer, List<String>> terms) throws FileNotFoundException {
        this.terms = terms;
        indexer = new HashMap<>();
    }
    
    public Map<String, Values> getIndexer() {
        return indexer;
    }
    
    public void createIndexer() {
        for (Map.Entry<Integer, List<String>> doc_terms : terms.entrySet()) {
            List<String> words = doc_terms.getValue();
            int docId = doc_terms.getKey();
            /*
            * If the indexer hasn't the term yet, it's created a new instance.
            * If the indexer has the term and not the document, the pair <docId, frequency> is added to the list.
            * If the indexer has the term and the docId, it's incrementd in his frequency.
            */
            for (String word : words) {
                if (indexer.keySet().contains(word)) {
                    Values doc_freq = indexer.get(word);
                    boolean containsPair = false;
                    for (Map.Entry<Integer, Double> entry : doc_freq.getValues().entrySet()) {
                        if (entry.getKey() == docId) {
                            double value = entry.getValue();
                            doc_freq.addValue(entry.getKey(), value + 1);
                            containsPair = true;
                            break;
                        }
                    }
                    if (!containsPair) {
                        doc_freq.addValue(docId, 1);
                    }
                    indexer.put(word, doc_freq);
                } else {
                    Values doc_freq = new Values();
                    doc_freq.addValue(docId, 1);
                    indexer.put(word, doc_freq);
                }
            }
        }
    }
}