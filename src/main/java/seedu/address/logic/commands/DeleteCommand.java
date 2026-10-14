package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using either a displayed index or a short UUID.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number used in the displayed person list, "
            + "or by the short UUID shown on the person's card.\n"
            + "Parameters: INDEX (must be a positive integer) or SHORT_UUID (7 hexadecimal characters)\n"
            + "Example: " + COMMAND_WORD + " 1\n"
            + "Example: " + COMMAND_WORD + " 1a2b3c4";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted person: %1$s";
    public static final String MESSAGE_NO_PERSON_WITH_UUID = "No person with UUID: %s";
    public static final String MESSAGE_AMBIGUOUS_UUID =
            "Multiple persons match UUID: %s. Please use a more specific identifier.";

    private final Index targetIndex;
    private final String targetUuid;

    /**
     * Creates a {@code DeleteCommand} that deletes the person at the given displayed index.
     */
    public DeleteCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
        this.targetUuid = null;
    }

    /**
     * Creates a {@code DeleteCommand} that deletes the person with the given short UUID.
     */
    public DeleteCommand(String targetUuid) {
        requireNonNull(targetUuid);
        this.targetIndex = null;
        this.targetUuid = targetUuid;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToDelete = (targetUuid != null)
                ? resolveByUuid(model)
                : resolveByIndex(model);
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    private Person resolveByIndex(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return lastShownList.get(targetIndex.getZeroBased());
    }

    private Person resolveByUuid(Model model) throws CommandException {
        List<Person> matches = model.getPersonsByShortUuid(targetUuid);
        if (matches.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NO_PERSON_WITH_UUID, targetUuid));
        }
        if (matches.size() > 1) {
            throw new CommandException(String.format(MESSAGE_AMBIGUOUS_UUID, targetUuid));
        }
        return matches.get(0);
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

        return Objects.equals(targetIndex, otherDeleteCommand.targetIndex)
                && Objects.equals(targetUuid, otherDeleteCommand.targetUuid);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        if (targetUuid != null) {
            builder.add("targetUuid", targetUuid);
        } else {
            builder.add("targetIndex", targetIndex);
        }
        return builder.toString();
    }
}
