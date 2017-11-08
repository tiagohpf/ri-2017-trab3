package Indexers;

import Tokenizers.CompleteTokenizer;
import Tokenizers.SimpleTokenizer;
import Utils.Filter;
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
    // List of terms (tokens)
    private Map<Integer, List<String>> terms;
    // Indexer. The Indexer has a list of terms like [term, docId: frequency] 
    private final Map<String, Values> indexer;
    // Type of tokenizer
    private final String tokenizeType;
    
    /**
     * Constructor. An Indexer needs the terms and the filename to write
     * @param terms
     * @param file
     * @param tokenizerType
     * @throws FileNotFoundException
     */
    public IndexerWriter(Map<Integer, List<String>> terms, File file, String tokenizerType) throws FileNotFoundException {
        this.terms = terms;
        this.tokenizeType = tokenizerType;
        indexer = new TreeMap<>();
        if (tokenizerType.equals(CompleteTokenizer.class.getName())) {
            Filter filter = new Filter();
            // Get stopWords
            List<String> stopWords = filter.loadStopwords();
            // Use stopWording
            terms = filter.stopwordsFiltering(terms);
            // Apply stemming
            terms = filter.stemmingWords(terms);
        }
        indexWords();
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
     * Get the ten first terms (order alphabetically) that appear in only one document
     * @return list of terms
     */
    public List<String> getTermsInOneDoc() {
        List<String> termsInOneDoc = new ArrayList<>();
        /*
        * For each entry on Map, get the term.
        * For each term, get its pairs <docId, frequency>.
        * If the term has only one pair, it appears in only one document.
        * The cycle finishes when the list has 10 terms.
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
     * Get the ten first terms with higher document frequency.
     * @return list of terms
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
        * Get the ten first terms, sorted by document's frequency.
        * When the list has 10 terms, it finishes the cycle and returns.
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
     * Index all words
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
                    if (++i == listFrequencies.getValues().size() - 1)
                        pw.print(docId + ":" + freq + "\n");
                    else
                        pw.print(docId + ":" + freq + ",");
                }
            }
            if (tokenizeType.equals("t1"))
               pw.println("\n-> " + SimpleTokenizer.class.getName());
            else
                pw.println("\n-> " + CompleteTokenizer.class.getName());
            pw.close();
        }
    }
}
