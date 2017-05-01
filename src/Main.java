import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Runnable main class
 * Created by robinsat on 4/30/2017.
 */
public class Main {

    static Map<String, List<String>> hitList;

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
        }

    }

    static void curateDocuments() {
        hitList = new HashMap<String, List<String>>();
        File corpusDirectory = new File("corpus");
        File[] list = corpusDirectory.listFiles();
        for (File file : list) {
            processDocument(file);
        }
    }

    static void processDocument(File document) {
        try {
            String content = new String(Files.readAllBytes(document.toPath()));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
