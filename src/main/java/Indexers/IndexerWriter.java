package Indexers;

import Tokenizers.CompleteTokenizer;
import Tokenizers.SimpleTokenizer;
import Utils.Filter;
import Utils.Key;
import Utils.Values;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
public class IndexerWriter {
    // List of documents (tokens)
    private Map<Integer, List<String>> terms;
    // Indexer. The Indexer has a list of documents like [term, docId: frequency] 
    private Map<String, Values> indexer;
    // Type of tokenizer
    private final String tokenizeType;
    
    /**
     * Constructor. An Indexer needs the documents and the filename to write
     * @param words
     * @param file
     * @param tokenizerType
     * @throws FileNotFoundException
     */
    public IndexerWriter(Map<Integer, List<String>> words, File file, String tokenizerType) throws FileNotFoundException {
        this.terms = words;
        this.tokenizeType = tokenizerType;
        indexer = new HashMap<>();
        if (tokenizerType.equals(CompleteTokenizer.class.getName())) {
            Filter filter = new Filter();
            // Use stopWording
            filter.loadStopwords();
            terms = filter.stopwordsFiltering(terms);
            // Apply stemming
            terms = filter.stemmingWords(terms);
        }
        indexWords();
        indexer = sortIndexer();
        writeToFile(file);
    }
    
    /**
     * Get size of indexer
     * @return indexer's size
     */
    public int getVocabularySize() {
        return indexer.size();
    }
    
    /**
     * Get the ten first documents (order alphabetically) that appear in only one document
     * @return list of documents
     */
    public List<String> getTermsInOneDoc() {
        List<String> termsInOneDoc = new ArrayList<>();
        /*
        * For each entry on Map, get the term.
        * For each term, get its pairs <docId, frequency>.
        * If the term has only one pair, it appears in only one document.
        * The cycle finishes when the list has 10 documents.
        */
        for (Map.Entry<String, Values> entry : indexer.entrySet()) {
            String word = entry.getKey();
            Values listFrequencies = entry.getValue();
            if (listFrequencies.getValues().size() == 1)
                termsInOneDoc.add(word);
            if (termsInOneDoc.size() == 10)
                break;
        }
        return termsInOneDoc;
    }
    
    /**
     * Get the ten first documents with higher document frequency.
     * @return list of documents
     */
    public Map<String, Integer> getTermsWithHigherFreq() {
        List<Map.Entry<String,Integer>> entries = new ArrayList<>(getTermsAndFreq().entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int res;
                if (o1.getValue() > o2.getValue())
                    res = -1;
                else
                    res = 1;
                return res;
            }
        });
        /*
        * Get the ten first documents, sorted by document's frequency.
        * When the list has 10 documents, it finishes the cycle and returns.
        */
        Map<String, Integer> sortedTerms = new LinkedHashMap<>();
        for (int i = 0; i < 10; i++)
            sortedTerms.put(entries.get(i).getKey(),entries.get(i).getValue());
        return sortedTerms;
    }
    
    /**
     * Get of list of pairs with (term, document frequency)
     * @return list of (term, frequency)
     */
    private Map<String, Integer> getTermsAndFreq() {
        // For each entry on Map, add the term and its document's frequency to a list
        Map<String, Integer> termsFreq = new HashMap<>();
        for (Map.Entry<String, Values> entry : indexer.entrySet()) {
            termsFreq.put(entry.getKey(), entry.getValue().getValues().size());
        }
        return termsFreq;
    }
    
    /**
     * Index all documents
     */
    private void indexWords() {
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
                    for (Map.Entry<Integer, Integer> entry : doc_freq.getValues().entrySet()) {
                        if (entry.getKey() == docId) {
                            int value = entry.getValue();
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
    
    private Map<String, Values> sortIndexer() {
        List<Map.Entry<String, Values>> words = new ArrayList<>(indexer.entrySet());
        // Comparator to sort by query_id
        Collections.sort(words, 
                (Map.Entry<String, Values> o1, Map.Entry<String, Values> o2) -> o1.getKey().compareTo(o2.getKey()));
        // Create new map with sorted results
        Map<String, Values> result = new LinkedHashMap<>();
        for (Map.Entry<String, Values> entry: words) {
            Values values = entry.getValue();
            Map<Integer, Integer> documents = sortDocuments(values.getValues());
            values.setValues(documents);
            result.put(entry.getKey(), values);
        }
        return result;
    }
    
    private Map<Integer, Integer> sortDocuments(Map<Integer, Integer> values) {
        List<Map.Entry<Integer, Integer>> documents = new ArrayList<>(values.entrySet());
        // Comparator to sort documents by their id
        Collections.sort(documents, 
                (Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) -> o1.getKey().compareTo(o2.getKey()));
        // Create new map with sorted results
        Map<Integer, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry: documents)
            result.put(entry.getKey(), entry.getValue());
        return result;
    }
    
    /**
     * Write Indexer to file
     * @param file
     * @throws FileNotFoundException 
     */
    private void writeToFile(File file) throws FileNotFoundException {
        try (PrintWriter pw = new PrintWriter(file)) {
            // For each entry of map, writes an entry like: term, docId:frequency
            for (Map.Entry<String, Values> entry : indexer.entrySet()) {
                pw.print(entry.getKey() + ",");
                Values listFrequencies = entry.getValue();
                int i = 0;
                for (Map.Entry<Integer, Integer> values : listFrequencies.getValues().entrySet()) {
                    int docId = values.getKey();
                    int freq = values.getValue();
                    if (i++ == listFrequencies.getValues().size() - 1)
                        pw.print(docId + ":" + freq + "\n");
                    else
                        pw.print(docId + ":" + freq + ",");
                }
            }
            if (tokenizeType.equals(SimpleTokenizer.class.getName()))
               pw.println("\n-> " + SimpleTokenizer.class.getName());
            else
                pw.println("\n-> " + CompleteTokenizer.class.getName());
            pw.close();
        }
    }
}
