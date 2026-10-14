package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using the short UUID shown on their card.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the short UUID shown on the person's card.\n"
            + "Parameters: SHORT_UUID (7 hexadecimal characters)\n"
            + "Example: " + COMMAND_WORD + " 1a2b3c4";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted person: %1$s";
    public static final String MESSAGE_NO_PERSON_WITH_UUID = "No person with UUID: %s";
    public static final String MESSAGE_AMBIGUOUS_UUID =
            "Multiple persons match UUID: %s. Please use a more specific identifier.";

    private final String targetUuid;

    /**
     * Creates a {@code DeleteCommand} that deletes the person with the given short UUID.
     */
    public DeleteCommand(String targetUuid) {
        requireNonNull(targetUuid);
        this.targetUuid = targetUuid;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> matches = model.getPersonsByShortUuid(targetUuid);
        if (matches.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NO_PERSON_WITH_UUID, targetUuid));
        }
        if (matches.size() > 1) {
            throw new CommandException(String.format(MESSAGE_AMBIGUOUS_UUID, targetUuid));
        }

        Person personToDelete = matches.get(0);
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
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

        return targetUuid.equals(otherDeleteCommand.targetUuid);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetUuid", targetUuid)
                .toString();
    }
}
