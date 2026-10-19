package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for RemarkCommand.
 */
public class RemarkCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_nonEmptyRemark_success() throws ParseException {
        final Remark remark = ParserUtil.parseRemark("Some remark");
        String targetUuid = "aaaaaaa";
        RemarkCommand command = new RemarkCommand(targetUuid, remark);
        PersonBuilder personBuilder = new PersonBuilder(model.getPersonsByShortUuid(targetUuid).get(0));
        PersonBuilder editedPersonBuilder = personBuilder.withRemark(remark.value);
        Person originalPerson = model.getPersonsByShortUuid(targetUuid).get(0);
        Person editedPerson = editedPersonBuilder.build();
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setPerson(originalPerson, editedPerson);

        assertCommandSuccess(command, model,
                String.format(RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS,
                        Messages.format(editedPerson)), expectedModel);
    }

    @Test
    public void execute_filteredList_success() throws ParseException {
        model.updateFilteredPersonList(person -> false);

        String targetUuid = "aaaaaaa";
        Person originalPerson = model.getPersonsByShortUuid(targetUuid).get(0);
        Remark remark = ParserUtil.parseRemark("Updated remark");
        RemarkCommand command = new RemarkCommand(targetUuid, remark);
        Person editedPerson = new PersonBuilder(originalPerson).withRemark(remark.value).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(originalPerson, editedPerson);

        assertCommandSuccess(command, model,
                String.format(RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS, Messages.format(editedPerson)), expectedModel);

        Person actualPerson = model.getAddressBook().getPersonList().get(0);
        assertEquals(originalPerson.getUuid(), actualPerson.getUuid());
    }

    @Test
    public void execute_emptyRemark_success() throws ParseException {
        final Remark remark = ParserUtil.parseRemark("");
        String targetUuid = "aaaaaaa";
        RemarkCommand command = new RemarkCommand(targetUuid, remark);
        Person originalPerson = model.getPersonsByShortUuid(targetUuid).get(0);
        Person editedPerson = new PersonBuilder(originalPerson).withRemark(remark.value).build();
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setPerson(originalPerson, editedPerson);

        assertCommandSuccess(command, model,
                String.format(RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS,
                        Messages.format(editedPerson)), expectedModel);
    }

    @Test
    public void execute_invalidUuid_failure() throws ParseException {
        RemarkCommand command = new RemarkCommand("9999999", ParserUtil.parseRemark("Some remark"));

        assertCommandFailure(command, model,
                String.format(RemarkCommand.MESSAGE_NO_PERSON_WITH_UUID, "9999999"));
    }

    @Test
    public void execute_ambiguousUuid_failure() throws ParseException {
        Model ambiguousModel = new ModelManager(new AddressBook(), new UserPrefs());
        ambiguousModel.addPerson(new PersonBuilder()
                .withName("Alice")
                .withUuid("1a2b3c4d-0000-0000-0000-000000000001").build());
        ambiguousModel.addPerson(new PersonBuilder()
                .withName("Bob")
                .withUuid("1a2b3c4d-0000-0000-0000-000000000002").build());
        RemarkCommand command = new RemarkCommand("1a2b3c4", ParserUtil.parseRemark("Some remark"));

        assertCommandFailure(command, ambiguousModel,
                String.format(RemarkCommand.MESSAGE_AMBIGUOUS_UUID, "1a2b3c4"));
    }

    @Test
    public void equals() throws ParseException {
        final Remark remark = ParserUtil.parseRemark(VALID_REMARK_AMY);
        final RemarkCommand standardCommand = new RemarkCommand("aaaaaaa", remark);

        // same values -> returns true
        RemarkCommand commandWithSameValues = new RemarkCommand("aaaaaaa", remark);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different UUID -> returns false
        assertFalse(standardCommand.equals(new RemarkCommand("bbbbbbb", remark)));

        // different remark -> returns false
        final Remark remarkSecond = ParserUtil.parseRemark(VALID_REMARK_BOB);
        assertFalse(standardCommand.equals(new RemarkCommand("aaaaaaa", remarkSecond)));
    }
}
