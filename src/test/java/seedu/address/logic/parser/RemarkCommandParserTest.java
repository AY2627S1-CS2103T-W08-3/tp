package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {
    private static final String NON_EMPTY_REMARK = "A remark.";

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_shortUuidSpecified_success() throws ParseException {
        // have remark
        String targetUuid = "aaaaaaa";

        String userInput = targetUuid + " " + PREFIX_REMARK + NON_EMPTY_REMARK;
        Remark remark = ParserUtil.parseRemark(NON_EMPTY_REMARK);
        RemarkCommand expectedCommand = new RemarkCommand(targetUuid, remark);
        assertParseSuccess(parser, userInput, expectedCommand);

        userInput = targetUuid + " " + PREFIX_REMARK + "  " + NON_EMPTY_REMARK + "  ";
        assertParseSuccess(parser, userInput, expectedCommand);

        // no remark
        userInput = targetUuid + " " + PREFIX_REMARK;
        Remark remarkEmpty = ParserUtil.parseRemark("");
        expectedCommand = new RemarkCommand(targetUuid, remarkEmpty);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_uppercaseShortUuid_normalisedToLowercase() {
        assertParseSuccess(parser, "AAAAAAA " + PREFIX_REMARK + NON_EMPTY_REMARK,
                new RemarkCommand("aaaaaaa", new Remark("A remark.")));
    }

    @Test
    public void parse_longerUuidPrefixWithDashes_normalisedToLowercase() {
        assertParseSuccess(parser, "AAAAAAAa-0000 " + PREFIX_REMARK + NON_EMPTY_REMARK,
                new RemarkCommand("aaaaaaaa0000", new Remark("A remark.")));

        assertParseSuccess(parser, "AAAAAAAa-0000-0000-0000-000000000000 "
                        + PREFIX_REMARK + NON_EMPTY_REMARK,
                new RemarkCommand("aaaaaaaa000000000000000000000000", new Remark("A remark.")));
    }

    @Test
    public void parse_unquotedRemarkContainingQuote_preservesQuote() {
        // EP: A quote inside an unquoted remark is ordinary text.
        assertParseSuccess(parser, "aaaaaaa " + PREFIX_REMARK + "Wants a 27\" monitor",
                new RemarkCommand("aaaaaaa", new Remark("Wants a 27\" monitor")));
    }

    @Test
    public void parse_quotedRemarkWithoutEmbeddedPrefix_removesDelimiters() {
        // EP: An entire quoted value is saved without its outer quotes.
        assertParseSuccess(parser, "aaaaaaa r/\"VIP\"",
                new RemarkCommand("aaaaaaa", new Remark("VIP")));
        assertParseSuccess(parser, "aaaaaaa r/\"\"",
                new RemarkCommand("aaaaaaa", new Remark("")));
    }

    @Test
    public void parse_quotedRemarkWithSurroundingSpaces_trimsSpaces() {
        // EP: Leading and trailing whitespace is trimmed even within a quoted remark.
        assertParseSuccess(parser, "aaaaaaa r/\"  Discuss r/ clause  \"",
                new RemarkCommand("aaaaaaa", new Remark("Discuss r/ clause")));
        assertParseSuccess(parser, "aaaaaaa r/  \"Discuss r/ clause\"  ",
                new RemarkCommand("aaaaaaa", new Remark("Discuss r/ clause")));
    }

    @Test
    public void parse_quotedRemarkWithEmbeddedPrefix_success() {
        // EP: Whitespace-preceded r/ inside an entire quoted value is literal text.
        String remark = "Discuss r/ clause r/ renewal";
        assertParseSuccess(parser, "aaaaaaa r/\"" + remark + "\"",
                new RemarkCommand("aaaaaaa", new Remark(remark)));
    }

    @Test
    public void parse_quotedRemarkWithEscapedQuotes_success() {
        // EP: Escaped quotes inside a quoted value are saved as literal quotes.
        assertParseSuccess(parser, "aaaaaaa r/\"Client said \\\"call me\\\" about r/ renewal\"",
                new RemarkCommand("aaaaaaa", new Remark("Client said \"call me\" about r/ renewal")));
        assertParseSuccess(parser, "aaaaaaa r/\"\\\"VIP\\\"\"",
                new RemarkCommand("aaaaaaa", new Remark("\"VIP\"")));
    }

    @Test
    public void parse_quotedRemarkWithEscapedBackslashBeforeClosingQuote_success() {
        // EP: An even number of backslashes leaves the final quote as a closing delimiter.
        assertParseSuccess(parser, "aaaaaaa r/\"Discuss r/ clause\\\\\"",
                new RemarkCommand("aaaaaaa", new Remark("Discuss r/ clause\\")));
    }

    @Test
    public void parse_unmatchedQuote_failure() {
        // EP: A quoted value must have a closing quote.
        assertParseFailure(parser, "aaaaaaa r/\"Discuss r/ clause",
                "Unclosed quoted remark. Add a closing double quote after the remark, "
                        + "or remove the opening quote.");
    }

    @Test
    public void parse_textAfterClosingQuote_failure() {
        // EP: An unescaped inner quote closes the value, so following text is rejected.
        String expectedMessage = "Quoted remark has text after its closing quote. Put the entire remark inside "
                + "the quotes and escape inner quotes as \\\".";
        assertParseFailure(parser, "b6549c1 r/\"Client said \"call me\\\" about renewal\"", expectedMessage);
        assertParseFailure(parser, "aaaaaaa r/\"Discuss r/ clause\" extra", expectedMessage);
        assertParseFailure(parser, "aaaaaaa r/\"Discuss r/ clause\"\"", expectedMessage);
    }

    @Test
    public void parse_remarkContainingEmbeddedPrefix_success() {
        // EP: r/ without preceding whitespace is part of an unquoted remark value.
        String remark = "To find out whether Bob is a saver/spender";
        assertParseSuccess(parser, "aaaaaaa r/" + remark,
                new RemarkCommand("aaaaaaa", new Remark(remark)));
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
        assertParseFailure(parser, " " + PREFIX_REMARK + NON_EMPTY_REMARK, missingIdentifierMessage);

        // no remark prefix
        assertParseFailure(parser, "aaaaaaa", missingRemarkPrefixMessage);
        assertParseFailure(parser, "aaaaaaa " + NON_EMPTY_REMARK,
                missingRemarkPrefixMessage);

        assertParseFailure(parser, "1 " + PREFIX_REMARK + NON_EMPTY_REMARK,
                invalidUuidMessage("1"));
        assertParseFailure(parser, "123456 " + PREFIX_REMARK + NON_EMPTY_REMARK,
                invalidUuidMessage("123456"));
        assertParseFailure(parser, "123456g " + PREFIX_REMARK + NON_EMPTY_REMARK,
                invalidUuidMessage("123456g"));
        assertParseFailure(parser, "#aaaaaaa " + PREFIX_REMARK + NON_EMPTY_REMARK,
                invalidUuidMessage("#aaaaaaa"));
        assertParseFailure(parser, "123456789012345678901234567890123 " + PREFIX_REMARK + NON_EMPTY_REMARK,
                invalidUuidMessage("123456789012345678901234567890123"));
    }

    private String invalidUuidMessage(String uuid) {
        return String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                String.format("Invalid person UUID '%s': it must be 7 to 32 hexadecimal characters "
                        + "(0-9 or a-f), without the # prefix.\n%s", uuid, RemarkCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateRemarkPrefix_failure() {
        // EP: A second r/ outside a quoted value is a duplicate field.
        String expectedMessage = "Only one r/ field is allowed. Remove the extra field, or enclose the entire "
                + "remark in double quotes if r/ is part of the text.";
        assertParseFailure(parser, "aaaaaaa r/First remark r/Second remark", expectedMessage);
        assertParseFailure(parser, "aaaaaaa r/Discuss r/ clause", expectedMessage);
        assertParseFailure(parser, "aaaaaaa r/\"Discuss r/ clause\" r/Second", expectedMessage);
    }

}
