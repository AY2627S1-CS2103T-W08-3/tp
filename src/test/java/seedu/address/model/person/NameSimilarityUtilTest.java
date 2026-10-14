package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class NameSimilarityUtilTest {
    @Test
    public void normalizeName_trimsCollapsesWhitespaceAndIgnoresCase() {
        assertEquals("john smith", NameSimilarityUtil.normalizeName("  John   Smith  "));
    }

    @Test
    public void calculateSimilarity_bothEmpty_returnsZero() {
        assertEquals(0.0, NameSimilarityUtil.calculateSimilarity("", ""));
    }

    @Test
    public void findMostSimilarName_exactMatchAfterNormalisation_returnsEmpty() {
        Person existingPerson = new PersonBuilder().withName("John Smith").build();

        assertEquals(Optional.empty(), NameSimilarityUtil.findMostSimilarName(
                "JOHN SMITH", List.of(existingPerson), null));
    }

    @Test
    public void findMostSimilarName_extraSpaces_returnsExistingName() {
        Person existingPerson = new PersonBuilder().withName("John Smith").build();

        assertEquals(Optional.of("John Smith"), NameSimilarityUtil.findMostSimilarName(
                "John  Smith", List.of(existingPerson), null));
    }

    @Test
    public void findMostSimilarName_oneCharacterInsertion_returnsExistingName() {
        Person existingPerson = new PersonBuilder().withName("John Smith").build();

        assertEquals(Optional.of("John Smith"), NameSimilarityUtil.findMostSimilarName(
                "John Smmith", List.of(existingPerson), null));
    }

    @Test
    public void findMostSimilarName_oneCharacterDeletion_returnsExistingName() {
        Person existingPerson = new PersonBuilder().withName("John Smith").build();

        assertEquals(Optional.of("John Smith"), NameSimilarityUtil.findMostSimilarName(
                "Jon Smith", List.of(existingPerson), null));
    }

    @Test
    public void findMostSimilarName_oneCharacterSubstitution_returnsExistingName() {
        Person existingPerson = new PersonBuilder().withName("John Smith").build();

        assertEquals(Optional.of("John Smith"), NameSimilarityUtil.findMostSimilarName(
                "John Smyth", List.of(existingPerson), null));
    }

    @Test
    public void findMostSimilarName_clearlyDifferentName_returnsEmpty() {
        Person existingPerson = new PersonBuilder().withName("John Smith").build();

        assertEquals(Optional.empty(), NameSimilarityUtil.findMostSimilarName(
                "Zachary Zimmer", List.of(existingPerson), null));
    }

    @Test
    public void findMostSimilarName_shortOrEmptyInput_returnsEmpty() {
        Person existingPerson = new PersonBuilder().withName("John Smith").build();

        assertEquals(Optional.empty(), NameSimilarityUtil.findMostSimilarName("Jo", List.of(existingPerson), null));
        assertEquals(Optional.empty(), NameSimilarityUtil.findMostSimilarName("", List.of(existingPerson), null));
    }

    @Test
    public void findMostSimilarName_shortExistingName_returnsEmpty() {
        Person existingPerson = new PersonBuilder().withName("Jo").build();

        assertEquals(Optional.empty(), NameSimilarityUtil.findMostSimilarName(
                "John", List.of(existingPerson), null));
    }

    @Test
    public void findMostSimilarName_shortSimilarValues_requireStrongerMatch() {
        Person existingPerson = new PersonBuilder().withName("abc").build();

        assertEquals(Optional.empty(), NameSimilarityUtil.findMostSimilarName(
                "abd", List.of(existingPerson), null));
    }

    @Test
    public void findMostSimilarName_multipleCandidates_returnsBestCandidate() {
        Person closeCandidate = new PersonBuilder().withName("John Smyth").build();
        Person lessCloseCandidate = new PersonBuilder().withName("Jon Smith").build();

        assertEquals(Optional.of("John Smyth"), NameSimilarityUtil.findMostSimilarName(
                "Jon Smyth", List.of(closeCandidate, lessCloseCandidate), null));
    }

    @Test
    public void findMostSimilarName_atThresholdWarns_belowThresholdDoesNotWarn() {
        String baseName = "abcdefghijklmnopqrst";
        Person existingPerson = new PersonBuilder().withName(baseName).build();
        String atThreshold = "abcxefghixklmnopqrxt";
        String belowThreshold = "abcxefghixklmnopqrxu";

        assertTrue(NameSimilarityUtil.calculateSimilarity(atThreshold, baseName)
                >= NameSimilarityUtil.SIMILARITY_THRESHOLD);
        assertEquals(Optional.of(baseName), NameSimilarityUtil.findMostSimilarName(
                atThreshold, List.of(existingPerson), null));
        assertTrue(NameSimilarityUtil.calculateSimilarity(belowThreshold, baseName)
                < NameSimilarityUtil.SIMILARITY_THRESHOLD);
        assertEquals(Optional.empty(), NameSimilarityUtil.findMostSimilarName(
                belowThreshold, List.of(existingPerson), null));
    }

    @Test
    public void findMostSimilarName_excludedPerson_returnsEmpty() {
        Person existingPerson = new PersonBuilder().withName("Alice Pauline").build();

        assertEquals(Optional.empty(), NameSimilarityUtil.findMostSimilarName(
                "Alice Paulin", List.of(existingPerson), existingPerson));
    }
}
