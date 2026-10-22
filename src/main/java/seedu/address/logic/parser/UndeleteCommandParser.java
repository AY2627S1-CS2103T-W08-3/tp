package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.UndeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new UndeleteCommand object
 */
public class UndeleteCommandParser implements Parser<UndeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the UndeleteCommand
     * and returns an UndeleteCommand object for execution.
     * @throws ParseException if any arguments are given, as undelete takes none
     */
    public UndeleteCommand parse(String args) throws ParseException {
        if (!args.isBlank()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, UndeleteCommand.MESSAGE_USAGE));
        }

        return new UndeleteCommand();
    }

}
