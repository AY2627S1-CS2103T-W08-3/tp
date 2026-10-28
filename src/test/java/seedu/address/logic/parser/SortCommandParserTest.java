package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.SortOrder;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests accepted sort directions and invalid arguments.
 */
public class SortCommandParserTest {

    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        // EP: null argument reference (invalid API input).
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_invalidDirection_preservesExistingDisplay() {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Zoe").build());
        model.addPerson(new PersonBuilder().withName("Alice").build());
        new SortCommand(SortOrder.DESCENDING).execute(model);
        model.updateFilteredPersonList(person -> person.getName().fullName.equals("Zoe"));
        Model expectedModel = new ModelManager(model.getAddressBook(), model.getUserPrefs());
        new SortCommand(SortOrder.DESCENDING).execute(expectedModel);
        expectedModel.updateFilteredPersonList(person -> person.getName().fullName.equals("Zoe"));

        // EPs: missing direction, extra arguments, and unsupported direction preserve existing state.
        for (String input : new String[] {"sort", "sort asc extra", "sort desc extra", "sort invalid"}) {
            ParseException error = assertThrows(
                    ParseException.class, () -> new AddressBookParser().parseCommand(input).execute(model));
            assertEquals(SortCommand.MESSAGE_INVALID_ARGUMENTS, error.getMessage());
            assertEquals(expectedModel, model);
        }
    }

    @Test
    public void parse_validDirection_returnsSortCommand() {
        // EP: supported lowercase directions with surrounding whitespace.
        assertParseSuccess(parser, " asc ", new SortCommand(SortOrder.ASCENDING));
        assertParseSuccess(parser, " desc ", new SortCommand(SortOrder.DESCENDING));

        // EP: supported directions with uppercase or mixed-case letters.
        assertParseSuccess(parser, " ASC ", new SortCommand(SortOrder.ASCENDING));
        assertParseSuccess(parser, "DeSc", new SortCommand(SortOrder.DESCENDING));
    }

    @Test
    public void parse_missingDirection_throwsParseException() {
        String message = SortCommand.MESSAGE_INVALID_ARGUMENTS;
        // EP: missing direction; boundary value: zero input characters.
        assertParseFailure(parser, "", message);
        // EP: whitespace-only direction.
        assertParseFailure(parser, "   ", message);
    }

    @Test
    public void parse_unsupportedDirection_throwsParseException() {
        String message = SortCommand.MESSAGE_INVALID_ARGUMENTS;
        // EP: unsupported direction values, including legacy parameters, words, and numbers.
        for (String input : new String[] {"A-Z", "Z-A", "ascending", "descending", "123", "A Z"}) {
            assertParseFailure(parser, input, message);
        }
    }

    @Test
    public void parse_extraArguments_throwsParseException() {
        String message = SortCommand.MESSAGE_INVALID_ARGUMENTS;
        // EP: multiple arguments; boundary value: two tokens exceed the one-token limit.
        for (String input : new String[] {"asc desc", "asc extra", "desc extra"}) {
            assertParseFailure(parser, input, message);
        }
    }
}
