package seedu.address.ui;

import java.util.Comparator;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
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
                .forEach(tag -> {
                    Label tagLabel = new Label(tag.tagName);
                    configureClickToCopy(tagLabel, tag.tagName);
                    tags.getChildren().add(tagLabel);
                });
        configureCopyMenu();
        configureClickToCopy(uuid, person.getShortUuid());
    }

    /**
     * Copies the underlying value on a left click, without any display decoration.
     */
    private static void configureClickToCopy(Label label, String value) {
        label.setCursor(Cursor.HAND);
        label.setTooltip(new Tooltip("Click to copy: " + value));
        label.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                copyToClipboard(value);
                event.consume();
            }
        });
    }

    /**
     * Offers read-only clipboard actions without changing the client or the current list.
     */
    private void configureCopyMenu() {
        ContextMenu copyMenu = new ContextMenu();
        copyMenu.getItems().addAll(
                createCopyItem("Copy name", person.getName().fullName),
                createCopyItem("Copy phone", person.getPhone().value),
                createCopyItem("Copy email", person.getEmail().value),
                createCopyItem("Copy address", person.getAddress().value),
                createCopyItem("Copy remark", person.getRemark().value),
                createCopyItem("Copy client ID", person.getShortUuid()),
                createCopyItem("Copy full UUID", person.getUuid().toString()),
                new SeparatorMenuItem(),
                createCopyItem("Copy all details", formatContactDetails()));
        cardPane.setOnContextMenuRequested(event -> {
            copyMenu.show(cardPane, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        uuid.setTooltip(new Tooltip("Right-click this card to copy client details."));
        cardPane.setAccessibleHelp("Open the context menu to copy client details.");
    }

    /**
     * Creates a copy action, disabling it when the optional field is empty.
     */
    private static MenuItem createCopyItem(String title, String value) {
        MenuItem item = new MenuItem(title);
        item.setDisable(value.isEmpty());
        item.setOnAction(event -> copyToClipboard(value));
        return item;
    }

    /**
     * Places plain text on the system clipboard.
     */
    private static void copyToClipboard(String value) {
        ClipboardContent content = new ClipboardContent();
        content.putString(value);
        Clipboard.getSystemClipboard().setContent(content);
    }

    /**
     * Formats a client as plain text with stable tag ordering for pasting elsewhere.
     */
    private String formatContactDetails() {
        String tagNames = person.getTags().stream()
                .map(tag -> tag.tagName)
                .sorted()
                .collect(Collectors.joining(", "));
        return String.join(System.lineSeparator(),
                "Name: " + person.getName().fullName,
                "Phone: " + person.getPhone().value,
                "Email: " + person.getEmail().value,
                "Address: " + person.getAddress().value,
                "Remark: " + person.getRemark().value,
                "Tags: " + tagNames,
                "Client ID: " + person.getUuid());
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
