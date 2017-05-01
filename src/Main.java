import java.io.File;
import java.nio.file.Files;
import java.util.*;

/**
 * Runnable main class
 * Created by robinsat on 4/30/2017.
 */
public class Main {

    static Map<String, Set<String>> hitList;
    static List<Document> documentList;

    static int averageDocumentLength;

    public static void main(String[] args) {

        curateDocuments();

        while(true) {
            System.out.println("Enter search query, or 'quit' to exit: ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.next();

            if(input.equalsIgnoreCase("quit")) {
                System.exit(0);
            }

            // Find relevant documents
            if(hitList.get(input) != null) {
                System.out.println("Yes");
            } else {
                System.out.println("NO");
            }
        }

    }

    static void curateDocuments() {
        hitList = new HashMap<>();
        documentList = new ArrayList<>();

        File corpusDirectory = new File("corpus");
        File[] list = corpusDirectory.listFiles();
        int sumLengths = 0;
        for (File file : list) {
            System.out.println("Processing " + file.getName());
            Document processedDoc = processDocument(file);
            sumLengths += processedDoc.getNumWords();
        }

        averageDocumentLength = sumLengths / list.length;
    }

    static Document processDocument(File document) {
        try {
            String content = new String(Files.readAllBytes(document.toPath()));
            Document doc = new Document(document.getName(), content);
            documentList.add(doc);
            List<String> words = doc.getActualWords();
            for (String word : words) {
                addToHitList(word, document.getName());
            }
            return doc;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    static void addToHitList(String word, String docName) {
        Set<String> documentsContainingWord = hitList.get(word);
        if(documentsContainingWord == null) {
            documentsContainingWord = new HashSet<>();
            String normalizedWord = normalize(word);
            if(normalizedWord.length() > 0)
                hitList.put(normalize(word), documentsContainingWord);
        }
        documentsContainingWord.add(docName);
    }

    public static String normalize(String original) {
        return original.replaceAll("[^a-zA-z0-9]", "").toLowerCase();
    }

}
