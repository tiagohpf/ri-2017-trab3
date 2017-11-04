package Scoring;

import Utils.Key;
import Utils.Pair;
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
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * IR, October 2017
 *
 * Assignment 2 
 *
 * @author Tiago Faria, 73714, tiagohpf@ua.pt
 * @author David dos Santos Ferreira, 72219, davidsantosferreira@ua.pt
 * 
 */

// Class that scores the queries
public class QueryScoring {
    // Indexer
    private final Map<String, List<Pair<Integer, Integer>>> indexer;
    // Queries
    private final List<Pair<String, Integer>> queries;
    // Number of words in the query that appear in the document
    private Map<Key, Integer> numberOfTerms;
    // Frequency of query words in the document
    private Map<Key, Integer> termsFrequency;
    // Number of queries
    private final int size;
    
    /**
     * Constructor
     * @param indexer
     * @param queries
     * @param size 
     */
    public QueryScoring(Map<String, List<Pair<Integer, Integer>>> indexer, List<Pair<String, Integer>> queries, int size) {
        this.indexer = indexer;
        this.queries = queries;
        this.size = size;
        numberOfTerms = new HashMap<>();
        termsFrequency = new HashMap<>();
    }
    
    /**
     * Get number of words in the query that appear in the document
     * @return number of words 
     */
    public Map<Key, Integer> getNumberOfTerms() {
        return numberOfTerms;
    }
    
    /**
     * Get frequency of query words in the document
     * @return frequency of words 
     */
    public Map<Key, Integer> getTermsFrequency() {
        return termsFrequency;
    }
    
    /**
     * Calculate the two scorings
     */
    public void calculateScores() {
        for (int i = 1; i <= size; i++) {
            List<String> terms = new ArrayList<>(getTermsOfQuery(i));
            for (String term : terms) {
                // Get documents and respectively frequency where appears a certain term
                List<Pair<Integer, Integer>> docId_freq = indexer.get(term);
                // If the Indexer has the term, add to scores
                if (docId_freq != null)
                    addQueryIdToScores(i, docId_freq);
            }
        }
        // Sort terms
        numberOfTerms = orderTerms(numberOfTerms);
        termsFrequency = orderTerms(termsFrequency);
    }
    
    /**
     * Write in file of number of words of query in each document
     * @param file 
     */ 
    public void writeNumberOfWords(File file) {
        // File has to be unique
        if (file.exists()) {
            System.err.println("ERROR: File already exists");
            System.exit(1);
        }
        PrintWriter pw;
        try {
            pw = new PrintWriter(file);
            pw.write(String.format("%-10s %-10s %-10s\n", "query_id", "doc_id", "doc_score"));
            for (Map.Entry<Key, Integer> term : numberOfTerms.entrySet())
                pw.write(String.format("%-10d %-10d %-10d\n",
                        term.getKey().getFirstValue(), term.getKey().getSecondValue(), term.getValue()));
            pw.close();
        } catch (FileNotFoundException ex) {
            System.err.println("ERROR: File not found!");
            System.exit(1);
        }
    }

    /**
     * Write file of words frequency of query in each document
     * @param file 
     */
    public void writeWordsFrequency(File file) {
        // File has to be unique
        if (file.exists()) {
            System.err.println("ERROR: File already exists");
            System.exit(1);
        }
        PrintWriter pw;
        try {
            pw = new PrintWriter(file);
            pw.write(String.format("%-10s %-10s %-10s\n", "query_id", "doc_id", "doc_score"));
            for (Map.Entry<Key, Integer> term : termsFrequency.entrySet())
                pw.write(String.format("%-10d %-10d %-10d\n",
                        term.getKey().getFirstValue(), term.getKey().getSecondValue(), term.getValue()));
            pw.close();
        } catch (FileNotFoundException ex) {
            System.err.println("ERROR: File not found!");
            System.exit(1);
        }
    }
    
    /**
     * Get terms of certain query 
     * @param id
     * @return terms
     */
    private List<String> getTermsOfQuery(int id) {
        return queries.stream()
                .filter(query -> query.getValue() == id)
                .map(query -> query.getKey())
                .collect(Collectors.toList()); 
    }
    
    /**
     * Add query to scores
     * @param queryId
     * @param docId_freq 
     */
    private void addQueryIdToScores(int queryId, List<Pair<Integer, Integer>> docId_freq) {
        for (int i = 0; i < docId_freq.size(); i++) {
           // Get docId and frequency of term
           Pair<Integer, Integer> pair = docId_freq.get(i);
           int docId = pair.getKey();
           /**
            * If docId is already associated to queryId, increment its value
            * Otherwise, create a new instance
            */
           if (numberOfTerms.get(new Key(queryId, docId)) == null)
               numberOfTerms.put(new Key(queryId, docId), 1);
           else {
               int value = numberOfTerms.get(new Key(queryId, docId)) + 1;
               numberOfTerms.put(new Key(queryId, docId), value);
           }
           int frequency = pair.getValue();
            /**
            * If docId is already associated to queryId, sum its frequency
            * Otherwise, create a new instance
            */
           if (termsFrequency.get(new Key(queryId, docId)) == null)
               termsFrequency.put(new Key(queryId, docId), frequency);
           else {
               int value = termsFrequency.get(new Key(queryId, docId)) + frequency;
               termsFrequency.put(new Key(queryId, docId), value);
           }
       }
    }
    
    /**
     * Sort data structures
     * @param terms
     * @return terms
     */
    private Map<Key, Integer> orderTerms(Map<Key, Integer> terms) {
        List<Map.Entry<Key,Integer>> entries = new ArrayList<>(terms.entrySet());
        // Comparator to sort by query_id
        Collections.sort(entries, new Comparator<Map.Entry<Key,Integer>>() {
            @Override
            public int compare(Entry<Key, Integer> o1, Entry<Key, Integer> o2) {
                int res;
                int id1 = o1.getKey().hashCode();
                int id2 = o2.getKey().hashCode();
                if (id1 < id2)
                    res = -1;
                else
                    res = 1;
                return res;
            }
        });
        // Comparator to sort by doc_score
        Collections.sort(entries, new Comparator<Map.Entry<Key,Integer>>() {
            @Override
            public int compare(Entry<Key, Integer> o1, Entry<Key, Integer> o2) {
                int res = 0;
                if (o1.getKey().getFirstValue() == o2.getKey().getFirstValue() && o1.getValue() > o2.getValue())
                    res = -1;
                else if (o1.getKey().getFirstValue() == o2.getKey().getFirstValue() && o1.getValue() < o2.getValue())
                    res = 1;
                return res;
            }
        });
        // Create new map with sorted results
        Map<Key, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Key, Integer> entry: entries)
            result.put(new Key(entry.getKey().getFirstValue(), entry.getKey().getSecondValue()), entry.getValue());
        return result;
    }
}
