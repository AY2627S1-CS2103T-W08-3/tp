package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code UndeleteCommand}.
 */
public class UndeleteCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_noDeletedPersons_throwsCommandException() {
        assertCommandFailure(new UndeleteCommand(), model, UndeleteCommand.MESSAGE_NO_DELETED_PERSONS);
    }

    @Test
    public void execute_afterDelete_restoresAtOriginalIndex() {
        model.deletePerson(BENSON);

        assertUndeleteSuccess(BENSON, typicalModel());
    }

    @Test
    public void execute_multipleDeletes_restoresMostRecentFirst() {
        model.deletePerson(ALICE);
        model.deletePerson(BENSON);

        Model expectedModelAfterFirstUndelete = typicalModel();
        expectedModelAfterFirstUndelete.deletePerson(ALICE);
        assertUndeleteSuccess(BENSON, expectedModelAfterFirstUndelete);

        assertUndeleteSuccess(ALICE, typicalModel());
    }

    @Test
    public void execute_personHiddenByFilter_showsAllPersons() {
        model.deletePerson(ALICE);
        model.updateFilteredPersonList(person -> false);

        assertUndeleteSuccess(ALICE, typicalModel());
    }

    @Test
    public void execute_originalIndexPastEndOfList_restoresAtEnd() {
        model.deletePerson(GEORGE);
        model.setAddressBook(new AddressBook());

        Model expectedModel = new ModelManager();
        expectedModel.addPerson(GEORGE);
        assertUndeleteSuccess(GEORGE, expectedModel);
    }

    @Test
    public void execute_samePersonAlreadyExists_throwsCommandExceptionAndKeepsHistory() {
        model.deletePerson(ALICE);
        model.addPerson(ALICE);

        assertCommandFailure(new UndeleteCommand(), model,
                String.format(UndeleteCommand.MESSAGE_DUPLICATE_PERSON, ALICE.getName()));
        assertTrue(model.hasDeletedPersons());
    }

    @Test
    public void equals() {
        UndeleteCommand undeleteCommand = new UndeleteCommand();

        // same object -> returns true
        assertTrue(undeleteCommand.equals(undeleteCommand));

        // another undelete command -> returns true
        assertTrue(undeleteCommand.equals(new UndeleteCommand()));

        // different types -> returns false
        assertFalse(undeleteCommand.equals(1));

        // null -> returns false
        assertFalse(undeleteCommand.equals(null));

        // different command -> returns false
        assertFalse(undeleteCommand.equals(new ListCommand()));
    }

    /**
     * Executes an {@code UndeleteCommand} on {@code model}, and confirms that {@code restoredPerson}
     * is reported as restored and that the model then matches {@code expectedModel}.
     */
    private void assertUndeleteSuccess(Person restoredPerson, Model expectedModel) {
        String expectedMessage = String.format(UndeleteCommand.MESSAGE_SUCCESS, Messages.format(restoredPerson));
        assertCommandSuccess(new UndeleteCommand(), model, expectedMessage, expectedModel);
    }

    private static Model typicalModel() {
        return new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }
}
