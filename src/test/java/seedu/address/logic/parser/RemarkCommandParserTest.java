package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {
    private RemarkCommandParser parser = new RemarkCommandParser();
    private final String nonEmptyRemark = "A remark.";

    @Test
    public void parse_shortUuidSpecified_success() throws ParseException {
        // have remark
        String targetUuid = "aaaaaaa";

        String userInput = targetUuid + " " + PREFIX_REMARK + nonEmptyRemark;
        Remark remark = ParserUtil.parseRemark(nonEmptyRemark);
        RemarkCommand expectedCommand = new RemarkCommand(targetUuid, remark);
        assertParseSuccess(parser, userInput, expectedCommand);

        userInput = targetUuid + " " + PREFIX_REMARK + "  " + nonEmptyRemark + "  ";
        assertParseSuccess(parser, userInput, expectedCommand);

        // no remark
        userInput = targetUuid + " " + PREFIX_REMARK;
        Remark remarkEmpty = ParserUtil.parseRemark("");
        expectedCommand = new RemarkCommand(targetUuid, remarkEmpty);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_uppercaseShortUuid_normalisedToLowercase() {
        assertParseSuccess(parser, "AAAAAAA " + PREFIX_REMARK + nonEmptyRemark,
                new RemarkCommand("aaaaaaa", new Remark("A remark.")));
    }

    @Test
    public void parse_longerUuidPrefixWithDashes_normalisedToLowercase() {
        assertParseSuccess(parser, "AAAAAAAa-0000 " + PREFIX_REMARK + nonEmptyRemark,
                new RemarkCommand("aaaaaaaa0000", new Remark("A remark.")));

        assertParseSuccess(parser, "AAAAAAAa-0000-0000-0000-000000000000 "
                        + PREFIX_REMARK + nonEmptyRemark,
                new RemarkCommand("aaaaaaaa000000000000000000000000", new Remark("A remark.")));
    }

    @Test
    public void parse_remarkContainingQuotes_preservesQuotes() {
        assertParseSuccess(parser, "aaaaaaa " + PREFIX_REMARK + "Wants a 27\" monitor",
                new RemarkCommand("aaaaaaa", new Remark("Wants a 27\" monitor")));
        assertParseSuccess(parser, "aaaaaaa " + PREFIX_REMARK + "\"VIP\"",
                new RemarkCommand("aaaaaaa", new Remark("\"VIP\"")));
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        String missingIdentifierMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                "Missing person UUID. Enter the 7-character hexadecimal UUID shown on the contact card.\n"
                        + RemarkCommand.MESSAGE_USAGE);
        String missingRemarkPrefixMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                "Missing remark prefix. Add r/ before the remark, or use r/ to remove the existing remark.\n"
                        + RemarkCommand.MESSAGE_USAGE);

        // no parameters
        assertParseFailure(parser, "", missingIdentifierMessage);

        // no UUID
        assertParseFailure(parser, " " + PREFIX_REMARK + nonEmptyRemark, missingIdentifierMessage);

        // no remark prefix
        assertParseFailure(parser, "aaaaaaa", missingRemarkPrefixMessage);
        assertParseFailure(parser, "aaaaaaa " + nonEmptyRemark,
                invalidUuidMessage("aaaaaaa " + nonEmptyRemark));

        assertParseFailure(parser, "1 " + PREFIX_REMARK + nonEmptyRemark,
                invalidUuidMessage("1"));
        assertParseFailure(parser, "123456 " + PREFIX_REMARK + nonEmptyRemark,
                invalidUuidMessage("123456"));
        assertParseFailure(parser, "123456g " + PREFIX_REMARK + nonEmptyRemark,
                invalidUuidMessage("123456g"));
        assertParseFailure(parser, "#aaaaaaa " + PREFIX_REMARK + nonEmptyRemark,
                invalidUuidMessage("#aaaaaaa"));
        assertParseFailure(parser, "123456789012345678901234567890123 " + PREFIX_REMARK + nonEmptyRemark,
                invalidUuidMessage("123456789012345678901234567890123"));
    }

    private String invalidUuidMessage(String uuid) {
        return String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                String.format("Invalid person UUID '%s': it must be 7 to 32 hexadecimal characters "
                        + "(0-9 or a-f), without the # prefix.\n%s", uuid, RemarkCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateRemarkPrefix_failure() {
        String input = "aaaaaaa " + PREFIX_REMARK + "First remark " + PREFIX_REMARK + "Second remark";

        assertParseFailure(parser, input,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
    }

}
