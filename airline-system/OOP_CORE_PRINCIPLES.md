# Core OOP Principles in the Airline System

This document focuses exclusively on the four foundational OOP principles—**Abstraction, Encapsulation, Inheritance, and Polymorphism**—showing **where** each appears in the codebase and **why** it improves the system.

---

## 1. Abstraction – *"Expose the what, hide the how."*

| Aspect | Details |
|--------|---------|
| **Key Artefacts** | • `BaseRepository<T, ID>` (interface)<br>• `AbstractJdbcRepository` (abstract class)<br>• Domain interfaces (`ValidationService<T>`, `NotificationService`) |
| **Concrete Examples** | `repository/BaseRepository.java`, `repository/AbstractJdbcRepository.java` |
| **How It Works** | Controllers/services depend on **interfaces** rather than concrete classes. `AbstractJdbcRepository` provides `getConnection()`; individual repositories write only SQL. |
| **Benefits** | 1. Loose coupling—swap JDBC for JPA easily.<br>2. Single-source DB config—no duplicated credentials.<br>3. Testability—mock interfaces in unit tests.<br>4. Simpler controllers—focus on operations, not details. |

---

## 2. Encapsulation – *"Protect and govern internal state."*

| Aspect | Details |
|--------|---------|
| **Key Artefacts** | Model classes (`Plane`, `Flight`, `SeatType`, `User`, …)<br>`DatabaseConfig` |
| **Implementation** | Fields are `private`; access via getters/setters or domain methods (`approve()`, `softDelete()`). |
| **Benefits** | 1. Data integrity—guards against invalid values.<br>2. Security—DB password never exposed.<br>3. Change isolation—future validation/logging added in one place. |

---

## 3. Inheritance – *"Reuse common structure and behaviour."*

| Aspect | Details |
|--------|---------|
| **Key Artefacts** | `BaseEntity` → parent of `Plane`, `Flight`, etc.<br>`AbstractJdbcRepository` → parent of repositories.<br>Your user hierarchy (`People_User` → Manager/Employee/Customer). |
| **Shared Behaviour** | `BaseEntity` supplies `id`, timestamps, `softDelete()` and enforces `isValid()`.<br>`AbstractJdbcRepository` centralises JDBC connection logic. |
| **Benefits** | 1. DRY—shared fields live once.<br>2. Consistent rules—every entity implements `isValid()`.<br>3. Bulk improvements—one change affects all subclasses. |

---

## 4. Polymorphism – *"Same message, many forms."*

| Aspect | Details |
|--------|---------|
| **Key Artefacts** | `ValidationService` implementations (`PlaneValidationService`, …)<br>`NotificationObserver` implementations (`AuditLogObserver`)<br>`viewLogs()` overrides in user hierarchy |
| **Behaviour** | Calling `validator.validateForSave(entity)` or `user.viewLogs()` triggers role-specific logic without `if`/`switch`. |
| **Benefits** | 1. Extensibility—add new roles/strategies without editing client code.<br>2. Cleaner APIs—uniform method names.<br>3. Decoupled features—observers plug in/out freely. |

---

### Net Result

Applying these four principles yields:

* **Maintainability** – fewer duplicated lines, clear separation of concerns.
* **Flexibility** – swap implementations, add features with minimal edits.
* **Robustness** – controlled data access, enforced validation, consistent behaviour across similar classes.

The system evolves from a tangle of hard-coded logic into a **modular, evolution-friendly architecture** rooted in classic OOP best practices. 