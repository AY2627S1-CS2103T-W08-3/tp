package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * A list of persons that enforces uniqueness between its elements and does not allow nulls.
 * Persons whose name is exactly {@code UNKNOWN} are exempt from the uniqueness check.
 * A person is considered unique by comparing using {@code Person#isSamePerson(Person)}. As such, adding and updating of
 * persons uses Person#isSamePerson(Person) for equality so as to ensure that the person being added or updated is
 * unique in terms of identity in the UniquePersonList. Replacement and removal locate the target by UUID so that
 * persons with identical fields can be modified independently.
 *
 * Supports a minimal set of list operations.
 *
 * @see Person#isSamePerson(Person)
 */
public class UniquePersonList implements Iterable<Person> {

    private final ObservableList<Person> internalList = FXCollections.observableArrayList();
    private final ObservableList<Person> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent person as the given argument.
     */
    public boolean contains(Person toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSamePerson);
    }

    /**
     * Adds a person to the list.
     * The person must not already exist in the list unless the person's name is {@code UNKNOWN}.
     */
    public void add(Person toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd) && !isUnknownName(toAdd)) {
            throw new DuplicatePersonException();
        }
        internalList.add(toAdd);
    }

    /**
     * Inserts a person into the list at {@code index}, shifting later persons back by one.
     * The person must not already exist in the list unless the person's name is {@code UNKNOWN}, and {@code index}
     * must be from 0 to the list size.
     */
    public void add(int index, Person toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd) && !isUnknownName(toAdd)) {
            throw new DuplicatePersonException();
        }
        internalList.add(index, toAdd);
    }

    /**
     * Replaces the person {@code target} in the list with {@code editedPerson}.
     * A person with {@code target}'s UUID must exist in the list.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the list.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        int index = indexOfUuid(target);
        if (index == -1) {
            throw new PersonNotFoundException();
        }

        if (!target.isSamePerson(editedPerson) && contains(editedPerson)) {
            throw new DuplicatePersonException();
        }

        internalList.set(index, editedPerson);
    }

    /**
     * Removes the person with {@code toRemove}'s UUID from the list.
     * A person with that UUID must exist in the list.
     */
    public void remove(Person toRemove) {
        requireNonNull(toRemove);
        int index = indexOfUuid(toRemove);
        if (index == -1) {
            throw new PersonNotFoundException();
        }
        internalList.remove(index);
    }

    /**
     * Returns the index of the person with {@code person}'s UUID, or -1 if no such person exists.
     */
    private int indexOfUuid(Person person) {
        for (int i = 0; i < internalList.size(); i++) {
            if (internalList.get(i).getUuid().equals(person.getUuid())) {
                return i;
            }
        }
        return -1;
    }

    public void setPersons(UniquePersonList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code persons}.
     * Duplicate names are allowed only when the later person's name is exactly {@code UNKNOWN}, as when adding persons.
     */
    public void setPersons(List<Person> persons) {
        requireAllNonNull(persons);
        if (!personsAreUnique(persons)) {
            throw new DuplicatePersonException();
        }

        internalList.setAll(persons);
    }

    private static boolean isUnknownName(Person person) {
        return "UNKNOWN".equals(person.getName().fullName);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Person> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Person> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UniquePersonList otherUniquePersonList)) {
            return false;
        }

        return internalList.equals(otherUniquePersonList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    /**
     * Returns true if {@code persons} satisfies the same uniqueness checks as adding persons in list order.
     */
    private boolean personsAreUnique(List<Person> persons) {
        for (int i = 0; i < persons.size() - 1; i++) {
            for (int j = i + 1; j < persons.size(); j++) {
                if (persons.get(i).isSamePerson(persons.get(j)) && !isUnknownName(persons.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
