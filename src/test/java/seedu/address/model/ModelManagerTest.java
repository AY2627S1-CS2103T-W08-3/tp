package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.testutil.AddressBookBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void hasDeletedPersons_noDeletes_returnsFalse() {
        assertFalse(modelManager.hasDeletedPersons());
    }

    @Test
    public void hasDeletedPersons_afterDelete_returnsTrue() {
        modelManager.addPerson(ALICE);
        modelManager.deletePerson(ALICE);
        assertTrue(modelManager.hasDeletedPersons());
    }

    @Test
    public void deletePerson_personNotInAddressBook_throwsPersonNotFoundExceptionAndKeepsNoHistory() {
        assertThrows(PersonNotFoundException.class, () -> modelManager.deletePerson(ALICE));
        assertFalse(modelManager.hasDeletedPersons());
    }

    @Test
    public void getLastDeletedPerson_noDeletes_throwsNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> modelManager.getLastDeletedPerson());
    }

    @Test
    public void getLastDeletedPerson_afterDeletes_returnsMostRecentWithoutRestoring() {
        modelManager.addPerson(ALICE);
        modelManager.addPerson(BENSON);
        modelManager.deletePerson(ALICE);
        modelManager.deletePerson(BENSON);

        assertEquals(BENSON, modelManager.getLastDeletedPerson());
        assertFalse(modelManager.hasPerson(BENSON));
    }

    @Test
    public void restoreLastDeletedPerson_noDeletes_throwsNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> modelManager.restoreLastDeletedPerson());
    }

    @Test
    public void restoreLastDeletedPerson_afterDelete_restoresAtOriginalIndexAndClearsHistory() {
        modelManager.addPerson(ALICE);
        modelManager.addPerson(BENSON);
        modelManager.deletePerson(ALICE);

        modelManager.restoreLastDeletedPerson();

        assertEquals(List.of(ALICE, BENSON), modelManager.getAddressBook().getPersonList());
        assertFalse(modelManager.hasDeletedPersons());
    }

    @Test
    public void restoreLastDeletedPerson_duplicatePerson_throwsDuplicatePersonExceptionAndKeepsHistory() {
        modelManager.addPerson(ALICE);
        modelManager.deletePerson(ALICE);
        modelManager.addPerson(ALICE);

        assertThrows(DuplicatePersonException.class, () -> modelManager.restoreLastDeletedPerson());
        assertTrue(modelManager.hasDeletedPersons());
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }
}
