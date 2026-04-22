package sessionhub.bootstrap;

import sessionhub.console.TerminalController;
import sessionhub.core.ActivitySession;
import sessionhub.core.Participant;
import sessionhub.engine.EnrollmentManager;
import sessionhub.engine.ReportGenerator;

import java.util.List;

/**
 * Entry point for the Furzefield Leisure Centre SessionHub system.
 *
 * Boot sequence:
 *   1. DataLoader creates participants and sessions
 *   2. DataLoader seeds historical attendance data
 *   3. TerminalController starts the interactive CLI
 */
public class Main {

    public static void main(String[] args) {
        DataLoader loader = new DataLoader();

        List<Participant>    participants = loader.loadParticipants();
        List<ActivitySession> sessions   = loader.loadSessions();

        EnrollmentManager manager = new EnrollmentManager();
        ReportGenerator   reports = new ReportGenerator();

        // Suppress startup seed output by temporarily redirecting — or just let it show
        loader.seedAttendance(manager, sessions, participants);

        TerminalController terminal = new TerminalController(manager, reports, sessions, participants);
        terminal.showMenu();
    }
}
