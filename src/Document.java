import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by robinsat on 4/30/2017.
 */
public class Document {

    static Pattern anchorPattern = Pattern.compile("<a(.*?)</a>");

    private String content;
    private List<String> anchorTags;

    public Document(String content) {
        this.content = content;
        this.anchorTags = new ArrayList<>();

        Matcher matcher = anchorPattern.matcher(content);
        while(matcher.find()) {
            anchorTags.add(matcher.group(1));
        }

    }

    public int getLength() {
        return this.content.length();
    }

    public List<String> getAnchorTags() {
        return this.anchorTags;
    }
}
