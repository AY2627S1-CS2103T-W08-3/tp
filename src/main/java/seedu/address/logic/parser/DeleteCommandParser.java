package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * A UUID prefix, once its dashes are removed, is between the short UUID shown on a person's card
     * and a full UUID in length.
     */
    private static final String UUID_PREFIX_REGEX =
            "\\p{XDigit}{" + Person.SHORT_UUID_LENGTH + "," + Person.UUID_HEX_LENGTH + "}";

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        String uuidPrefix = normalise(args);

        if (!isValidUuidPrefix(uuidPrefix)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }

        return new DeleteCommand(uuidPrefix);
    }

    /**
     * Removes surrounding whitespace and dashes, and converts to lowercase, so that a full UUID
     * copied from the data file and a short UUID typed from a card are treated alike.
     */
    private static String normalise(String args) {
        return args.trim().replace("-", "").toLowerCase();
    }

    private static boolean isValidUuidPrefix(String uuidPrefix) {
        return uuidPrefix.matches(UUID_PREFIX_REGEX);
    }

}
