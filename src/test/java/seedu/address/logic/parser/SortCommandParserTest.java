package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
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

        for (String input : new String[] {"sort", "sort A-Z extra", "sort Z-A extra", "sort invalid"}) {
            ParseException error = assertThrows(
                    ParseException.class, () -> new AddressBookParser().parseCommand(input).execute(model));
            assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE), error.getMessage());
            assertEquals(expectedModel, model);
        }
    }

    @Test
    public void parse_validDirection_returnsSortCommand() {
        assertParseSuccess(parser, " A-Z ", new SortCommand(SortOrder.ASCENDING));
        assertParseSuccess(parser, " Z-A ", new SortCommand(SortOrder.DESCENDING));
    }

    @Test
    public void parse_missingDirection_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", message);
        assertParseFailure(parser, "   ", message);
    }

    @Test
    public void parse_unsupportedDirection_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        for (String input : new String[] {"a-z", "z-a", "ascending", "descending", "123", "A Z"}) {
            assertParseFailure(parser, input, message);
        }
    }

    @Test
    public void parse_extraArguments_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        for (String input : new String[] {"A-Z Z-A", "A-Z extra", "Z-A extra"}) {
            assertParseFailure(parser, input, message);
        }
    }
}
