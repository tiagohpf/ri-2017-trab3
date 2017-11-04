package Tokenizers;

import Documents.Document;
import Documents.XMLDocument;
import Utils.Pair;
import java.util.ArrayList;
import java.util.List;

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
* Complete Tokenizer.
* Class that tokenizes a file or a directory, given special attention to certain characters.
*/
public class CompleteTokenizer implements Tokenizer{
    private List<Pair<String, Integer>> terms;
    
    /**
     * Constructor
     */
    public CompleteTokenizer() {
        terms = new ArrayList<>();
    }
    
    /**
     * Get all terms of tokenizer
     * @return list of terms
     */
    public List<Pair<String, Integer>> getTerms() {
        return terms;
    }
    
    /**
     * Tokenizing all documents
     * @param documents 
     */
    @Override
    public void tokenize(List<Document> documents) {
        for (Document document : documents) {
            int id = document.getId();
            String content;
            // Use title in case of be a XML file
            if (document instanceof XMLDocument)
                content = ((XMLDocument) document).getTitle() + "\n" + document.getText();
            else
                content = document.getText();
            // Remove some special characters
            content = content.replaceAll("[*+/:;'(),\"]", "");
            content = content.replaceAll("\n", " ");
            // Tokenize by white space
            String []text = content.split(" ");
            for (int i = 0; i < text.length; i++) {
                String term = text[i];
                /*
                * Remove '-' in small terms. Otherwise, keep the term
                */
                if (term.contains("-")) {
                    term = term.replaceAll(".", "");
                    // If theres is a term like '--2', change to '-2'.
                    if (term.length() >= 2 && term.charAt(0) == '-' && term.charAt(1) == '-') {
                        if (term.length() > 2 && Character.isDigit(term.charAt(2)))
                            term = term.substring(1, term.length());
                        // Remove '-' in small terms.
                        else if (term.length() < 10)
                            term = term.replaceAll("-", "");
                    }
                    // If theres that starts with '-' and after that is a letter, remove the firs '-'.
                    else if (term.length() >= 2 && term.charAt(0) == '-' && Character.isLetter(term.charAt(1))) {
                        term = term.substring(1, term.length());
                        // Remove '-' in small terms.
                        if (term.length() < 10)
                           term = term.replaceAll("-", ""); 
                    }
                    // Remove '-' in small terms.
                    else if (term.length() <= 1 || term.length() < 10)
                        term = term.replaceAll("-", "");
                }
                // In case of number like 92.3, keep the term.
                else if (term.contains(".")) {
                    int index = term.indexOf('.');
                    // Check if '.' is between at least two numbers
                    if (term.length() > index + 1 && index > 0
                            && (Character.isDigit(term.charAt(index - 1))) 
                            && (Character.isDigit(term.charAt(index + 1)))) {
                        term = term.replaceAll("-", "");
                        // In case of term ends with '.', remove it.
                        if ((term.charAt(term.length() - 1) + "").contains(".")) {
                            term = term.substring(0, term.length() - 1);
                            }
                    }
                    else
                        term = term.replaceAll("[.-]", "");
                } 
                // If term starts with '=', remove it.
                else if (term.length() == 1 && term.charAt(0) == '=') {
                    term = term.replaceAll("=", "");                }
                // Just add tokens with content and without whitespaces
                if (term.trim().length() > 0)
                    terms.add(new Pair<>(term, id));
            }
        }
    }
    
}
