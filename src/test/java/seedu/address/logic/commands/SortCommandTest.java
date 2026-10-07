package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

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
    public void execute_unsortedContacts_displaysAlphabeticallyWithoutChangingData() {
        Model model = createModel();
        assertEquals(SortCommand.MESSAGE_SUCCESS, new SortCommand().execute(model).getFeedbackToUser());
        assertEquals(List.of(alice, bob, zoe), model.getFilteredPersonList());
        assertEquals(List.of(zoe, alice, bob), model.getAddressBook().getPersonList());
        new SortCommand().execute(model);
        assertEquals(List.of(alice, bob, zoe), model.getFilteredPersonList());
    }

    @Test
    public void execute_filteredContacts_showsAllContacts() {
        Model model = createModel();
        model.updateFilteredPersonList(person -> person.equals(zoe));
        new SortCommand().execute(model);
        assertEquals(List.of(alice, bob, zoe), model.getFilteredPersonList());
    }

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        new SortCommand().execute(model);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_sortedContacts_indexCommandsUseDisplayedOrder() throws Exception {
        Model model = createModel();
        new SortCommand().execute(model);
        new DeleteCommand(INDEX_FIRST_PERSON).execute(model);
        assertEquals(List.of(bob, zoe), model.getFilteredPersonList());
        assertEquals(List.of(zoe, bob), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_sortedContacts_addAndEditMaintainOrder() {
        Model model = createModel();
        new SortCommand().execute(model);
        Person aaron = new PersonBuilder().withName("Aaron").build();
        model.addPerson(aaron);
        assertEquals(List.of(aaron, alice, bob, zoe), model.getFilteredPersonList());
        Person charlie = new PersonBuilder(bob).withName("Charlie").build();
        model.setPerson(bob, charlie);
        assertEquals(List.of(aaron, alice, charlie, zoe), model.getFilteredPersonList());
    }

    private Model createModel() {
        AddressBook addressBook = new AddressBook();
        addressBook.setPersons(List.of(zoe, alice, bob));
        return new ModelManager(addressBook, new UserPrefs());
    }
}
