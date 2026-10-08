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

    private static final Comparator<Person> NAME_COMPARATOR = Comparator.comparing(
            person -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER);

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.updatePersonListComparator(NAME_COMPARATOR);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
