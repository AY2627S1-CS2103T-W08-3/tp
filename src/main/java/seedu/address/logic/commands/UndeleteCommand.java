package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Restores the most recently deleted person to the position they were deleted from.
 */
public class UndeleteCommand extends Command {

    public static final String COMMAND_WORD = "undelete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Restores the most recently deleted person that has not been restored yet.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Restored person: %1$s";
    public static final String MESSAGE_NO_DELETED_PERSONS = "No deleted persons to restore.";
    public static final String MESSAGE_DUPLICATE_PERSON =
            "Cannot undelete %1$s: a person with the same name already exists.\n"
            + "Rename that person with edit, then run undelete again.";

    private static final Logger logger = LogsCenter.getLogger(UndeleteCommand.class);

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.hasDeletedPersons()) {
            logger.info("Undelete requested with no deleted persons");
            throw new CommandException(MESSAGE_NO_DELETED_PERSONS);
        }

        Person personToRestore = model.getLastDeletedPerson();
        if (model.hasPerson(personToRestore)) {
            logger.info("Undelete refused, as " + personToRestore.getName() + " already exists");
            throw new CommandException(String.format(MESSAGE_DUPLICATE_PERSON, personToRestore.getName()));
        }

        model.restoreLastDeletedPerson();
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(personToRestore)));
    }

    @Override
    public boolean equals(Object other) {
        // UndeleteCommand has no fields, so all instances are equal
        return other instanceof UndeleteCommand;
    }
}
