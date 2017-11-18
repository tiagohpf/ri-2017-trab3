package Parsers;

import Documents.Document;
import Documents.GSDocument;
import Utils.Key;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GSParser implements Strategy<Document>{
    private static Scanner sc;

    @Override
    public Document parseFile(File file) {
        Map<Key, Integer> values = new HashMap<>();
        try {
            sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String []data = line.split("\\s+");
                values.put(new Key(Integer.parseInt(data[0]), 
                        Integer.parseInt(data[1])), Integer.parseInt(data[2]));
            }
            return new GSDocument(1, values);
        } catch (FileNotFoundException ex) {
            System.err.println("ERROR: File of Golden Standard not found!");
            System.exit(1);
        }
        return null;
    }

    @Override
    public List<Document> parseDir(File file) {
        File []files = file.listFiles();
        Arrays.sort(files);    
        List<Document> documents = new ArrayList<>();
        for (File f : files) {
            Document document = parseFile(f);
            // Auto-increment in DOCNO of all Documents
            if (document.getId() != documents.size() + 1)
                document.setId(documents.size() + 1);
            documents.add(document);
        }
     return documents;
    } 
}
