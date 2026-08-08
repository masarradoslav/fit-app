# Coding Guidelines

These rules apply to the whole `fitness_app` project. Claude Code (or anyone else
working on this project) should follow them when writing or modifying code.

## General principles

* **Clarity over cleverness.** Code is read far more often than it's written.
  If there's a simpler way to express something, prefer it over a "clever" one-liner.
* **Small, single-purpose changes.** One change/commit should address one thing.
  Don't mix refactoring with new functionality in the same change.
* **Consistency with the rest of the project.** Stick to existing conventions
  (naming, the Controller → Service → Repository layering), even if you'd
  personally prefer a different style.
* **Explain non-obvious decisions.** If a solution isn't self-evident (e.g. why
  a fork copies `TrainingDay`/`TrainingDayExercise` instead of reusing the
  original rows), add a short comment explaining *why*, not just *what* the
  code does.

## Secure coding practices

* **Never trust input by default.** Treat every input coming from the client
  (`@RequestBody`, `@PathVariable`, query parameters) as potentially invalid
  or malicious until it's been validated.
* **Validate input at the application boundary.** Use `@Valid` plus Bean
  Validation annotations (`@NotBlank`, `@Min`, `@Positive`, etc.) on DTOs
  instead of scattering manual `if` checks through the service layer.
  Numeric fields like `reps` and `weightKg` in `SetLog` must never accept
  negative values — validate this at the DTO, not deep in a formula.
* **Passwords are always hashed.** `BCryptPasswordEncoder`, never plain text,
  never a custom hash. This project has real auth from day one, so there is
  no "if auth is ever added" exception here.
* **JWT is for authentication, ownership checks are for authorization.**
  A valid token only proves *who* the user is. Every service method that reads
  or writes a `Split`, `WorkoutSession`, or `SetLog` must still explicitly
  check that the resource belongs to (or is visible to) the requesting user —
  don't assume "has a valid token" means "may see this data."
* **Don't build native SQL by string concatenation.** Use parameterized JPQL/
  `@Query` with `:param`, never `"SELECT * FROM x WHERE y = " + input`.
* **Don't leak internal details in error messages.** Return a clear but
  generic message to the client (e.g. `"Split not found"`), not a stack trace
  or database schema details.
* **Take CORS restrictions seriously even in a training project.**
  `@CrossOrigin(origins = "*")` is acceptable for local development only; if
  the app ever went to production, restrict it to the actual frontend domain.
* **Never hardcode secrets** (JWT signing key, DB password, API keys) in code.
  They belong in `application.yaml` (and, in a real project, environment
  variables / a secrets manager), not in committed source code.

## Error handling

* Instead of returning `null` for a record that wasn't found, use
  `ResponseEntity` with the correct HTTP status code (404 for "not found",
  400 for invalid input, 403 for "exists but not yours", 409 for conflict).
* Don't swallow exceptions silently (`catch (Exception e) {}` with nothing
  inside) — at minimum, log what happened.
* On update/delete operations, always verify the record exists **and belongs
  to the requesting user** before acting on it (see the `Optional` pattern
  already used in the task manager's `WorkerService`/`TaskService`).

## Structure and layering

* **Controllers stay thin.** They receive the request, call exactly one
  service method, and return the response. No business logic directly in
  the controller.
* **Services hold the logic.** Validation, combining multiple repository
  calls, and data transformations belong here.
* **Repositories are never called directly from a controller.** Always go
  through the service layer.
* **Entities (`model/`) shouldn't contain business logic** beyond their own
  state — logic that spans multiple entities (e.g. computing progress
  compared to the last session) belongs in a service.
* **DTOs, not entities, cross the controller boundary.** Every controller
  method takes a request DTO (validated with `@Valid`) and returns a
  response DTO — never a JPA entity directly, in either direction.
  Mapping happens in the service layer (plain constructor/builder calls, or
  a mapper class once there are enough fields to justify one — no need for
  MapStruct on a learning project unless you want the practice). Keep naming
  consistent: `SplitRequest`/`SplitResponse`, `FeedPostRequest`/
  `FeedPostResponse`, etc.

## Multi-step / transactional operations

This project has a few operations that touch several tables at once — most
notably **forking a `Split`** (copying it plus all its `TrainingDay` and
`TrainingDayExercise` rows to a new owner). These need extra care:

* Any operation that writes to more than one repository as a single logical
  unit **must** be wrapped in `@Transactional`, so a failure halfway through
  doesn't leave a half-copied split in the database.
* Keep this logic in the service layer, in one method, not spread across
  helper calls in the controller.
* Write a test that simulates a failure partway through (e.g. the second
  `TrainingDay` save throws) and asserts nothing was persisted.

## Self-referencing relationships

`Follow` (user → user) and `Split.forkedFromSplitId` (split → split) are
self-referencing. Keep in mind:

* Never eagerly fetch a self-referencing relationship by default
  (`FetchType.LAZY`) — an eager `Follow` or `forkedFrom` chain can trigger
  unbounded recursion when serializing to JSON.
* **Entities are never returned directly from controllers — always map to a
  DTO.** This is the project-wide rule, not just a fallback for
  self-referencing relationships: `@JsonIgnore` on the "back" side of a
  relationship is a trap door for accidentally-eager or newly-added fields to
  leak into the API later. A DTO has no cyclical references by construction,
  so there's no recursion risk to begin with, and it also means the JSON
  shape stays stable even if the entity's fields change. This applies to
  `AppUser`, `Follow`, `Split` (`forkedFromSplitId`), and everywhere else —
  the task manager's `tasks`/`@JsonIgnore` pattern should be treated as
  legacy, not the model to copy here.
* `Follow` needs a unique constraint on `(follower_id, followee_id)` and a
  check that `follower_id <> followee_id` — enforce both at the DB level,
  not just in the service.

## Visibility vs. permissions — two different questions

Don't conflate these two checks in the service layer:

* **Visibility** — is this `Split` or `FeedPost` marked `PUBLIC`, so *anyone*
  is allowed to see it at all?
* **Permission/ownership** — does *this specific user* own it, and therefore
  may edit or delete it?

A public split is readable by everyone but writable only by its owner. Write
these as two separate, clearly named checks (e.g. `assertVisible(...)` and
`assertOwner(...)`) rather than one combined `if`.

## Testing

* New business logic (especially in the service layer) should have at least
  a basic unit test.
* Name tests descriptively based on the scenario, e.g.
  `forkSplit_shouldCopyAllTrainingDays_whenOriginalIsPublic`, not `test1`.
* Feed queries (`findFollowingFeed`, `findExploreFeed`) should have a test
  covering the empty case (no follows yet / no public posts yet) — an empty
  feed is easy to get wrong with an `IN ()` clause.

## Git

* Write commit messages in the present/imperative tense, kept short:
  `"Add SetLog validation for negative reps"`, not `"added some stuff"`.
* Don't commit generated/build files (`target/`) or IDE configuration
  (`.idea/`) — make sure they're in `.gitignore`.
