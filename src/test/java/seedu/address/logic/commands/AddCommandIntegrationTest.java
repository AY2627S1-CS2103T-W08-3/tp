package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_similarName_addsPersonWithWarning() {
        Person existingPerson = model.getAddressBook().getPersonList().get(0);
        Person personWithTypo = new PersonBuilder().withName("Alice Paulin").build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(personWithTypo);
        String expectedMessage = String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(personWithTypo))
                + String.format(Messages.MESSAGE_SIMILAR_NAME_WARNING_FORMAT,
                        personWithTypo.getName(), existingPerson.getName());

        assertCommandSuccess(new AddCommand(personWithTypo), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_extraSpacesInName_duplicate() {
        Person personWithExtraSpace = new PersonBuilder().withName("Alice  Pauline").build();

        assertCommandFailure(new AddCommand(personWithExtraSpace), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_unrelatedName_succeedsWithoutWarning() {
        Person unrelatedPerson = new PersonBuilder().withName("Zachary Zimmer").build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(unrelatedPerson);

        assertCommandSuccess(new AddCommand(unrelatedPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(unrelatedPerson)), expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

}
