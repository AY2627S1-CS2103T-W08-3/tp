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
 * Tests lexicographical display ordering and its interaction with other commands.
 */
public class SortCommandTest {

    private final Person zoe = new PersonBuilder().withName("Zoe").build();
    private final Person alice = new PersonBuilder().withName("alice").build();
    private final Person bob = new PersonBuilder().withName("Bob").build();

    @Test
    public void constructor_nullSortOrder_throwsNullPointerException() {
        // EP: null sort direction (invalid compulsory dependency).
        assertThrows(NullPointerException.class, () -> new SortCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        // EP: null model (invalid execution dependency).
        SortCommand command = new SortCommand(SortCommand.SortOrder.ASCENDING);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_modelDoesNotApplyComparator_throwsAssertionError() {
        // Fault injection: a model violates the sorting postcondition.
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
        // EP: singleton list; boundary value: one contact, with no adjacent pair to compare.
        Model model = new ModelManager();
        model.addPerson(alice);
        for (SortCommand.SortOrder order : SortCommand.SortOrder.values()) {
            new SortCommand(order).execute(model);
            assertEquals(List.of(alice), model.getFilteredPersonList());
        }
    }

    @Test
    public void execute_descendingThenAscending_changesDirectionWithoutChangingData() throws Exception {
        // State transition: descending order followed by ascending order after deletion.
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
        // EP: multiple unsorted contacts, including mixed-case names.
        Model model = createModel();
        assertEquals(SortCommand.MESSAGE_SUCCESS,
                new SortCommand(SortCommand.SortOrder.ASCENDING).execute(model).getFeedbackToUser());
        assertEquals(List.of(alice, bob, zoe), model.getFilteredPersonList());
        assertEquals(List.of(zoe, alice, bob), model.getAddressBook().getPersonList());
        // State transition: applying the same direction again is idempotent.
        new SortCommand(SortCommand.SortOrder.ASCENDING).execute(model);
        assertEquals(List.of(alice, bob, zoe), model.getFilteredPersonList());
    }

    @Test
    public void execute_filteredContacts_showsAllContacts() {
        // EP: an active filter hides some contacts before sorting.
        Model model = createModel();
        model.updateFilteredPersonList(person -> person.equals(zoe));
        new SortCommand(SortCommand.SortOrder.ASCENDING).execute(model);
        assertEquals(List.of(alice, bob, zoe), model.getFilteredPersonList());
    }

    @Test
    public void execute_emptyAddressBook_success() {
        // EP: empty list; boundary value: zero contacts.
        Model model = new ModelManager();
        new SortCommand(SortCommand.SortOrder.ASCENDING).execute(model);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_sortedContacts_deleteByUuidRemovesCorrectPerson() throws Exception {
        // Interaction test: UUID-based deletion is independent of display order.
        Model model = createModel();
        new SortCommand(SortCommand.SortOrder.ASCENDING).execute(model);
        new DeleteCommand(alice.getShortUuid()).execute(model);
        assertEquals(List.of(bob, zoe), model.getFilteredPersonList());
        assertEquals(List.of(zoe, bob), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_sortedContacts_addAndEditMaintainOrder() {
        // State transitions: adding a contact and editing a name while sorting is active.
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

        // EP: same command instance.
        assertTrue(ascending.equals(ascending));

        // EP: different command instances with equal directions.
        assertTrue(ascending.equals(new SortCommand(SortCommand.SortOrder.ASCENDING)));

        // EP: different command instances with unequal directions.
        assertFalse(ascending.equals(descending));

        // EP: null comparison target.
        assertFalse(ascending.equals(null));

        // EP: comparison target of a different type.
        assertFalse(ascending.equals("sort"));
    }

    private Model createModel() {
        AddressBook addressBook = new AddressBook();
        addressBook.setPersons(List.of(zoe, alice, bob));
        return new ModelManager(addressBook, new UserPrefs());
    }
}
