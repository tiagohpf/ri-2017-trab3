package Metrics;

import Documents.GSDocument;
import Utils.Values;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * IR, November 2017
 *
 * Assignment 3 
 *
 * @author Tiago Faria, 73714, tiagohpf@ua.pt
 * @author David dos Santos Ferreira, 72219, davidsantosferreira@ua.pt
 * 
 */

// Class that calculates evaluation and efficiency metrics
public class MetricsCalculator {
    // Indexer of documents in queries
    private final Map<Integer, Values> indexer;
    private final GSDocument gsDocument;
    private double precision;
    private double recall;
    // F-Measure
    private double fmeasure;
    // Mean Average Precision
    private double avgPrecision;
    // Mean Precision at rank 10
    private double precision10;
    // Mean Reciprocal Rank
    private double reciprocalRank;
    
    /**
     * Constructor
     * @param indexer
     * @param gsDocument 
     */
    public MetricsCalculator(Map<Integer, Values> indexer, GSDocument gsDocument) {
        this.indexer = indexer;
        this.gsDocument = gsDocument;
        precision = recall = fmeasure = avgPrecision = precision10 = reciprocalRank = 0;
        calculateMeasures();
        calculateAvgPrecisionReciprocalRank();
    }
    
    /**
     * Get Mean Precision
     * @return precision
     */
    public double getMeanPrecision() {
        return precision;
    }
    
    /**
     * Get Mean Precision
     * @return recall
     */
    public double getMeanRecall() {
        return recall;
    }
    
    /**
     * Get Mean F-Measure
     * @return fmeasure
     */
    public double getMeanFMeasure() {
        return fmeasure;
    }
    
    /**
     * Get Mean Average Precision
     * @return avgPrecision
     */
    public double getMeanAveragePrecision() {
        return avgPrecision;
    }
    
    /**
     * Get Mean Average Precision at Rank 10
     * @return precision10
     */
    public double getMeanPrecisionAtRank10() {
        return precision10;
    }
    
    /**
     * Get Mean Reciprocal Rank
     * @return reciprocalRank
     */
    public double getMeanReciprocalRank() {
        return reciprocalRank;
    }
      
    /**
     * Calculate measures of precision, precision at rank 10, recall and f-measure
     */
    private void calculateMeasures() {
        for (Map.Entry<Integer, Values> query : gsDocument.getRelevants().entrySet()) {
            int queryId = query.getKey();
            // Documents of query present in Gold Standard file
            Map<Integer, Double> gsDocuments = query.getValue().getValues();
            // Documents of query present in indexer
            Map<Integer, Double> indexerDocuments = indexer.get(queryId).getValues();
            // Use only 10 documents of indexer
            Map<Integer, Double> tenFirstDocuments = getTenFirstDocumentsOfMap(indexerDocuments);
            // Number of relevants found in documents
            int nRelevantsFound = relevantsFound(gsDocuments, indexerDocuments).size();
            // Number of relevants found in 10 documents
            int nRelevantsFoundInTen = relevantsFound(gsDocuments, tenFirstDocuments).size();
            double queryPrecision = (double) nRelevantsFound / indexerDocuments.size();
            double queryPrecision10 = (double) nRelevantsFoundInTen / indexerDocuments.size();
            double queryRecall = (double) nRelevantsFound / gsDocuments.size();
            precision += queryPrecision;
            precision10 += queryPrecision10;
            recall += queryRecall;
            fmeasure += (2 * queryRecall * queryPrecision) / (queryRecall + queryPrecision);
        }
        // Calculate averages
        precision = precision / indexer.size();
        precision10 = precision10 / indexer.size();
        recall = recall / indexer.size();
        fmeasure = fmeasure / indexer.size();
    }
    
    /**
     * Calculate mean average precision and reciprocal rank
     */
    private void calculateAvgPrecisionReciprocalRank() {
        for (Map.Entry<Integer, Values> query : gsDocument.getRelevants().entrySet()) {
            int queryId = query.getKey();
            // Documents of query present in Gold Standard file
            Map<Integer, Double> gsDocuments = query.getValue().getValues();
            // Documents of query present in indexer
            Map<Integer, Double> indexerDocuments = indexer.get(queryId).getValues();
            // Documents of query that are relevant
            Map<Integer, Double> relevantDocuments = relevantsFound(gsDocuments, indexerDocuments);
            // Number of documents read and number of relevant documents read
            int docsRead = 0, relevantDocsRead = 0;
            double queryPrecision = 0, index = 0;
            // Check if first document was found
            boolean foundDocument = false;
            for (Map.Entry<Integer, Double> document : indexerDocuments.entrySet()) {
                // When a document is read, increment the number of documents read
                docsRead++;
                // Index of document
                index++;
                /** Increment number of relevant documents read if a relavant document was found
                 * After that, calculate its precision
                 */
                if (relevantDocuments.containsKey(document.getKey())) {
                    relevantDocsRead++;
                    queryPrecision += (double) docsRead / relevantDocsRead;
                    if (!foundDocument) {
                        reciprocalRank += 1 / index;
                        foundDocument = true;
                    }
                }
            }
            // Calculate average of precision
            avgPrecision += queryPrecision / relevantDocuments.size();
        }
        // Calculate mean of average precision
        avgPrecision = avgPrecision / indexer.size();
        // Calculate mean of reciprocal rank
        reciprocalRank = reciprocalRank / indexer.size();
    }
    
    /**
     * Get relevant documents found in indexer
     * @param queryValues
     * @param indexerValues
     * @return relevants
     */
    private Map<Integer, Double> relevantsFound(Map<Integer, Double> queryValues, Map<Integer, Double> indexerValues) {
        return queryValues.entrySet().stream()
                .filter(value -> indexerValues.containsKey(value.getKey()))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }
    
    /**
     * Get ten first documents of indexer
     * @param indexer
     * @return 
     */
    private Map<Integer, Double> getTenFirstDocumentsOfMap(Map<Integer, Double> indexer) {
        return indexer.entrySet().stream()
                .limit(10)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }
}
