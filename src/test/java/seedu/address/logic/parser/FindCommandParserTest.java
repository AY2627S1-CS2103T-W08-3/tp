package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.NameOrTagContainsKeywordsPredicate;
import seedu.address.model.tag.Tag;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_nullArgs_throwsAssertionError() {
        assertThrows(AssertionError.class, "Find command arguments must not be null", () -> parser.parse(null));
    }

    @Test
    public void parse_emptyArg_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FindCommand.MESSAGE_MISSING_SEARCH_TERMS + "\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "     ", expectedMessage);
        assertParseFailure(parser, " \t\n ", expectedMessage);
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);

        FindCommand quotedWordCommand = new FindCommand(
                new NameContainsKeywordsPredicate(List.of("\"VIP")));
        assertParseSuccess(parser, "\"VIP", quotedWordCommand);
    }

    @Test
    public void parse_tags_returnsFindCommand() {
        FindCommand expectedCommand = new FindCommand(
                new NameOrTagContainsKeywordsPredicate(List.of(), List.of("friends", "colleagues")));
        assertParseSuccess(parser, "t/friends t/colleagues", expectedCommand);
        assertParseSuccess(parser, " \t t/friends\n\t t/colleagues ", expectedCommand);
    }

    @Test
    public void parse_namesAndTags_returnsFindCommand() {
        FindCommand expectedCommand = new FindCommand(
                new NameOrTagContainsKeywordsPredicate(List.of("Alice", "Richards"), List.of("friends")));
        assertParseSuccess(parser, "Alice Richards t/friends", expectedCommand);
        assertParseSuccess(parser, " Alice\t Richards\n t/friends ", expectedCommand);
    }

    @Test
    public void parse_emptyTag_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FindCommand.MESSAGE_MISSING_TAG + "\n" + FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "t/", expectedMessage);
        assertParseFailure(parser, "t/ \t\n ", expectedMessage);
        assertParseFailure(parser, "Alice t/", expectedMessage);
        assertParseFailure(parser, "t/friends t/", expectedMessage);
        assertParseFailure(parser, "t/ t/friends", expectedMessage);
    }

    @Test
    public void parse_nameContainingTagPrefix_returnsFindCommand() {
        assertParseSuccess(parser, "agent/0",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("agent/0"))));
        assertParseSuccess(parser, "/0",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("/0"))));
    }

    @Test
    public void parse_invalidTag_throwsParseException() {
        assertParseFailure(parser, "t/friends!", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "t/friends colleagues", Tag.MESSAGE_CONSTRAINTS);
    }

}
