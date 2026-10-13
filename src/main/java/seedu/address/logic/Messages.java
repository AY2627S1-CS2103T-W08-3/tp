package seedu.address.logic;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.NameSimilarityUtil;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d person(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_SIMILAR_NAME_WARNING_FORMAT =
            "\nWarning: \"%1$s\" is very similar to existing entry \"%2$s\".";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Remark: ")
                .append(person.getRemark())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Returns a warning when a person's name is very similar to an existing name.
     *
     * @param person person being added or edited
     * @param existingPersons existing entries to compare against
     * @param excludedPerson entry to exclude, such as the entry currently being edited
     * @return formatted warning, or an empty string when no warning is needed
     */
    public static String getSimilarNameWarning(Person person, Collection<Person> existingPersons,
            Person excludedPerson) {
        return NameSimilarityUtil.findMostSimilarName(person.getName().fullName, existingPersons, excludedPerson)
                .map(similarName -> String.format(MESSAGE_SIMILAR_NAME_WARNING_FORMAT,
                        person.getName(), similarName))
                .orElse("");
    }

}
