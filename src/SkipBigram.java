import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a set of n-skip bigrams for a corpus. Bigrams can be assimilated by sets of tokens at a time.
 */
public class SkipBigram {
    private Map<Pair<String, String>, Integer> bigramMap = new HashMap<>();
    private Map<String, Integer> wordMap = new HashMap<>();
    private int nSkip;
    private int totalWordCount;
    private int totalBigramCount;

    public SkipBigram(int nSkip) {
        this.nSkip = nSkip;
    }

    /**
     * Iterates through the given tokens and maps
     *
     * @param sourceText
     */
    public void processBigrams(String[] sourceText) {
        totalWordCount += sourceText.length;
        totalBigramCount += sourceText.length - nSkip - 1;
        for (int i = 0; i < sourceText.length; i++) {
            // track bigram occurrences
            if (i < sourceText.length - nSkip - 1) {
                Pair<String, String> bigram = new Pair<>(sourceText[i], sourceText[i + nSkip + 1]);
                Integer oldCount = bigramMap.get(bigram);
                if (oldCount == null) {
                    bigramMap.put(bigram, 1);
                } else {
                    oldCount++;
                    bigramMap.put(bigram, oldCount);
                }
            }

            // count occurrences of each word
            Integer oldCount = wordMap.get(sourceText[i]);
            if (oldCount == null) {
                wordMap.put(sourceText[i], 1);
            } else {
                oldCount++;
                wordMap.put(sourceText[i], oldCount);
            }
        }
    }

    /**
     * Returns the conditional probability of the given word occurring 'n' skips ('n' determined upon construction of
     * this class after the preceding word.
     *
     * @param precedingWord
     * @param word
     * @return
     */
    public double getWordProbability(String precedingWord, String word) {
        Pair<String, String> bigramWords = new Pair<>(precedingWord, word);

        int bigramCount = bigramMap.get(bigramWords);
        double bigramProb = ((double) bigramCount) / totalBigramCount;
        int precedingWordCount = wordMap.get(precedingWord);
        double wordProb = ((double) precedingWordCount) / totalWordCount;
        return bigramProb / wordProb;
    }
}
