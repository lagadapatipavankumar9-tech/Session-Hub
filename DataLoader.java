/*package sessionhub.bootstrap;

import sessionhub.core.*;
import sessionhub.engine.EnrollmentManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Loads all pre-defined sample data into the system at startup.
 *
 * Provides:
 *   - 10 participants  (P01 – P10)
 *   - 48 sessions      (8 weekends × 6 sessions each)
 *   - 22 completed enrollments with feedback notes
 *
 * Exercise types and prices (fixed):
 *   Power Sculpt       £15.00
 *   Metabolic Burn     £20.00
 *   Dynamic Stretch    £12.00
 *   Functional Fitness £18.00
 *
 * Timetable rotation per weekend:
 *   Saturday — Morning: Power Sculpt, Afternoon: Metabolic Burn, Evening: Functional Fitness
 *   Sunday   — Morning: Dynamic Stretch, Afternoon: Power Sculpt, Evening: Metabolic Burn
 */
/*public class DataLoader {

    // Fixed prices per exercise type
    private static final double PRICE_POWER_SCULPT       = 15.00;
    private static final double PRICE_METABOLIC_BURN     = 20.00;
    private static final double PRICE_DYNAMIC_STRETCH    = 12.00;
    private static final double PRICE_FUNCTIONAL_FITNESS = 18.00;

    // ── Public entry points ───────────────────────────────────────────────────

    public List<Participant> loadParticipants() {
        return Arrays.asList(
            new Participant("P01", "Alice Carter"),
            new Participant("P02", "Brian Stone"),
            new Participant("P03", "Clara Hughes"),
            new Participant("P04", "David Wong"),
            new Participant("P05", "Ella Johnson"),
            new Participant("P06", "Franklin Moore"),
            new Participant("P07", "Grace Patel"),
            new Participant("P08", "Henry Smith"),
            new Participant("P09", "Isla Brown"),
            new Participant("P10", "Jack Wilson")
        );
    }

    public List<ActivitySession> loadSessions() {
        List<ActivitySession> sessions = new ArrayList<>();
        int counter = 1;

        for (int week = 1; week <= 8; week++) {
            // Saturday
            sessions.add(new ActivitySession("S" + fmt(counter++), "Power Sculpt",
                    DayCategory.SATURDAY, SessionWindow.MORNING,   week, PRICE_POWER_SCULPT));
            sessions.add(new ActivitySession("S" + fmt(counter++), "Metabolic Burn",
                    DayCategory.SATURDAY, SessionWindow.AFTERNOON, week, PRICE_METABOLIC_BURN));
            sessions.add(new ActivitySession("S" + fmt(counter++), "Functional Fitness",
                    DayCategory.SATURDAY, SessionWindow.EVENING,   week, PRICE_FUNCTIONAL_FITNESS));
            // Sunday
            sessions.add(new ActivitySession("S" + fmt(counter++), "Dynamic Stretch",
                    DayCategory.SUNDAY,   SessionWindow.MORNING,   week, PRICE_DYNAMIC_STRETCH));
            sessions.add(new ActivitySession("S" + fmt(counter++), "Power Sculpt",
                    DayCategory.SUNDAY,   SessionWindow.AFTERNOON, week, PRICE_POWER_SCULPT));
            sessions.add(new ActivitySession("S" + fmt(counter++), "Metabolic Burn",
                    DayCategory.SUNDAY,   SessionWindow.EVENING,   week, PRICE_METABOLIC_BURN));
        }
        return sessions;
    }

    /**
     * Seeds historical attendance and reviews using the provided manager and participants.
     * Call this after loadSessions() and loadParticipants().
     */
    /*public void seedAttendance(EnrollmentManager manager,
                                List<ActivitySession> sessions,
                                List<Participant> participants) {

        // Helper: find session by ID
        // Week 1 — Saturday Morning: Power Sculpt (S001)
        attend(manager, find(participants,"P01"), find(sessions,"S001"), 5, "Fantastic energy from the instructor!");
        attend(manager, find(participants,"P02"), find(sessions,"S001"), 4, "Great strength workout, felt results.");
        attend(manager, find(participants,"P03"), find(sessions,"S001"), 5, "Best start to the weekend!");

        // Week 1 — Saturday Afternoon: Metabolic Burn (S002)
        attend(manager, find(participants,"P04"), find(sessions,"S002"), 4, "High intensity — loved every minute.");
        attend(manager, find(participants,"P05"), find(sessions,"S002"), 3, "Good session, a bit fast-paced for me.");

        // Week 1 — Sunday Morning: Dynamic Stretch (S004)
        attend(manager, find(participants,"P06"), find(sessions,"S004"), 5, "Perfect recovery session. Very calming.");
        attend(manager, find(participants,"P07"), find(sessions,"S004"), 4, "Well structured and relaxing.");

        // Week 2 — Saturday Evening: Functional Fitness (S009)
        attend(manager, find(participants,"P01"), find(sessions,"S009"), 4, "Full body workout — great variety.");
        attend(manager, find(participants,"P08"), find(sessions,"S009"), 5, "Outstanding class, pushing my limits.");

        // Week 2 — Sunday Evening: Metabolic Burn (S012)
        attend(manager, find(participants,"P09"), find(sessions,"S012"), 3, "Tough but fair. Would attend again.");
        attend(manager, find(participants,"P10"), find(sessions,"S012"), 5, "Excellent class, loved the challenge.");

        // Week 3 — Saturday Morning: Power Sculpt (S013)
        attend(manager, find(participants,"P02"), find(sessions,"S013"), 5, "Even better second time around.");
        attend(manager, find(participants,"P04"), find(sessions,"S013"), 4, "Consistent quality. No complaints.");

        // Week 3 — Sunday Morning: Dynamic Stretch (S016)
        attend(manager, find(participants,"P05"), find(sessions,"S016"), 4, "Helped my flexibility noticeably.");
        attend(manager, find(participants,"P06"), find(sessions,"S016"), 3, "Good, but music was too loud.");

        // Week 4 — Saturday Afternoon: Metabolic Burn (S020)
        attend(manager, find(participants,"P07"), find(sessions,"S020"), 5, "Instructor was incredibly motivating.");
        attend(manager, find(participants,"P08"), find(sessions,"S020"), 4, "Great interval structure. Very effective.");

        // Week 4 — Sunday Afternoon: Power Sculpt (S023)
        attend(manager, find(participants,"P09"), find(sessions,"S023"), 5, "My favourite class so far.");
        attend(manager, find(participants,"P10"), find(sessions,"S023"), 4, "Good workout. Will book again.");

        // Week 5 — Saturday Morning: Power Sculpt (S025)
        attend(manager, find(participants,"P03"), find(sessions,"S025"), 5, "Surpassed my expectations again.");

        // Week 5 — Saturday Evening: Functional Fitness (S027)
        attend(manager, find(participants,"P01"), find(sessions,"S027"), 4, "Challenging and rewarding.");

        // Week 5 — Sunday Evening: Metabolic Burn (S030)
        attend(manager, find(participants,"P02"), find(sessions,"S030"), 5, "High-energy and well-paced session.");

        // Active (not yet attended) bookings for demo purposes
        silentBook(manager, find(participants,"P03"), find(sessions,"S031")); // Wk6 Sat Morning
        silentBook(manager, find(participants,"P04"), find(sessions,"S034")); // Wk6 Sun Morning
        silentBook(manager, find(participants,"P05"), find(sessions,"S037")); // Wk7 Sat Morning
        silentBook(manager, find(participants,"P06"), find(sessions,"S044")); // Wk8 Sat Morning
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String fmt(int n) {
        return String.format("%03d", n);
    }

    private Participant findParticipant(List<Participant> list, String id) {
        return list.stream().filter(p -> p.getId().equals(id)).findFirst().orElseThrow();
    }

    private Participant find(List<Participant> list, String id) {
        return findParticipant(list, id);
    }

    private ActivitySession find(List<ActivitySession> list, String id) {
        return list.stream().filter(s -> s.getId().equals(id)).findFirst().orElseThrow();
    }

    private void attend(EnrollmentManager manager, Participant p, ActivitySession s,
                        int rating, String comment) {
        Enrollment e = manager.createEnrollment(p, s);
        if (e != null) {
            manager.confirmAttendance(e.getId(), rating, comment);
        }
    }

    private void silentBook(EnrollmentManager manager, Participant p, ActivitySession s) {
        manager.createEnrollment(p, s);
    }
}
*/

package sessionhub.bootstrap;

import sessionhub.core.*;
        import sessionhub.engine.EnrollmentManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Loads all pre-defined sample data into the system at startup.
 *
 * Provides:
 *   - 10 participants  (P01 – P10)
 *   - 48 sessions      (8 weekends × 6 sessions each)
 *   - 22 completed enrollments with feedback notes
 *
 * Exercise types and prices (fixed):
 *   Power Sculpt       £15.00
 *   Metabolic Burn     £20.00
 *   Dynamic Stretch    £12.00
 *   Functional Fitness £18.00
 *
 * Timetable rotation per weekend:
 *   Saturday — Morning: Power Sculpt, Afternoon: Metabolic Burn, Evening: Functional Fitness
 *   Sunday   — Morning: Dynamic Stretch, Afternoon: Power Sculpt, Evening: Metabolic Burn
 */
public class DataLoader {

    // Fixed prices per exercise type
    private static final double PRICE_POWER_SCULPT       = 15.00;
    private static final double PRICE_METABOLIC_BURN     = 20.00;
    private static final double PRICE_DYNAMIC_STRETCH    = 12.00;
    private static final double PRICE_FUNCTIONAL_FITNESS = 18.00;

    // ── Public entry points ───────────────────────────────────────────────────

    public List<Participant> loadParticipants() {
        return Arrays.asList(
                new Participant("P01", "Alice Carter"),
                new Participant("P02", "Brian Stone"),
                new Participant("P03", "Clara Hughes"),
                new Participant("P04", "David Wong"),
                new Participant("P05", "Ella Johnson"),
                new Participant("P06", "Franklin Moore"),
                new Participant("P07", "Grace Patel"),
                new Participant("P08", "Henry Smith"),
                new Participant("P09", "Isla Brown"),
                new Participant("P10", "Jack Wilson")
        );
    }

    public List<ActivitySession> loadSessions() {
        List<ActivitySession> sessions = new ArrayList<>();
        int counter = 1;

        for (int week = 1; week <= 8; week++) {
            // Saturday
            sessions.add(new ActivitySession("S" + fmt(counter++), "Power Sculpt",
                    DayCategory.SATURDAY, SessionWindow.MORNING,   week, PRICE_POWER_SCULPT));
            sessions.add(new ActivitySession("S" + fmt(counter++), "Metabolic Burn",
                    DayCategory.SATURDAY, SessionWindow.AFTERNOON, week, PRICE_METABOLIC_BURN));
            sessions.add(new ActivitySession("S" + fmt(counter++), "Functional Fitness",
                    DayCategory.SATURDAY, SessionWindow.EVENING,   week, PRICE_FUNCTIONAL_FITNESS));
            // Sunday
            sessions.add(new ActivitySession("S" + fmt(counter++), "Dynamic Stretch",
                    DayCategory.SUNDAY,   SessionWindow.MORNING,   week, PRICE_DYNAMIC_STRETCH));
            sessions.add(new ActivitySession("S" + fmt(counter++), "Power Sculpt",
                    DayCategory.SUNDAY,   SessionWindow.AFTERNOON, week, PRICE_POWER_SCULPT));
            sessions.add(new ActivitySession("S" + fmt(counter++), "Metabolic Burn",
                    DayCategory.SUNDAY,   SessionWindow.EVENING,   week, PRICE_METABOLIC_BURN));
        }
        return sessions;
    }

    /**
     * Seeds historical attendance and reviews using the provided manager and participants.
     * Call this after loadSessions() and loadParticipants().
     */
    public void seedAttendance(EnrollmentManager manager,
                               List<ActivitySession> sessions,
                               List<Participant> participants) {

        // Week 1 — Saturday Morning: Power Sculpt (S001)
        attend(manager, findParticipant(participants,"P01"), findSession(sessions,"S001"), 5, "Fantastic energy from the instructor!");
        attend(manager, findParticipant(participants,"P02"), findSession(sessions,"S001"), 4, "Great strength workout, felt results.");
        attend(manager, findParticipant(participants,"P03"), findSession(sessions,"S001"), 5, "Best start to the weekend!");

        // Week 1 — Saturday Afternoon: Metabolic Burn (S002)
        attend(manager, findParticipant(participants,"P04"), findSession(sessions,"S002"), 4, "High intensity — loved every minute.");
        attend(manager, findParticipant(participants,"P05"), findSession(sessions,"S002"), 3, "Good session, a bit fast-paced for me.");

        // Week 1 — Sunday Morning: Dynamic Stretch (S004)
        attend(manager, findParticipant(participants,"P06"), findSession(sessions,"S004"), 5, "Perfect recovery session. Very calming.");
        attend(manager, findParticipant(participants,"P07"), findSession(sessions,"S004"), 4, "Well structured and relaxing.");

        // Week 2 — Saturday Evening: Functional Fitness (S009)
        attend(manager, findParticipant(participants,"P01"), findSession(sessions,"S009"), 4, "Full body workout — great variety.");
        attend(manager, findParticipant(participants,"P08"), findSession(sessions,"S009"), 5, "Outstanding class, pushing my limits.");

        // Week 2 — Sunday Evening: Metabolic Burn (S012)
        attend(manager, findParticipant(participants,"P09"), findSession(sessions,"S012"), 3, "Tough but fair. Would attend again.");
        attend(manager, findParticipant(participants,"P10"), findSession(sessions,"S012"), 5, "Excellent class, loved the challenge.");

        // Week 3 — Saturday Morning: Power Sculpt (S013)
        attend(manager, findParticipant(participants,"P02"), findSession(sessions,"S013"), 5, "Even better second time around.");
        attend(manager, findParticipant(participants,"P04"), findSession(sessions,"S013"), 4, "Consistent quality. No complaints.");

        // Week 3 — Sunday Morning: Dynamic Stretch (S016)
        attend(manager, findParticipant(participants,"P05"), findSession(sessions,"S016"), 4, "Helped my flexibility noticeably.");
        attend(manager, findParticipant(participants,"P06"), findSession(sessions,"S016"), 3, "Good, but music was too loud.");

        // Week 4 — Saturday Afternoon: Metabolic Burn (S020)
        attend(manager, findParticipant(participants,"P07"), findSession(sessions,"S020"), 5, "Instructor was incredibly motivating.");
        attend(manager, findParticipant(participants,"P08"), findSession(sessions,"S020"), 4, "Great interval structure. Very effective.");

        // Week 4 — Sunday Afternoon: Power Sculpt (S023)
        attend(manager, findParticipant(participants,"P09"), findSession(sessions,"S023"), 5, "My favourite class so far.");
        attend(manager, findParticipant(participants,"P10"), findSession(sessions,"S023"), 4, "Good workout. Will book again.");

        // Week 5 — Saturday Morning: Power Sculpt (S025)
        attend(manager, findParticipant(participants,"P03"), findSession(sessions,"S025"), 5, "Surpassed my expectations again.");

        // Week 5 — Saturday Evening: Functional Fitness (S027)
        attend(manager, findParticipant(participants,"P01"), findSession(sessions,"S027"), 4, "Challenging and rewarding.");

        // Week 5 — Sunday Evening: Metabolic Burn (S030)
        attend(manager, findParticipant(participants,"P02"), findSession(sessions,"S030"), 5, "High-energy and well-paced session.");

        // Active (not yet attended) bookings for demo purposes
        silentBook(manager, findParticipant(participants,"P03"), findSession(sessions,"S031")); // Wk6 Sat Morning
        silentBook(manager, findParticipant(participants,"P04"), findSession(sessions,"S034")); // Wk6 Sun Morning
        silentBook(manager, findParticipant(participants,"P05"), findSession(sessions,"S037")); // Wk7 Sat Morning
        silentBook(manager, findParticipant(participants,"P06"), findSession(sessions,"S044")); // Wk8 Sat Morning
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String fmt(int n) {
        return String.format("%03d", n);
    }

    private Participant findParticipant(List<Participant> list, String id) {
        return list.stream().filter(p -> p.getId().equals(id)).findFirst().orElseThrow();
    }

    private ActivitySession findSession(List<ActivitySession> list, String id) {
        return list.stream().filter(s -> s.getId().equals(id)).findFirst().orElseThrow();
    }

    private void attend(EnrollmentManager manager, Participant p, ActivitySession s,
                        int rating, String comment) {
        Enrollment e = manager.createEnrollment(p, s);
        if (e != null) {
            manager.confirmAttendance(e.getId(), rating, comment);
        }
    }

    private void silentBook(EnrollmentManager manager, Participant p, ActivitySession s) {
        manager.createEnrollment(p, s);
    }
}