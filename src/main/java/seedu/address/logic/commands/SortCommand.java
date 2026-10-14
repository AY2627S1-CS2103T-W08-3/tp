package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Comparator;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Displays all persons alphabetically by name, ignoring capitalization.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_SUCCESS = "Listed all persons alphabetically by name.";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all persons alphabetically by name, ignoring capitalization.\n"
            + "Parameters: A-Z (ascending) or Z-A (descending)\n"
            + "Example: " + COMMAND_WORD + " A-Z";

    /**
     * The alphabetical direction of the displayed list.
     */
    public enum SortOrder {
        ASCENDING, DESCENDING
    }

    private static final Comparator<Person> NAME_COMPARATOR = Comparator.comparing(
            person -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER);

    private final SortOrder sortOrder;

    /**
     * Creates a command with the specified alphabetical direction.
     */
    public SortCommand(SortOrder sortOrder) {
        this.sortOrder = requireNonNull(sortOrder);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.updatePersonListComparator(sortOrder == SortOrder.ASCENDING
                ? NAME_COMPARATOR : NAME_COMPARATOR.reversed());
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SortCommand otherCommand && sortOrder == otherCommand.sortOrder;
    }
}
