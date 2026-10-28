package seedu.address.logic.parser;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tokenizes arguments string of the form: {@code preamble <prefix>value <prefix>value ...}<br>
 *     e.g. {@code some preamble text t/ 11.00 t/12.00 k/ m/ July}  where prefixes are {@code t/ k/ m/}.<br>
 * 1. An argument's value can be an empty string e.g. the value of {@code k/} in the above example.<br>
 * 2. Leading and trailing whitespaces of an argument value will be discarded.<br>
 * 3. An argument may be repeated and all its values will be accumulated e.g. the value of {@code t/}
 *    in the above example.<br>
 * 4. Double quotes protect field-prefix-looking text from being interpreted as a new prefix.
 */
public class ArgumentTokenizer {

    public static final String MESSAGE_UNMATCHED_QUOTE = "Unmatched double quote in command arguments.";

    /**
     * Tokenizes an arguments string and returns an {@code ArgumentMultimap} object that maps prefixes to their
     * respective argument values. Only the given prefixes will be recognized in the arguments string.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes   Prefixes to tokenize the arguments string with
     * @return           ArgumentMultimap object that maps prefixes to their arguments
     */
    public static ArgumentMultimap tokenize(String argsString, Prefix... prefixes) throws ParseException {
        List<PrefixPosition> positions = findAllPrefixPositions(argsString, prefixes);
        return extractArguments(argsString, positions);
    }

    /**
     * Finds all zero-based prefix positions in the given arguments string.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes   Prefixes to find in the arguments string
     * @return           List of zero-based prefix positions in the given arguments string
     */
    private static List<PrefixPosition> findAllPrefixPositions(String argsString, Prefix... prefixes)
            throws ParseException {
        List<PrefixPosition> positions = new ArrayList<>();
        for (Prefix prefix : prefixes) {
            positions.addAll(findPrefixPositions(argsString, prefix));
        }
        return positions;
    }

    /**
     * @see #findAllPrefixPositions(String, Prefix...)
     */
    private static List<PrefixPosition> findPrefixPositions(String argsString, Prefix prefix) throws ParseException {
        List<PrefixPosition> positions = new ArrayList<>();

        boolean isInsideQuotes = false;
        for (int index = 0; index < argsString.length(); index++) {
            if (argsString.charAt(index) == '"' && !isEscaped(argsString, index)) {
                isInsideQuotes = !isInsideQuotes;
            }
            if (!isInsideQuotes && index > 0 && Character.isWhitespace(argsString.charAt(index - 1))
                    && argsString.startsWith(prefix.getPrefix(), index)) {
                positions.add(new PrefixPosition(prefix, index));
            }
        }
        if (isInsideQuotes) {
            throw new ParseException(MESSAGE_UNMATCHED_QUOTE);
        }

        return positions;
    }

    /**
     * Extracts prefixes and their argument values, and returns an {@code ArgumentMultimap} object that maps the
     * extracted prefixes to their respective arguments. Prefixes are extracted based on their zero-based positions in
     * {@code argsString}.
     *
     * @param argsString      Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixPositions Zero-based positions of all prefixes in {@code argsString}
     * @return                ArgumentMultimap object that maps prefixes to their arguments
     */
    private static ArgumentMultimap extractArguments(String argsString, List<PrefixPosition> prefixPositions)
            throws ParseException {

        // Sort by start position
        prefixPositions.sort((prefix1, prefix2) -> prefix1.getStartPosition() - prefix2.getStartPosition());

        // Insert a PrefixPosition to represent the preamble
        PrefixPosition preambleMarker = new PrefixPosition(new Prefix(""), 0);
        prefixPositions.addFirst(preambleMarker);

        // Add a dummy PrefixPosition to represent the end of the string
        PrefixPosition endPositionMarker = new PrefixPosition(new Prefix(""), argsString.length());
        prefixPositions.add(endPositionMarker);

        // Map prefixes to their argument values (if any)
        ArgumentMultimap argMultimap = new ArgumentMultimap();
        for (int i = 0; i < prefixPositions.size() - 1; i++) {
            // Extract and store prefixes and their arguments
            Prefix argPrefix = prefixPositions.get(i).getPrefix();
            String argValue = extractArgumentValue(argsString, prefixPositions.get(i), prefixPositions.get(i + 1));
            argMultimap.put(argPrefix, argValue);
        }

        return argMultimap;
    }

    /**
     * Returns the trimmed value of the argument in the arguments string specified by {@code currentPrefixPosition}.
     * The end position of the value is determined by {@code nextPrefixPosition}.
     */
    private static String extractArgumentValue(String argsString,
            PrefixPosition currentPrefixPosition, PrefixPosition nextPrefixPosition) throws ParseException {
        Prefix prefix = currentPrefixPosition.getPrefix();

        int valueStartPos = currentPrefixPosition.getStartPosition() + prefix.getPrefix().length();
        String value = argsString.substring(valueStartPos, nextPrefixPosition.getStartPosition());

        String trimmedValue = value.trim();
        if (trimmedValue.startsWith("\"") && trimmedValue.endsWith("\"")) {
            return unescapeQuotedValue(trimmedValue.substring(1, trimmedValue.length() - 1));
        }
        return trimmedValue;
    }

    /**
     * Returns whether a character is escaped by an odd number of preceding backslashes.
     */
    private static boolean isEscaped(String value, int index) {
        int backslashCount = 0;
        for (int currentIndex = index - 1; currentIndex >= 0 && value.charAt(currentIndex) == '\\'; currentIndex--) {
            backslashCount++;
        }
        return backslashCount % 2 == 1;
    }

    /**
     * Removes escape characters from a quoted field value.
     */
    private static String unescapeQuotedValue(String value) {
        StringBuilder unescapedValue = new StringBuilder();
        boolean isEscaped = false;
        for (char character : value.toCharArray()) {
            if (isEscaped) {
                appendUnescapedCharacter(unescapedValue, character);
                isEscaped = false;
                continue;
            }
            if (character == '\\') {
                isEscaped = true;
                continue;
            }
            unescapedValue.append(character);
        }
        if (isEscaped) {
            unescapedValue.append('\\');
        }
        return unescapedValue.toString();
    }

    /**
     * Appends a character after an escape marker, preserving unsupported escape sequences literally.
     */
    private static void appendUnescapedCharacter(StringBuilder unescapedValue, char character) {
        if (character == '"' || character == '\\') {
            unescapedValue.append(character);
            return;
        }
        unescapedValue.append('\\').append(character);
    }

    /**
     * Represents a prefix's position in an arguments string.
     */
    private static class PrefixPosition {
        private int startPosition;
        private final Prefix prefix;

        PrefixPosition(Prefix prefix, int startPosition) {
            this.prefix = prefix;
            this.startPosition = startPosition;
        }

        int getStartPosition() {
            return startPosition;
        }

        Prefix getPrefix() {
            return prefix;
        }
    }

}
