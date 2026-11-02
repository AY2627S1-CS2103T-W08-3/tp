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
    private static final String MESSAGE_MISSING_IDENTIFIER =
            "Missing person UUID. Enter the 7-character hexadecimal UUID shown on the contact card.";
    private static final String MESSAGE_INVALID_IDENTIFIER =
            "Invalid person UUID '%s': it must be 7 to 32 hexadecimal characters "
                    + "(0-9 or a-f), without the # prefix.";
    private static final String MESSAGE_MISSING_REMARK_PREFIX =
            "Missing remark prefix. Add r/ before the remark, or use r/ to remove the existing remark.";
    private static final String MESSAGE_REPEATED_REMARK_PREFIX =
            "Only one r/ field is allowed. Remove the extra field, or enclose the entire remark in double quotes "
                    + "if r/ is part of the text.";
    private static final String MESSAGE_UNCLOSED_REMARK_QUOTE =
            "Unclosed quoted remark. Add a closing double quote after the remark, or remove the opening quote.";
    private static final String MESSAGE_TRAILING_TEXT_AFTER_QUOTE =
            "Quoted remark has text after its closing quote. Put the entire remark inside the quotes and "
                    + "escape inner quotes as \\\".";

    /**
     * Parses the given {@code String} of arguments in the context of the
     * {@code RemarkCommand} and returns a {@code RemarkCommand} object.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public RemarkCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap;
        try {
            argMultimap = ArgumentTokenizer.tokenizeWithQuotedValues(args, PREFIX_REMARK);
        } catch (ParseException e) {
            if (ArgumentTokenizer.MESSAGE_UNMATCHED_QUOTE.equals(e.getMessage())) {
                throw new ParseException(MESSAGE_UNCLOSED_REMARK_QUOTE, e);
            }
            throw e;
        }

        String preamble = argMultimap.getPreamble();
        String targetUuid = normalise(preamble);
        if (targetUuid.isEmpty()) {
            throw new ParseException(withUsage(MESSAGE_MISSING_IDENTIFIER));
        }
        if (argMultimap.getValue(PREFIX_REMARK).isEmpty()
                && normalise(preamble.split("\\s+", 2)[0]).matches(UUID_PREFIX_REGEX)) {
            throw new ParseException(withUsage(MESSAGE_MISSING_REMARK_PREFIX));
        }
        if (!targetUuid.matches(UUID_PREFIX_REGEX)) {
            throw new ParseException(withUsage(String.format(MESSAGE_INVALID_IDENTIFIER,
                    argMultimap.getPreamble().trim())));
        }

        if (argMultimap.getAllValues(PREFIX_REMARK).size() > 1) {
            throw new ParseException(MESSAGE_REPEATED_REMARK_PREFIX);
        }
        validateEntireQuotedRemark(args);
        String remarkValue = argMultimap.getValue(PREFIX_REMARK)
                .orElseThrow(() -> new ParseException(withUsage(MESSAGE_MISSING_REMARK_PREFIX)));
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

    /**
     * Rejects non-whitespace text after the closing quote of a quoted remark value.
     *
     * @throws ParseException if the quoted value is not closed or text follows the closing quote.
     */
    private static void validateEntireQuotedRemark(String args) throws ParseException {
        int prefixEnd = args.indexOf(PREFIX_REMARK.toString()) + PREFIX_REMARK.toString().length();
        String rawRemark = args.substring(prefixEnd).trim();
        if (!rawRemark.startsWith("\"")) {
            return;
        }

        int precedingBackslashes = 0;
        for (int index = 1; index < rawRemark.length(); index++) {
            char character = rawRemark.charAt(index);
            if (character == '"' && precedingBackslashes % 2 == 0) {
                if (index != rawRemark.length() - 1) {
                    throw new ParseException(MESSAGE_TRAILING_TEXT_AFTER_QUOTE);
                }
                return;
            }
            precedingBackslashes = character == '\\' ? precedingBackslashes + 1 : 0;
        }
        throw new ParseException(MESSAGE_UNCLOSED_REMARK_QUOTE);
    }

    /**
     * Returns a specific parse error followed by the command usage instructions.
     */
    private static String withUsage(String specificMessage) {
        return String.format(MESSAGE_INVALID_COMMAND_FORMAT, specificMessage + "\n" + RemarkCommand.MESSAGE_USAGE);
    }

}
