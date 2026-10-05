package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.logic.commands.RemarkCommand.MESSAGE_ARGUMENTS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Remark;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class RemarkCommandTest {

    private static final String MESSAGE_NOT_IMPLEMENTED_YET = "MESSAGE_NOT_IMPLEMENTED_YET";
    private Model model;
    private Model expectedModel;

    @Test
    public void execute() throws ParseException {
        final Remark remark = ParserUtil.parseRemark("Some remark");

        assertCommandFailure(new RemarkCommand(INDEX_FIRST_PERSON, remark), model,
                String.format(MESSAGE_ARGUMENTS, INDEX_FIRST_PERSON.getOneBased(), remark));
    }

    @Test
    public void equals() throws ParseException {
        final Remark remark = ParserUtil.parseRemark(VALID_REMARK_AMY);
        final RemarkCommand standardCommand = new RemarkCommand(INDEX_FIRST_PERSON, remark);

        // same values -> returns true
        RemarkCommand commandWithSameValues = new RemarkCommand(INDEX_FIRST_PERSON, remark);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new RemarkCommand(INDEX_SECOND_PERSON, remark)));

        // different remark -> returns false
        final Remark remarkSecond = ParserUtil.parseRemark(VALID_REMARK_BOB);
        assertFalse(standardCommand.equals(new RemarkCommand(INDEX_FIRST_PERSON, remarkSecond)));
    }


}
