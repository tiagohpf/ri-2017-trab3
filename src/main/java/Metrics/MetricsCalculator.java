package Metrics;

import Documents.GSDocument;
import Utils.Key;
import java.util.List;
import java.util.Map;

public class MetricsCalculator {
    private Map<Integer, List<String>> queries;
    private Map<Key, Double> scorer;
    private GSDocument gsDoc;
    
    public MetricsCalculator(Map<Integer, List<String>> queries, Map<Key, Double> scorer, GSDocument gsDoc) {
        this.queries = queries;
        this.scorer = scorer;
        this.gsDoc = gsDoc;
    }
    
    public double getMeanPrecision() {
        double sum = 0;
        
        return sum / queries.size();
    }
    
    public double getMeanRecall() {
        return 0;
    }
}
