package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {
    public static final String MESSAGE_INVALID_NAME_FIELD = "Invalid name field";
    public static final String MESSAGE_INVALID_PHONE_NUMBER = "Invalid phone number";
    public static final String MESSAGE_INVALID_EMAIL_ADDRESS =
            "Invalid email address: @ is required unless the value is UNKNOWN";
    public static final String MESSAGE_INVALID_ADDRESS_FIELD = "Invalid address field";
    public static final String MESSAGE_INVALID_TAG = "Invalid tag";
    public static final String MESSAGE_UNKNOWN_INFORMATION =
            "If information currently unknown, feel free to type UNKNOWN into the field";
    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the address book. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_ADDRESS + "ADDRESS "
            + "[" + PREFIX_TAG + "TAG]... "
            + "[" + PREFIX_REMARK + "REMARK]\n"
            + MESSAGE_UNKNOWN_INFORMATION + "\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "1234 5678 (HP) 1111-3333 (Office) "
            + PREFIX_EMAIL + "johnd@example.com (Work) jane@example.com (Personal) "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney "
            + PREFIX_REMARK + "Likes to swim.";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This name already exists in the address book. "
            + "Add more details to the name.";
    private static final Logger logger = LogsCenter.getLogger(AddCommand.class);
    private static final String UNKNOWN_NAME = "UNKNOWN";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd) && !UNKNOWN_NAME.equals(toAdd.getName().fullName)) {
            logger.info("Add command rejected because the person already exists: " + toAdd.getName());
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        String warning = Messages.getSimilarNameWarning(toAdd, model.getAddressBook().getPersonList(), null);
        model.addPerson(toAdd);
        logger.info("Added person with UUID " + toAdd.getUuid());
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)) + warning);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
