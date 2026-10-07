package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a person's name matches any name keyword or a tag matches any complete tag keyword.
 * Both kinds of matching are case insensitive.
 */
public class NameOrTagContainsKeywordsPredicate implements Predicate<Person> {
    private final NameContainsKeywordsPredicate namePredicate;
    private final List<String> tagKeywords;

    /**
     * Creates an OR search using name keywords and complete tag keywords.
     */
    public NameOrTagContainsKeywordsPredicate(List<String> nameKeywords, List<String> tagKeywords) {
        this.namePredicate = new NameContainsKeywordsPredicate(List.copyOf(nameKeywords));
        this.tagKeywords = List.copyOf(tagKeywords);
    }

    @Override
    public boolean test(Person person) {
        return namePredicate.test(person) || person.getTags().stream()
                .anyMatch(tag -> tagKeywords.stream().anyMatch(keyword -> tag.tagName.equalsIgnoreCase(keyword)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof NameOrTagContainsKeywordsPredicate otherPredicate)) {
            return false;
        }
        return namePredicate.equals(otherPredicate.namePredicate) && tagKeywords.equals(otherPredicate.tagKeywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("namePredicate", namePredicate).add("tagKeywords", tagKeywords).toString();
    }
}
