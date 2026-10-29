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
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    // EP: Null input is outside the parser's valid input domain.
    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    // EP: All required fields are present, with zero, one, or multiple optional values.
    @Test
    public void parse_allFieldsPresent_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND).build();

        // Boundary value: whitespace-only preamble is accepted as equivalent to no preamble.
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_FRIEND, new AddCommand(expectedPerson));


        // EP: Multiple tags are accepted and preserved as a set.
        Person expectedPersonMultipleTags = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .build();
        assertParseSuccess(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                new AddCommand(expectedPersonMultipleTags));
    }

    // EP: Remarks may be non-empty or empty; empty is the boundary value for clearing a remark.
    @Test
    public void parse_remark_success() {
        String remark = "Likes to swim.";
        Person expectedPerson = new PersonBuilder(BOB).withRemark(remark).withTags().build();
        String userInput = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + " " + PREFIX_REMARK + remark;

        assertParseSuccess(parser, userInput, new AddCommand(expectedPerson));
    }

    @Test
    public void parse_emptyRemark_success() {
        Person expectedPerson = new PersonBuilder(BOB).withRemark("").withTags().build();
        String userInput = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + " " + PREFIX_REMARK;

        assertParseSuccess(parser, userInput, new AddCommand(expectedPerson));
    }

    // EP: Repeated non-tag prefixes are rejected regardless of whether the repeated value is valid.
    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedPersonString = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_FRIEND;

        // EP: One repeated required prefix.
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

        // multiple remarks
        assertParseFailure(parser, " " + PREFIX_REMARK + "First" + validExpectedPersonString
                        + " " + PREFIX_REMARK + "Second",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));

        // EP: Several repeated required prefixes are all reported.
        assertParseFailure(parser,
                validExpectedPersonString + PHONE_DESC_AMY + EMAIL_DESC_AMY + NAME_DESC_AMY + ADDRESS_DESC_AMY
                        + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_ADDRESS, PREFIX_EMAIL, PREFIX_PHONE));

        // EP: Invalid value followed by a valid value still belongs to the duplicate-prefix partition.

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

        // EP: Valid value followed by an invalid value is also a duplicate-prefix case.

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

    // EP: Optional tags are absent; the lower boundary is zero tags.
    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        assertParseSuccess(parser, NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY,
                new AddCommand(expectedPerson));
    }

    // EP: UNKNOWN is a valid standard indicator for a required field and is case-sensitive in stored names.
    @Test
    public void parse_unknownName_success() {
        Person expectedPerson = new PersonBuilder(BOB).withName("UNKNOWN").withTags().build();
        String nameDescription = " " + PREFIX_NAME + "UNKNOWN";

        assertParseSuccess(parser, nameDescription + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                new AddCommand(expectedPerson));
    }

    // EP: Valid names may include supported punctuation and notation.
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

    // EP: Valid non-name field syntax includes quotes, labels, and punctuation.
    @Test
    public void parse_addressWithQuote_success() {
        String address = "Blk 5 \"Tower";
        Person expectedPerson = new PersonBuilder(BOB).withAddress(address).withTags().build();
        String addressDescription = " " + PREFIX_ADDRESS + address;

        assertParseSuccess(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + addressDescription,
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
    public void parse_unknownEmail_success() {
        Person expectedPerson = new PersonBuilder(BOB).withEmail("UNKNOWN").withTags().build();
        String emailDescription = " " + PREFIX_EMAIL + "UNKNOWN";

        assertParseSuccess(parser, NAME_DESC_BOB + PHONE_DESC_BOB + emailDescription + ADDRESS_DESC_BOB,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_invalidEmail_messageExplainsAtRequirement() {
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + " " + PREFIX_EMAIL + "invalid-email"
                + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_EMAIL_ADDRESS, false));
    }

    @Test
    public void parse_unknownPhone_success() {
        Person expectedPerson = new PersonBuilder(BOB).withPhone("Unknown").withTags().build();
        String phoneDescription = " " + PREFIX_PHONE + "Unknown";

        assertParseSuccess(parser, NAME_DESC_BOB + phoneDescription + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
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
    public void parse_punctuationInFields_success() {
        String name = "ACME p/Research";
        String phone = "+65 (123)#45";
        String email = "person+tag@example.com?backup";
        String address = "10, Main St. #2/3";
        Person expectedPerson = new PersonBuilder(BOB).withName(name).withPhone(phone).withEmail(email)
                .withAddress(address).withTags().build();
        String userInput = " " + PREFIX_NAME + "\"" + name + "\" " + PREFIX_PHONE + "\"" + phone + "\" "
                + PREFIX_EMAIL + email + " " + PREFIX_ADDRESS + "\"" + address + "\"";

        assertParseSuccess(parser, userInput, new AddCommand(expectedPerson));
    }

    // Boundary value: every required field is exactly MAX_FIELD_LENGTH code points long and remains valid.
    @Test
    public void parse_maxLengthFields_success() {
        String name = "N".repeat(StringUtil.MAX_FIELD_LENGTH);
        String phone = "1".repeat(StringUtil.MAX_FIELD_LENGTH);
        String email = "e".repeat(StringUtil.MAX_FIELD_LENGTH - 1) + "@";
        String address = "A".repeat(StringUtil.MAX_FIELD_LENGTH);
        String userInput = " " + PREFIX_NAME + name + " " + PREFIX_PHONE + phone + " " + PREFIX_EMAIL + email
                + " " + PREFIX_ADDRESS + address;
        Person expectedPerson = new PersonBuilder(BOB).withName(name).withPhone(phone).withEmail(email)
                .withAddress(address).withTags().build();

        assertParseSuccess(parser, userInput, new AddCommand(expectedPerson));
    }

    // Boundary value: one code point beyond the maximum invalidates each person field partition.
    @Test
    public void parse_fieldsBeyondMaximumLength_failure() {
        String tooLongName = "N".repeat(StringUtil.MAX_FIELD_LENGTH + 1);
        String tooLongPhone = "1".repeat(StringUtil.MAX_FIELD_LENGTH + 1);
        String tooLongEmail = "e".repeat(StringUtil.MAX_FIELD_LENGTH) + "@";
        String tooLongAddress = "A".repeat(StringUtil.MAX_FIELD_LENGTH + 1);

        assertParseFailure(parser, " " + PREFIX_NAME + tooLongName + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_NAME_FIELD, false));
        assertParseFailure(parser, NAME_DESC_BOB + " " + PREFIX_PHONE + tooLongPhone + EMAIL_DESC_BOB
                        + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_PHONE_NUMBER, false));
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + " " + PREFIX_EMAIL + tooLongEmail
                        + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_EMAIL_ADDRESS, false));
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + " " + PREFIX_ADDRESS
                        + tooLongAddress,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_ADDRESS_FIELD, false));
    }

    // EP: A missing required prefix is distinct from a present prefix with an empty value.
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
        // Boundary value: an empty value is rejected and displays the UNKNOWN suggestion.
        assertParseFailure(parser, " " + PREFIX_NAME + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_NAME_FIELD, true));
        assertParseFailure(parser, NAME_DESC_BOB + " " + PREFIX_PHONE + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_PHONE_NUMBER, true));
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + " " + PREFIX_EMAIL + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_EMAIL_ADDRESS, true));
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + " " + PREFIX_ADDRESS,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_ADDRESS_FIELD, true));
    }

    // EP: Invalid values are partitioned into control characters, unsupported semantic values, invalid tags,
    // and a non-empty preamble.
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
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_ADDRESS_FIELD, false));

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

    @Test
    public void parse_semanticInvalidValue_failure() {
        // Boundary value: punctuation-only names and phone numbers contain no letter or digit.
        assertParseFailure(parser, " " + PREFIX_NAME + "!!!" + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_NAME_FIELD, false));
        assertParseFailure(parser, NAME_DESC_BOB + " " + PREFIX_PHONE + "+++" + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_PHONE_NUMBER, false));
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + " " + PREFIX_EMAIL + "not-an-email"
                + ADDRESS_DESC_BOB,
                expectedInvalidFieldMessage(AddCommand.MESSAGE_INVALID_EMAIL_ADDRESS, false));
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
