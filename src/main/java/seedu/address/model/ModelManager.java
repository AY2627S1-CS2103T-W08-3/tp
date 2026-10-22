package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final SortedList<Person> sortedPersons;

    /** Persons deleted this session, most recent first. Not saved to storage, and not part of equality. */
    private final Deque<DeletedPerson> deletedPersons = new ArrayDeque<>();

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        sortedPersons = new SortedList<>(filteredPersons);
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        requireNonNull(target);
        int index = addressBook.getPersonList().indexOf(target);
        addressBook.removePerson(target);

        assert index >= 0 : "A person that was removed must have been in the list";
        deletedPersons.push(new DeletedPerson(target, index));
    }

    @Override
    public List<Person> getPersonsByUuidPrefix(String uuidPrefix) {
        requireNonNull(uuidPrefix);
        return addressBook.getPersonList().stream()
                .filter(person -> person.hasUuidPrefix(uuidPrefix))
                .toList();
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return sortedPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public void updatePersonListComparator(Comparator<Person> comparator) {
        sortedPersons.setComparator(comparator);
    }

    //=========== Deleted Persons ============================================================================

    @Override
    public boolean hasDeletedPersons() {
        return !deletedPersons.isEmpty();
    }

    @Override
    public Person getLastDeletedPerson() {
        return deletedPersons.element().person();
    }

    @Override
    public void restoreLastDeletedPerson() {
        DeletedPerson lastDeleted = deletedPersons.element();
        // Persons removed since, e.g. by clear, can leave the original index past the end of the list
        int index = Math.min(lastDeleted.index(), addressBook.getPersonList().size());

        addressBook.addPerson(index, lastDeleted.person());
        deletedPersons.pop();
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        logger.fine("Restored person with UUID " + lastDeleted.person().getUuid() + " at index " + index);
    }

    /**
     * A deleted person, and the index in the address book they were deleted from.
     */
    private record DeletedPerson(Person person, int index) {}

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && sortedPersons.equals(otherModelManager.sortedPersons);
    }

}
