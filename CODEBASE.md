# Codebase Overview

## 1. What this project is

A fork of **AddressBook Level 3 (AB3)** from se-edu / NUS School of Computing (CS2103T-style team project). It is a desktop contact-manager app with a JavaFX GUI and a command-line-style input box. Contacts are stored locally as JSON.

- Language: Java 25 (`sourceCompatibility`/`targetCompatibility` = 25)
- UI: JavaFX 17.0.7 (FXML + CSS)
- Persistence: Jackson 2.7 (JSON)
- Tests: JUnit 5.14.4, JaCoCo coverage
- Style: Checkstyle 14.1.0
- Build: Gradle (wrapper included), ShadowJar produces `addressbook.jar`
- Main class: `seedu.address.Main` (launches `MainApp`)
- Size: ~4.7 KLoC main code, 67 test files
- License: see [LICENSE](LICENSE)

Current branch: `Update-AboutUs` (master is the main branch). Recent work is on the About Us page ([docs/AboutUs.md](docs/AboutUs.md)), which lists team members, roles and GitHub links.

## 2. Repository layout

```
.
├── build.gradle, gradle.properties, gradlew(.bat)   Build config
├── config/checkstyle/                               Checkstyle rules + suppressions
├── .github/
│   ├── workflows/gradle.yml                         CI (Linux/macOS/Windows, JDK 25)
│   └── check-*.sh, run-checks.sh                    Repo-wide checks (EOF newline, line endings, trailing whitespace)
├── docs/                                            Jekyll site (GitHub Pages) + diagrams
├── src/main/java/seedu/address/                     Application code
├── src/main/resources/view/                         FXML + CSS
└── src/test/                                        Tests and test data
```

## 3. Architecture

Standard AB3 layered architecture. Each component has an interface and a `*Manager` implementation.

| Component | Package | Responsibility |
|---|---|---|
| **Main / MainApp** | `seedu.address` | App entry point; wires Ui, Logic, Model, Storage together, loads data and prefs |
| **Ui** | `ui` | JavaFX windows and widgets |
| **Logic** | `logic` | Parses user input into `Command` objects and executes them |
| **Model** | `model` | In-memory data (address book, user prefs, filtered person list) |
| **Storage** | `storage` | Reads/writes address book and user prefs as JSON |
| **Commons** | `commons` | Shared utilities, `Index`, `GuiSettings`, `LogsCenter`, exceptions |

Flow of a command: `Ui (CommandBox)` → `LogicManager.execute(text)` → `AddressBookParser` → specific `XCommandParser` → `XCommand.execute(Model)` → `CommandResult` → back to Ui. After execution, `LogicManager` saves the address book through `Storage`.

### 3.1 Ui (`src/main/java/seedu/address/ui`)
- `UiManager` (implements `Ui`) starts `MainWindow`.
- `MainWindow` composes `CommandBox`, `ResultDisplay`, `PersonListPanel` (of `PersonCard`), `StatusBarFooter`, and `HelpWindow`.
- `UiPart<T>` is the base class that loads an FXML file.
- Layouts live in `src/main/resources/view/*.fxml`; styling in `DarkTheme.css`, `Extensions.css`, `HelpWindow.css`.

### 3.2 Logic (`logic`)
- `Logic` / `LogicManager`: entry point; executes commands, exposes the filtered person list, file paths and GUI settings.
- `Messages`: shared user-facing message strings.
- `commands/`: `Command` (abstract), `CommandResult`, and the concrete commands below. `commands/exceptions/CommandException`.
- `parser/`: `AddressBookParser` dispatches on the command word. `ArgumentTokenizer` / `ArgumentMultimap` / `Prefix` / `CliSyntax` handle prefixed arguments. `ParserUtil` converts strings into model types. `ParseException` is thrown on bad input.

**Commands**

| Word | Class | Purpose |
|---|---|---|
| `add` | `AddCommand` | Add a person: `add n/NAME p/PHONE e/EMAIL a/ADDRESS [t/TAG]…` |
| `edit` | `EditCommand` | Edit a person by index (uses `EditPersonDescriptor`) |
| `delete` | `DeleteCommand` | Delete a person by index |
| `find` | `FindCommand` | Filter by name keywords (`NameContainsKeywordsPredicate`) |
| `list` | `ListCommand` | Show all persons |
| `clear` | `ClearCommand` | Clear the whole address book |
| `help` | `HelpCommand` | Open the help window |
| `exit` | `ExitCommand` | Quit the app |

**Prefixes** (`CliSyntax`): `n/` name, `p/` phone, `e/` email, `a/` address, `t/` tag.

### 3.3 Model (`model`)
- `Model` / `ModelManager`: holds an `AddressBook`, `UserPrefs`, and a `FilteredList<Person>`.
- `AddressBook` (implements `ReadOnlyAddressBook`): wraps a `UniquePersonList`.
- `UserPrefs` (implements `ReadOnlyUserPrefs`): GUI settings and the data file path.
- `person/`: `Person` (immutable; identity fields name/phone/email, data fields address/tags), `Name`, `Phone`, `Email`, `Address`, `UniquePersonList`, `NameContainsKeywordsPredicate`, plus `DuplicatePersonException` and `PersonNotFoundException`.
- `tag/Tag`: a tag value object.
- `util/SampleDataUtil`: default sample data on first run.

### 3.4 Storage (`storage`)
- `Storage` combines `AddressBookStorage` and `UserPrefsStorage`; `StorageManager` implements it.
- `JsonAddressBookStorage`, `JsonUserPrefsStorage`: file IO.
- `JsonSerializableAddressBook`, `JsonAdaptedPerson`, `JsonAdaptedTag`: Jackson-friendly adapters that convert to and from model objects and validate on load.

### 3.5 Commons (`commons`)
- `core/`: `GuiSettings`, `LogsCenter` (logging), `index/Index`.
- `util/`: `AppUtil`, `CollectionUtil`, `FileUtil`, `JsonUtil`, `StringUtil`, `ToStringBuilder`.
- `exceptions/`: `DataLoadingException`, `IllegalValueException`.

## 4. Tests (`src/test`)

Mirrors the main package structure:
- `commons/`, `logic/commands/`, `logic/parser/`, `model/`, `model/person/`, `storage/`, `ui/`
- `testutil/`: builders and fixtures (`PersonBuilder`, `AddressBookBuilder`, `EditPersonDescriptorBuilder`, `TypicalPersons`, `TypicalIndexes`, `PersonUtil`, `Assert`, `TestUtil`)
- `src/test/data/`: JSON fixtures for storage tests (valid, invalid, duplicate, non-JSON, user prefs)

## 5. Build, run, test

```bash
./gradlew run                 # run the app
./gradlew test                # unit tests (finalized by JaCoCo report)
./gradlew checkstyleMain checkstyleTest
./gradlew check coverage      # what CI runs
./gradlew shadowJar           # builds build/libs/addressbook.jar
java -jar build/libs/addressbook.jar
```

Default tasks: `clean`, `test`.

## 6. CI

[.github/workflows/gradle.yml](.github/workflows/gradle.yml) runs on every push and pull request, on Ubuntu, macOS and Windows:
1. (Linux only) `.github/run-checks.sh`: EOF newline, line endings, trailing whitespace checks
2. Gradle wrapper validation
3. JDK 25 (Zulu, with JavaFX) setup
4. `./gradlew check coverage`
5. (Linux only) Upload coverage to Codecov

## 7. Documentation (`docs/`)

Jekyll site (minima theme) published via GitHub Pages.

| File | Content |
|---|---|
| `index.md` | Landing page |
| `AboutUs.md` | Team members, roles and responsibilities |
| `UserGuide.md` | End-user guide |
| `DeveloperGuide.md` | Design, implementation, requirements |
| `SettingUp.md` | IDE and environment setup |
| `Testing.md`, `Logging.md`, `Documentation.md`, `DevOps.md` | Dev process guides |
| `team/johndoe.md` | Project portfolio page template |
| `diagrams/*.puml` | PlantUML sources (architecture, UI, logic, model, storage, undo/redo, delete sequence, etc.) |
| `_config.yml`, `_layouts`, `_includes`, `_sass`, `assets` | Site configuration and theme |

## 8. Conventions

- Checkstyle config in [config/checkstyle/checkstyle.xml](config/checkstyle/checkstyle.xml).
- Files must end with a newline, use consistent line endings, and have no trailing whitespace (enforced in CI).
- Model classes are immutable and validate input in constructors (`requireAllNonNull`, static `isValidX` methods).
- Commands expose `COMMAND_WORD`, `MESSAGE_USAGE` and success/failure message constants.
- Logging goes through `LogsCenter`.

## 9. Data files

- Address book data: `data/addressbook.json` (default, under the working directory)
- User preferences: `preferences.json`
