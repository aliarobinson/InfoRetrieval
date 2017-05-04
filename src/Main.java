import java.io.File;
import java.nio.file.Files;
import java.util.*;

/**
 * Runnable main class
 * Created by robinsat on 4/30/2017.
 */
public class Main {

    static Map<String, Set<String>> hitList;
    static Map<String, Document> documentList;

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
            TreeMap<Double, Document> scoredDocs = getRelevantDocs(input);

            StringBuilder sb = new StringBuilder();
            double previousScore = 0;
            double previousGap = 0;
            for (Double score : scoredDocs.descendingKeySet()) {
                if((previousGap > 0 && previousScore - score > previousGap * 5) || previousScore - score > 5) {
                    break;
                }
                sb.append(scoredDocs.get(score).getTitle()).append(", ");

                if(previousScore > 0)
                    previousGap = previousScore - score;
                previousScore = score;
            }

            if(sb.length() > 0)
                System.out.println(sb.substring(0, sb.length() - 2));
            else
                System.out.println("No results found");
        }

    }

    static void curateDocuments() {
        hitList = new HashMap<>();
        documentList = new HashMap<>();

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
            documentList.put(document.getName(), doc);
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
        word = normalize(word);
        if(word.length() == 0)
            return;

        Set<String> documentsContainingWord = hitList.get(word);
        if(documentsContainingWord == null) {
            documentsContainingWord = new HashSet<>();
            hitList.put(word, documentsContainingWord);
        }
        documentsContainingWord.add(docName);
    }

    static String normalize(String original) {
        return original.replaceAll("[^a-zA-z0-9]", "").toLowerCase();
    }

    static TreeMap<Double, Document> getRelevantDocs(String query) {
        String[] words = query.split(" ");
        Set<String> toSearch = new HashSet<>();
        for(String word : words) {
            Set<String> docs = hitList.get(word);
            if(docs != null)
                toSearch.addAll(hitList.get(word));
        }

        List<Document> docsToSearch = new ArrayList<>();
        for (String docName : toSearch) {
            docsToSearch.add(documentList.get(docName));
        }

        TreeMap<Double, Document> results = new TreeMap<>();
        for (Document d : docsToSearch) {
            double score = Math.abs(BM25(d, query));
            if(d.isInTitle(query))
                score += 5;
            if(d.isInAnchorTags(query))
                score += 2;
            results.put(score, d);
        }

        return results;
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
            String normalizedWord = normalize(keyword);
            double value = inverseDocumentFrequency(normalizedWord);
            double frequencyInDocument = document.getFrequencyOfWord(normalizedWord);
            value *= frequencyInDocument * (k1 + 1);
            value /= (frequencyInDocument + (k1 * (1 - b + b * document.getNumWords() / averageDocumentLength)));
            sum += value;
        }
        return sum;
    }

}
