package Tokenizers;

import Documents.Document;
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
* Interface to tokenize documents
*/
public interface Tokenizer {
    /**
     * Tokenizing documents
     * @param documents 
     */
    public void tokenize(List<Document> documents);
}
