import Tokenizers.SimpleTokenizer;
import Parsers.Parser;
import CorpusReader.CorpusReader;
import Documents.Document;
import Parsers.QueryParser;
import Indexers.IndexerReader;
import Scoring.QueryScoring;
import Tokenizers.CompleteTokenizer;
import Utils.Filter;
import Utils.Key;
import Utils.Pair;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * IR, October 2017
 *
 * Assignment 2 
 *
 * @author Tiago Faria, 73714, tiagohpf@ua.pt
 * @author David dos Santos Ferreira, 72219, davidsantosferreira@ua.pt
 * 
 */
public class Main {

    /**
     * Read files and options from arguments. After that, show the results with the queries.
     * 
     * All files have to be unique.
     * USAGE: [indexer file] [query file] [file result 1] [file result 2]
     * 
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {         
        // The program needs all of 4 arguments
        if (args.length == 4) {
            long startTime = System.currentTimeMillis();
            IndexerReader indexReader = new IndexerReader(args[0]);
            File file = new File(args[1]);
            if (!file.exists()) {
                System.err.println("ERROR: File not found");
                System.exit(1);
            }
            Parser parser = new Parser(new QueryParser());
            // Set documents in Corpus Reader
            CorpusReader corpusReader = new CorpusReader();
            if (file.isDirectory())
                corpusReader.setDocuments(parser.parseDir(file));
            else
                corpusReader.setDocuments((List<Document>)parser.parseFile(file));
            // Queries
            List<Document> documents = corpusReader.getDocuments();
            // Type of Tokenizer in use
            String tokenizerType = indexReader.getTokenizerType();
            // Terms after tokenizing
            List<Pair<String, Integer>> terms;
            // Indexer loadaded from file
            Map<String, List<Pair<Integer, Integer>>> indexer = indexReader.getIndexer();
            System.out.println("***********************************************************************");
            if (tokenizerType.equals(SimpleTokenizer.class.getName())) {
                SimpleTokenizer simpleTokenizer = new SimpleTokenizer();
                System.out.println("\t\tQueries with Simple Tokenizer");
                simpleTokenizer.tokenize(documents);
                terms = simpleTokenizer.getTerms();
                showResults(indexer, terms, documents.size(), args[2], args[3], startTime);
            } else if (tokenizerType.equals(CompleteTokenizer.class.getName())) {
                CompleteTokenizer completeTokenizer = new CompleteTokenizer();
                System.out.println("\t\tQueries with Complete Tokenizer");
                completeTokenizer.tokenize(documents);
                terms = completeTokenizer.getTerms();
                // Apply stopWording and stemming in case of using Complete Tokenizer
                Filter filter = new Filter();
                terms = filter.stopwordsFiltering(terms);
                terms = filter.stemmingWords(terms);
                showResults(indexer, terms, documents.size(), args[2], args[3], startTime);           
            } else {    
                System.err.println("ERROR: Invalid type of Tokenizer!");
                System.exit(1);
            }
        } else {
            System.err.println("ERROR: Invalid number of arguments!");
            System.out.println("USAGE: <indexer file> <queries file> <file result 1> <file result 2>");
            System.exit(1);
        }
    }
    
    /**
     * Show number of words in the query that appear in the document 
     * and total frequency of query words in the document
     * 
     * @param indexer
     * @param terms
     * @param size
     * @param firstFile
     * @param secondFile 
     */
    private static void showResults(Map<String, List<Pair<Integer, Integer>>> indexer, List<Pair<String, Integer>> terms, 
            int size, String firstFile, String secondFile, long startTime) {
        System.out.println("***********************************************************************");
        QueryScoring score = new QueryScoring(indexer, terms, size);
        score.calculateScores();
        score.writeNumberOfWords(new File(firstFile));
        // Get number of words in the query that appear in the document
        Map<Key, Integer> numberOfTerms = score.getNumberOfTerms();
        System.out.println("1. Number of query's words that appear in documents");
        System.out.println("------------------------------------------------------------------------");
        System.out.println(numberOfTerms);
        score.writeWordsFrequency(new File(secondFile));
        // Get total frequency of query words in the document
        Map<Key, Integer> termsFrequency = score.getTermsFrequency();
        System.out.println("------------------------------------------------------------------------");
        System.out.println("2. Total frequency of query words in the documents");
        System.out.println("------------------------------------------------------------------------");
        System.out.println(termsFrequency);
        System.out.println("------------------------------------------------------------------------");
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Indexing Time: " + elapsedTime + " ms.");
    } 
}
