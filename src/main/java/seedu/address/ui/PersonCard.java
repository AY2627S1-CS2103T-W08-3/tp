package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import seedu.address.model.person.Person;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";
    private static final double TOOLTIP_MAX_WIDTH = 480;
    private static final Duration TOOLTIP_SHOW_DELAY = Duration.millis(300);

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label uuid;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private FlowPane tags;
    @FXML
    private Label remark;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        configureValueLabel(name, person.getName().fullName, false);
        name.setMinWidth(0);
        uuid.setText("#" + person.getShortUuid());
        uuid.setMinWidth(Region.USE_PREF_SIZE);
        uuid.setMaxWidth(Region.USE_PREF_SIZE);
        configureValueLabel(phone, person.getPhone().value, true);
        configureValueLabel(address, person.getAddress().value, true);
        configureValueLabel(email, person.getEmail().value, true);
        configureValueLabel(remark, person.getRemark().value, true);
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }

    /**
     * Configures a field label to display its complete value across multiple lines and on hover.
     */
    private static void configureValueLabel(Label label, String value, boolean fillsAvailableWidth) {
        label.setText(value);
        label.setWrapText(true);
        if (fillsAvailableWidth) {
            label.setMaxWidth(Double.MAX_VALUE);
        }

        if (!value.isEmpty()) {
            Tooltip tooltip = new Tooltip(value);
            tooltip.setWrapText(true);
            tooltip.setMaxWidth(TOOLTIP_MAX_WIDTH);
            tooltip.setShowDelay(TOOLTIP_SHOW_DELAY);
            label.setTooltip(tooltip);
        }
    }
}
