package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import seedu.address.logic.commands.DeleteCommand;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1a2b3c4" and "1a2b3c4 abc" take
 * the same path through the DeleteCommand, and therefore we test only one of them.
 */
public class DeleteCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);

    private final DeleteCommandParser parser = new DeleteCommandParser();

    @ParameterizedTest
    @CsvSource({
        "1a2b3c4, 1a2b3c4", // short UUID, as shown on a card
        "'  1a2b3c4  ', 1a2b3c4", // surrounding whitespace
        "1A2B3C4, 1a2b3c4", // uppercase
        "1a2b3c4d1, 1a2b3c4d1", // longer prefix
        "1a2b3c4d-1000, 1a2b3c4d1000", // longer prefix with a dash
        "1a2b3c4d-1000-0000-0000-000000000000, 1a2b3c4d100000000000000000000000" // full UUID
    })
    public void parse_validUuidPrefix_returnsDeleteCommand(String input, String expectedUuidPrefix) {
        assertParseSuccess(parser, input, new DeleteCommand(expectedUuidPrefix));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "", // missing
        "1", // index, no longer supported
        "123456", // one character too short
        "123456g", // non-hexadecimal character
        "1a2b3c4d100000000000000000000000a", // one character longer than a full UUID
        "1a2b3c4 abc", // extra argument
        "-------" // only dashes
    })
    public void parse_invalidUuidPrefix_throwsParseException(String input) {
        assertParseFailure(parser, input, MESSAGE_INVALID_FORMAT);
    }
}
