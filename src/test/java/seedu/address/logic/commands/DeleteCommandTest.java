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

    private static final String SHARED_SHORT_UUID = "1a2b3c4";

    // Both persons have the short UUID SHARED_SHORT_UUID, and differ from the 9th hex digit onwards
    private static final Person ALICE = new PersonBuilder().withName("Alice")
            .withUuid("1a2b3c4d-1000-0000-0000-000000000000").build();
    private static final Person BOB = new PersonBuilder().withName("Bob")
            .withUuid("1a2b3c4d-2000-0000-0000-000000000000").build();

    @Test
    public void execute_uniqueShortUuid_success() {
        assertDeleteSuccess(new DeleteCommand(SHARED_SHORT_UUID), ALICE, modelWith(ALICE));
    }

    @Test
    public void execute_personHiddenByFilter_success() {
        Model model = modelWith(ALICE);
        model.updateFilteredPersonList(person -> false);

        assertDeleteSuccess(new DeleteCommand(SHARED_SHORT_UUID), ALICE, model);
    }

    @Test
    public void execute_longerPrefixResolvesSharedShortUuid_success() {
        Model expectedModel = modelWith(BOB);

        assertCommandSuccess(new DeleteCommand("1a2b3c4d1"), modelWith(ALICE, BOB),
                expectedDeleteMessage(ALICE), expectedModel);
    }

    @Test
    public void execute_unknownUuidPrefix_throwsCommandException() {
        assertCommandFailure(new DeleteCommand("9999999"), modelWith(ALICE),
                String.format(DeleteCommand.MESSAGE_NO_PERSON_WITH_UUID, "9999999"));
    }

    @Test
    public void execute_sharedShortUuid_throwsCommandExceptionListingFullUuids() {
        String expectedMatches = ALICE.getUuid() + " " + ALICE.getName() + "\n"
                + BOB.getUuid() + " " + BOB.getName();

        assertCommandFailure(new DeleteCommand(SHARED_SHORT_UUID), modelWith(ALICE, BOB),
                String.format(DeleteCommand.MESSAGE_AMBIGUOUS_UUID, SHARED_SHORT_UUID, expectedMatches));
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

        // different uuid prefix -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        DeleteCommand deleteCommand = new DeleteCommand("1a2b3c4");
        String expected = DeleteCommand.class.getCanonicalName() + "{targetUuidPrefix=1a2b3c4}";
        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Executes {@code command} on {@code model}, which holds only {@code personToDelete},
     * and confirms that the address book ends up empty.
     */
    private static void assertDeleteSuccess(DeleteCommand command, Person personToDelete, Model model) {
        assertCommandSuccess(command, model, expectedDeleteMessage(personToDelete), modelWith());
    }

    private static String expectedDeleteMessage(Person deletedPerson) {
        return String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(deletedPerson));
    }

    private static Model modelWith(Person... persons) {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        for (Person person : persons) {
            model.addPerson(person);
        }
        return model;
    }
}
