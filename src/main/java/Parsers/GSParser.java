package Parsers;

import Documents.Document;
import Documents.GSDocument;
import Utils.Key;
import Utils.Values;
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
        Map<Integer, Values> relevances = new HashMap<>();
        try {
            sc = new Scanner(file);
            int actualId = 1;
            Map<Integer, Double> values = new HashMap<>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String []data = line.split("\\s+");
                int queryId = Integer.parseInt(data[0]);
                int docId = Integer.parseInt(data[1]);
                int relevance = Integer.parseInt(data[2]);
                if (relevance >= 1 && relevance <= 3) {
                    if (queryId != actualId) {
                        relevances.put(actualId, new Values(values));
                        values = new HashMap<>();
                        actualId++;
                    }
                    values.put(docId, (double)relevance);
                }
            }
            return new GSDocument(1, relevances);
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
