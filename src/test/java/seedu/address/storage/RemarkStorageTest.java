package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests remark persistence and loading existing files without remarks.
 */
public class RemarkStorageTest {
    @TempDir
    public Path tempDir;

    @Test
    public void saveAndRead_nonEmptyRemark_preserved() throws Exception {
        AddressBook book = new AddressBook();
        book.addPerson(new PersonBuilder().withRemark("Likes swimming").build());
        Path path = tempDir.resolve("addressbook.json");
        new JsonAddressBookStorage(path).saveAddressBook(book);
        assertTrue(java.nio.file.Files.readString(path).contains("Likes swimming"));
        assertEquals(book, new JsonAddressBookStorage(path).readAddressBook().orElseThrow());
    }

    @Test
    public void read_legacyPersonWithoutRemark_defaultsToEmpty() throws Exception {
        Person person = new PersonBuilder().build();
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(person));
        String legacyJson = json.replaceAll("\\s*\"remark\"\\s*:\\s*\"\"\\s*,", "");
        assertEquals(person, JsonUtil.fromJsonString(legacyJson, JsonAdaptedPerson.class).toModelType());
    }
}
