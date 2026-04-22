# FLC SessionHub — Group Exercise Booking System
### Module 7COM1025 | Programming for Software Engineers

---

## Overview of project structure

A command-line Java application for managing group exercise lesson bookings
at Furzefield Leisure Centre (FLC). Members can view the timetable, create
bookings, change or cancel bookings, attend sessions, and submit feedback.
Management reports show attendance, average ratings, and income by activity type.

---

## Project Structure

```
SessionHub/
├── pom.xml                          Maven build file
├── README.md                        This file
└── src/
    ├── main/java/
    │   └── sessionhub/
    │       ├── bootstrap/
    │       │   ├── Main.java         Entry point
    │       │   └── DataLoader.java   Sample data (10 members, 48 sessions, 22+ reviews)
    │       ├── console/
    │       │   └── TerminalController.java   CLI menu interface
    │       ├── core/
    │       │   ├── DayCategory.java          Enum: SATURDAY, SUNDAY
    │       │   ├── SessionWindow.java        Enum: MORNING, AFTERNOON, EVENING
    │       │   ├── EnrollmentStatus.java     Enum: ENROLLED, UPDATED, CANCELLED, COMPLETED
    │       │   ├── Participant.java          Member domain object
    │       │   ├── ActivitySession.java      Lesson domain object
    │       │   ├── Enrollment.java           Booking domain object
    │       │   └── FeedbackNote.java         Review domain object
    │       └── engine/
    │           ├── EnrollmentManager.java    Core business logic & constraint enforcement
    │           └── ReportGenerator.java      Attendance and income reports
    └── test/java/
        └── sessionhub/
            └── SessionHubTest.java           JUnit 5 test suite
```

---

## How to Run

### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Compile and run
```bash
mvn compile
mvn exec:java -Dexec.mainClass="sessionhub.bootstrap.Main"
```

### Build executable JAR
```bash
mvn package
java -jar target/flc-sessionhub.jar
```

### Run tests
```bash
mvn test
```

---

## Exercise Types and Prices

| Activity            | Price per session |
|---------------------|-------------------|
| Power Sculpt        | £15.00            |
| Metabolic Burn      | £20.00            |
| Dynamic Stretch     | £12.00            |
| Functional Fitness  | £18.00            |

---

## Sample Data

- **10 participants**: P01 (Alice Carter) through P10 (Jack Wilson)
- **48 sessions**: 8 weekends × 6 sessions per weekend
- **22+ completed enrollments** with ratings and feedback pre-loaded
- **Session ID format**: S001 – S048
- **Enrollment ID format**: ENR-0001, ENR-0002, …

---

## Key Business Rules

| Rule                    | Behaviour                                                  |
|-------------------------|------------------------------------------------------------|
| Capacity limit          | Maximum 4 participants per session                         |
| Duplicate prevention    | Same participant cannot book the same session twice        |
| Time conflict           | Same participant cannot have two bookings at same time slot|
| ID uniqueness           | Enrollment IDs are never reused after cancellation         |
| Report scope            | Only COMPLETED enrollments count in attendance/income      |
| Rating validation       | Rating must be 1–5; anything outside is rejected           |

---

## Menu Options

```
1. View timetable by day
2. View timetable by activity
3. Create enrollment
4. Update enrollment
5. Cancel enrollment
6. Confirm attendance & submit feedback
7. View my enrollments
8. Generate reports
9. List all participants
0. Exit
```

---

## Report Output Examples

**Attendance Report** — shows per-session attendee count and average rating.

**Income Report** — shows total income per exercise type from completed sessions,
and highlights the highest earning activity.

---

## Architecture Notes

- **Layered design**: core domain → engine (business logic) → console (I/O) → bootstrap (startup)
- **Façade pattern**: `EnrollmentManager` is the single entry point for all booking operations
- **Encapsulation**: collections returned as unmodifiable views; all state changes via named methods
- **No external database**: all data held in memory; `DataLoader` seeds startup data

---

*Furzefield Leisure Centre | 7COM1025 Coursework | University of Hertfordshire*
