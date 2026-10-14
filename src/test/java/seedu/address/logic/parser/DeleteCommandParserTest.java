package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1a2b3c4" and "1a2b3c4 abc" take
 * the same path through the DeleteCommand, and therefore we test only one of them.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validShortUuid_returnsDeleteCommand() {
        assertParseSuccess(parser, "1a2b3c4", new DeleteCommand("1a2b3c4"));
    }

    @Test
    public void parse_uppercaseShortUuid_normalisedToLowercase() {
        assertParseSuccess(parser, "1A2B3C4", new DeleteCommand("1a2b3c4"));
    }

    @Test
    public void parse_index_throwsParseException() {
        assertParseFailure(parser, "1", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_nonHexSevenChars_throwsParseException() {
        assertParseFailure(parser, "123456g", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteCommand.MESSAGE_USAGE));
    }
}
