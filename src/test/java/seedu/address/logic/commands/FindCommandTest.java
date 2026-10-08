package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(List.of("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(List.of("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_misrememberedName_matchesEitherWholeWord() throws ParseException, CommandException {
        Person aliceDavidson = new PersonBuilder().withName("Alice Davidson").build();
        Person alisonRichards = new PersonBuilder().withName("Alison Richards").build();
        Person aliceRichards = new PersonBuilder().withName("Alice Richards").build();
        Person alisonRichardson = new PersonBuilder().withName("Alison Richardson").build();
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(aliceDavidson);
        addressBook.addPerson(alisonRichards);
        addressBook.addPerson(aliceRichards);
        addressBook.addPerson(alisonRichardson);
        Model searchModel = new ModelManager(addressBook, new UserPrefs());
        Command command = new AddressBookParser().parseCommand("find aLIce rICHards");

        CommandResult result = command.execute(searchModel);

        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3), result.getFeedbackToUser());
        assertEquals(List.of(aliceDavidson, alisonRichards, aliceRichards), searchModel.getFilteredPersonList());
    }

    @Test
    public void execute_namesAndTags_matchesAnyTermWithoutDuplicates() throws ParseException, CommandException {
        Person nameMatch = new PersonBuilder().withName("Alice Davidson").build();
        Person tagMatch = new PersonBuilder().withName("Alison Richards").withTags("FRIENDS").build();
        Person bothMatch = new PersonBuilder().withName("Alice Richards").withTags("colleagues").build();
        Person noMatch = new PersonBuilder().withName("Bob Smith").withTags("friendship").build();
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(nameMatch);
        addressBook.addPerson(tagMatch);
        addressBook.addPerson(bothMatch);
        addressBook.addPerson(noMatch);
        Model searchModel = new ModelManager(addressBook, new UserPrefs());
        AddressBookParser parser = new AddressBookParser();

        CommandResult mixedResult = parser.parseCommand("find Alice t/friends t/colleagues").execute(searchModel);

        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3), mixedResult.getFeedbackToUser());
        assertEquals(List.of(nameMatch, tagMatch, bothMatch), searchModel.getFilteredPersonList());

        CommandResult tagsResult = parser.parseCommand("find t/friends t/colleagues").execute(searchModel);

        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2), tagsResult.getFeedbackToUser());
        assertEquals(List.of(tagMatch, bothMatch), searchModel.getFilteredPersonList());

        CommandResult noMatchResult = parser.parseCommand("find t/family").execute(searchModel);

        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0), noMatchResult.getFeedbackToUser());
        assertEquals(List.of(), searchModel.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(List.of(userInput.split("\\s+")));
    }
}
