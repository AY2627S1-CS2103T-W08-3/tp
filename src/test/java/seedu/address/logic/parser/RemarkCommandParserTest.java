package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {
    private RemarkCommandParser parser = new RemarkCommandParser();
    private final String nonEmptyRemark = "A remark.";

    @Test
    public void parse_indexSpecified_success() throws ParseException {
        // have remark
        Index targetIndex = INDEX_FIRST_PERSON;

        String userInput = targetIndex.getOneBased() + " " + PREFIX_REMARK + nonEmptyRemark;
        Remark remark = ParserUtil.parseRemark(nonEmptyRemark);
        RemarkCommand expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, remark);
        assertParseSuccess(parser, userInput, expectedCommand);

        userInput = targetIndex.getOneBased() + " " + PREFIX_REMARK + "  " + nonEmptyRemark + "  ";
        assertParseSuccess(parser, userInput, expectedCommand);

        // no remark
        userInput = targetIndex.getOneBased() + " " + PREFIX_REMARK;
        Remark remarkEmpty = ParserUtil.parseRemark("");
        expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, remarkEmpty);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

        // no parameters
        assertParseFailure(parser, RemarkCommand.COMMAND_WORD, expectedMessage);

        // no index
        assertParseFailure(parser, RemarkCommand.COMMAND_WORD + " " + nonEmptyRemark, expectedMessage);

        assertParseFailure(parser, "0 " + PREFIX_REMARK + nonEmptyRemark, expectedMessage);
        assertParseFailure(parser, "-1 " + PREFIX_REMARK + nonEmptyRemark, expectedMessage);
    }

    @Test
    public void parse_duplicateRemarkPrefix_failure() {
        String input = "1 " + PREFIX_REMARK + "First remark " + PREFIX_REMARK + "Second remark";

        assertParseFailure(parser, input,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
    }

}
