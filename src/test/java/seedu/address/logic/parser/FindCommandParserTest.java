package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.NameOrTagContainsKeywordsPredicate;
import seedu.address.model.tag.Tag;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
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
    public void parse_invalidTag_throwsParseException() {
        assertParseFailure(parser, "t/", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "Alice t/", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "t/friends t/", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "t/friends!", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "t/friends colleagues", Tag.MESSAGE_CONSTRAINTS);
    }

}
