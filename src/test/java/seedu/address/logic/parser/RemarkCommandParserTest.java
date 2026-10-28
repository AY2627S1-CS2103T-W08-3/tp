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
    public void parse_missingCompulsoryField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

        // no parameters
        assertParseFailure(parser, RemarkCommand.COMMAND_WORD, expectedMessage);

        // no UUID
        assertParseFailure(parser, RemarkCommand.COMMAND_WORD + " " + nonEmptyRemark, expectedMessage);

        // no remark prefix
        assertParseFailure(parser, "aaaaaaa", expectedMessage);
        assertParseFailure(parser, "aaaaaaa " + nonEmptyRemark, expectedMessage);

        assertParseFailure(parser, "1 " + PREFIX_REMARK + nonEmptyRemark, expectedMessage);
        assertParseFailure(parser, "123456g " + PREFIX_REMARK + nonEmptyRemark, expectedMessage);
    }

    @Test
    public void parse_duplicateRemarkPrefix_failure() {
        String input = "aaaaaaa " + PREFIX_REMARK + "First remark " + PREFIX_REMARK + "Second remark";

        assertParseFailure(parser, input,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
    }

}
