package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    @Test
    public void execute_validShortUuid_success() {
        Person personToDelete = new PersonBuilder()
                .withUuid("1a2b3c4d-0000-0000-0000-000000000000").build();
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        model.addPerson(personToDelete);

        DeleteCommand deleteCommand = new DeleteCommand("1a2b3c4");

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(new AddressBook(), new UserPrefs());

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownShortUuid_throwsCommandException() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        model.addPerson(new PersonBuilder()
                .withUuid("1a2b3c4d-0000-0000-0000-000000000000").build());

        DeleteCommand deleteCommand = new DeleteCommand("9999999");

        assertCommandFailure(deleteCommand, model,
                String.format(DeleteCommand.MESSAGE_NO_PERSON_WITH_UUID, "9999999"));
    }

    @Test
    public void execute_ambiguousShortUuid_throwsCommandException() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        model.addPerson(new PersonBuilder().withName("Alice")
                .withUuid("1a2b3c4d-0000-0000-0000-000000000001").build());
        model.addPerson(new PersonBuilder().withName("Bob")
                .withUuid("1a2b3c4d-0000-0000-0000-000000000002").build());

        DeleteCommand deleteCommand = new DeleteCommand("1a2b3c4");

        assertCommandFailure(deleteCommand, model,
                String.format(DeleteCommand.MESSAGE_AMBIGUOUS_UUID, "1a2b3c4"));
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand("1a2b3c4");
        DeleteCommand deleteSecondCommand = new DeleteCommand("7654321");

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand("1a2b3c4");
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different uuid -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        DeleteCommand deleteCommand = new DeleteCommand("1a2b3c4");
        String expected = DeleteCommand.class.getCanonicalName() + "{targetUuid=1a2b3c4}";
        assertEquals(expected, deleteCommand.toString());
    }
}
