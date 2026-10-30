package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
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
                ArgumentTokenizer.tokenizeWithQuotedValues(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                        PREFIX_ADDRESS, PREFIX_TAG, PREFIX_REMARK);

        String nameValue = getFieldValue(argMultimap, PREFIX_NAME, AddCommand.MESSAGE_INVALID_NAME_FIELD);
        String phoneValue = getFieldValue(argMultimap, PREFIX_PHONE, AddCommand.MESSAGE_INVALID_PHONE_NUMBER);
        String emailValue = getFieldValue(argMultimap, PREFIX_EMAIL, AddCommand.MESSAGE_INVALID_EMAIL_ADDRESS);
        String addressValue = getFieldValue(argMultimap, PREFIX_ADDRESS, AddCommand.MESSAGE_INVALID_ADDRESS_FIELD);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_REMARK);
        Name name = parseName(nameValue);
        Phone phone = parsePhone(phoneValue);
        Email email = parseEmail(emailValue);
        Address address = parseAddress(addressValue);
        Set<Tag> tagList = parseTags(argMultimap.getAllValues(PREFIX_TAG));
        Remark remark = ParserUtil.parseRemark(argMultimap.getValue(PREFIX_REMARK).orElse(""));

        Person person = new Person(name, phone, email, address, remark, tagList);

        return new AddCommand(person);
    }

    /**
     * Parses the name field and reports the field that caused an error.
     */
    private static Name parseName(String name) throws ParseException {
        try {
            return ParserUtil.parseName(name);
        } catch (ParseException exception) {
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_NAME_FIELD, exception);
        }
    }

    /**
     * Parses the phone field and reports the field that caused an error.
     */
    private static Phone parsePhone(String phone) throws ParseException {
        try {
            return ParserUtil.parsePhone(phone);
        } catch (ParseException exception) {
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_PHONE_NUMBER, exception);
        }
    }

    /**
     * Parses the email field and reports the field that caused an error.
     */
    private static Email parseEmail(String email) throws ParseException {
        try {
            return ParserUtil.parseEmail(email);
        } catch (ParseException exception) {
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_EMAIL_ADDRESS, exception);
        }
    }

    /**
     * Parses the address field and reports the field that caused an error.
     */
    private static Address parseAddress(String address) throws ParseException {
        try {
            return ParserUtil.parseAddress(address);
        } catch (ParseException exception) {
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_ADDRESS_FIELD, exception);
        }
    }

    /**
     * Parses tags and reports the tag field when a tag is invalid.
     */
    private static Set<Tag> parseTags(List<String> values) throws ParseException {
        try {
            return ParserUtil.parseTags(values);
        } catch (ParseException exception) {
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_TAG, exception);
        }
    }

    /**
     * Returns a required field value, reporting the field when it is not present.
     */
    private static String getFieldValue(ArgumentMultimap argumentMultimap, Prefix prefix, String fieldMessage)
            throws ParseException {
        return argumentMultimap.getValue(prefix)
                .orElseThrow(() -> createInvalidFieldException(fieldMessage, null));
    }

    /**
     * Creates a field-specific parse error while retaining the add command usage guidance.
     */
    private static ParseException createInvalidFieldException(String fieldMessage, ParseException cause) {
        String message = fieldMessage + "\n" + AddCommand.MESSAGE_USAGE;
        return cause == null ? new ParseException(message) : new ParseException(message, cause);
    }

}
