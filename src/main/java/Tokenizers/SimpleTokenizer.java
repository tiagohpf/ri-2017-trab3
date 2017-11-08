package Tokenizers;

import Documents.Document;
import Documents.XMLDocument;
import java.util.ArrayList;
import java.util.HashMap;
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

/*
* Simple Tokenizer.
* Class that tokenizes a file or a directory, divided by whitespaces.
*/
public class SimpleTokenizer implements Tokenizer{
    private Map<Integer, List<String>> terms;
    
    /**
     * Constructor
     */
    public SimpleTokenizer() {
        terms = new HashMap<>();
    }
    
    /**
     * Get all terms of tokenizer.
     * @return list of terms
     */
    public Map<Integer, List<String>>  getTerms() {
        return terms;
    }
    
    /**
     * Tokenizing all documents
     * @param documents 
     */
    @Override
    public void tokenize(List<Document> documents) {
        for(int i = 0; i < documents.size(); i++){
            Document document = documents.get(i);
            String content;
            // Use title in case of be a XML file
            if (document instanceof XMLDocument)
                content = ((XMLDocument) document).getTitle() + "\n" + document.getText();
            else
                content = document.getText();
            // Remove all non-alphabetical characters.
            String newContent = content.replaceAll("[^a-zA-Z ]", " ");
            // Tokenize by whitespaces.
            String[] temp = newContent.split(" ");
            List<String> words = new ArrayList<>();
            for(String s : temp){
                // Only accept words with equal or more than 3 characters.
                if(s.trim().length() >=3 )
                    words.add(s.trim().toLowerCase());
            }
            terms.put(document.getId(), words);
        }
    }
}
