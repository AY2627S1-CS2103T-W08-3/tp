# Epic: Replace delete-by-index with delete-by-UUID

Goal: change `delete` so it identifies the target person by UUID instead of the
displayed list index. The index is unstable — it changes whenever the list is
filtered (`find`) or reordered — so a user can delete the wrong person. Each
person already has a stable UUID shown on their card as `#xxxxxxx`
(`Person#getShortUuid()`); this makes that UUID the deletion key.

Broken into the issues below.

---

## Issue 1 — Delete a person by UUID

**Labels:** `type.Enhancement`, `priority.High`

### Description
Make `delete` accept the short 7-char UUID shown on each card as the target.

```
delete 1a2b3c4      // deletes the person whose short UUID is 1a2b3c4
```

- Match against the short UUID (first 7 chars) of **every** person in the
  address book, not just the filtered list.
- No match → error: `"No person with UUID: %s"`.
- Ambiguous match (more than one person) → error asking the user to disambiguate.

### Suggested implementation
- Parse the arg as a UUID string in `DeleteCommandParser.java`.
- Add a UUID lookup path in `DeleteCommand.java`.
- Add a lookup helper (e.g. `getPersonByShortUuid`) in `Model.java` / `ModelManager`.
- Add not-found and ambiguous messages.

### Acceptance criteria
- [ ] `delete <shortUuid>` deletes the matching person.
- [ ] Clear error messages for not-found and ambiguous UUID.
- [ ] Unit tests for parser and command (success, not-found, ambiguous).

---

## Issue 2 — Remove index-based deletion

**Labels:** `type.Enhancement`, `priority.High`

### Description
Once delete-by-UUID works, remove the old index behaviour so there is one way
to delete.

### Suggested implementation
- Drop the `Index` path from `DeleteCommand.java` and `DeleteCommandParser.java`.
- Update `MESSAGE_USAGE` to show the UUID syntax and example.
- Remove `MESSAGE_INVALID_PERSON_DISPLAYED_INDEX` usage in delete.

### Acceptance criteria
- [ ] `delete 1` is rejected with the new usage message (no longer deletes by index).
- [ ] No dead index-handling code remains in the delete path.
- [ ] Existing index-based delete tests removed or converted.

---

## Issue 3 — Update UG and DG

**Labels:** `type.Documentation`, `priority.Medium`

### Description
Reflect the new delete behaviour in the docs.

### Acceptance criteria
- [ ] UG `delete` section documents UUID syntax, example, and error cases.
- [ ] DG updated (command description and any affected diagrams/sequence).
- [ ] No remaining references to deleting by index.
