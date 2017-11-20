package Weighters;

import Utils.Values;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
public class QueryWeighter {
    private final Map<String, Values> indexer;
    private final int numberOfDocs;
    private final Map<Integer, List<String>> queries;
    private final Map<Integer, Double> queryLength;
    private Map<Integer, Values> docScorer;
    
    public QueryWeighter(Map<String, Values> indexer, Map<Integer, List<String>> queries, int numberOfDocs) {
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
                    Map<Integer, Double> docsScores = new HashMap<>();
                    for (Map.Entry<Integer, Double> docId_score : docValues.getValues().entrySet()) {
                        int docId = docId_score.getKey();
                        double score = queryId_score.getValue() * docId_score.getValue();
                        Values inputValues = docScorer.get(queryId);
                        if (inputValues != null) {
                            if(inputValues.getValues().get(docId) != null)
                                score += inputValues.getValues().get(docId);
                            inputValues.addValue(docId, score);
                            docScorer.put(queryId, inputValues);
                        } else {
                            Map<Integer, Double> newValue = new HashMap<>();
                            newValue.put(docId, score);
                            docScorer.put(queryId, new Values(newValue));
                        }
                    }
                }
            }
        }
    }
    
   public void writeToFile(File file) {
        docScorer = orderScores();
        PrintWriter pw;
        try {
            pw = new PrintWriter(file);
            pw.write(String.format("%-10s %-10s %-10s\n", "query_id", "doc_id", "doc_score"));
            for (Map.Entry<Integer, Values> score : docScorer.entrySet()) {
                Values values = score.getValue();
                for (Map.Entry<Integer, Double> value : values.getValues().entrySet())
                    pw.write(String.format("%-10s %-10d %-10.5f\n",score.getKey(), value.getKey(), value.getValue()));
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            System.err.println("ERROR: File not found!");
            System.exit(1);
        }
    }
   
    public Map<Integer, Values> getQueryScorer() {
       return docScorer;
    }
    
    private Map<Integer, Values> orderScores() {
        Map<Integer, Values> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Values> query : docScorer.entrySet())
            result.put(query.getKey(), new Values(query.getValue().sortValues()));
        return result;
    }
}
