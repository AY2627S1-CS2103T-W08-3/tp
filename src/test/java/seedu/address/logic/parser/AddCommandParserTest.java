package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_allFieldsPresent_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_FRIEND, new AddCommand(expectedPerson));


        // multiple tags - all accepted
        Person expectedPersonMultipleTags = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .build();
        assertParseSuccess(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                new AddCommand(expectedPersonMultipleTags));
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedPersonString = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_FRIEND;

        // multiple names
        assertParseFailure(parser, NAME_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // multiple phones
        assertParseFailure(parser, PHONE_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple emails
        assertParseFailure(parser, EMAIL_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // multiple addresses
        assertParseFailure(parser, ADDRESS_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // multiple fields repeated
        assertParseFailure(parser,
                validExpectedPersonString + PHONE_DESC_AMY + EMAIL_DESC_AMY + NAME_DESC_AMY + ADDRESS_DESC_AMY
                        + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_ADDRESS, PREFIX_EMAIL, PREFIX_PHONE));

        // invalid value followed by valid value

        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, INVALID_EMAIL_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, INVALID_PHONE_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, INVALID_ADDRESS_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // valid value followed by invalid value

        // invalid name
        assertParseFailure(parser, validExpectedPersonString + INVALID_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, validExpectedPersonString + INVALID_EMAIL_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, validExpectedPersonString + INVALID_PHONE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, validExpectedPersonString + INVALID_ADDRESS_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        assertParseSuccess(parser, NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_nameWithParentheses_success() {
        Person expectedPerson = new PersonBuilder(BOB).withName("Peter (Pete) Tan").withTags().build();
        String nameWithParentheses = " " + PREFIX_NAME + "Peter (Pete) Tan";

        assertParseSuccess(parser, nameWithParentheses + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_nameWithSlash_success() {
        Person expectedPerson = new PersonBuilder(BOB).withName("Peter s/o Tan").withTags().build();
        String nameWithSlash = " " + PREFIX_NAME + "Peter s/o Tan";

        assertParseSuccess(parser, nameWithSlash + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_nameWithDaughterOrSonNotation_success() {
        Person expectedPerson = new PersonBuilder(BOB).withName("Jane d/o Mary").withTags().build();
        String nameWithDaughterOrSonNotation = " " + PREFIX_NAME + "Jane d/o Mary";

        assertParseSuccess(parser,
                nameWithDaughterOrSonNotation + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_phoneWithLettersSpacesParenthesesAndHyphens_success() {
        String phoneWithLabels = "1234 5678 (HP) 1111-3333 (Office)";
        Person expectedPerson = new PersonBuilder(BOB).withPhone(phoneWithLabels).withTags().build();
        String phoneDescription = " " + PREFIX_PHONE + phoneWithLabels;

        assertParseSuccess(parser, NAME_DESC_BOB + phoneDescription + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_unknownPhone_success() {
        Person expectedPerson = new PersonBuilder(BOB).withPhone("Unknown").withTags().build();
        String phoneDescription = " " + PREFIX_PHONE + "Unknown";

        assertParseSuccess(parser, NAME_DESC_BOB + phoneDescription + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_unknownEmail_success() {
        Person expectedPerson = new PersonBuilder(BOB).withEmail("UNKNOWN").withTags().build();
        String emailDescription = " " + PREFIX_EMAIL + "UNKNOWN";

        assertParseSuccess(parser, NAME_DESC_BOB + PHONE_DESC_BOB + emailDescription + ADDRESS_DESC_BOB,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_emailWithSpacesAndParentheses_success() {
        String emailWithLabels = "peter@example.com (Work) jane@example.com (Personal)";
        Person expectedPerson = new PersonBuilder(BOB).withEmail(emailWithLabels).withTags().build();
        String emailDescription = " " + PREFIX_EMAIL + emailWithLabels;

        assertParseSuccess(parser, NAME_DESC_BOB + PHONE_DESC_BOB + emailDescription + ADDRESS_DESC_BOB,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        // missing name prefix
        assertParseFailure(parser, VALID_NAME_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_NAME_FIELD, true));

        // missing phone prefix
        assertParseFailure(parser, NAME_DESC_BOB + VALID_PHONE_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_PHONE_NUMBER, true));

        // missing email prefix
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + VALID_EMAIL_BOB + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_EMAIL_ADDRESS, true));

        // missing address prefix
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + VALID_ADDRESS_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_ADDRESS_FIELD, true));

        // all prefixes missing
        assertParseFailure(parser, VALID_NAME_BOB + VALID_PHONE_BOB + VALID_EMAIL_BOB + VALID_ADDRESS_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_NAME_FIELD, true));
    }

    @Test
    public void parse_emptyField_failure() {
        assertParseFailure(parser, " " + PREFIX_NAME + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_NAME_FIELD, true));
        assertParseFailure(parser, NAME_DESC_BOB + " " + PREFIX_PHONE + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_PHONE_NUMBER, true));
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + " " + PREFIX_EMAIL + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_EMAIL_ADDRESS, true));
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + " " + PREFIX_ADDRESS,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_ADDRESS_FIELD, true));
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_NAME_FIELD, false));

        // invalid phone
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_PHONE_DESC + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_PHONE_NUMBER, false));

        // invalid email
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + INVALID_EMAIL_DESC + ADDRESS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_EMAIL_ADDRESS, false));

        // invalid address
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + INVALID_ADDRESS_DESC
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_ADDRESS_FIELD, true));

        // invalid tag
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + INVALID_TAG_DESC + VALID_TAG_FRIEND,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_TAG, false));

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB + INVALID_ADDRESS_DESC,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_NAME_FIELD, false));

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }

    private static String expectedInvalidFieldMessage(String fieldMessage, boolean hasEmptyValue) {
        String usage = AddCommand.MESSAGE_USAGE;
        if (hasEmptyValue) {
            int firstLineEnd = usage.indexOf('\n');
            usage = usage.substring(0, firstLineEnd + 1) + AddCommand.MESSAGE_UNKNOWN_INFORMATION + "\n"
                    + usage.substring(firstLineEnd + 1);
        }
        return fieldMessage + "\n" + usage;
    }
}
