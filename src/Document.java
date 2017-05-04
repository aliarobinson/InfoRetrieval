import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by robinsat on 4/30/2017.
 */
public class Document {

    static Pattern wordsPattern = Pattern.compile(">(.*?)<");
    static Pattern anchorPattern = Pattern.compile("<a.*?>(.*?)</a>");
    static Pattern titlePattern = Pattern.compile("<title>(.*?) - Wikipedia");

    private String name;
    private String content;

    private String articleTitle;
    private List<String> actualWords;
    private List<String> anchorTags;

    public Document(String name, String content) {
        this.name = name;
        this.content = content;

        extractTitle();
        extractWords();
        extractAnchorTags();

    }

    public String getFileName() {
        return this.name;
    }

    public String getTitle() {
        return this.articleTitle;
    }

    private void extractTitle() {
        Matcher matcher = titlePattern.matcher(content);
        if(matcher.find()) {
            this.articleTitle = matcher.group(1);
        } else {
            this.articleTitle = this.name;
        }
    }

    private void extractWords() {
        this.actualWords = new ArrayList<>();
        Matcher matcher = wordsPattern.matcher(content);
        while(matcher.find()) {
            String text = matcher.group(1);
            // Now break the text down into individual words
            if(text.length() > 0) {
                StringTokenizer tokenizer = new StringTokenizer(text);
                while (tokenizer.hasMoreTokens()) {
                    this.actualWords.add(tokenizer.nextToken());
                }
            }
        }
    }

    private void extractAnchorTags() {
        this.anchorTags = new ArrayList<>();
        Matcher matcher = anchorPattern.matcher(content);
        while(matcher.find()) {
            anchorTags.add(matcher.group(1));
        }
    }

    public int getNumWords() {
        return this.actualWords.size();
    }

    public List<String> getAnchorTags() {
        return this.anchorTags;
    }

    public List<String> getActualWords() {
        return this.actualWords;
    }

    public int getFrequencyOfWord(String word) {
        int numOccurrences = 0;
        for (String s : this.actualWords) {
            if(Main.normalize(s).equals(Main.normalize(word))) {
                numOccurrences++;
            }
        }
        return numOccurrences;
    }

    public boolean isInAnchorTags(String word) {
        for(String tag :this.anchorTags) {
            if(Main.normalize(tag).contains(Main.normalize(word))) {
                return true;
            }
        }
        return false;
    }

    public boolean isInTitle(String query) {
        String[] words = query.split(" ");
        for(String word : words) {
            if(Main.normalize(articleTitle).contains(Main.normalize(word))) {
                return true;
            }
        }
        return false;
    }

}
