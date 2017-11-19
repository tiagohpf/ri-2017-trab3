package Metrics;

import Documents.GSDocument;
import Utils.Values;
import java.util.Map;
import java.util.stream.Collectors;

public class MetricsCalculator {
    private Map<Integer, Values> scorer;
    private GSDocument gsDocument;
    
    public MetricsCalculator(Map<Integer, Values> scorer, GSDocument gsDocument) {
        this.scorer = scorer;
        this.gsDocument = gsDocument;
    }
    
    public double getMeanPrecision() {
        double sum = 0;
        for (Map.Entry<Integer, Values> query : gsDocument.getRelevances().entrySet()) {
            int queryId = query.getKey();
            Map<Integer, Double> queryValues = query.getValue().getValues();
            Map<Integer, Double> docValues = scorer.get(queryId).getValues();
            sum += (double) countRelevantsFound(queryValues, docValues) / scorer.get(queryId).getValues().size();
        }
        return sum / scorer.size();
    }
    
    public double getMeanRecall() {
        double sum = 0;
        for (Map.Entry<Integer, Values> query : gsDocument.getRelevances().entrySet()) {
            int queryId = query.getKey();
            Map<Integer, Double> queryValues = query.getValue().getValues();
            Map<Integer, Double> docValues = scorer.get(queryId).getValues();
            sum += (double) countRelevantsFound(queryValues, docValues) / queryValues.size();
        }
        return sum / scorer.size();
    }
    
    private int countRelevantsFound(Map<Integer, Double> queryValues, Map<Integer, Double> docValues) {
        return queryValues.entrySet().stream()
                .filter(value -> docValues.containsKey(value.getKey()))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())).size();
    }
}
