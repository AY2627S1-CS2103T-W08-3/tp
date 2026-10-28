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
import java.util.Optional;
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
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG,
                        PREFIX_REMARK);

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

    private static String getFieldValue(ArgumentMultimap argumentMultimap, Prefix prefix, String fieldMessage)
            throws ParseException {
        Optional<String> fieldValue = argumentMultimap.getValue(prefix);
        if (!fieldValue.isPresent()) {
            throw createInvalidFieldException(fieldMessage, null);
        }
        return fieldValue.get();
    }

    private static Name parseName(String value) throws ParseException {
        try {
            return ParserUtil.parseName(value);
        } catch (ParseException exception) {
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_NAME_FIELD, exception);
        }
    }

    private static Phone parsePhone(String value) throws ParseException {
        try {
            return ParserUtil.parsePhone(value);
        } catch (ParseException exception) {
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_PHONE_NUMBER, exception);
        }
    }

    private static Email parseEmail(String value) throws ParseException {
        try {
            return ParserUtil.parseEmail(value);
        } catch (ParseException exception) {
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_EMAIL_ADDRESS, exception);
        }
    }

    private static Address parseAddress(String value) throws ParseException {
        try {
            return ParserUtil.parseAddress(value);
        } catch (ParseException exception) {
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_ADDRESS_FIELD, exception);
        }
    }

    private static Set<Tag> parseTags(List<String> values) throws ParseException {
        try {
            return ParserUtil.parseTags(values);
        } catch (ParseException exception) {
            throw createInvalidFieldException(AddCommand.MESSAGE_INVALID_TAG, exception);
        }
    }

    private static ParseException createInvalidFieldException(String fieldMessage, ParseException cause) {
        String message = fieldMessage + "\n" + AddCommand.MESSAGE_USAGE;
        return cause == null ? new ParseException(message) : new ParseException(message, cause);
    }

}
