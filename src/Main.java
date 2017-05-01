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
        for (File file : list) {
            System.out.println("Processing " + file.getName());
            processDocument(file);
        }
    }

    static void processDocument(File document) {
        try {
            String content = new String(Files.readAllBytes(document.toPath()));
            StringTokenizer tokenizer = new StringTokenizer(content);
            while(tokenizer.hasMoreTokens()) {
                addToHitList(tokenizer.nextToken(), document.getName());
            }
            Document doc = new Document(content);
            documentList.add(doc);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static void addToHitList(String word, String docName) {
        Set<String> documentsContainingWord = hitList.get(word);
        if(documentsContainingWord == null) {
            documentsContainingWord = new HashSet<>();
        }
        documentsContainingWord.add(docName);
    }
}
