import java.util.Arrays;

/**
 * This program demonstrates how to implement PeerMessageAnalyzer
 *
 * @author criss.tmd@gmail.com
 */
public class PeerMessageHelper {

    private PeerMessageHelper() {}

    public static String[] getNonEmptyWords(final String message) {
        return Arrays.stream(message.trim().split(" "))
                .map(String::trim)
                .filter(word -> !word.isEmpty())
                .toArray(String[]::new);
    }
}
