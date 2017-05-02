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

    static double k1 = 1.2;
    static double b = 0.75;

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
            TreeMap<Double, Document> scoredDocs = new TreeMap<>();
            for (Document d : documentList) {
                scoredDocs.put(BM25(d, input), d);
            }
            for (Double score : scoredDocs.descendingKeySet()) {
                System.out.println(scoredDocs.get(score).getName() + ", " + score);
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

    static String normalize(String original) {
        return original.replaceAll("[^a-zA-z0-9]", "").toLowerCase();
    }

    static double inverseDocumentFrequency(String term) {
        Set<String> documentsContainingWord = hitList.get(term);
        int numDocsWithWord = 0;
        if (documentsContainingWord != null)
            numDocsWithWord = documentsContainingWord.size();
        double result = ((double) documentList.size()) - ((double) numDocsWithWord) + 0.5;
        result = result / (numDocsWithWord + 0.5);
        return Math.log(result);
    }

    static double BM25(Document document, String input){
        String[] query = input.split(" ");
        double sum = 0;
        for(String keyword : query) {
            double value = inverseDocumentFrequency(keyword);
            double frequencyInDocument = document.getFrequencyOfWord(keyword);
            value *= frequencyInDocument * (k1 + 1);
            value /= (frequencyInDocument + (k1 * (1 - b + b * document.getNumWords() / averageDocumentLength)));
            sum += value;
        }
        return sum;
    }

}
