package seedu.address.logic.parser;

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
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no UUID specified
        assertParseFailure(parser, NAME_DESC_AMY, expectedUuidError(EditCommand.MESSAGE_MISSING_UUID));

        // no field specified
        assertParseFailure(parser, "aaaaaaa", EditCommand.MESSAGE_NOT_EDITED);

        // no UUID and no field specified
        assertParseFailure(parser, "", expectedUuidError(EditCommand.MESSAGE_MISSING_UUID));
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // invalid UUID characters
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, expectedUuidError(EditCommand.MESSAGE_INVALID_UUID));

        // UUID that is too short
        assertParseFailure(parser, "0" + NAME_DESC_AMY, expectedUuidError(EditCommand.MESSAGE_INVALID_UUID));

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", expectedUuidError(EditCommand.MESSAGE_INVALID_UUID));

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", expectedUuidError(EditCommand.MESSAGE_INVALID_UUID));

        // UUID prefix that is too long after dashes are removed
        assertParseFailure(parser, "1234567890abcdef1234567890abcdef0" + NAME_DESC_AMY,
                expectedUuidError(EditCommand.MESSAGE_INVALID_UUID));
    }

    @Test
    public void parse_dashedFullUuid_success() {
        String dashedUuid = "ABCDEF12-3456-7890-abcd-ef1234567890";
        String normalizedUuid = "abcdef1234567890abcdef1234567890";
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();

        assertParseSuccess(parser, dashedUuid + NAME_DESC_AMY, new EditCommand(normalizedUuid, descriptor));
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "aaaaaaa" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "aaaaaaa" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "aaaaaaa" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "aaaaaaa" + INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parser, "aaaaaaa" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parser, "aaaaaaa" + INVALID_PHONE_DESC + EMAIL_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Person} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "aaaaaaa" + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "aaaaaaa" + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "aaaaaaa" + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "aaaaaaa" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_ADDRESS_AMY
                        + VALID_PHONE_AMY,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        String targetUuid = "bbbbbbb";
        String userInput = targetUuid + PHONE_DESC_BOB + TAG_DESC_HUSBAND
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + NAME_DESC_AMY + TAG_DESC_FRIEND;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
        EditCommand expectedCommand = new EditCommand(targetUuid, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_punctuationInFields_success() {
        String name = "ACME p/Research";
        String phone = "+65 (123)#45";
        String email = "person+tag@example.com?backup";
        String address = "10, Main St. #2/3";
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(name).withPhone(phone)
                .withEmail(email).withAddress(address).build();
        String targetUuid = "aaaaaaa";
        String userInput = targetUuid + " " + PREFIX_NAME + "\"" + name + "\" "
                + PREFIX_PHONE + "\"" + phone + "\" " + PREFIX_EMAIL + email + " " + PREFIX_ADDRESS + "\""
                + address + "\"";

        assertParseSuccess(parser, userInput, new EditCommand(targetUuid, descriptor));
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        String targetUuid = "aaaaaaa";
        String userInput = targetUuid + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetUuid, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        String targetUuid = "ccccccc";
        String userInput = targetUuid + NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetUuid, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetUuid + PHONE_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(targetUuid, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetUuid + EMAIL_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditCommand(targetUuid, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = targetUuid + ADDRESS_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withAddress(VALID_ADDRESS_AMY).build();
        expectedCommand = new EditCommand(targetUuid, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetUuid + TAG_DESC_FRIEND;
        descriptor = new EditPersonDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditCommand(targetUuid, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // remark
        userInput = targetUuid + " " + PREFIX_REMARK + "Likes to swim.";
        descriptor = new EditPersonDescriptorBuilder().withRemark("Likes to swim.").build();
        expectedCommand = new EditCommand(targetUuid, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // empty remark clears the existing remark
        userInput = targetUuid + " " + PREFIX_REMARK;
        descriptor = new EditPersonDescriptorBuilder().withRemark("").build();
        expectedCommand = new EditCommand(targetUuid, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        String targetUuid = "aaaaaaa";
        String userInput = targetUuid + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = targetUuid + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple valid fields repeated
        userInput = targetUuid + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY
                + TAG_DESC_FRIEND + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY + TAG_DESC_FRIEND
                + PHONE_DESC_BOB + ADDRESS_DESC_BOB + EMAIL_DESC_BOB + TAG_DESC_HUSBAND;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        // multiple invalid values
        userInput = targetUuid + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));
    }

    @Test
    public void parse_resetTags_success() {
        String targetUuid = "ccccccc";
        String userInput = targetUuid + TAG_EMPTY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetUuid, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    private static String expectedUuidError(String message) {
        return message + "\n" + EditCommand.MESSAGE_USAGE;
    }
}
