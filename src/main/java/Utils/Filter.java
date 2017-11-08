package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.tartarus.snowball.ext.englishStemmer;

/**
 * IR, October 2017
 *
 * Assignment 2 
 *
 * @author Tiago Faria, 73714, tiagohpf@ua.pt
 * @author David dos Santos Ferreira, 72219, davidsantosferreira@ua.pt
 * 
 */

/**
 * Class that applys stopWording and stemming
 */
public class Filter {
    // List of stopwords
    private final List<String> stopWords;
    
    /**
     * Constructor
     * @throws FileNotFoundException 
     */
    public Filter() throws FileNotFoundException {
        stopWords = loadStopwords();
    }
    
    /**
     * Read the file of stopwords and load them to a list
     * @return stopWords
     * @throws FileNotFoundException 
     */
    public List<String> loadStopwords() throws FileNotFoundException {
        List<String> words = new ArrayList<>();
        File file = new File ("stopwords.txt");
        Scanner sc = new Scanner(file);
        while (sc.hasNext()) {
            String word = sc.nextLine();
            // String.trim() to remove white spaces after text
            if (word.trim().length() > 0)
                words.add(word.trim());
        }
        return words;
    }
    
    /**
     * Apply the stopwording filtering
     * If there's any term in the stopwording list, it'll be removed from terms list
     * @param terms
     * @return filtered words
     */
    public Map<Integer, List<String>> stopwordsFiltering(Map<Integer, List<String>> terms) {
        Map<Integer, List<String>> result = new HashMap<>();
        for (Map.Entry<Integer, List<String>> entry : terms.entrySet()) {
            List<String> words = entry.getValue();
            List<String> filter = words.stream()
                    .filter(line -> !stopWords.contains(line))
                    .collect(Collectors.toList());
            result.put(entry.getKey(), filter);
        }
        return result;
    }
    
    /**
     * Stemming terms
     * @param terms
     * @return stemmed terms
     */
    public Map<Integer, List<String>> stemmingWords(Map<Integer, List<String>> terms) {
        Map<Integer, List<String>> result = new HashMap<>();
        englishStemmer stemmer = new englishStemmer();
        for (Map.Entry<Integer, List<String>> entry : terms.entrySet()) {
            List<String> words = entry.getValue();
            for (int i = 0; i < words.size(); i++) {
                stemmer.setCurrent(words.get(i));
                if (stemmer.stem())
                    words.set(i, stemmer.getCurrent());
            }
            result.put(entry.getKey(), words);
        }
        return result;
    } 
}