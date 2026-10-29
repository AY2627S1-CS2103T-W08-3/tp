package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.NameOrTagContainsKeywordsPredicate;
import seedu.address.model.person.Person;

/**
 * Finds and lists all persons whose name or tags match any of the supplied search terms.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_TAG_SEARCH_HINT =
            "\nNo results matched the tag search. t/ searches tags, not names. "
            + "To find a name containing t/, use a substring that does not start with t/ "
            + "(e.g. find /1 for the name agent/1).";
    public static final String MESSAGE_MISSING_SEARCH_TERMS =
            "Specify at least part of a name or a tag.";
    public static final String MESSAGE_MISSING_TAG =
            "Specify a tag description after t/. If the name contains t/, "
            + "use a substring that does not start with t/ instead (e.g. /0 for agent/0).";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons matching any name keyword or tag "
            + "(case-insensitive, partial names and complete tags, OR search) "
            + "and displays them as a list with index numbers.\n"
            + "Parameters: [KEYWORD [MORE_KEYWORDS]...] [t/TAG]... (at least one keyword or tag required)\n"
            + "Example: " + COMMAND_WORD + " Alice t/friends t/colleagues\n"
            + "Matches names containing Alice or persons tagged friends or colleagues.";

    private final Predicate<Person> predicate;

    /**
     * Creates a find command with the given non-null search {@code predicate}.
     */
    public FindCommand(Predicate<Person> predicate) {
        assert predicate != null : "Find command requires a search predicate";
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        int resultCount = model.getFilteredPersonList().size();
        String feedback = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, resultCount);
        if (resultCount == 0 && predicate instanceof NameOrTagContainsKeywordsPredicate) {
            feedback += MESSAGE_TAG_SEARCH_HINT;
        }
        return new CommandResult(feedback);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
