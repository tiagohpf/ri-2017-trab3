package Metrics;

import Documents.GSDocument;
import Utils.Values;
import java.util.Map;
import java.util.stream.Collectors;

public class MetricsCalculator {
    private final Map<Integer, Values> scorer;
    private final GSDocument gsDocument;
    private double precision;
    private double recall;
    private double fmeasure;
    private double avgPrecision;
    private double precision10;
    private double reciprocalRank;
    
    public MetricsCalculator(Map<Integer, Values> scorer, GSDocument gsDocument) {
        this.scorer = scorer;
        this.gsDocument = gsDocument;
        precision = recall = fmeasure = avgPrecision = precision10 = reciprocalRank = 0;
        calculateMeasures();
        calculateAveragePrecision();
    }
    
    public double getAvgPrecision() {
        return precision;
    }
    
    public double getMeanRecall() {
        return recall;
    }
    
    public double getMeanFMeasure() {
        return fmeasure;
    }
    
    public double getMeanAveragePrecision() {
        return avgPrecision;
    }
    
    public double getMeanPrecisionAtRank10() {
        return precision10;
    }
    
    public double getMeanReciprocalRank() {
        return reciprocalRank;
    }
      
    private void calculateMeasures() {
        for (Map.Entry<Integer, Values> query : gsDocument.getRelevances().entrySet()) {
            int queryId = query.getKey();
            Map<Integer, Double> values = query.getValue().getValues();
            Map<Integer, Double> documents = scorer.get(queryId).getValues();
            Map<Integer, Double> tenFirstDocuments = getTenFirstDocumentsOfMap(documents);
            int nRelevantsFound = relevantsFound(values, documents).size();
            int nRelevantsFoundInTen = relevantsFound(values, tenFirstDocuments).size();
            double queryPrecision = (double) nRelevantsFound / scorer.get(queryId).getValues().size();
            double queryPrecision10 = (double) nRelevantsFoundInTen / scorer.get(queryId).getValues().size();
            double queryRecall = (double) nRelevantsFound / values.size();
            precision += queryPrecision;
            precision10 += queryPrecision10;
            recall += queryRecall;
            fmeasure += (2 * queryRecall * queryPrecision) / (queryRecall + queryPrecision);
        }
        precision = precision / scorer.size();
        precision10 = precision10 / scorer.size();
        recall = recall / scorer.size();
        fmeasure = fmeasure / scorer.size();
    }
    
    private void calculateAveragePrecision() {
        for (Map.Entry<Integer, Values> query : gsDocument.getRelevances().entrySet()) {
            int queryId = query.getKey();
            Map<Integer, Double> values = query.getValue().getValues();
            Map<Integer, Double> documents = scorer.get(queryId).getValues();
            Map<Integer, Double> relevantDocs = relevantsFound(values, documents);
            int docsRead = 0, relevantDocsRead = 0;
            double queryPrecision = 0, index = 0;
            boolean foundIndex = false;
            for (Map.Entry<Integer, Double> document : documents.entrySet()) {
                docsRead++;
                index++;
                if (relevantDocs.containsKey(document.getKey())) {
                    relevantDocsRead++;
                    queryPrecision += (double) docsRead / relevantDocsRead;
                    if (!foundIndex) {
                        reciprocalRank += 1 / index;
                        foundIndex = true;
                    }
                }
            }
            avgPrecision += queryPrecision / relevantDocs.size();
        }
        avgPrecision = avgPrecision / scorer.size();
        reciprocalRank = reciprocalRank / scorer.size();
    }
    
    private Map<Integer, Double> relevantsFound(Map<Integer, Double> queryValues, Map<Integer, Double> docValues) {
        return queryValues.entrySet().stream()
                .filter(value -> docValues.containsKey(value.getKey()))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }
    
    private Map<Integer, Double> getTenFirstDocumentsOfMap(Map<Integer, Double> map) {
        return map.entrySet().stream()
                .limit(10)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }
}
