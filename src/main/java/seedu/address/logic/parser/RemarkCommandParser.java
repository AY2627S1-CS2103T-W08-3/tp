package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;

import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Remark;

/**
 * Parses input arguments for the {@code remark} command.
 */
public class RemarkCommandParser implements Parser<RemarkCommand> {
    /** A short UUID is the first 7 hexadecimal characters of a person's UUID. */
    private static final String SHORT_UUID_REGEX = "[0-9a-fA-F]{7}";

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

        String targetUuid = argMultimap.getPreamble().trim();
        if (!targetUuid.matches(SHORT_UUID_REGEX)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RemarkCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_REMARK);
        String remarkValue = argMultimap.getValue(PREFIX_REMARK)
                .orElseThrow(() -> new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        RemarkCommand.MESSAGE_USAGE)));
        Remark remark = ParserUtil.parseRemark(remarkValue);

        return new RemarkCommand(targetUuid.toLowerCase(), remark);
    }

}
