package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    // EP: Constructor targets are null, invalid UUID prefixes, or valid 7-to-32-character prefixes.
    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();

        assertThrows(NullPointerException.class, () -> new EditCommand(null, descriptor));
        assertThrows(NullPointerException.class, () -> new EditCommand("1234567", null));
    }

    // Boundary value: six and 33 hexadecimal characters are invalid, while seven and 32 are valid.
    @Test
    public void constructor_uuidLengthBoundaries() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();

        assertThrows(IllegalArgumentException.class, () -> new EditCommand("123456", descriptor));
        assertDoesNotThrow(() -> new EditCommand("1234567", descriptor));
        assertDoesNotThrow(() -> new EditCommand("1234567890abcdef1234567890abcdef", descriptor));
        assertThrows(IllegalArgumentException.class, () -> new EditCommand(
                "1234567890abcdef1234567890abcdef0", descriptor));
    }

    // EP: Non-hexadecimal UUID prefixes are rejected.
    @Test
    public void constructor_invalidUuid_failure() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();

        assertThrows(IllegalArgumentException.class, () -> new EditCommand("123456g", descriptor));
    }

    // EP: A valid target can update every field in an unfiltered list.
    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder()
                .withRemark(model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased())
                        .getRemark().value)
                .build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(personToEdit.getShortUuid(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    // EP: A valid target can update a non-empty subset of fields.
    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(lastPerson.getShortUuid(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    // EP: Remarks can be replaced with a non-empty value.
    @Test
    public void execute_remark_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withRemark("Updated remark").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withRemark("Updated remark").build();
        EditCommand editCommand = new EditCommand(personToEdit.getShortUuid(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
        assertEquals(personToEdit.getUuid(), model.getAddressBook().getPersonList().get(0).getUuid());
    }

    // Boundary value: an empty remark clears the existing remark.
    @Test
    public void execute_clearRemark_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withRemark("").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withRemark("").build();
        EditCommand editCommand = new EditCommand(personToEdit.getShortUuid(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    // EP: Similar names produce warnings unless the only similar entry is the edited person itself.
    @Test
    public void execute_similarName_succeedsWithWarning() {
        Person existingPerson = new PersonBuilder().withName("Alice Paulin").build();
        model.addPerson(existingPerson);

        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withName("Alice Paulina").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName("Alice Paulina").build();
        EditCommand editCommand = new EditCommand(personToEdit.getShortUuid(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + String.format(Messages.MESSAGE_SIMILAR_NAME_WARNING_FORMAT,
                        editedPerson.getName(), existingPerson.getName());
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    // EP: Direct command execution accepts a no-op descriptor; the parser rejects this input earlier.
    @Test
    public void execute_nameSimilarOnlyToEditedEntry_succeedsWithoutWarning() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withName("Alice Paulin").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName("Alice Paulin").build();
        EditCommand editCommand = new EditCommand(personToEdit.getShortUuid(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    // EP: UUID targeting works independently of the current filtered list.
    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(editedPerson.getShortUuid(), new EditPersonDescriptor());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    // EP: Editing into a duplicate person is rejected for both filtered and unfiltered lists.
    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(personInFilteredList.getShortUuid(),
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    // EP: UUID execution targets partition into no match and multiple matches.
    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(secondPerson.getShortUuid(), descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personToEdit = model.getFilteredPersonList().get(0);
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(personToEdit.getShortUuid(),
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    // EP: Equality partitions cover same object, equal values, null, another type, UUID, and descriptor changes.
    @Test
    public void execute_unknownUuid_failure() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand("1234567", descriptor);

        assertCommandFailure(editCommand, model, String.format(EditCommand.MESSAGE_NO_PERSON_WITH_UUID, "1234567"));
    }

    @Test
    public void execute_ambiguousUuid_failure() {
        Person ambiguousPerson = new PersonBuilder(model.getAddressBook().getPersonList().get(0))
                .withName("Another Person")
                .withUuid("aaaaaaa1-0000-0000-0000-000000000000")
                .build();
        model.addPerson(ambiguousPerson);

        EditCommand editCommand = new EditCommand("aaaaaaa",
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model,
                String.format(EditCommand.MESSAGE_AMBIGUOUS_UUID, "aaaaaaa"));
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand("aaaaaaa", DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand("aaaaaaa", copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different UUID -> returns false
        assertFalse(standardCommand.equals(new EditCommand("bbbbbbb", DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand("aaaaaaa", DESC_BOB)));
    }

    // EP: Descriptor remarks participate in equality; different remarks are not equivalent.
    @Test
    public void descriptorEquals_differentRemark_returnsFalse() {
        EditPersonDescriptor firstDescriptor = new EditPersonDescriptorBuilder()
                .withRemark("First remark").build();
        EditPersonDescriptor secondDescriptor = new EditPersonDescriptorBuilder()
                .withRemark("Second remark").build();

        assertFalse(firstDescriptor.equals(secondDescriptor));
    }

    // EP: String representation includes the UUID and descriptor state.
    @Test
    public void toStringMethod() {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand("aaaaaaa", editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{targetUuid=aaaaaaa, editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

}
