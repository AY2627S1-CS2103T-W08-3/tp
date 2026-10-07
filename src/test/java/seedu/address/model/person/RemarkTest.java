package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    @Test
    public void equals() {
        Remark remark = new Remark("A remark");

        assertTrue(remark.equals(remark));
        assertFalse(remark.equals(null));
        assertFalse(remark.equals("A remark"));
        assertTrue(remark.equals(new Remark("A remark")));
    }

    @Test
    public void hashCode_sameValues_returnsSameHashCode() {
        assertEquals(new Remark("A remark").hashCode(), new Remark("A remark").hashCode());
    }
}
