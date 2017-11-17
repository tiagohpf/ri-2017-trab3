package Indexers;

import Utils.Values;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * IR, October 2017
 *
 * Assignment 2 
 *
 * @author Tiago Faria, 73714, tiagohpf@ua.pt
 * @author David dos Santos Ferreira, 72219, davidsantosferreira@ua.pt
 * 
 */

// Class that loads an Indexer.
public class IndexerReader {
    // Indexer. The Indexer has a list of terms like [term, docId: frequency] 
    private final Map<String, Values> indexer;
    private static Scanner sc;
    // Type of Tokenizer in use
    private String tokenizerType;
    
    /**
     * Constructor
     * 
     * @param file 
     */
    public IndexerReader(String file) {
        indexer = new HashMap<>();
        readFile(file);
    }
    
    /**
     * Get Indexer
     * @return indexer 
     */
    public Map<String, Values> getIndexer() {
        return new TreeMap<>(indexer);
    }
    
    /**
     * Read Indexer's file
     * @param filename 
     */
    private void readFile(String filename) {
        // Create the file
        File file = new File(filename);
        // Read the file
        try {
           sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Index not found!");
            System.exit(1);
        }
        List<String> lines = new ArrayList<>();
        while (sc.hasNext()) {
            String line = sc.nextLine();
            if (line.length() > 0)
                lines.add(line);
        }
        // Get index of line of Tokenizer's type
        int index = lines.size() - 1;
        tokenizerType = lines.get(index).replaceAll("[-> ]", "");
        lines.remove(index);
        loadIndexer(lines);
    }
    
    /**
     * Load Indexer
     * @param lines 
     */
    private void loadIndexer(List<String> lines) {
        for (String line : lines) {
            // Slipt the term of his occurrences in the documents
            String []elements = line.split(",");
            String term = elements[0];
            Values documents = new Values();
            for (int i = 1; i < elements.length; i++) {
                String []doc = elements[i].split(":");
                int docId = Integer.parseInt(doc[0]);
                int frequency = Integer.parseInt(doc[1]);
                documents.addValue(docId, frequency);
            }
            indexer.put(term, documents);
        }
    }
}
