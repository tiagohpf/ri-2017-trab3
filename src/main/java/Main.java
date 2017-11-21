import CorpusReader.CorpusReader;
import Documents.Document;
import Documents.GSDocument;
import Documents.QueryScoresDocument;
import Indexers.IndexerCreator;
import Metrics.MetricsCalculator;
import Parsers.GSParser;
import Weighters.DocumentWeighter;
import Parsers.Parser;
import Parsers.QueryParser;
import Parsers.QueryScoresParser;
import Parsers.XMLParser;
import Tokenizers.CompleteTokenizer;
import Utils.Filter;
import Utils.Values;
import Weighters.QueryWeighter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * IR, November 2017
 *
 * Assignment 3 
 *
 * @author Tiago Faria, 73714, tiagohpf@ua.pt
 * @author David dos Santos Ferreira, 72219, davidsantosferreira@ua.pt
 * 
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        // Arguments: documents; stopwords; queries; queries with relevance; indexer with weights; ranked query's results
        if (args.length == 6) {
            File file = new File(args[0]);
            if (!file.exists()) {
                System.err.println("ERROR: Files to read not found!");
                System.exit(1);
            }
            // The documents, the indexer, the ranked results and the queries must have unique names
            if (args[2].equals(args[0]) || args[4].equals(args[0]) || args[5].equals(args[0])) {
                System.err.println("ERROR: The file you want to create was read before!");
                System.exit(1);
            }
            Parser parser = new Parser(new XMLParser());
            CorpusReader documentReader = new CorpusReader();
            // In case of filename is a directory
            if (file.isDirectory())
                documentReader.setDocuments(parser.parseDir(file));
            // In case of a filename is an only file
            else
                documentReader.addDocument((Document) parser.parseFile(file));
            List<Document> documents = documentReader.getDocuments();
            CompleteTokenizer documentTokenizer = new CompleteTokenizer();
            // Tokenize all documents
            documentTokenizer.tokenize(documents);
            Map<Integer, List<String>> documentTerms = documentTokenizer.getTerms();
            Filter filter = new Filter();
            // Load stopwords
            filter.loadStopwords(new File(args[1]));
            // Apply stopwording filtering
            documentTerms = filter.stopwordsFiltering(documentTerms);
            // Apply stemming
            documentTerms = filter.stemmingWords(documentTerms);
            IndexerCreator documentIndexCreator = new IndexerCreator(documentTerms);
            // Create an indexer
            documentIndexCreator.createIndexer();
            Map<String, Values> documentIndexer = documentIndexCreator.getIndexer();
            // Calculate weights of documents
            DocumentWeighter documentWeighter = new DocumentWeighter(documentTerms);
            // Calculate tf
            documentWeighter.calculateTermFreq(documentIndexer);
            // Write results to file
            documentWeighter.writeToFile(new File(args[4]));

            // StartTime of processing queries
            long startTime = System.currentTimeMillis();
            File queriesFile = new File(args[2]);
            if (!queriesFile.exists()) {
                System.err.println("ERROR: Files of queries not found!");
                System.exit(1);
            }
            parser = new Parser(new QueryParser());
            CorpusReader queryReader = new CorpusReader();
            queryReader.setDocuments((List<Document>)parser.parseFile(queriesFile));
            List<Document> queryDocuments = queryReader.getDocuments();
            CompleteTokenizer queryTokenizer = new CompleteTokenizer();
            // Tokenize terms of queries
            queryTokenizer.tokenize(queryDocuments);
            Map<Integer, List<String>> queries = queryTokenizer.getTerms();
            // Apply stopwording filtering
            queries = filter.stopwordsFiltering(queries);
            // Apply stemming
            queries = filter.stemmingWords(queries);
            // Create an indexer for queries to calculate weights later
            IndexerCreator queryCreator = new IndexerCreator(queries);
            queryCreator.createIndexer();
            Map<String, Values> queryIndexer = queryCreator.getIndexer();
            // Calculate weights of queries
            QueryWeighter queryWeighter = new QueryWeighter(queryIndexer, queries, documents.size());
            // Calculate idf
            queryWeighter.calculateInverseDocFreq(queryIndexer, documentIndexer);
            // Calculate score of document
            queryWeighter.calculateDocumentScore(documentIndexer);
            // Write results to file
            queryWeighter.writeToFile(new File(args[5]));
            // EndTime of processing queries
            long endTime = System.currentTimeMillis();
           
            // Gold Standard file
            File gsFile = new File(args[3]);
            if (!gsFile.exists()) {
                System.err.println("ERROR: File of Gold Standard not found!");
                System.exit(1);
            }
            parser = new Parser(new GSParser());
            // Parse file of queries relevances
            GSDocument gsDocument = (GSDocument) parser.parseFile(gsFile);
            Map<Integer, Values> queryScorer = queryWeighter.getQueryScorer();
            // Calculate evaluation and efficiency metrics
            MetricsCalculator metrics = new MetricsCalculator(queryScorer, gsDocument);
            System.out.println("--------------------------------------------------------------------");
            System.out.println("\t\t\t Assignment 3 Metrics");
            System.out.println("--------------------------------------------------------------------");
            System.out.format("Precision: %.3f\n", metrics.getMeanPrecision());
            System.out.format("Recall: %.3f\n", metrics.getMeanRecall());
            System.out.format("F-Measure: %.3f\n", metrics.getMeanFMeasure());
            System.out.format("Mean Average Precision: %.3f\n", metrics.getMeanAveragePrecision());
            System.out.format("Mean Precision at Rank 10: %.3f\n", metrics.getMeanPrecisionAtRank10());
            System.out.format("Mean Reciprocal Rank: %.3f\n", metrics.getMeanReciprocalRank());
            double latency = ((double) (endTime - startTime) / queries.size()) / 1000;
            System.out.format("Mean Latency: %.3f second/query\n", latency);
            System.out.format("Query Throughput: %d queries/second\n", Math.round(1 / latency));
            
            /**
             * Results to the implementation of Assignment 2
             * To view the results and compara, uncomment
             */
            /*Map<String, File> assign2Files = new LinkedHashMap<>();
            assign2Files.put("Assignment 2 : Simple Tokenizer - Words In Documents", new File("assign2-results/ST1.txt"));
            assign2Files.put("Assignment 2 : Simple Tokenizer - Frequency Of Words", new File("assign2-results/ST2.txt"));
            assign2Files.put("Assignment 2 : Complete Tokenizer - Words In Documents", new File("assign2-results/CT1.txt"));
            assign2Files.put("Assignment 2 : Coplete Tokenizer - Frequency Of Words", new File("assign2-results/CT2.txt"));
            parser = new Parser(new QueryScoresParser());
            for (Map.Entry<String, File> entry : assign2Files.entrySet()) {
                long newStart = System.currentTimeMillis();
                GSDocument newDocument = (GSDocument) parser.parseFile(entry.getValue());
                Map<Integer, Values> newScorer = newDocument.getRelevants();
                MetricsCalculator newMetrics = new MetricsCalculator(newScorer, gsDocument);
                long newEnd = System.currentTimeMillis();
                System.out.println("--------------------------------------------------------------------");
                System.out.println("\t " + entry.getKey());
                System.out.println("--------------------------------------------------------------------");
                System.out.format("Precision: %.3f\n", newMetrics.getMeanPrecision());
                System.out.format("Recall: %.3f\n", newMetrics.getMeanRecall());
                System.out.format("F-Measure: %.3f\n", newMetrics.getMeanFMeasure());
                System.out.format("Mean Average Precision: %.3f\n", newMetrics.getMeanAveragePrecision());
                System.out.format("Mean Precision at Rank 10: %.5f\n", newMetrics.getMeanPrecisionAtRank10());
                System.out.format("Mean Reciprocal Rank: %.3f\n", newMetrics.getMeanReciprocalRank());
                double newLatency = ((double) (newEnd - newStart) / queries.size()) / 1000;
                System.out.format("Mean Latency: %.3f second/query\n", newLatency);
                System.out.format("Query Throughput: %d queries/second\n", Math.round(1 / newLatency));
            }*/
        } else {
            System.err.println("ERROR: Invalid number of arguments!");
            System.out.println("USAGE: <file/dir> <stopwords> <queries> <gold standard> <indexer weights> <ranked queries>");
        }
    }
}
