---
layout: page
title: Developer Guide
---
ClientConnect is a keyboard-driven desktop address book for insurance agents who manage large client portfolios. This
guide explains how ClientConnect is designed and implemented, for developers who want to maintain or extend it. To
learn how to use the app, see the [User Guide](UserGuide.html).

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* ClientConnect is based on [AddressBook-Level3](https://github.com/se-edu/addressbook-level3) (AB3) by the [SE-EDU initiative](https://se-education.org). Its code, tests, and documentation, including this Developer Guide and the User Guide, were adapted from AB3's ([AB3 Developer Guide](https://se-education.org/addressbook-level3/DeveloperGuide.html), [AB3 User Guide](https://se-education.org/addressbook-level3/UserGuide.html)).
* Third-party libraries used: [JavaFX](https://openjfx.io/) for the GUI, [Jackson](https://github.com/FasterXML/jackson) for reading and writing JSON data files, and [JUnit 5](https://junit.org/junit5/) for testing.
* The UML diagrams in this guide are created with [PlantUML](https://plantuml.com/).
* The similar-name warning compares names using the [Levenshtein distance](https://en.wikipedia.org/wiki/Levenshtein_distance), a standard edit distance algorithm.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in `docs/diagrams`. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2627S1-CS2103T-W08-3/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2627S1-CS2103T-W08-3/tp/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1a2b3c4`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103T-W08-3/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-W08-3/tp/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103T-W08-3/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

`PersonListPanel` renders each person using a `PersonCard`. The card also handles clipboard interactions
locally in the UI; copying does not execute a command or update the model or storage.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2627S1-CS2103T-W08-3/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1a2b3c4")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1a2b3c4` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifelines for `DeleteCommandParser` and `DeleteCommand` should end at their destroy markers (X), but due to a limitation of PlantUML, they continue to the end of the diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103T-W08-3/tp/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object). Each `Person` has a `UUID` that never changes, which commands such as `delete`, `edit`, and `remark` use to select a person, and an optional `Remark`.
* stores the _displayed_ list of `Person` objects. `ModelManager` wraps the address book's list in a `FilteredList`, which keeps the persons selected by the current filter (e.g. search results), and wraps that in a `SortedList`, which applies the current sort order. `Model#getFilteredPersonList()` exposes the sorted view as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the filter or order changes.
* stores the persons deleted during the current session in a stack of `DeletedPerson` entries, so that `undelete` can restore them (see [Undelete feature](#undelete-feature)). The stack is kept in memory only.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103T-W08-3/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Copying client details

The feature is implemented in `src/main/java/seedu/address/ui/PersonCard.java` using JavaFX clipboard and menu controls.

* `configureClickToCopy(Label, String)` adds a hand cursor, a tooltip showing the copied value, and a mouse-click handler.
  Only `MouseButton.PRIMARY` copies text, and the handled click is consumed. Regular tags use `Tag#tagName`,
  preserving their original capitalization. The client ID badge uses `Person#getShortUuid()`, so its displayed `#` is excluded.
* `configureCopyMenu()` attaches a `ContextMenu` to `cardPane`. A context-menu request opens the menu at the request's
  screen coordinates and consumes the event. The menu offers name, phone, email, address, remark, short ID, full UUID,
  and all-details actions. Right-clicking a tag or ID badge opens this menu without triggering its click-to-copy action.
* `createCopyItem(String, String)` creates each menu action and disables it if its value is empty, such as an empty remark.
* `copyToClipboard(String)` is shared by the click handlers and menu actions. It puts plain text in a `ClipboardContent`
  and passes it to `Clipboard.getSystemClipboard().setContent(...)`, replacing the existing clipboard contents.
* `formatContactDetails()` produces labelled lines in this order: name, phone, email, address, remark, tags, and client ID.
  It uses the platform line separator, joins sorted tag names with `, `, and includes the full UUID with dashes.
  Empty remarks and tag sets still have their labelled lines in the summary.

All handlers run on the JavaFX Application Thread. The clipboard actions use the values of the `Person` represented by
the card and leave client records, list filtering, and command feedback unchanged.

### Find command

The `find` command filters the displayed client list by name keywords and/or tags. The following sequence diagram shows a successful search using `find Alice t/friends`.

![Sequence diagram for finding clients by name or tag](images/FindSequenceDiagram.png)

1. `LogicManager` passes the command text to `AddressBookParser`, which delegates the arguments to `FindCommandParser`.
2. `FindCommandParser` trims and normalizes whitespace, then uses `ArgumentTokenizer` to separate name keywords in the preamble from `t/` tag values. It validates each tag using `ParserUtil.parseTag()`.
3. The parser creates a `NameContainsKeywordsPredicate` for a name-only search, or a `NameOrTagContainsKeywordsPredicate` when tags are supplied, and passes that predicate to a new `FindCommand`. Tag-only searches use the latter predicate with an empty name-keyword list.
4. `FindCommand.execute(model)` calls `Model.updateFilteredPersonList(predicate)`. `ModelManager` sets the predicate on its internal `FilteredList`, applying the search to the full client list. Names match any keyword as a case-insensitive substring; tags match any complete tag value, also ignoring case. These conditions are combined with **OR**, so the example matches clients whose names contain `Alice` or whose tags include `friends`.
5. The command reads the size of `Model.getFilteredPersonList()` and returns a `CommandResult` containing the number of matches, including zero when no clients match. The UI observes the filtered and sorted list and displays the matching clients.

Empty arguments or invalid tags cause a `ParseException` during parsing, before a `FindCommand` is created or the displayed list is changed. Finding clients changes the list filter without modifying client records. The diagram focuses on parsing and filtering; the usual `LogicManager` storage save after command execution is omitted. Destruction markers indicate when the temporary parser and command are no longer referenced in this interaction, rather than when Java garbage collection occurs. The `CommandResult` remains alive because it is returned to the caller.

### Remark command

The `remark` command adds, replaces, or removes the remark of one client. Like `delete` and `edit`, it selects the
client by UUID rather than by list index.

The high-level sequence diagram below shows the parsing path for `remark 1a2b3c4 r/Likes to swim.`. The parser creates
a command from the user's text and delegates the command's execution to a reference frame. The reference frame
abstracts the detailed model interaction shown in the second diagram.

![Remark command sequence diagram](images/RemarkSequenceDiagram.png)

The following diagram expands the `execute RemarkCommand` reference frame. The command obtains the target from the
complete address book, creates a replacement `Person`, updates the model's filtered list, and returns a
`CommandResult`.

![Remark command execution sequence diagram](images/RemarkCommandExecutionSequenceDiagram.png)

The following class diagrams show the classes involved in parsing and executing the command:

<img src="images/ParserClass.png" width="600"/>

`RemarkCommandParser` delegates tokenisation and remark validation to the shared parser utilities. It removes dashes
from the identifier and lowercases it, then checks that 7 to 32 hexadecimal characters remain. A short UUID typed from
a card (e.g. `1a2b3c4`) and a full UUID copied from the data file are therefore treated alike. A missing or invalid
identifier, a missing `r/` prefix, and a repeated `r/` prefix are reported as a `ParseException` before a command
reaches the model.

<img src="images/RemarkClass.png" width="600"/>

`RemarkCommand` treats a remark update as a replacement of the immutable `Person` value. It calls
`Model#getPersonsByUuidPrefix` to find every person whose UUID starts with the identifier, searching the full address
book rather than the displayed list. If exactly one person matches, it creates an edited `Person` carrying over the
unchanged fields and asks the model to replace the old value.

If no person matches, `RemarkCommand` throws a `CommandException` with the message `No person with UUID: <identifier>`.
If more than one person matches, the `CommandException` lists the full UUID and name of each match, so that the user
can enter more characters of the intended UUID. In both cases, no data is replaced. If the `r/` value is empty, the same
successful path stores an empty `Remark`, which removes the displayed remark without requiring a separate command.

### Add command

#### Implementation

The `add` command creates a new `Person` and stores it in the address book. It requires a name,
phone number, email address, and postal address. A remark (`r/`) is optional and may be supplied at
most once, while tags are optional and may be supplied more than once.

The main classes involved in the implementation are shown in the following class diagram:

<img src="images/AddCommandClassDiagram.png" width="650" />

The `add` command flow is split into the following sequence diagrams:

Parsing and validation:

<img src="images/AddCommandParsingSequenceDiagram.png" width="800" />

Execution and duplicate detection:

<img src="images/AddCommandDuplicateSequenceDiagram.png" width="800" />

Successful person addition:

<img src="images/AddCommandSuccessSequenceDiagram.png" width="800" />

How the `add` command works:

1. `LogicManager` passes the user input to `AddressBookParser`, which creates an
   `AddCommandParser` for the `add` command.
2. `AddCommandParser` tokenizes the input and checks that all required prefixes are present,
   that there is no non-empty preamble, and that non-tag prefixes are not repeated. The tokenizer
   ignores field-looking prefixes inside double-quoted values and rejects unmatched quotes.
3. `ParserUtil` parses each field. The `Name`, `Phone`, `Email`, `Address`, `Remark`, and `Tag`
   value classes validate their respective constraints. Name, phone, email, address, and remark
   values allow punctuation; name, phone, email, and address values must be non-blank,
   control-character-free, and at most 255 Unicode code points. Names and phone numbers must contain
   a letter or digit, while emails must contain `@` or be `UNKNOWN`. Missing required fields,
   repeated non-tag prefixes, and invalid field values produce field-specific parse errors while
   retaining the add usage guidance.
   When a required field is empty or missing, that guidance also reminds users that they can enter
   the uppercase value `UNKNOWN` when the information is currently unavailable.
   The parser creates a `Remark` from the trimmed `r/` value, or an empty `Remark` if `r/` is not
   supplied, then creates an `AddCommand` containing the new `Person`.
4. During execution, `AddCommand` asks the `Model` whether a person with the same name, ignoring
   letter case, already exists. If so, it throws a `CommandException` and does not modify the
   address book. The one exception is the exact placeholder name `UNKNOWN`, which users can enter
   for information they do not have yet. Persons named `UNKNOWN` are not treated as duplicates of
   each other, so several can be added.
5. Otherwise, it requests a similar-name warning from `Messages`, adds the person through the
   `Model`, logs the outcome, and returns a `CommandResult` containing the success message and any warning.

### Edit command

#### Implementation

The `edit` command updates one existing `Person`, identified by a UUID prefix, without changing
the person's UUID. It supports optional name, phone, email, address, remark, and tag fields. At least
one optional field must be supplied; tags replace the existing tag set, while `t/` clears it. A
remark replaces the existing remark, and `r/` clears it.

`EditCommandParser` tokenizes quoted values and recognizes the `n/`, `p/`, `e/`, `a/`, `r/`, and
`t/` prefixes. The target UUID is trimmed, normalized to lower case, and has dashes removed before
validation. It must contain between 7 and 32 hexadecimal characters, inclusive. The parser rejects
duplicate non-tag prefixes, missing UUIDs, invalid UUIDs, unmatched quotes, and commands that do not
specify a field to edit. The same punctuation, blank-value, control-character, length, and
field-specific content constraints used by add apply to editable person fields.

During execution, `EditCommand` searches the complete client list rather than only the currently
displayed filtered list. It rejects a UUID prefix that matches no person or more than one person,
creates a replacement with the original UUID, checks that the edited identity is not a duplicate,
and then updates the model and restores the unfiltered display. Name typo warnings are generated
for the edited name in the same way as for add.

### Sort command

The `sort` command displays all contacts lexicographically by name, ignoring capitalization.
It requires exactly one direction: `sort asc` selects ascending order, while `sort desc`
selects descending order. Missing directions, unsupported values, and extra arguments
produce a `ParseException` using `Messages.MESSAGE_INVALID_COMMAND_FORMAT` and
`SortCommand.MESSAGE_USAGE` before the displayed list is changed. This standardized error
retains the shared format-error prefix and lists both valid commands, `sort asc` and `sort desc`,
with their text-order directions.

#### Implementation

`AddressBookParser#parseCommand()` delegates sort arguments to `SortCommandParser#parse()`.
The parser trims surrounding whitespace, normalizes direction arguments with `Locale.ROOT`,
and creates a `SortCommand` with the corresponding
`SortOrder` value, either `ASCENDING` or `DESCENDING`. The command stores this required,
non-null direction in a final field.

When `LogicManager` calls `SortCommand#execute(model)`, the command selects a comparator
using `Person#getName().fullName` and `String.CASE_INSENSITIVE_ORDER`. Descending order
uses the reversed comparator. The command then calls these model operations in order:

1. `Model#updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS)` removes any existing filter
   so that all contacts are displayed.
2. `Model#updatePersonListComparator(comparator)` applies the requested lexicographical order.

`SortCommand#getSuccessMessage()` formats `MESSAGE_SUCCESS` with the selected direction,
so the returned `CommandResult` confirms ascending or descending text order.

`ModelManager` maintains a `FilteredList<Person>` over the address book's person list and
a `SortedList<Person>` over that filtered list. `ModelManager#getFilteredPersonList()`
exposes the sorted view to the UI. Updating its comparator notifies the observing
`PersonListPanel`, so the command does not call the UI directly.

Sorting changes the displayed order without rearranging the underlying address book.
The comparator remains active during subsequent additions, edits, and searches in the
same session. `list` shows all contacts while retaining the comparator. Restarting the
application restores the saved order because the comparator is not persisted.

#### Sequence diagram

The following diagram illustrates the successful execution of `sort asc`.
The object named `s` is the created `SortCommand`, `r` is its `CommandResult`, and `m`
is the `ModelManager` supplied through the `Model` interface.

![Sequence diagram for the successful sort asc command](images/SortSequenceDiagram.png)

The editable PlantUML source is [SortSequenceDiagram.puml](diagrams/SortSequenceDiagram.puml).
It uses the shared [diagram style](diagrams/style.puml).

The diagram shows `SortCommand#getComparator()` selecting the comparator for the requested direction.
Null checks, contact-count retrieval, assertions, logging, and subsequent storage
interactions are omitted to keep the diagram focused on parsing and sorting.
`sort desc` follows the same interaction sequence with `DESCENDING` and a reversed comparator.

The destruction markers indicate when the temporary `SortCommandParser` and `SortCommand`
instances become eligible for garbage collection, following the course's UML convention.
They do not indicate when Java actually reclaims the memory. `CommandResult` remains alive
because `LogicManager#execute()` returns it to its caller.

#### Defensive checks

`SortCommand` rejects a null sort order during construction
and a null model before execution. Invalid user arguments are handled by `ParseException`,
while assertions check internal postconditions: sorting preserves the contact count,
displays all contacts, and produces the requested order. Assertions require `-ea` when
running the application manually; normal input validation does not depend on assertions.
Diagnostic messages at `FINE` level record the sort direction, contact count, completion,
and rejected input format.

### Undelete feature

#### Implementation

The undelete mechanism is facilitated by `ModelManager`, which keeps a stack, `deletedPersons`, of the persons deleted during the current session. Each entry is a `DeletedPerson` record holding the deleted `Person` and the index in the address book that they were deleted from. `ModelManager` exposes the stack through three `Model` operations:

* `Model#hasDeletedPersons()` — Returns true if there is a deleted person that can be restored.
* `Model#getLastDeletedPerson()` — Returns the most recently deleted person, without restoring them.
* `Model#restoreLastDeletedPerson()` — Reinserts the most recently deleted person at their original index, removes them from the stack, and resets the filtered person list to show all persons.

`Model#deletePerson()` pushes an entry onto the stack after it removes the person, so every successful `delete` can be reversed. `clear` replaces the address book through `Model#setAddressBook()` instead, so it does not add to the stack.

Given below is an example usage scenario and how the undelete mechanism behaves at each step.

Step 1. The user launches the application. `deletedPersons` is empty, so `undelete` fails with an error.

Step 2. The user executes `delete 1a2b3c4` to delete Benson, the 2nd person in the address book. `Model#deletePerson()` pushes `(Benson, 1)` onto `deletedPersons`.

Step 3. The user executes `delete 0f9e8d7` to delete Alice, the 1st person. `Model#deletePerson()` pushes `(Alice, 0)`, which is now on top of `(Benson, 1)`.

Step 4. The user realises that deleting Alice was a mistake, and executes `undelete`. `UndeleteCommand` checks that there is a deleted person to restore, and that no person with the same identity as Alice already exists. It then calls `Model#restoreLastDeletedPerson()`, which reinserts Alice at index 0 and pops `(Alice, 0)` off the stack.

The following sequence diagram shows how the `undelete` command in step 4 goes through the `Logic` and `Model` components:

![UndeleteSequenceDiagram](images/UndeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifelines for `UndeleteCommandParser` and `UndeleteCommand` should end at their destroy markers (X), but due to a limitation of PlantUML, they continue to the end of the diagram.
</div>

Step 5. The user executes `undelete` again. Benson is reinserted at index 1, so the address book is back in its original order, and `deletedPersons` is empty again.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a person with the same name has been added since the delete (e.g. the user re-added Alice by hand), `UndeleteCommand` fails before calling `Model#restoreLastDeletedPerson()`, as restoring the deleted person would leave two persons with the same name. The entry stays on the stack, and the error message tells the user to rename the clashing person with `edit` and run `undelete` again.

</div>

<div markdown="span" class="alert alert-info">:information_source: **Note:** If persons were removed after the delete without going through `Model#deletePerson()` (e.g. by `clear`), the original index can be past the end of the list. `Model#restoreLastDeletedPerson()` then appends the person to the end instead.

</div>

`deletedPersons` exists only in memory. It is not saved by `Storage`, and it is not part of `ModelManager#equals()`, so it is lost when the app closes.

#### Design considerations

**Aspect: What is stored to restore a deleted person:**

* **Alternative 1 (current choice):** Store only the deleted person and their index.
  * Pros: Uses little memory, and does not affect any other command.
  * Cons: Only `delete` can be reversed.

* **Alternative 2:** Save a copy of the whole address book before every change, and restore the copy.
  * Pros: Can reverse any command.
  * Cons: Uses memory in proportion to the address book size for every command. Restoring a copy also reverses every command made after it, e.g. reversing a delete would also remove a person added afterwards.

**Aspect: Command word:**

* **Alternative 1 (current choice):** `undelete`.
  * Pros: The name states exactly what it reverses, so users do not expect it to reverse `add` or `edit`.
  * Cons: Users who know `undo` from other applications may try that first.

* **Alternative 2:** `undo`.
  * Pros: Familiar to most users.
  * Cons: Users would expect it to reverse every command, not just `delete`.

**Aspect: Where the restored person is placed:**

* **Alternative 1 (current choice):** At the index they were deleted from.
  * Pros: The list looks exactly as it did before the delete.
  * Cons: The index must be stored, and clamped when the list has since become shorter.

* **Alternative 2:** At the end of the list, like `add`.
  * Pros: Simpler.
  * Cons: The list order changes, and in a long list the restored person is harder to find.


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* Insurance advisory agent
* Oversees a large contact portfolio
* Highly comfortable with CLI-first, GUI-assisted applications
* Types quickly and intentionally minimises mouse usage
* Prefers dedicated, low latency desktop software over browser-based or mobile apps for productivity and lookup

**Value proposition**: A keyboard-driven, ultra-fast desktop address book that empowers insurance agents to manage and search through high volumes of client portfolios, eliminating mouse friction completely.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​      | I want to …​                                                  | So that I can…​                                                          |
| -------- |-----------------|------------------------------------------------------------------|-----------------------------------------------------------------------------|
| `* * *`  | insurance agent | quickly update client information                                | maintain accurate records of life changes that impact policy coverage needs |
| `* * *`  | insurance agent | sort clients alphabetically by name                              | efficiently structure my daily outreach                                     |
| `* * *`  | insurance agent | view a complete list of all my clients                           | scan key metrics and navigate quickly to specific profiles                  |
| `* * *`  | insurance agent | add a new client with their contact information                  | immediately capture leads and initiate the policy assessment process        |
| `* * *`  | insurance agent | search clients by name                                           | access their profile immediately during real time client interactions       |
| `* * *`  | insurance agent | search client by tag                                             | generate target lists for specialised policy reviews                        |
| `* * *`  | insurance agent | delete client contact                                            | keep my active directory free of outdated or redundant contacts             |
| `* * *`  | insurance agent | assign tags to clients                                           | categorise them according to risk profile and product interest              |
| `* *`    | insurance agent | add a remark to a client                                         | record notes from conversations, such as preferences or follow-ups          |
| `* *`    | insurance agent | be warned when a new client's name is similar to an existing one | avoid creating duplicate records because of a typo                          |
| `* *`    | insurance agent | link contacts                                                    | understand family and corporate policy ecosystems                           |
| `* *`    | insurance agent | link individual personal contact information to business details | manage both individual policies and corporate plans seamlessly              |
| `* *`    | insurance agent | restore a client I deleted by mistake                            | recover their details without re-entering them                              |
| `*`      | insurance agent | set reminders for important clients/urgent appointments          | be notified ahead of time for renewals, reviews, or appointments            |

### Use cases

(For all use cases below, the **System** is `ClientConnect` and the **Actor** is the `user` (an insurance agent), unless specified otherwise)

**Use case: UC1 - Add a new client**

**MSS**

1.  User requests to add a client by entering the name, phone, email, address, an optional remark and optional tags.
2.  ClientConnect validates the details.
3.  ClientConnect adds the client and shows a success message with the client's details.

    Use case ends.

**Extensions**

* 1a. The command format is invalid 

    * 1a1. ClientConnect shows an invalid command format error with the correct usage.
    * 1a2. User re-enters the command with the corrected format.

      Steps 1a1-1a2 are repeated until the command format is valid.
      Use case resumes at step 2.


* 2a. One or more field values are invalid (e.g. an email address without `@`, or a tag containing spaces).

    * 2a1. ClientConnect shows the error message for the first invalid field.
    * 2a2. User re-enters the command with a corrected value.

      Steps 2a1-2a2 are repeated until all values are valid.
      Use case resumes at step 3.

* 2b. A client with the same name already exists (case-insensitive, ignoring leading and trailing spaces), and the name is not the placeholder `UNKNOWN`.

    * 2b1. ClientConnect rejects the entry and shows "This name already exists in the address book. Add more details to the name."
    * 2b2. User decides to update the existing client instead (see UC4).

      Use case ends.

**Use case: UC2 - Find a client and retrieve their details during a call**

**MSS**

1.  User requests to find clients by entering one or more name keywords and/or tags (each tag with the `t/` prefix).
2.  ClientConnect shows the matching clients in the list and the number of clients listed.
3.  User reads the required details from the client's card.

    Use case ends.

**Extensions**

* 1a. No keyword is provided.

    * 1a1. ClientConnect shows an invalid command format error with the correct usage.
    * 1a2. User re-enters the command with at least one keyword.

      Steps 1a1-1a2 are repeated until a keyword is provided.
      Use case resumes at step 2.

* 2a. No clients match the keywords.

    * 2a1. ClientConnect shows "0 person(s) listed!" and an empty list.
    * 2a2. User re-enters the command with different keywords (e.g. an alternative spelling).

      Steps 2a1-2a2 are repeated until at least one client matches.
      Use case resumes at step 3.

* 2b. Too many clients match to find the right one quickly.

    * 2b1. User re-enters the command with a single, longer name keyword (e.g. `find Alex` instead of `find Al`).
      Adding separate keywords does not narrow the results because `find` matches any keyword (OR search).

      Use case resumes at step 2.

* 3a. User wants to return to the full client list.

    * 3a1. User requests to list all clients.
    * 3a2. ClientConnect shows all clients.

      Use case ends.

**Use case: UC3 - Delete an obsolete client**

**MSS**

1.  User reads the short UUID shown on the card of the client to delete.
2.  User requests to delete that client by its short UUID.
3.  ClientConnect deletes the client and shows a success message with the deleted client's name.

    Use case ends.

**Extensions**

* 1a. The address book is empty.

    Use case ends.

* 2a. The identifier is not 7 to 32 hexadecimal characters once dashes are removed (e.g. missing, too short, too long or contains non-hex characters).

    * 2a1. ClientConnect shows an invalid command format error with the correct usage.
    * 2a2. User re-enters the command with a valid identifier.

      Steps 2a1-2a2 are repeated until the identifier is valid.
      Use case resumes at step 3.

* 2b. No client's UUID starts with the given identifier.

    * 2b1. ClientConnect shows "No person with UUID: &lt;identifier&gt;".
    * 2b2. User re-enters the command with an identifier that exists.

      Steps 2b1-2b2 are repeated until a matching client is found.
      Use case resumes at step 3.

* 2c. More than one client's UUID starts with the given identifier.

    * 2c1. ClientConnect shows an ambiguity error listing the full UUID and name of each matching client.
    * 2c2. User re-enters the command with enough characters of the intended client's UUID to tell the clients apart.

      Use case resumes at step 3.

**Use case: UC4 - Update an existing client's details**

**MSS**

1.  User searches for the client to update (UC2).
2.  User requests to edit a specific client by its short UUID, providing the fields to change (e.g. a new phone number, remark, or additional tags).
3.  ClientConnect validates the new values.
4.  ClientConnect updates the client and shows a success message with the updated details.

    Use case ends.

**Extensions**

* 1a. The displayed list is empty.

    Use case ends.

* 2a. The short UUID is invalid, unknown, or ambiguous.

    * 2a1. ClientConnect shows an error message.
    * 2a2. User re-enters the command with a valid, sufficiently specific short UUID.

      Steps 2a1-2a2 are repeated until the short UUID identifies exactly one client.
      Use case resumes at step 3.

* 2b. No fields to edit are provided.

    * 2b1. ClientConnect shows an error message stating that at least one field must be provided.
    * 2b2. User re-enters the command with at least one field.

      Steps 2b1-2b2 are repeated until at least one field is provided.
      Use case resumes at step 3.

* 3a. A provided value is invalid.

    * 3a1. ClientConnect shows the error message for the first invalid field.
    * 3a2. User re-enters the command with a corrected value.

      Steps 3a1-3a2 are repeated until all values are valid.
      Use case resumes at step 4.

* 3b. The new name matches another existing client's name (case-insensitive).

    * 3b1. ClientConnect rejects the edit and shows "This person already exists in the address book."
    * 3b2. User re-enters the command with a different name.

      Steps 3b1-3b2 are repeated until the name is unique.
      Use case resumes at step 4.

### Non-Functional Requirements

1.  The application should work on any _mainstream OS_ with Java `25` or above installed.
2.  With up to 1000 client records, adding, editing, finding, deleting, and listing records should each complete within 2 seconds on a computer with at least a dual-core processor and 8 GB of RAM.
3.  An insurance agent should be able to complete the core client-management tasks (add, edit, find, list, and delete) using only the keyboard, without needing to use a mouse.
4.  The command input and search results should remain usable through keyboard navigation, including when a search returns multiple clients.
5.  The application should save each successful change to client data automatically, and saved data should remain available after the application is closed and reopened.
6.  Invalid commands or invalid client data should show an actionable error message without changing existing client records.
7.  The application should be usable without an internet connection and should not transmit client data to an external service during normal use.

### Glossary

* **CLI (Command Line Interface)**: A way of using an application by typing text commands, as in ClientConnect's command box
* **GUI (Graphical User Interface)**: A way of using an application through visual elements such as windows, lists, and cards
* **Hexadecimal**: Using the digits `0`–`9` and the letters `a`–`f`. UUIDs are written in hexadecimal
* **Insurance Agent**: Point of contact for insurance products, manages a list of insurance clients' information
* **Insurance Client**: Consumer of insurance products, with an associated insurance agent who has the client's contact information
* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **MSS (Main Success Scenario)**: The steps of a use case in which everything goes as expected. Extensions describe what happens otherwise
* **Remark**: An optional free-text note stored with a client, e.g. `Likes to swim.`
* **Short UUID**: The first 7 hexadecimal characters of a client's UUID, shown as `#xxxxxxx` on the client's card
* **Tag**: An alphanumeric label attached to a client, e.g. `friends`. A client can have any number of tags
* **UUID (Universally Unique Identifier)**: A 32-character hexadecimal identifier given to each client when they are added, e.g. `1a2b3c4d-1000-0000-0000-000000000000`. It does not change when the client is edited
* **UUID prefix**: The first 7 to 32 hexadecimal characters of a UUID, ignoring dashes. Commands such as `delete`, `edit`, and `remark` use it to select a client

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file. If double-clicking does not open the app (e.g. on some macOS or Linux setups), open a terminal in that folder and run `java -jar ClientConnect.jar` instead.<br>
      Expected: The GUI opens with six sample contacts (Alex Yeoh, Bernice Yu, Charlotte Oliveiro, David Li, Irfan Ibrahim and Roy Balakrishnan). The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. Shutting down

   1. Test case: `exit`<br>
      Expected: The window closes and the app shuts down.

<div markdown="span" class="alert alert-info">:information_source: **Note:** The test cases below assume the app starts with the sample contacts. To reset to the sample contacts, close the app and delete the `data` folder next to the JAR file. Short UUIDs are generated randomly, so read them from the `#xxxxxxx` label on each contact's card.

</div>

### Adding a person

1. Adding a person with valid details

   1. Test case: `add n/John Doe p/98765432 e/johnd@example.com a/311, Clementi Ave 2, #02-25 t/friends`<br>
      Expected: John Doe is added to the end of the list. The status message starts with `New person added: John Doe; Phone: 98765432; ...`.

   1. Test case: `add n/Mary Tan p/93333333 e/mary@example.com a/Blk 1 r/Prefers email`<br>
      Expected: Mary Tan is added, and her card shows the remark `Prefers email`.

   1. Test case: `add n/Kim Lee p/94444444 e/kim@example.com a/"Blk 3 t/b"`<br>
      Expected: Kim Lee is added with the address `Blk 3 t/b`. The double quotes stop `t/` in the address from being read as a tag.

1. Adding a person whose name is already taken or similar to an existing name

   1. Prerequisites: John Doe has been added as above.

   1. Test case: `add n/john doe p/91111111 e/x@example.com a/Somewhere`<br>
      Expected: No person is added, as names are compared ignoring case. The status message shows `This name already exists in the address book. Add more details to the name.`

   1. Test case: `add n/Jon Doe p/92222222 e/jon@example.com a/Elsewhere`<br>
      Expected: Jon Doe is added. The status message also shows `Warning: "Jon Doe" is very similar to existing entry "John Doe".`

   1. Test case: `add n/UNKNOWN p/UNKNOWN e/UNKNOWN a/UNKNOWN`, entered twice<br>
      Expected: Both entries are added. `UNKNOWN` is a placeholder for missing information, so it is not treated as a duplicate name.

1. Adding a person with invalid details

   1. Test case: `add n/Kim Lee p/94444444 e/kimlee.example.com a/Blk 2`<br>
      Expected: No person is added. The status message shows `Invalid email address: @ is required unless the value is UNKNOWN`, followed by the command usage.

   1. Test case: `add n/Kim Lee p/94444444 e/kim@example.com a/Blk 2 t/high risk`<br>
      Expected: No person is added. The status message shows `Invalid tag`, followed by the command usage. Tags must be alphanumeric.

   1. Test case: `add n/Kim Lee p/94444444 e/kim@example.com e/kim2@example.com a/Blk 2`<br>
      Expected: No person is added. The status message shows `Multiple values specified for the following single-valued field(s): e/`, followed by `Use double quotes around the value containing a field prefix. Example: n/"John s/o Josh"`.

   1. Other incorrect add commands to try: `add`, `add n/Kim Lee p/94444444 a/Blk 2` (missing email)<br>
      Expected: No person is added, and an error message with the command usage is shown.

### Editing a person

1. Editing a person by short UUID

   1. Prerequisites: Note the short UUIDs of Alex Yeoh and Bernice Yu.

   1. Test case: `edit <Alex's short UUID> p/91234567 e/alex.new@example.com`<br>
      Expected: Alex's phone and email are updated, and the other fields are unchanged. The status message starts with `Edited person: Alex Yeoh; Phone: 91234567; Email: alex.new@example.com; ...`.

   1. Test case: `edit <Alex's short UUID> t/`<br>
      Expected: All of Alex's tags are removed.

   1. Test case: `edit <Alex's short UUID> r/Renewal due in March`<br>
      Expected: Alex's card shows the remark `Renewal due in March`.

   1. Test case: `edit <Alex's short UUID> n/Bernice Yu`<br>
      Expected: Alex is not edited. The status message shows `This person already exists in the address book.`

   1. Test case: `edit <Alex's short UUID>`<br>
      Expected: Alex is not edited. The status message shows `At least one field to edit must be provided.`

   1. Test case: `edit 9999999 p/91234567`<br>
      Expected: No person is edited. The status message shows `No person with UUID: 9999999`.

   1. Test case: `edit 1 p/91234567`<br>
      Expected: No person is edited. The status message shows `Invalid UUID prefix. It must contain 7 to 32 hexadecimal characters, ignoring dashes.`, followed by the command usage.

### Finding persons

1. Finding persons by name or tag

   1. Test case: `find alex`<br>
      Expected: Only Alex Yeoh is listed. The status message shows `1 person(s) listed!`.

   1. Test case: `find ber yu`<br>
      Expected: Only Bernice Yu is listed, as names match partial keywords.

   1. Test case: `find t/colleagues`<br>
      Expected: Bernice Yu and Roy Balakrishnan are listed. The status message shows `2 person(s) listed!`.

   1. Test case: `find alex t/family`<br>
      Expected: Alex Yeoh and David Li are listed, as name keywords and tags are combined with OR.

   1. Test case: `find zzz`<br>
      Expected: No persons are listed. The status message shows `0 person(s) listed!`.

   1. Test case: `find`<br>
      Expected: The list is unchanged. The status message shows an invalid command format error with `Specify at least part of a name or a tag.`

   1. Other incorrect find commands to try: `find t/` (empty tag), `find t/high risk` (tag with a space)<br>
      Expected: The list is unchanged, and an error message is shown.

### Sorting persons

1. Sorting the list by name

   1. Test case: `sort desc`<br>
      Expected: All persons are listed in reverse alphabetical order, ignoring case (with only the sample contacts, Roy Balakrishnan first and Alex Yeoh last). The status message shows `Listed all persons lexicographically by name.`

   1. Test case: `sort asc`<br>
      Expected: All persons are listed in alphabetical order (Alex Yeoh first).

   1. Test case: `sort desc`, then `find t/colleagues`<br>
      Expected: Roy Balakrishnan is listed before Bernice Yu, as the sort order still applies to search results.

   1. Test case: `sort desc`, then `exit`, then relaunch the app<br>
      Expected: The list is back in its original order, as the sort order is not saved.

   1. Incorrect sort commands to try: `sort`, `sort A-Z`, `sort asc extra`<br>
      Expected: The list order is unchanged. The status message shows an invalid command format error with the sort usage.

### Adding or removing a remark

1. Changing a remark by short UUID

   1. Prerequisites: Note the short UUID of Bernice Yu.

   1. Test case: `remark <Bernice's short UUID> r/Likes to swim.`<br>
      Expected: Bernice's card shows the remark `Likes to swim.` The status message starts with `Added remark to Person: Bernice Yu; ...`.

   1. Test case: `remark <Bernice's short UUID> r/`<br>
      Expected: Bernice's remark is removed. The status message starts with `Removed remark from Person: Bernice Yu; ...`.

   1. Test case: `remark <Bernice's short UUID>` (no `r/`)<br>
      Expected: The remark is unchanged. The status message shows `Missing remark prefix. Add r/ before the remark, or use r/ to remove the existing remark.`, followed by the command usage.

   1. Test case: `remark 9999999 r/Hello`<br>
      Expected: No remark is changed. The status message shows `No person with UUID: 9999999`.

   1. Test case: `remark #<Bernice's short UUID> r/Hello` (with the `#` from the card)<br>
      Expected: No remark is changed. The status message shows that the UUID must be 7 to 32 hexadecimal characters, without the `#` prefix.

### Copying client details

Prerequisites: Run `list` and choose a client with at least two tags and a nonempty remark.
Paste into a plain-text editor after each action to inspect the clipboard contents.

1. Click the client's `#xxxxxxx` badge.<br>
   Expected: Pasting produces the 7-character ID without `#`. Hovering over the badge shows a hand cursor and a copy tooltip.
1. Click a tag.<br>
   Expected: Pasting produces just that tag's name with its original capitalization, without `#` or `t/`.
   The client list is not filtered. Hovering over the tag shows a hand cursor and a copy tooltip.
1. Right-click the tag or ID badge.<br>
   Expected: The card's copy menu opens. Opening the menu alone does not replace the clipboard contents.
1. Select each individual copy option in turn.<br>
   Expected: The name, phone, email, address, and remark match their respective fields. **Copy client ID** produces
   the short ID without `#`; **Copy full UUID** produces the full UUID with dashes.
1. Select **Copy all details**.<br>
   Expected: Pasting produces labelled lines for name, phone, email, address, remark, tags, and the full UUID.
   Tags are sorted and separated by a comma and space.
1. Repeat with a client whose remark is empty and who has no tags.<br>
   Expected: **Copy remark** is disabled. **Copy all details** still works and includes empty `Remark:` and `Tags:` lines.
1. After copying, inspect the list and command feedback.<br>
   Expected: Client records, the current filter, and command feedback are unchanged by copying.

### Deleting a person

1. Deleting a person by short UUID

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list. Note the short UUID (`#xxxxxxx`) shown on a contact's card.

   1. Test case: `delete <shortUuid>` (using a short UUID that appears on a card)<br>
      Expected: The matching contact is deleted, regardless of the current filter. The status message shows the deleted contact's details.

   1. Test case: `delete 9999999` (a well-formed short UUID that no contact has)<br>
      Expected: No person is deleted. The status message shows `No person with UUID: 9999999`.

   1. Test case: `delete 1`<br>
      Expected: No person is deleted. The status message shows an invalid command format error, as the identifier must be 7 to 32 hexadecimal characters.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `delete 123456g` (contains a non-hex character), `delete 1a2b3c4 abc` (extra argument)<br>
      Expected: Similar to previous — an invalid command format error.

1. Deleting one of two persons that share a short UUID

   1. Prerequisites: Close the app. In `data/ClientConnect.json`, change the `uuid` of two persons so that both start with the same 7 characters but differ afterwards, e.g. `1a2b3c4d-1000-0000-0000-000000000000` and `1a2b3c4d-2000-0000-0000-000000000000`. Relaunch the app.

   1. Test case: `delete 1a2b3c4`<br>
      Expected: No person is deleted. The status message lists both persons' full UUIDs and names.

   1. Test case: `delete 1a2b3c4d1`<br>
      Expected: Only the first person is deleted.

   1. Test case: `delete 1a2b3c4d-2000-0000-0000-000000000000` (the full UUID, with dashes)<br>
      Expected: The second person is deleted.

### Restoring a deleted person

1. Restoring persons after deleting them

   1. Prerequisites: List all persons using the `list` command, with at least two persons in the list. Note the short UUIDs of the first two persons.

   1. Test case: `delete <first person's short UUID>`, then `undelete`<br>
      Expected: The first person is restored at the top of the list, with the same short UUID. The status message shows `Restored person:` followed by their details.

   1. Test case: `delete <first person's short UUID>`, `delete <second person's short UUID>`, then `undelete` twice<br>
      Expected: The first `undelete` restores the second person, and the second `undelete` restores the first person. The list is back in its original order.

   1. Test case: `find <a name that matches nobody>` after a delete, then `undelete`<br>
      Expected: The person is restored, and the list shows all persons.

   1. Test case: `undelete` with no deletes since the app was launched<br>
      Expected: Nothing is restored. The status message shows `No deleted persons to restore.`

   1. Test case: `undelete 1`<br>
      Expected: Nothing is restored. The status message shows an invalid command format error.

1. Restoring a person when a person with the same name exists

   1. Prerequisites: Delete a person, e.g. `John Doe`, then add a new person with the same name.

   1. Test case: `undelete`<br>
      Expected: Nothing is restored. The status message shows `Cannot undelete John Doe: a person with the same name already exists.`, followed by a prompt to rename that person with `edit`.

   1. Test case: Rename the new `John Doe` with `edit`, then `undelete`<br>
      Expected: The original `John Doe` is restored, with their original short UUID.

### Saving data

1. Dealing with missing/corrupted data files

   1. Test case: Close the app. In `data/ClientConnect.json`, change one person's `uuid` to `not-a-uuid`. Relaunch the app.<br>
      Expected: The app starts with an empty address book instead of crashing. The log shows that the data file could not be loaded.

   1. Test case: Close the app. Delete `data/ClientConnect.json`. Relaunch the app.<br>
      Expected: The app starts with the six sample contacts, and creates a new data file when the data is next saved.

1. Saving changes automatically

   1. Test case: `add n/Save Test p/90000000 e/save@example.com a/Blk 9`, then `exit`, then relaunch the app.<br>
      Expected: Save Test is still in the list.
