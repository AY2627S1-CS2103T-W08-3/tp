package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.SortOrder;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the required lexicographical direction for a sort command.
 */
public class SortCommandParser implements Parser<SortCommand> {

    private static final Logger logger = LogsCenter.getLogger(SortCommandParser.class);

    /**
     * Parses exactly one case-insensitive direction after trimming surrounding whitespace.
     *
     * @throws ParseException If the direction is missing or unsupported, or extra arguments are provided.
     * @throws NullPointerException If {@code args} is null.
     */
    @Override
    public SortCommand parse(String args) throws ParseException {
        requireNonNull(args, "Sort arguments must not be null");
        return switch (args.trim().toLowerCase(Locale.ROOT)) {
            case "asc" -> new SortCommand(SortOrder.ASCENDING);
            case "desc" -> new SortCommand(SortOrder.DESCENDING);
            default -> {
                logger.fine("Rejected sort command: expected exactly asc or desc");
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
            }
        };
    }
}
