package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;

import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;

/**
 * Parses input arguments for the {@code remark} command.
 */
public class RemarkCommandParser implements Parser<RemarkCommand> {
    /** A UUID prefix is between the short UUID and full UUID lengths, excluding dashes. */
    private static final String UUID_PREFIX_REGEX =
            "\\p{XDigit}{" + Person.SHORT_UUID_LENGTH + "," + Person.UUID_HEX_LENGTH + "}";

    /**
     * Parses the given {@code String} of arguments in the context of the
     * {@code RemarkCommand} and returns a {@code RemarkCommand} object.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public RemarkCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_REMARK);

        String targetUuid = normalise(argMultimap.getPreamble());
        if (!targetUuid.matches(UUID_PREFIX_REGEX)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RemarkCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_REMARK);
        String remarkValue = argMultimap.getValue(PREFIX_REMARK)
                .orElseThrow(() -> new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        RemarkCommand.MESSAGE_USAGE)));
        Remark remark = ParserUtil.parseRemark(remarkValue);

        return new RemarkCommand(targetUuid, remark);
    }

    /**
     * Removes surrounding whitespace and dashes, and converts to lowercase, so that a full UUID
     * copied from the data file and a short UUID typed from a card are treated alike.
     */
    private static String normalise(String targetUuid) {
        return targetUuid.trim().replace("-", "").toLowerCase();
    }

}
