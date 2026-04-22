package sessionhub.engine;

import sessionhub.core.ActivitySession;
import sessionhub.core.FeedbackNote;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates the two management reports required by the specification.
 *
 * Both reports count only COMPLETED enrollments.
 */
public class ReportGenerator {

    private static final String LINE = "─".repeat(72);

    // ── Attendance report ─────────────────────────────────────────────────────

    /**
     * Prints attendee count and average satisfaction rating per session.
     * Only sessions with at least one completed attendance are listed.
     */
    public void generateAttendanceReport(List<ActivitySession> sessions) {
        System.out.println("\n" + "═".repeat(72));
        System.out.println("  FLC GROUP EXERCISE — ATTENDANCE & SATISFACTION REPORT");
        System.out.println("═".repeat(72));
        System.out.printf("  %-8s %-20s %-10s %-12s %-6s %-9s %-10s%n",
                "ID", "Activity", "Day", "Window", "Wk", "Attended", "Avg Rating");
        System.out.println("  " + LINE);

        sessions.stream()
                .filter(s -> s.completedAttendeeCount() > 0)
                .sorted(Comparator.comparingInt(ActivitySession::getWeekNumber)
                        .thenComparing(ActivitySession::getDay)
                        .thenComparing(ActivitySession::getWindow))
                .forEach(s -> System.out.printf(
                        "  %-8s %-20s %-10s %-12s %-6d %-9d %.2f / 5%n",
                        s.getSessionId(),
                        s.getActivityName(),
                        s.getDay(),
                        s.getWindow(),
                        s.getWeekNumber(),
                        s.completedAttendeeCount(),
                        s.calculateAverageRating()));

        System.out.println("═".repeat(72) + "\n");
    }

    // ── Income report ─────────────────────────────────────────────────────────

    /**
     * Prints total income per activity type and highlights the highest earner.
     * Only COMPLETED enrollments contribute to income.
     */
    public void generateIncomeReport(List<ActivitySession> sessions) {
        Map<String, Double> incomeMap = sessions.stream()
                .collect(Collectors.groupingBy(
                        ActivitySession::getActivityName,
                        Collectors.summingDouble(ActivitySession::calculateIncome)));

        if (incomeMap.values().stream().allMatch(v -> v == 0.0)) {
            System.out.println("\n  No income data available — no completed attendances recorded.\n");
            return;
        }

        String topActivity = findHighestEarningActivity(sessions);

        System.out.println("\n" + "═".repeat(56));
        System.out.println("  FLC GROUP EXERCISE — INCOME REPORT");
        System.out.println("═".repeat(56));

        incomeMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    String badge = entry.getKey().equals(topActivity) ? "  ◉ TOP EARNER" : "";
                    System.out.printf("  %-22s  £%,9.2f%s%n",
                            entry.getKey(), entry.getValue(), badge);
                });

        System.out.println("  " + LINE.substring(0, 54));
        System.out.printf("  Highest earning activity : %s  (£%,.2f)%n",
                topActivity, incomeMap.get(topActivity));
        System.out.println("═".repeat(56) + "\n");
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    public String findHighestEarningActivity(List<ActivitySession> sessions) {
        return sessions.stream()
                .collect(Collectors.groupingBy(
                        ActivitySession::getActivityName,
                        Collectors.summingDouble(ActivitySession::calculateIncome)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }
}
