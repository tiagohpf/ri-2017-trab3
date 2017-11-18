import CorpusReader.CorpusReader;
import Documents.Document;
import Documents.GSDocument;
import Indexers.IndexerCreator;
import Metrics.MetricsCalculator;
import Parsers.GSParser;
import Weights.IndexerWeight;
import Parsers.Parser;
import Parsers.QueryParser;
import Parsers.XMLParser;
import Tokenizers.CompleteTokenizer;
import Utils.Filter;
import Utils.Key;
import Utils.Values;
import Weights.QueryScoring;
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
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 6) {
            File file = new File(args[0]);
            if (!file.exists()) {
                System.err.println("ERROR: Files to read not found!");
                System.exit(1);
            }
            if (args[2].equals(args[0]) || args[4].equals(args[0]) || args[5].equals(args[0])) {
                System.err.println("ERROR: The file you want to create was read before!");
                System.exit(1);
            }
            Parser parser = new Parser(new XMLParser());
            CorpusReader docsReader = new CorpusReader();
            // In case of filename is a directory
            if (file.isDirectory())
                docsReader.setDocuments(parser.parseDir(file));
            // In case of a filename is an only file
            else
                docsReader.addDocument((Document) parser.parseFile(file));
            List<Document> documents = docsReader.getDocuments();
            CompleteTokenizer docsTokenizer = new CompleteTokenizer();
            docsTokenizer.tokenize(documents);
            Map<Integer, List<String>> terms = docsTokenizer.getTerms();
            Filter filter = new Filter();
            filter.loadStopwords(new File(args[1]));
            terms = filter.stopwordsFiltering(terms);
            terms = filter.stemmingWords(terms);
            IndexerCreator docCreator = new IndexerCreator(terms);
            docCreator.createIndexer();
            IndexerWeight weighter = new IndexerWeight(terms);
            Map<String, Values> docsIndexer = docCreator.getIndexer();
            weighter.calculateTermFreq(docsIndexer);
            weighter.writeToFile(new File(args[4]));

            File queriesFile = new File(args[2]);
            if (!queriesFile.exists()) {
                System.err.println("ERROR: Files of queries not found!");
                System.exit(1);
            }
            parser = new Parser(new QueryParser());
            CorpusReader queriesReader = new CorpusReader();
            queriesReader.setDocuments((List<Document>)parser.parseFile(queriesFile));
            List<Document> queriesDocs = queriesReader.getDocuments();
            CompleteTokenizer queriesTokenizer = new CompleteTokenizer();
            queriesTokenizer.tokenize(queriesDocs);
            Map<Integer, List<String>> queries = queriesTokenizer.getTerms();
            queries = filter.stopwordsFiltering(queries);
            queries = filter.stemmingWords(queries);
            IndexerCreator queryCreator = new IndexerCreator(queries);
            queryCreator.createIndexer();
            Map<String, Values> queriesIndexer = queryCreator.getIndexer();
            QueryScoring scoring = new QueryScoring(queriesIndexer, queries, documents.size());
            scoring.calculateInverseDocFreq(queriesIndexer, docsIndexer);
            scoring.calculateDocScorer(docsIndexer);
            scoring.writeToFile(new File(args[5]));
            Map<Integer, Values> scorer = scoring.getQueryScorer();
            
            File gsFile = new File(args[3]);
            if (!gsFile.exists()) {
                System.err.println("ERROR: File of Gold Standard not found!");
                System.exit(1);
            }
            parser = new Parser(new GSParser());
            GSDocument gsDoc = (GSDocument) parser.parseFile(gsFile);
            MetricsCalculator metrics = new MetricsCalculator(queries, scorer, gsDoc);
        } else {
            System.err.println("ERROR: Invalid number of arguments!");
            System.out.println("USAGE: <file/dir> <stopwords> <queries> <gold standard> <indexer weights> <ranked queries>");
        }
    }
}
