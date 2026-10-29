package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    @BeforeAll
    public static void setUpJavaFx() {
        Platform.startup(() -> { });
    }

    @Test
    public void constructor_createsPersonCard() {
        PersonCard personCard = new PersonCard(ALICE, 1);

        assertEquals(ALICE, personCard.person);
    }

    @Test
    public void constructor_withEmptyRemark_createsPersonCard() {
        Person personWithEmptyRemark = new PersonBuilder(ALICE)
                .withRemark("")
                .build();

        PersonCard personCard = new PersonCard(personWithEmptyRemark, 1);

        assertEquals(personWithEmptyRemark, personCard.person);
    }
}
