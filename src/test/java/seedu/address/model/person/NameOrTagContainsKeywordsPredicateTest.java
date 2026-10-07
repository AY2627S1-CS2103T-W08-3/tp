package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class NameOrTagContainsKeywordsPredicateTest {
    @Test
    public void test_matchesNameOrAnyCompleteTag() {
        NameOrTagContainsKeywordsPredicate predicate = new NameOrTagContainsKeywordsPredicate(
                List.of("Alice"), List.of("friends", "colleagues"));

        assertTrue(predicate.test(new PersonBuilder().withName("Alice Davidson").build()));
        assertTrue(predicate.test(new PersonBuilder().withName("Alison Richards").withTags("FRIENDS").build()));
        assertTrue(predicate.test(new PersonBuilder().withName("Bob Smith").withTags("colleagues").build()));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Richards").withTags("friends").build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Alison Davidson").withTags("friendship").build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Bob Smith").build()));
    }

    @Test
    public void test_tagKeywords_onlyMatchTags() {
        NameOrTagContainsKeywordsPredicate predicate = new NameOrTagContainsKeywordsPredicate(
                List.of(), List.of("friends"));

        assertFalse(predicate.test(new PersonBuilder().withName("Friends").build()));
        assertTrue(predicate.test(new PersonBuilder().withTags("friends").build()));
        assertFalse(predicate.test(new PersonBuilder().withTags("bestfriends").build()));
        assertFalse(predicate.test(new PersonBuilder().withTags("friend").build()));
    }

    @Test
    public void test_nameKeywords_onlyMatchNames() {
        NameOrTagContainsKeywordsPredicate predicate = new NameOrTagContainsKeywordsPredicate(
                List.of("Alice"), List.of());

        assertFalse(predicate.test(new PersonBuilder().withName("Alison").withTags("Alice").build()));
        assertTrue(predicate.test(new PersonBuilder().withName("ALICE Davidson").build()));
    }

    @Test
    public void equals() {
        NameOrTagContainsKeywordsPredicate predicate = new NameOrTagContainsKeywordsPredicate(
                List.of("Alice"), List.of("friends"));
        assertTrue(predicate.equals(predicate));
        assertEquals(predicate, new NameOrTagContainsKeywordsPredicate(List.of("Alice"), List.of("friends")));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals(new NameContainsKeywordsPredicate(List.of("Alice"))));
        assertFalse(predicate.equals(new NameOrTagContainsKeywordsPredicate(List.of("Bob"), List.of("friends"))));
        assertFalse(predicate.equals(new NameOrTagContainsKeywordsPredicate(List.of("Alice"), List.of("colleagues"))));
    }
}
