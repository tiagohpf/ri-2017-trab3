import CorpusReader.CorpusReader;
import Documents.Document;
import Indexers.IndexerWriter;
import Parsers.Parser;
import Parsers.XMLParser;
import Tokenizers.CompleteTokenizer;
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
        if (args.length == 3) {
            File file = new File(args[0]);
                if (!file.exists()) {
                    System.err.println("ERROR: File not found");
                    System.exit(1);
                }
                Parser parser = new Parser(new XMLParser());
                CorpusReader reader = new CorpusReader();
                // In case of filename is a directory
                if (file.isDirectory())
                    reader.setDocuments(parser.parseDir(file));
                // In case of a filename is an only file
                else
                    reader.addDocument((Document) parser.parseFile(file));
                List<Document> documents = reader.getDocuments();
                // Indexer file need to have an unique name in actual directory
                File indexerFile = new File(args[2]);
                if (indexerFile.exists()) {
                    System.err.println("ERROR: The file you want to create already exists!");
                    System.exit(1);
                }
                CompleteTokenizer tokenizer = new CompleteTokenizer();
                tokenizer.tokenize(documents);
                Map<Integer, List<String>> terms = tokenizer.getTerms();
                String tokenizerType = CompleteTokenizer.class.getName();
                IndexerWriter indexer = new IndexerWriter(terms, indexerFile, tokenizerType);
        } else {
            System.err.println("ERROR: Invalid number of arguments!");
            System.out.println("USAGE: <file or dir> <file with queries> <indexer file>");
            System.exit(1);
        }       
    }
}
