package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * Helper functions for handling strings.
 */
public class StringUtil {

    /** Maximum number of Unicode code points allowed in a person field. */
    public static final int MAX_FIELD_LENGTH = 255;

    /**
     * Returns whether a person field contains a bounded, printable, non-blank value.
     * Leading and trailing whitespace is rejected because command parsing trims field boundaries.
     */
    public static boolean isValidFieldValue(String value) {
        requireNonNull(value);

        if (value.isEmpty() || value.codePointCount(0, value.length()) > MAX_FIELD_LENGTH) {
            return false;
        }
        if (value.codePoints().anyMatch(Character::isISOControl)) {
            return false;
        }

        int firstCodePoint = value.codePointAt(0);
        int lastCodePoint = value.codePointBefore(value.length());
        return !isWhitespace(firstCodePoint) && !isWhitespace(lastCodePoint);
    }

    /**
     * Returns whether a string contains a control character.
     */
    public static boolean containsControlCharacter(String value) {
        requireNonNull(value);
        return value.codePoints().anyMatch(Character::isISOControl);
    }

    /**
     * Returns whether a string contains at least one letter or digit.
     */
    public static boolean containsLetterOrDigit(String value) {
        requireNonNull(value);
        return value.codePoints().anyMatch(Character::isLetterOrDigit);
    }

    private static boolean isWhitespace(int codePoint) {
        return Character.isWhitespace(codePoint) || Character.isSpaceChar(codePoint);
    }

    /**
     * Returns true if the {@code sentence} contains the {@code word}.
     *   Ignores case, but a full word match is required.
     *   <br>examples:<pre>
     *       containsWordIgnoreCase("ABc def", "abc") == true
     *       containsWordIgnoreCase("ABc def", "DEF") == true
     *       containsWordIgnoreCase("ABc def", "AB") == false //not a full word match
     *       </pre>
     * @param sentence cannot be null
     * @param word cannot be null, cannot be empty, must be a single word
     */
    public static boolean containsWordIgnoreCase(String sentence, String word) {
        requireNonNull(sentence);
        requireNonNull(word);

        String preppedWord = word.trim();
        checkArgument(!preppedWord.isEmpty(), "Word parameter cannot be empty");
        checkArgument(preppedWord.split("\\s+").length == 1, "Word parameter should be a single word");

        String preppedSentence = sentence;
        String[] wordsInPreppedSentence = preppedSentence.split("\\s+");

        return Arrays.stream(wordsInPreppedSentence)
                .anyMatch(preppedWord::equalsIgnoreCase);
    }

    /**
     * Returns a detailed message of {@code t}, including the stack trace.
     */
    public static String getDetails(Throwable t) {
        requireNonNull(t);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return t.getMessage() + "\n" + sw.toString();
    }

    /**
     * Returns true if {@code s} represents a non-zero unsigned integer
     * e.g. 1, 2, 3, ..., {@code Integer.MAX_VALUE} <br>
     * Will return false for any other non-null string input
     * e.g. empty string, "-1", "0", "+1", and " 2 " (untrimmed), "3 0" (contains whitespace), "1 a" (contains letters)
     * @throws NullPointerException if {@code s} is null.
     */
    public static boolean isNonZeroUnsignedInteger(String s) {
        requireNonNull(s);

        try {
            int value = Integer.parseInt(s);
            return value > 0 && !s.startsWith("+"); // "+1" is successfully parsed by Integer#parseInt(String)
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
