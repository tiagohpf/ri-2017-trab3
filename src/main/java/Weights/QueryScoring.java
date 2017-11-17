package Weights;

import Utils.Key;
import Utils.Values;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private final Map<String, Values> indexer;
    private final int numberOfDocs;
    private final Map<Integer, List<String>> queries;
    private final Map<Integer, Double> queryLength;
    private Map<Key, Double> docScorer;
    
    public QueryScoring(Map<String, Values> indexer, Map<Integer, List<String>> queries, int numberOfDocs) {
        this.indexer = indexer;
        this.queries = queries;
        this.numberOfDocs = numberOfDocs;
        queryLength = new HashMap<>();
        docScorer = new HashMap<>();
    }
    
    public void calculateInverseDocFreq(Map<String, Values> indexerQueries, Map<String, Values> indexerDocs) {
        for (Map.Entry<String, Values> term : indexerQueries.entrySet()) {
            String termId = term.getKey();
            Map<Integer, Double> values = term.getValue().getValues();
            for (Map.Entry<Integer, Double> doc_freq : values.entrySet()) {
                int docId = doc_freq.getKey();
                double idf = 0;
                if (indexerDocs.get(termId) != null)
                    idf = doc_freq.getValue() * Math.log10(numberOfDocs / indexerDocs.get(termId).getValues().size());
                values.put(docId, idf);
            }
            indexer.put(termId, new Values(values));
        }
        calculateQueryLength();
        normalizeQueries();
    }
    
    private void calculateQueryLength() {
        for (Map.Entry<Integer, List<String>> document : queries.entrySet()) {
            int docId = document.getKey();
            double sum = 0;
            List<String> words = document.getValue();
            Set<String> set = new HashSet<>(words);
            for (String word : set) {
                Values values = indexer.get(word);
                sum += Math.pow(values.getValues().get(docId), 2);
            }
            queryLength.put(docId, Math.sqrt(sum));
        }
    }
    
    private void normalizeQueries() {
       for (Map.Entry<String, Values> term : indexer.entrySet()) {
            Map<Integer, Double> values = term.getValue().getValues();
            for (Map.Entry<Integer, Double> query_weight : values.entrySet()) {
                int queryId = query_weight.getKey();
                double length = queryLength.get(queryId);
                double normalization = query_weight.getValue() / length;
                values.put(queryId, normalization);
            }
            indexer.put(term.getKey(), new Values(values));
        }
    }
    
    public void calculateDocScorer(Map<String, Values> docIndexer) {
        for (Map.Entry<String, Values> term : indexer.entrySet()) {
            String word = term.getKey();
            Values queryValues = term.getValue();
            for (Map.Entry<Integer, Double> queryId_score : queryValues.getValues().entrySet()) {
                int queryId = queryId_score.getKey();
                if (docIndexer.get(word) != null) {
                    Values docValues = docIndexer.get(word);
                    for (Map.Entry<Integer, Double> docId_score : docValues.getValues().entrySet()) {
                        int docId = docId_score.getKey();
                        double score = queryId_score.getValue() * docId_score.getValue();
                        Key key = new Key(queryId, docId);
                        if (docScorer.get(key) != null)
                           score += docScorer.get(key);
                        docScorer.put(key, score);                    }
                }
            }
        }
    }
    
    private void sortDocScorer() {
        List<Map.Entry<Key,Double>> entries = new ArrayList<>(docScorer.entrySet());
        // Comparator to sort by query_id
        Collections.sort(entries, new Comparator<Map.Entry<Key,Double>>() {
            @Override
            public int compare(Map.Entry<Key, Double> o1, Map.Entry<Key, Double> o2) {
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
        Collections.sort(entries, new Comparator<Map.Entry<Key,Double>>() {
            @Override
            public int compare(Map.Entry<Key, Double> o1, Map.Entry<Key, Double> o2) {
                int res = 0;
                if (o1.getKey().getFirstValue() == o2.getKey().getFirstValue() && o1.getValue() > o2.getValue())
                    res = -1;
                else if (o1.getKey().getFirstValue() == o2.getKey().getFirstValue() && o1.getValue() < o2.getValue())
                    res = 1;
                return res;
            }
        });
        // Create new map with sorted results
        docScorer = new LinkedHashMap<>();
        for (Map.Entry<Key, Double> entry: entries)
            docScorer.put(new Key(entry.getKey().getFirstValue(), entry.getKey().getSecondValue()), entry.getValue());
    }
    
   public void writeToFile(File file) {
        sortDocScorer();
        PrintWriter pw;
        try {
            pw = new PrintWriter(file);
            pw.write(String.format("%-10s %-10s %-10s\n", "query_id", "doc_id", "doc_score"));
            for (Map.Entry<Key, Double> score : docScorer.entrySet()) {
                pw.write(String.format("%-10s %-10d %-10.5f\n",
                        score.getKey().getFirstValue(), score.getKey().getSecondValue(), score.getValue()));
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            System.err.println("ERROR: File not found!");
            System.exit(1);
        }
    }
}
