package Weights;

import Utils.Values;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class IndexerWeight {
    private Map<String, Values> indexerDocs;
    private final Map<Integer, List<String>> terms;
    private final Map<Integer, Double> docLength;
    
    public IndexerWeight(Map<Integer, List<String>> terms) {
        this.terms = terms;
        indexerDocs = new HashMap<>();
        docLength = new HashMap<>();
    }
    
    public void calculateTermFreq(Map<String, Values> docsIndexer) {
        for (Map.Entry<String, Values> term : docsIndexer.entrySet()) {
            Map<Integer, Double> values = term.getValue().getValues();
            for (Map.Entry<Integer, Double> doc_freq : values.entrySet()) {
                int docId = doc_freq.getKey();
                double frequency = 1 + Math.log10(doc_freq.getValue());
                values.put(docId, frequency);
            }
            indexerDocs.put(term.getKey(), new Values(values));
        }
        calculateDocsLength();
        normalizeTermFreq();
    }
    
    private void calculateDocsLength() {
        for (Map.Entry<Integer, List<String>> document : terms.entrySet()) {
            int docId = document.getKey();
            double sum = 0;
            List<String> words = document.getValue();
            Set<String> set = new HashSet<>(words);
            for (String word : set) {
                Values values = indexerDocs.get(word);
                if(values !=  null)
                    sum += Math.pow(values.getValues().get(docId), 2);
            }
            docLength.put(docId, Math.sqrt(sum));
        }
    }
    
    private void normalizeTermFreq() {
       for (Map.Entry<String, Values> term : indexerDocs.entrySet()) {
            Map<Integer, Double> values = term.getValue().getValues();
            for (Map.Entry<Integer, Double> doc_weight : values.entrySet()) {
                int docId = doc_weight.getKey();
                double length = docLength.get(docId);
                double normalization = doc_weight.getValue() / length;
                values.put(docId, normalization);
            }
            indexerDocs.put(term.getKey(), new Values(values));
        } 
    }
    
    
    /**
     * Write Indexer to file
     * @param file
     * @throws FileNotFoundException 
     */
    public void writeToFile(File file) throws FileNotFoundException {
        indexerDocs = sortIndexerByTerm();
        try (PrintWriter pw = new PrintWriter(file)) {
            // For each entry of map, writes an entry like: term, docId:frequency
            for (Map.Entry<String, Values> entry : indexerDocs.entrySet()) {
                pw.print(entry.getKey() + ",");
                Values listFrequencies = entry.getValue();
                int i = 0;
                for (Map.Entry<Integer, Double> values : listFrequencies.getValues().entrySet()) {
                    int docId = values.getKey();
                    double freq = values.getValue();
                    if (i++ == listFrequencies.getValues().size() - 1)
                        pw.printf(Locale.US, "%d:%.5f\n", docId, freq);
                    else
                        pw.printf(Locale.US, "%d:%.5f,", docId, freq);
                }
            }
            pw.close();
        }
    }
    
    private Map<String, Values> sortIndexerByTerm() {
        List<Map.Entry<String, Values>> words = new ArrayList<>(indexerDocs.entrySet());
        // Comparator to sort by query_id
        Collections.sort(words, 
                (Map.Entry<String, Values> o1, Map.Entry<String, Values> o2) -> o1.getKey().compareTo(o2.getKey()));
        // Create new map with sorted results
        Map<String, Values> result = new LinkedHashMap<>();
        for (Map.Entry<String, Values> entry: words) {
            Values values = entry.getValue();
            Map<Integer, Double> documents = sortDocuments(values.getValues());
            values.setValues(documents);
            result.put(entry.getKey(), values);
        }
        return result;
    }
    
    private Map<Integer, Double> sortDocuments(Map<Integer, Double> values) {
        List<Map.Entry<Integer, Double>> documents = new ArrayList<>(values.entrySet());
        // Comparator to sort documents by their id
        Collections.sort(documents, 
                (Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) -> o1.getKey().compareTo(o2.getKey()));
        // Create new map with sorted results
        Map<Integer, Double> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Double> entry: documents)
            result.put(entry.getKey(), entry.getValue());
        return result;
    }
}
