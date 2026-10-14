package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

/**
 * Provides name normalisation and typo similarity checks.
 */
public final class NameSimilarityUtil {
    public static final double SIMILARITY_THRESHOLD = 0.85;
    public static final int MINIMUM_NAME_LENGTH = 3;
    private static final int SHORT_NAME_MAX_LENGTH = 5;
    private static final double SHORT_NAME_THRESHOLD = 0.9;

    private NameSimilarityUtil() {
    }

    /**
     * Normalises a name for comparison while preserving meaningful punctuation.
     *
     * @param name name to normalise
     * @return trimmed, whitespace-collapsed, lower-case name
     */
    public static String normalizeName(String name) {
        return requireNonNull(name).trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Calculates the normalised Levenshtein similarity between two names.
     *
     * @param first first name
     * @param second second name
     * @return similarity from 0.0 to 1.0, or 0.0 when both names are empty
     */
    public static double calculateSimilarity(String first, String second) {
        String firstNormalized = normalizeName(first);
        String secondNormalized = normalizeName(second);
        int maximumLength = Math.max(firstNormalized.length(), secondNormalized.length());
        if (maximumLength == 0) {
            return 0.0;
        }
        return 1.0 - calculateEditDistance(firstNormalized, secondNormalized) / (double) maximumLength;
    }

    /**
     * Finds the most similar existing name that is sufficiently close to the input.
     * Names with five or fewer characters require a similarity of at least 0.9;
     * longer names require a similarity of at least 0.85. Names that are identical
     * after normalisation are ignored unless their original forms differ in spacing.
     *
     * @param inputName name being entered
     * @param existingPersons existing entries to compare against
     * @param excludedPerson entry to exclude, such as the entry currently being edited
     * @return the original existing name when a likely typo is found
     */
    public static Optional<String> findMostSimilarName(String inputName,
            Collection<Person> existingPersons, Person excludedPerson) {
        requireNonNull(inputName);
        requireNonNull(existingPersons);
        String normalizedInput = normalizeName(inputName);
        if (normalizedInput.length() < MINIMUM_NAME_LENGTH) {
            return Optional.empty();
        }

        String mostSimilarName = null;
        double highestSimilarity = -1.0;
        for (Person existingPerson : existingPersons) {
            requireNonNull(existingPerson);
            if (isExcluded(existingPerson, excludedPerson)) {
                continue;
            }
            String existingName = existingPerson.getName().fullName;
            String normalizedExistingName = normalizeName(existingName);
            if (normalizedExistingName.length() < MINIMUM_NAME_LENGTH) {
                continue;
            }
            boolean exactNormalizedMatch = normalizedInput.equals(normalizedExistingName);
            if (exactNormalizedMatch && inputName.equalsIgnoreCase(existingName)) {
                continue;
            }

            int maximumLength = Math.max(normalizedInput.length(), normalizedExistingName.length());
            double similarity = calculateSimilarity(normalizedInput, normalizedExistingName);
            if (similarity >= getThreshold(maximumLength) && similarity > highestSimilarity) {
                highestSimilarity = similarity;
                mostSimilarName = existingName;
            }
        }
        return Optional.ofNullable(mostSimilarName);
    }

    private static boolean isExcluded(Person existingPerson, Person excludedPerson) {
        return excludedPerson != null && existingPerson.getUuid().equals(excludedPerson.getUuid());
    }

    private static double getThreshold(int maximumLength) {
        return maximumLength <= SHORT_NAME_MAX_LENGTH ? SHORT_NAME_THRESHOLD : SIMILARITY_THRESHOLD;
    }

    private static int calculateEditDistance(String first, String second) {
        int[] previousRow = new int[second.length() + 1];
        for (int index = 0; index <= second.length(); index++) {
            previousRow[index] = index;
        }

        for (int firstIndex = 1; firstIndex <= first.length(); firstIndex++) {
            int[] currentRow = new int[second.length() + 1];
            currentRow[0] = firstIndex;
            for (int secondIndex = 1; secondIndex <= second.length(); secondIndex++) {
                int insertionCost = currentRow[secondIndex - 1] + 1;
                int deletionCost = previousRow[secondIndex] + 1;
                int substitutionCost = previousRow[secondIndex - 1]
                        + (first.charAt(firstIndex - 1) == second.charAt(secondIndex - 1) ? 0 : 1);
                currentRow[secondIndex] = Math.min(insertionCost, Math.min(deletionCost, substitutionCost));
            }
            previousRow = currentRow;
        }
        return previousRow[second.length()];
    }
}
