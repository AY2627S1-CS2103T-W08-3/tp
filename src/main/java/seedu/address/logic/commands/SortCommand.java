package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Displays all persons lexicographically by name, ignoring capitalization.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_SUCCESS = "Sorted all persons by name in %1$s text order.";
    public static final String MESSAGE_USAGE = "Use one of these commands:\n"
            + COMMAND_WORD + " asc  - Sort names in ascending text order\n"
            + COMMAND_WORD + " desc - Sort names in descending text order";

    /**
     * The lexicographical direction of the displayed list.
     */
    public enum SortOrder {
        ASCENDING, DESCENDING
    }

    private static final Comparator<Person> NAME_COMPARATOR = Comparator.comparing(
            person -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER);
    private static final Logger logger = LogsCenter.getLogger(SortCommand.class);

    private final SortOrder sortOrder;

    /**
     * Creates a command with the specified lexicographical direction.
     *
     * @throws NullPointerException If {@code sortOrder} is null.
     */
    public SortCommand(SortOrder sortOrder) {
        this.sortOrder = requireNonNull(sortOrder, "Sort order must not be null");
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model, "Model must not be null");
        Comparator<Person> comparator = getComparator();
        int personCount = model.getAddressBook().getPersonList().size();

        logger.fine("Sorting " + personCount + " contacts in " + sortOrder + " order");
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.updatePersonListComparator(comparator);

        assert model.getAddressBook().getPersonList().size() == personCount : "Sorting must not change contact count";
        assert model.getFilteredPersonList().size() == personCount : "Sorting must display all contacts";
        assert isSorted(model.getFilteredPersonList(), comparator) : "Displayed contacts must follow the sort order";
        logger.fine("Successfully sorted contacts in " + sortOrder + " order");
        return new CommandResult(getSuccessMessage());
    }

    /**
     * Returns feedback identifying the direction applied to the displayed list.
     */
    private String getSuccessMessage() {
        String direction = switch (sortOrder) {
            case ASCENDING -> "ascending";
            case DESCENDING -> "descending";
            default -> throw new AssertionError("Unhandled sort order: " + sortOrder);
        };
        return String.format(MESSAGE_SUCCESS, direction);
    }

    /**
     * Returns the comparator for the command's required sort direction.
     */
    private Comparator<Person> getComparator() {
        return switch (sortOrder) {
            case ASCENDING -> NAME_COMPARATOR;
            case DESCENDING -> NAME_COMPARATOR.reversed();
            default -> throw new AssertionError("Unhandled sort order: " + sortOrder);
        };
    }

    /**
     * Returns whether adjacent persons follow the given order, without changing the list.
     */
    private static boolean isSorted(List<Person> persons, Comparator<Person> comparator) {
        for (int i = 1; i < persons.size(); i++) {
            if (comparator.compare(persons.get(i - 1), persons.get(i)) > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SortCommand otherCommand)) {
            return false;
        }
        return sortOrder == otherCommand.sortOrder;
    }
}
