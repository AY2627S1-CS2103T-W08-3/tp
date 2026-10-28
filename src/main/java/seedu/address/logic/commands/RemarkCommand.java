package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;

/**
 * Changes the remark of an existing person in the address book.
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";
    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Added remark to Person: %1$s";
    public static final String MESSAGE_DELETE_REMARK_SUCCESS = "Removed remark from Person: %1$s";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the remark of the person identified "
            + "by the short UUID shown on the person's card. "
            + "Existing remark will be overwritten by the input.\n"
            + "Parameters: UUID_PREFIX (7 to 32 hexadecimal characters; dashes are ignored; "
            + "without the # prefix) "
            + "r/ [REMARK]\n"
            + "Example: " + COMMAND_WORD + " 1a2b3c4 "
            + "r/ Likes to swim.";

    public static final String MESSAGE_NO_PERSON_WITH_UUID = "No person with UUID: %s";
    public static final String MESSAGE_AMBIGUOUS_UUID =
            "Multiple persons match UUID: %s. Enter more characters of the UUID to pick one:\n%s";

    private static final String UUID_PREFIX_REGEX =
            "[0-9a-fA-F]{" + Person.SHORT_UUID_LENGTH + "," + Person.UUID_HEX_LENGTH + "}";
    private static final Logger logger = LogsCenter.getLogger(RemarkCommand.class);

    private final String targetUuid;
    private final Remark remark;

    /**
     * @param targetUuid short UUID of the person to edit the remark
     * @param remark of the person to be updated to
     * @throws IllegalArgumentException if {@code targetUuid} is not a normalized UUID prefix
     */
    public RemarkCommand(String targetUuid, Remark remark) {
        requireAllNonNull(targetUuid, remark);
        if (!targetUuid.matches(UUID_PREFIX_REGEX)) {
            throw new IllegalArgumentException(
                    "Target UUID must contain 7 to 32 hexadecimal characters without the # prefix.");
        }

        this.targetUuid = targetUuid;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        logger.info("Executing remark command for UUID: " + targetUuid);

        List<Person> matches = requireNonNull(model.getPersonsByUuidPrefix(targetUuid));
        if (matches.isEmpty()) {
            logger.warning("No person found for remark command UUID: " + targetUuid);
            throw new CommandException(String.format(MESSAGE_NO_PERSON_WITH_UUID, targetUuid));
        }
        if (matches.size() > 1) {
            logger.warning("Ambiguous UUID for remark command: " + targetUuid);
            throw new CommandException(String.format(MESSAGE_AMBIGUOUS_UUID, targetUuid,
                    formatUuids(matches)));
        }

        assert matches.size() == 1 : "A valid UUID must resolve to exactly one person";
        Person personToEdit = matches.get(0);
        assert personToEdit != null : "The matching person must not be null";
        Person editedPerson = new Person(
                personToEdit.getUuid(), personToEdit.getName(), personToEdit.getPhone(),
                personToEdit.getEmail(), personToEdit.getAddress(), remark,
                personToEdit.getTags());

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        logger.info("Updated remark for person with UUID: " + targetUuid);
        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Generates a command execution success message based on whether
     * the remark is added to or removed from
     * {@code personToEdit}.
     */
    private String generateSuccessMessage(Person personToEdit) {
        assert personToEdit != null : "The person used to generate a success message must not be null";

        String message = !remark.value.isEmpty() ? MESSAGE_ADD_REMARK_SUCCESS : MESSAGE_DELETE_REMARK_SUCCESS;
        return String.format(message, Messages.format(personToEdit));
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
        if (!(other instanceof RemarkCommand)) {
            return false;
        }

        RemarkCommand e = (RemarkCommand) other;
        return targetUuid.equals(e.targetUuid)
                && remark.equals(e.remark);
    }
}
