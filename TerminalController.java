package sessionhub.console;

import sessionhub.core.*;
import sessionhub.engine.EnrollmentManager;
import sessionhub.engine.ReportGenerator;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menu-driven terminal interface for the FLC SessionHub booking system.
 *
 * All business logic is delegated to EnrollmentManager.
 * This class is responsible solely for I/O.
 */
public class TerminalController {

    private final EnrollmentManager    manager;
    private final ReportGenerator      reports;
    private final List<ActivitySession> sessions;
    private final List<Participant>    participants;
    private final Scanner              scanner = new Scanner(System.in);

    public TerminalController(EnrollmentManager manager, ReportGenerator reports,
                              List<ActivitySession> sessions, List<Participant> participants) {
        this.manager      = manager;
        this.reports      = reports;
        this.sessions     = sessions;
        this.participants = participants;
    }

    // ── Main loop ─────────────────────────────────────────────────────────────

    public void showMenu() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("  Enter your choice: ", 0, 9);
            switch (choice) {
                case 1 -> displayTimetableByDay();
                case 2 -> displayTimetableByActivity();
                case 3 -> enrollParticipant();
                case 4 -> updateEnrollment();
                case 5 -> cancelEnrollment();
                case 6 -> confirmAttendance();
                case 7 -> viewMyEnrollments();
                case 8 -> generateReports();
                case 9 -> listParticipants();
                case 0 -> running = false;
            }
        }
        System.out.println("\n  Thank you for using FLC SessionHub. Goodbye!\n");
    }

    // ── Feature handlers ─────────────────────────────────────────────────────

    private void displayTimetableByDay() {
        section("VIEW TIMETABLE BY DAY");
        System.out.println("  1. Saturday   2. Sunday");
        int choice = readInt("  Select: ", 1, 2);
        DayCategory day = choice == 1 ? DayCategory.SATURDAY : DayCategory.SUNDAY;

        List<ActivitySession> result = manager.viewTimetableByDay(sessions, day);
        if (result.isEmpty()) {
            System.out.println("  No sessions found for " + day + ".");
            return;
        }
        printSessionHeader();
        result.forEach(s -> System.out.println("  " + s));
        System.out.println();
    }

    private void displayTimetableByActivity() {
        section("VIEW TIMETABLE BY ACTIVITY");
        System.out.println("  Available: Power Sculpt | Metabolic Burn | Dynamic Stretch | Functional Fitness");
        System.out.print("  Enter activity name: ");
        String activity = scanner.nextLine().trim();

        List<ActivitySession> result = manager.viewTimetableByActivity(sessions, activity);
        if (result.isEmpty()) {
            System.out.println("  No sessions found for activity: " + activity);
            return;
        }
        printSessionHeader();
        result.forEach(s -> System.out.println("  " + s));
        System.out.println();
    }

    private void enrollParticipant() {
        section("CREATE ENROLLMENT");
        listParticipants();

        System.out.print("  Enter Participant ID: ");
        String pid = scanner.nextLine().trim().toUpperCase();
        Participant p = findParticipant(pid);
        if (p == null) { System.out.println("  Error: Participant not found."); return; }

        printAvailableSessions();
        System.out.print("  Enter Session ID: ");
        String sid = scanner.nextLine().trim().toUpperCase();
        ActivitySession s = findSession(sid);
        if (s == null) { System.out.println("  Error: Session not found."); return; }

        manager.createEnrollment(p, s);
    }

    private void updateEnrollment() {
        section("UPDATE ENROLLMENT");
        System.out.print("  Enter Enrollment ID (e.g. ENR-0001): ");
        String eid = scanner.nextLine().trim().toUpperCase();

        Optional<Enrollment> existing = manager.findById(eid);
        if (existing.isEmpty() || !existing.get().isActive()) {
            System.out.println("  Error: No active enrollment found with that ID."); return;
        }
        System.out.println("  Current: " + existing.get());

        printAvailableSessions();
        System.out.print("  Enter new Session ID: ");
        String sid = scanner.nextLine().trim().toUpperCase();
        ActivitySession s = findSession(sid);
        if (s == null) { System.out.println("  Error: Session not found."); return; }

        manager.updateEnrollment(eid, s);
    }

    private void cancelEnrollment() {
        section("CANCEL ENROLLMENT");
        System.out.print("  Enter Enrollment ID: ");
        String eid = scanner.nextLine().trim().toUpperCase();
        System.out.print("  Confirm cancellation? (yes/no): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("  Cancellation aborted."); return;
        }
        manager.cancelEnrollment(eid);
    }

    private void confirmAttendance() {
        section("CONFIRM ATTENDANCE & FEEDBACK");
        System.out.print("  Enter Enrollment ID: ");
        String eid = scanner.nextLine().trim().toUpperCase();

        int rating = readInt("  Satisfaction rating (1=Very Dissatisfied … 5=Very Satisfied): ", 1, 5);
        System.out.print("  Your feedback comment: ");
        String comment = scanner.nextLine().trim();
        if (comment.isEmpty()) comment = "No comment provided.";

        manager.confirmAttendance(eid, rating, comment);
    }

    private void viewMyEnrollments() {
        section("MY ENROLLMENTS");
        System.out.print("  Enter Participant ID: ");
        String pid = scanner.nextLine().trim().toUpperCase();
        Participant p = findParticipant(pid);
        if (p == null) { System.out.println("  Error: Participant not found."); return; }

        List<Enrollment> active = manager.activeEnrollmentsFor(pid);
        if (active.isEmpty()) {
            System.out.println("  No active enrollments for " + p.getName() + ".");
        } else {
            System.out.printf("%n  %-12s %-10s %-20s %-12s %-10s%n",
                    "EnrollID", "Session", "Activity", "Day/Window", "Status");
            System.out.println("  " + "─".repeat(66));
            active.forEach(e -> System.out.printf("  %-12s %-10s %-20s %-12s %-10s%n",
                    e.getId(),
                    e.getSession().getSessionId(),
                    e.getSession().getActivityName(),
                    e.getSession().getDay() + "/" + e.getSession().getWindow(),
                    e.getStatus()));
        }
        System.out.println();
    }

    private void generateReports() {
        section("GENERATE REPORTS");
        System.out.println("  1. Attendance & Ratings Report");
        System.out.println("  2. Income Report");
        System.out.println("  3. Both Reports");
        int choice = readInt("  Select: ", 1, 3);
        if (choice == 1 || choice == 3) reports.generateAttendanceReport(sessions);
        if (choice == 2 || choice == 3) reports.generateIncomeReport(sessions);
    }

    private void listParticipants() {
        System.out.printf("%n  %-6s %-24s%n", "ID", "Name");
        System.out.println("  " + "─".repeat(30));
        participants.forEach(p -> System.out.printf("  %-6s %-24s%n", p.getId(), p.getName()));
        System.out.println();
    }

    // ── Display helpers ───────────────────────────────────────────────────────

    private void printBanner() {
        System.out.println("\n ╔══════════════════════════════════════════════════════╗");
        System.out.println(" ║     FURZEFIELD LEISURE CENTRE — SessionHub           ║");
        System.out.println(" ║     Group Exercise Booking System                    ║");
        System.out.println(" ╚══════════════════════════════════════════════════════╝");
    }

    private void printMainMenu() {
        System.out.println("\n  ┌──────────────────────────────────────────────────┐");
        System.out.println("  │                   MAIN MENU                      │");
        System.out.println("  ├──────────────────────────────────────────────────┤");
        System.out.println("  │  1.  View timetable by day                       │");
        System.out.println("  │  2.  View timetable by activity                  │");
        System.out.println("  │  3.  Create enrollment                           │");
        System.out.println("  │  4.  Update enrollment                           │");
        System.out.println("  │  5.  Cancel enrollment                           │");
        System.out.println("  │  6.  Confirm attendance & submit feedback        │");
        System.out.println("  │  7.  View my enrollments                         │");
        System.out.println("  │  8.  Generate reports                            │");
        System.out.println("  │  9.  List all participants                       │");
        System.out.println("  │  0.  Exit                                        │");
        System.out.println("  └──────────────────────────────────────────────────┘");
    }

    private void printSessionHeader() {
        System.out.printf("%n  %-6s %-5s %-10s %-12s %-20s %-7s %-6s%n",
                "ID", "Wk", "Day", "Window", "Activity", "Price", "Spots");
        System.out.println("  " + "─".repeat(70));
    }

    private void printAvailableSessions() {
        System.out.println("\n  Available sessions (with open spots):");
        printSessionHeader();
        sessions.stream()
                .filter(ActivitySession::hasCapacity)
                .limit(20)
                .forEach(s -> System.out.printf(
                        "  %-6s %-5d %-10s %-12s %-20s £%-6.2f %d/%d%n",
                        s.getSessionId(), s.getWeekNumber(),
                        s.getDay(), s.getWindow(),
                        s.getActivityName(), s.getPrice(),
                        s.availableSpots(), ActivitySession.CAPACITY_LIMIT));
        System.out.println("  (Showing first 20. Use timetable view for full list.)");
        System.out.println();
    }

    private void section(String title) {
        System.out.println("\n  ┄┄ " + title + " " + "┄".repeat(Math.max(0, 48 - title.length())));
        System.out.println();
    }

    // ── Input helpers ─────────────────────────────────────────────────────────

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = Integer.parseInt(scanner.nextLine().trim());
                if (v >= min && v <= max) return v;
                System.out.println("  Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input — please enter a whole number.");
            }
        }
    }

    private Participant findParticipant(String id) {
        return participants.stream()
                .filter(p -> p.getId().equalsIgnoreCase(id))
                .findFirst().orElse(null);
    }

    private ActivitySession findSession(String id) {
        return sessions.stream()
                .filter(s -> s.getSessionId().equalsIgnoreCase(id))
                .findFirst().orElse(null);
    }
}
