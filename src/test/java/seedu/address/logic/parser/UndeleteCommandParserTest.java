package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UndeleteCommand;

public class UndeleteCommandParserTest {

    private final UndeleteCommandParser parser = new UndeleteCommandParser();

    @Test
    public void parse_noArguments_returnsUndeleteCommand() {
        assertParseSuccess(parser, "", new UndeleteCommand());
        assertParseSuccess(parser, "   ", new UndeleteCommand());
    }

    @Test
    public void parse_extraArguments_throwsParseException() {
        assertParseFailure(parser, " 1a2b3c4",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, UndeleteCommand.MESSAGE_USAGE));
    }
}
