package sessionhub.engine;

import sessionhub.core.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Central engine for the SessionHub booking system.
 *
 * All business rules enforced here:
 *   - Capacity limit (max 4 per session)
 *   - Duplicate enrollment prevention
 *   - Time-conflict detection (same participant, same day/window/week)
 *   - Unique, non-recycled enrollment ID generation (ENR-XXXX)
 *   - Enrollment lifecycle management
 */
public class EnrollmentManager {

    private final Map<String, Enrollment> enrollments = new LinkedHashMap<>();
    private int idCounter = 1;

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Creates a new enrollment after enforcing all constraints.
     *
     * @return the new Enrollment on success, or null on failure
     */
    public Enrollment createEnrollment(Participant participant, ActivitySession session) {

        // 1. Capacity check
        if (!session.hasCapacity()) {
            System.out.println("  Error: Session capacity exceeded — no spots available.");
            return null;
        }

        // 2. Duplicate check — same participant already enrolled in this session
        boolean duplicate = enrollments.values().stream()
                .anyMatch(e -> e.getParticipant().getId().equals(participant.getId())
                            && e.getSession().getSessionId().equals(session.getSessionId())
                            && e.isActive());
        if (duplicate) {
            System.out.println("  Error: " + participant.getName()
                    + " is already enrolled in session " + session.getSessionId() + ".");
            return null;
        }

        // 3. Time-conflict check — same participant, same day/window/week
        boolean conflict = enrollments.values().stream()
                .filter(e -> e.getParticipant().getId().equals(participant.getId()))
                .filter(Enrollment::isActive)
                .anyMatch(e -> e.getSession().getDay()        == session.getDay()
                            && e.getSession().getWindow()     == session.getWindow()
                            && e.getSession().getWeekNumber() == session.getWeekNumber());
        if (conflict) {
            System.out.println("  Conflict detected: overlapping session timing for "
                    + participant.getName() + " on " + session.getDay()
                    + " " + session.getWindow() + " (Week " + session.getWeekNumber() + ").");
            return null;
        }

        // All checks passed — create enrollment
        String id = generateId();
        Enrollment enrollment = new Enrollment(id, participant, session);
        enrollments.put(id, enrollment);
        session.addEnrollment(enrollment);
        System.out.println("  Enrollment successful. Reference: " + id);
        return enrollment;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /**
     * Transfers an enrollment to a different session.
     */
    public void updateEnrollment(String enrollmentId, ActivitySession newSession) {
        Enrollment e = enrollments.get(enrollmentId);

        if (e == null || !e.isActive()) {
            System.out.println("  Error: No active enrollment found with ID " + enrollmentId + ".");
            return;
        }
        if (!newSession.hasCapacity()) {
            System.out.println("  Error: Target session is full. Transfer cannot proceed.");
            return;
        }
        if (e.getSession().getSessionId().equals(newSession.getSessionId())) {
            System.out.println("  Error: Target session must differ from the current one.");
            return;
        }

        // Check time conflict for new slot (exclude current enrollment)
        boolean conflict = enrollments.values().stream()
                .filter(r -> r.getParticipant().getId().equals(e.getParticipant().getId()))
                .filter(Enrollment::isActive)
                .filter(r -> !r.getId().equals(enrollmentId))
                .anyMatch(r -> r.getSession().getDay()        == newSession.getDay()
                            && r.getSession().getWindow()     == newSession.getWindow()
                            && r.getSession().getWeekNumber() == newSession.getWeekNumber());
        if (conflict) {
            System.out.println("  Conflict detected: overlapping session timing at new slot.");
            return;
        }

        e.getSession().removeEnrollment(enrollmentId);
        newSession.addEnrollment(e);
        e.updateSession(newSession);
        System.out.println("  Enrollment updated. Now attending: " + newSession.getSessionId());
    }

    // ── Cancel ────────────────────────────────────────────────────────────────

    /**
     * Cancels an enrollment. The record is retained with CANCELLED status; ID is never reused.
     */
    public void cancelEnrollment(String enrollmentId) {
        Enrollment e = enrollments.get(enrollmentId);

        if (e == null || !e.isActive()) {
            System.out.println("  Error: No active enrollment found with ID " + enrollmentId + ".");
            return;
        }

        e.getSession().removeEnrollment(enrollmentId);
        e.cancel();
        System.out.println("  Enrollment cancelled. Spot released for session "
                + e.getSession().getSessionId() + ".");
    }

    // ── Attend ────────────────────────────────────────────────────────────────

    /**
     * Marks an enrollment as completed and records participant feedback.
     */
    public void confirmAttendance(String enrollmentId, int rating, String comment) {
        Enrollment e = enrollments.get(enrollmentId);

        if (e == null) {
            System.out.println("  Error: Enrollment ID not found.");
            return;
        }
        if (e.isCancelled()) {
            System.out.println("  Error: Cannot confirm attendance on a cancelled enrollment.");
            return;
        }
        if (e.isCompleted()) {
            System.out.println("  Error: Attendance already confirmed for this enrollment.");
            return;
        }
        if (rating < 1 || rating > 5) {
            System.out.println("  Error: Rating must be between 1 and 5.");
            return;
        }

        e.markCompleted();
        FeedbackNote note = new FeedbackNote(
                "FB-" + enrollmentId, e.getParticipant(), e.getSession(), rating, comment);
        e.getSession().addFeedback(note);
        System.out.println("  Attendance confirmed with feedback. Thank you, "
                + e.getParticipant().getName() + "!");
    }

    // ── Timetable views ───────────────────────────────────────────────────────

    public List<ActivitySession> viewTimetableByDay(List<ActivitySession> sessions, DayCategory day) {
        return sessions.stream()
                .filter(s -> s.getDay() == day)
                .sorted(Comparator.comparingInt(ActivitySession::getWeekNumber)
                        .thenComparing(ActivitySession::getWindow))
                .collect(Collectors.toList());
    }

    public List<ActivitySession> viewTimetableByActivity(List<ActivitySession> sessions, String activityName) {
        return sessions.stream()
                .filter(s -> s.getActivityName().equalsIgnoreCase(activityName))
                .sorted(Comparator.comparingInt(ActivitySession::getWeekNumber)
                        .thenComparing(ActivitySession::getDay)
                        .thenComparing(ActivitySession::getWindow))
                .collect(Collectors.toList());
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public Optional<Enrollment> findById(String enrollmentId) {
        return Optional.ofNullable(enrollments.get(enrollmentId));
    }

    public List<Enrollment> activeEnrollmentsFor(String participantId) {
        return enrollments.values().stream()
                .filter(e -> e.getParticipant().getId().equals(participantId) && e.isActive())
                .collect(Collectors.toList());
    }

    public Collection<Enrollment> allEnrollments() {
        return Collections.unmodifiableCollection(enrollments.values());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String generateId() {
        return String.format("ENR-%04d", idCounter++);
    }
}
