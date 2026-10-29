package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Remark;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG);

        // Check required fields before the preamble so a misplaced or missing field is identified precisely.
        getFieldValue(argMultimap, PREFIX_NAME, AddCommand.MESSAGE_INVALID_NAME_FIELD);
        getFieldValue(argMultimap, PREFIX_PHONE, AddCommand.MESSAGE_INVALID_PHONE_NUMBER);
        getFieldValue(argMultimap, PREFIX_EMAIL, AddCommand.MESSAGE_INVALID_EMAIL_ADDRESS);
        getFieldValue(argMultimap, PREFIX_ADDRESS, AddCommand.MESSAGE_INVALID_ADDRESS_FIELD);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);
        Name name = parseName(argMultimap);
        Phone phone = parsePhone(argMultimap);
        Email email = parseEmail(argMultimap);
        Address address = parseAddress(argMultimap);
        Set<Tag> tagList = parseTags(argMultimap);
        Remark remark = new Remark("");

        Person person = new Person(name, phone, email, address, remark, tagList);

        return new AddCommand(person);
    }

    /**
     * Parses the name field and reports the field that caused an error.
     */
    private static Name parseName(ArgumentMultimap argumentMultimap) throws ParseException {
        String name = getFieldValue(argumentMultimap, PREFIX_NAME, AddCommand.MESSAGE_INVALID_NAME_FIELD);
        try {
            return ParserUtil.parseName(name);
        } catch (ParseException exception) {
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_NAME_FIELD, name.isEmpty(), exception);
        }
    }

    /**
     * Parses the phone field and reports the field that caused an error.
     */
    private static Phone parsePhone(ArgumentMultimap argumentMultimap) throws ParseException {
        String phone = getFieldValue(argumentMultimap, PREFIX_PHONE, AddCommand.MESSAGE_INVALID_PHONE_NUMBER);
        try {
            return ParserUtil.parsePhone(phone);
        } catch (ParseException exception) {
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_PHONE_NUMBER, phone.isEmpty(), exception);
        }
    }

    /**
     * Parses the email field and reports the field that caused an error.
     */
    private static Email parseEmail(ArgumentMultimap argumentMultimap) throws ParseException {
        String email = getFieldValue(argumentMultimap, PREFIX_EMAIL, AddCommand.MESSAGE_INVALID_EMAIL_ADDRESS);
        try {
            return ParserUtil.parseEmail(email);
        } catch (ParseException exception) {
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_EMAIL_ADDRESS, email.isEmpty(), exception);
        }
    }

    /**
     * Parses the address field and reports the field that caused an error.
     */
    private static Address parseAddress(ArgumentMultimap argumentMultimap) throws ParseException {
        String address = getFieldValue(argumentMultimap, PREFIX_ADDRESS, AddCommand.MESSAGE_INVALID_ADDRESS_FIELD);
        try {
            return ParserUtil.parseAddress(address);
        } catch (ParseException exception) {
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_ADDRESS_FIELD, address.isEmpty(), exception);
        }
    }

    /**
     * Parses tags and reports the tag field when a tag is invalid.
     */
    private static Set<Tag> parseTags(ArgumentMultimap argumentMultimap) throws ParseException {
        try {
            return ParserUtil.parseTags(argumentMultimap.getAllValues(PREFIX_TAG));
        } catch (ParseException exception) {
            boolean hasEmptyTag = argumentMultimap.getAllValues(PREFIX_TAG).stream().anyMatch(String::isEmpty);
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_TAG, hasEmptyTag, exception);
        }
    }

    /**
     * Returns a required field value, reporting the field when it is not present.
     */
    private static String getFieldValue(ArgumentMultimap argumentMultimap, Prefix prefix, String fieldMessage)
            throws ParseException {
        return argumentMultimap.getValue(prefix)
                .orElseThrow(() -> createInvalidFieldException(fieldMessage, true, null));
    }

    /**
     * Creates a field-specific parse error while retaining the add command usage guidance.
     */
    private static ParseException createInvalidFieldException(String fieldMessage, boolean hasEmptyValue,
            ParseException cause) {
        String usage = AddCommand.MESSAGE_USAGE;
        if (hasEmptyValue) {
            int firstLineEnd = usage.indexOf('\n');
            usage = usage.substring(0, firstLineEnd + 1) + AddCommand.MESSAGE_UNKNOWN_INFORMATION + "\n"
                    + usage.substring(firstLineEnd + 1);
        }
        String message = fieldMessage + "\n" + usage;
        return cause == null ? new ParseException(message) : new ParseException(message, cause);
    }

}
