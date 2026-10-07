package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;

/**
 * Tests remark argument parsing and command routing.
 */
public class RemarkCommandParserTest {
    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_remarkWithSpaces_returnsRemarkCommand() {
        assertParseSuccess(parser, "1 r/Likes to swim",
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Likes to swim")));
    }

    @Test
    public void parse_emptyOrMissingRemark_returnsEmptyRemark() {
        RemarkCommand expected = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(""));
        assertParseSuccess(parser, "1 r/", expected);
        assertParseSuccess(parser, "1", expected);
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "0 r/note", message);
        assertParseFailure(parser, "r/note", message);
        assertParseFailure(parser, "abc r/note", message);
    }

    @Test
    public void parseCommand_remark_routesToRemarkParser() throws Exception {
        assertEquals(new RemarkCommand(INDEX_FIRST_PERSON, new Remark("note")),
                new AddressBookParser().parseCommand("remark 1 r/note"));
    }
}
