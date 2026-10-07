package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.NameOrTagContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + trimmedArgs.replaceAll("\\s+", " "), PREFIX_TAG);
        String nameArgs = arguments.getPreamble();
        List<String> nameKeywords = nameArgs.isEmpty() ? List.of() : List.of(nameArgs.split("\\s+"));
        List<String> tagKeywords = arguments.getAllValues(PREFIX_TAG);
        for (String tagKeyword : tagKeywords) {
            ParserUtil.parseTag(tagKeyword);
        }

        if (tagKeywords.isEmpty()) {
            return new FindCommand(new NameContainsKeywordsPredicate(nameKeywords));
        }
        return new FindCommand(new NameOrTagContainsKeywordsPredicate(nameKeywords, tagKeywords));
    }

}
