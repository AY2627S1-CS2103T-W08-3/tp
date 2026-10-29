package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified by the start of their UUID, such as the short UUID shown on their card.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person with the given short UUID, the code shown as #xxxxxxx on the person's card.\n"
            + "Parameters: SHORT_UUID (" + Person.SHORT_UUID_LENGTH
            + " hexadecimal characters, typed without the #)\n"
            + "If two persons share a short UUID, type more of the UUID, up to all " + Person.UUID_HEX_LENGTH
            + " characters. Dashes are ignored.\n"
            + "Example: " + COMMAND_WORD + " 1a2b3c4 (deletes the person whose card shows #1a2b3c4)";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted person: %1$s";
    public static final String MESSAGE_NO_PERSON_WITH_UUID = "No person with UUID: %s";
    public static final String MESSAGE_AMBIGUOUS_UUID =
            "Multiple persons match UUID: %s. Enter more characters of the UUID to pick one:\n%s";

    private static final Logger logger = LogsCenter.getLogger(DeleteCommand.class);

    private final String targetUuidPrefix;

    /**
     * Creates a {@code DeleteCommand} that deletes the person whose UUID starts with {@code targetUuidPrefix}.
     * {@code targetUuidPrefix} must already be lowercase and contain no dashes.
     */
    public DeleteCommand(String targetUuidPrefix) {
        requireNonNull(targetUuidPrefix);
        this.targetUuidPrefix = targetUuidPrefix;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person personToDelete = findUniqueMatch(model);
        model.deletePerson(personToDelete);

        logger.fine("Deleted person with UUID " + personToDelete.getUuid());
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    /**
     * Returns the only person whose UUID starts with the target prefix.
     *
     * @throws CommandException if no person, or more than one person, matches.
     */
    private Person findUniqueMatch(Model model) throws CommandException {
        List<Person> matches = model.getPersonsByUuidPrefix(targetUuidPrefix);

        if (matches.isEmpty()) {
            logger.info("No person matches UUID prefix " + targetUuidPrefix);
            throw new CommandException(String.format(MESSAGE_NO_PERSON_WITH_UUID, targetUuidPrefix));
        }
        if (matches.size() > 1) {
            logger.info(matches.size() + " persons match UUID prefix " + targetUuidPrefix);
            throw new CommandException(String.format(MESSAGE_AMBIGUOUS_UUID, targetUuidPrefix, formatUuids(matches)));
        }

        assert matches.size() == 1 : "Exactly one person should match at this point";
        return matches.get(0);
    }

    /**
     * Returns one line per person, showing their full UUID and name, so the user can see how much
     * of the UUID to type to tell them apart.
     */
    private static String formatUuids(List<Person> persons) {
        return persons.stream()
                .map(person -> person.getUuid() + " " + person.getName())
                .collect(Collectors.joining("\n"));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetUuidPrefix.equals(otherDeleteCommand.targetUuidPrefix);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetUuidPrefix", targetUuidPrefix)
                .toString();
    }
}
