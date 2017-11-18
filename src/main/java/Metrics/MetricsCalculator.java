package Metrics;

import Documents.GSDocument;
import Utils.Values;
import java.util.List;
import java.util.Map;

public class MetricsCalculator {
    private Map<Integer, List<String>> queries;
    private Map<Integer, Values> scorer;
    private GSDocument gsDoc;
    
    public MetricsCalculator(Map<Integer, List<String>> queries, Map<Integer, Values> scorer, GSDocument gsDoc) {
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
