package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.SortOrder;

/**
 * Tests accepted sort directions and invalid arguments.
 */
public class SortCommandParserTest {

    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_validDirection_returnsSortCommand() {
        assertParseSuccess(parser, " A-Z ", new SortCommand(SortOrder.ASCENDING));
        assertParseSuccess(parser, " Z-A ", new SortCommand(SortOrder.DESCENDING));
    }

    @Test
    public void parse_missingOrInvalidDirection_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        for (String input : new String[] {"", "   ", "a-z", "ascending", "A-Z Z-A", "A-Z extra"}) {
            assertParseFailure(parser, input, message);
        }
    }
}
