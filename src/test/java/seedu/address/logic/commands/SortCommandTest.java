package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests alphabetical display ordering and its interaction with other commands.
 */
public class SortCommandTest {

    private final Person zoe = new PersonBuilder().withName("Zoe").build();
    private final Person alice = new PersonBuilder().withName("alice").build();
    private final Person bob = new PersonBuilder().withName("Bob").build();

    @Test
    public void constructor_nullSortOrder_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SortCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        SortCommand command = new SortCommand(SortCommand.SortOrder.ASCENDING);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_modelDoesNotApplyComparator_throwsAssertionError() {
        Model model = new ModelManager() {
            @Override
            public void updatePersonListComparator(Comparator<Person> comparator) {
                // Simulates a model bug: the requested comparator is ignored.
            }
        };
        model.addPerson(zoe);
        model.addPerson(alice);
        SortCommand command = new SortCommand(SortCommand.SortOrder.ASCENDING);

        AssertionError error = assertThrows(AssertionError.class, () -> command.execute(model));
        assertEquals("Displayed contacts must follow the sort order", error.getMessage());
    }

    @Test
    public void execute_singleContact_successInBothDirections() {
        Model model = new ModelManager();
        model.addPerson(alice);
        for (SortCommand.SortOrder order : SortCommand.SortOrder.values()) {
            new SortCommand(order).execute(model);
            assertEquals(List.of(alice), model.getFilteredPersonList());
        }
    }

    @Test
    public void execute_descendingThenAscending_changesDirectionWithoutChangingData() throws Exception {
        Model model = createModel();
        new SortCommand(SortCommand.SortOrder.DESCENDING).execute(model);
        assertEquals(List.of(zoe, bob, alice), model.getFilteredPersonList());
        assertEquals(List.of(zoe, alice, bob), model.getAddressBook().getPersonList());
        new DeleteCommand(zoe.getShortUuid()).execute(model);
        assertEquals(List.of(bob, alice), model.getFilteredPersonList());
        new SortCommand(SortCommand.SortOrder.ASCENDING).execute(model);
        assertEquals(List.of(alice, bob), model.getFilteredPersonList());
    }

    @Test
    public void execute_unsortedContacts_displaysAlphabeticallyWithoutChangingData() {
        Model model = createModel();
        assertEquals(SortCommand.MESSAGE_SUCCESS,
                new SortCommand(SortCommand.SortOrder.ASCENDING).execute(model).getFeedbackToUser());
        assertEquals(List.of(alice, bob, zoe), model.getFilteredPersonList());
        assertEquals(List.of(zoe, alice, bob), model.getAddressBook().getPersonList());
        new SortCommand(SortCommand.SortOrder.ASCENDING).execute(model);
        assertEquals(List.of(alice, bob, zoe), model.getFilteredPersonList());
    }

    @Test
    public void execute_filteredContacts_showsAllContacts() {
        Model model = createModel();
        model.updateFilteredPersonList(person -> person.equals(zoe));
        new SortCommand(SortCommand.SortOrder.ASCENDING).execute(model);
        assertEquals(List.of(alice, bob, zoe), model.getFilteredPersonList());
    }

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        new SortCommand(SortCommand.SortOrder.ASCENDING).execute(model);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_sortedContacts_deleteByUuidRemovesCorrectPerson() throws Exception {
        Model model = createModel();
        new SortCommand(SortCommand.SortOrder.ASCENDING).execute(model);
        new DeleteCommand(alice.getShortUuid()).execute(model);
        assertEquals(List.of(bob, zoe), model.getFilteredPersonList());
        assertEquals(List.of(zoe, bob), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_sortedContacts_addAndEditMaintainOrder() {
        Model model = createModel();
        new SortCommand(SortCommand.SortOrder.ASCENDING).execute(model);
        Person aaron = new PersonBuilder().withName("Aaron").build();
        model.addPerson(aaron);
        assertEquals(List.of(aaron, alice, bob, zoe), model.getFilteredPersonList());
        Person charlie = new PersonBuilder(bob).withName("Charlie").build();
        model.setPerson(bob, charlie);
        assertEquals(List.of(aaron, alice, charlie, zoe), model.getFilteredPersonList());
    }

    @Test
    public void equals() {
        SortCommand ascending = new SortCommand(SortCommand.SortOrder.ASCENDING);
        SortCommand descending = new SortCommand(SortCommand.SortOrder.DESCENDING);

        // Same instance.
        assertTrue(ascending.equals(ascending));

        // Different instances with the same direction.
        assertTrue(ascending.equals(new SortCommand(SortCommand.SortOrder.ASCENDING)));

        // Different directions.
        assertFalse(ascending.equals(descending));

        // Null.
        assertFalse(ascending.equals(null));

        // Different type.
        assertFalse(ascending.equals("sort"));
    }

    private Model createModel() {
        AddressBook addressBook = new AddressBook();
        addressBook.setPersons(List.of(zoe, alice, bob));
        return new ModelManager(addressBook, new UserPrefs());
    }
}
