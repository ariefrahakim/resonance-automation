package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Generates a test case document in Excel (.xlsx) format.
 *
 * Run as main() to produce docs/test_cases.xlsx.
 * The output contains two sheets:
 *   - "API Test Cases"           — REST Assured + TestNG tests (data from DataProvider classes)
 *   - "Web Test Cases (Gherkin)" — Selenium + Cucumber BDD tests (data from feature files)
 *
 * Credentials in test data reference config.properties keys (${usernameOrEmailResonance})
 * rather than literal values, consistent with the automation framework itself.
 */
public class TestCaseDocGenerator {

    public static void main(String[] args) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();

        CellStyle headerStyle   = createHeaderStyle(wb);
        CellStyle titleStyle    = createTitleStyle(wb);
        CellStyle positiveStyle = createStatusStyle(wb, new byte[]{(byte)198,(byte)239,(byte)206}, (byte)0,(byte)97,(byte)0);
        CellStyle negativeStyle = createStatusStyle(wb, new byte[]{(byte)255,(byte)199,(byte)206}, (byte)156,(byte)0,(byte)6);
        CellStyle normalStyle   = createNormalStyle(wb);

        createApiSheet(wb, headerStyle, titleStyle, positiveStyle, negativeStyle, normalStyle);
        createWebSheet(wb, headerStyle, titleStyle, positiveStyle, negativeStyle, normalStyle);

        String outputPath = "docs/test_cases.xlsx";
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            wb.write(fos);
        }
        wb.close();
        System.out.println("Test case document generated: " + outputPath);
    }

    // ================================================================
    //  API SHEET — REST Assured + TestNG
    // ================================================================
    private static void createApiSheet(XSSFWorkbook wb, CellStyle header, CellStyle title,
                                        CellStyle pos, CellStyle neg, CellStyle normal) {
        XSSFSheet sheet = wb.createSheet("API Test Cases");
        sheet.setColumnWidth(0,  4000);   // TC ID
        sheet.setColumnWidth(1,  7000);   // Module
        sheet.setColumnWidth(2,  16000);  // Test Case Name
        sheet.setColumnWidth(3,  20000);  // Test Step / Request
        sheet.setColumnWidth(4,  16000);  // Test Data
        sheet.setColumnWidth(5,  14000);  // Expected Result
        sheet.setColumnWidth(6,  5000);   // Exp. Status
        sheet.setColumnWidth(7,  5000);   // Type

        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RESONANCE — API Test Cases (REST Assured + TestNG)");
        titleCell.setCellStyle(title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

        String[] cols = {"TC ID", "Module", "Test Case Name", "Test Step / Request",
                         "Test Data", "Expected Result", "Exp. Status", "Type"};
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < cols.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(header);
        }

        Object[][] data = {
            // ── AUTH ──────────────────────────────────────────────
            {"TC-API-001", "Auth", "Login succeeds with valid credentials",
             "POST /api/rest/login",
             "usernameOrEmail: ${usernameOrEmailResonance}, password: ${passwordResonance}",
             "Returns Bearer token, status 200, ok: true", "200", "Positive"},

            {"TC-API-002", "Auth", "Login fails — unregistered email",
             "POST /api/rest/login",
             "usernameOrEmail: unknown@fake.com, password: password",
             "Status != 200, error returned", "401", "Negative"},

            {"TC-API-003", "Auth", "Login fails — wrong password",
             "POST /api/rest/login",
             "usernameOrEmail: ${usernameOrEmailResonance}, password: wrongpassword",
             "Status != 200", "4xx", "Negative"},

            {"TC-API-004", "Auth", "Login fails — empty username",
             "POST /api/rest/login",
             "usernameOrEmail: (empty), password: password",
             "Status != 200, required field validation", "4xx", "Negative"},

            {"TC-API-005", "Auth", "Login fails — empty password",
             "POST /api/rest/login",
             "usernameOrEmail: ${usernameOrEmailResonance}, password: (empty)",
             "Status != 200", "4xx", "Negative"},

            {"TC-API-006", "Auth", "Login fails — password too short (minLength: 4)",
             "POST /api/rest/login",
             "usernameOrEmail: ${usernameOrEmailResonance}, password: ab",
             "Status != 200, minLength violation", "4xx", "Negative"},

            {"TC-API-007", "Auth", "Login fails — password too long (maxLength: 12)",
             "POST /api/rest/login",
             "usernameOrEmail: ${usernameOrEmailResonance}, password: passwordyang13ch",
             "Status != 200, maxLength violation", "4xx", "Negative"},

            {"TC-API-008", "Auth", "Login fails — both fields empty",
             "POST /api/rest/login",
             "usernameOrEmail: (empty), password: (empty)",
             "Status != 200", "4xx", "Negative"},

            {"TC-API-009", "Auth", "Login fails — username without @ symbol",
             "POST /api/rest/login",
             "usernameOrEmail: notanemailformat, password: password",
             "Status != 200, unregistered identifier", "401", "Negative"},

            {"TC-API-010", "Auth", "Login fails — unregistered username 'test'",
             "POST /api/rest/login",
             "usernameOrEmail: test, password: password",
             "Status != 200, user does not exist", "401", "Negative"},

            // ── TICKET POSITIVE ──────────────────────────────────
            {"TC-API-011", "Ticket", "Create a public ticket",
             "POST /api/rest/createTicket — Authorization: Bearer <token>",
             "title: 'Public Ticket Automation', description: '...', isPublic: true",
             "Ticket created, ID returned, status 200", "200", "Positive"},

            {"TC-API-012", "Ticket", "Create a private ticket",
             "POST /api/rest/createTicket — Authorization: Bearer <token>",
             "title: 'Private Ticket Automation', isPublic: false",
             "Private ticket created, isPublic: false, status 200", "200", "Positive"},

            {"TC-API-013", "Ticket", "Create a public bug report ticket",
             "POST /api/rest/createTicket — Authorization: Bearer <token>",
             "title: 'Bug: Login page error', isPublic: true",
             "Bug report ticket created, status 200", "200", "Positive"},

            {"TC-API-014", "Ticket", "Get active tickets — order by VOTE",
             "GET /api/rest/activeTickets?date=...&order=VOTE",
             "order: VOTE",
             "Returns ticket array sorted by vote count, status 200", "200", "Positive"},

            {"TC-API-015", "Ticket", "Get active tickets — order by NEWEST",
             "GET /api/rest/activeTickets?date=...&order=NEWEST",
             "order: NEWEST",
             "Returns ticket array sorted by creation date, status 200", "200", "Positive"},

            {"TC-API-016", "Ticket", "Get active tickets — order by SOLVE",
             "GET /api/rest/activeTickets?date=...&order=SOLVE",
             "order: SOLVE",
             "Returns ticket array sorted by solved status, status 200", "200", "Positive"},

            {"TC-API-017", "Ticket", "Get my tickets",
             "GET /api/rest/myTickets?limit=5&page=0",
             "limit: 5, page: 0",
             "Returns ticket array owned by the logged-in user, status 200", "200", "Positive"},

            {"TC-API-018", "Ticket", "Get ticket by ID",
             "GET /api/rest/ticketById?id=<ticketId>",
             "id: (ID from previous create test)",
             "Returns ticket detail with matching ID, status 200", "200", "Positive"},

            {"TC-API-019", "Ticket", "Count active tickets",
             "GET /api/rest/countActiveTickets?date=...",
             "date: (current date)",
             "Returns a number (ticket count), status 200", "200", "Positive"},

            {"TC-API-020", "Ticket", "Search active tickets by keyword",
             "GET /api/rest/activeTickets?date=...&search=Automation",
             "search: Automation",
             "Returns matching tickets, status 200", "200", "Positive"},

            {"TC-API-021", "Ticket", "Mark ticket as Solved",
             "PUT /api/rest/updateStatusTicket",
             "ticketId: ..., solvedAt: <timestamp>",
             "solvedAt is set, status 200", "200", "Positive"},

            {"TC-API-022", "Ticket", "Mark ticket as Unsolved",
             "PUT /api/rest/updateStatusTicket",
             "ticketId: ..., solvedAt: null",
             "solvedAt is null, status 200", "200", "Positive"},

            {"TC-API-023", "Ticket", "Delete ticket",
             "DELETE /api/rest/deleteTicket?ticketId=...",
             "ticketId: (ID of created ticket)",
             "Ticket deleted, status 200", "200", "Positive"},

            // ── TICKET NEGATIVE ──────────────────────────────────
            {"TC-API-024", "Ticket", "Create ticket — empty title (minLength violation)",
             "POST /api/rest/createTicket — Authorization: Bearer <token>",
             "title: (empty), isPublic: true",
             "Status != 200, title minLength: 1 validation fails", "4xx", "Negative"},

            {"TC-API-025", "Ticket", "Create ticket without authentication",
             "POST /api/rest/createTicket — no Authorization header",
             "title: 'Test'",
             "Status 401/403 — request rejected", "401", "Negative"},

            {"TC-API-026", "Ticket", "Get ticket — non-existent ID",
             "GET /api/rest/ticketById?id=id-does-not-exist-12345",
             "id: id-does-not-exist-12345",
             "Status != 200", "4xx", "Negative"},

            {"TC-API-027", "Ticket", "Get ticket — empty ID",
             "GET /api/rest/ticketById?id=",
             "id: (empty)",
             "Status != 200", "4xx", "Negative"},

            {"TC-API-028", "Ticket", "Get active tickets — missing date parameter",
             "GET /api/rest/activeTickets (no date param)",
             "date: (not provided)",
             "Status != 200, date is required", "4xx", "Negative"},

            {"TC-API-029", "Ticket", "Delete ticket without authentication",
             "DELETE /api/rest/deleteTicket?ticketId=... — no Authorization header",
             "ticketId: valid",
             "Status 401/403", "401", "Negative"},

            // ── COMMENT POSITIVE ─────────────────────────────────
            {"TC-API-030", "Comment", "Create a comment on a ticket",
             "POST /api/rest/createComment — Authorization: Bearer <token>",
             "ticketId: ..., body: 'First comment from automation test'",
             "Comment ID returned, status 200", "200", "Positive"},

            {"TC-API-031", "Comment", "Get comments by ticket ID",
             "GET /api/rest/commentsByTicketId?ticketId=...&limit=10",
             "ticketId: ...",
             "Returns comment array, status 200", "200", "Positive"},

            {"TC-API-032", "Comment", "Update a comment",
             "PUT /api/rest/updateComment",
             "id: <commentId>, body: 'Updated comment text', attachment: ''",
             "Comment body updated, status 200", "200", "Positive"},

            {"TC-API-033", "Comment", "Delete a comment",
             "DELETE /api/rest/deleteComment?commentId=...",
             "commentId: (ID of created comment)",
             "Comment deleted, status 200", "200", "Positive"},

            // ── COMMENT NEGATIVE ─────────────────────────────────
            {"TC-API-034", "Comment", "Create comment — empty body (minLength violation)",
             "POST /api/rest/createComment — Authorization: Bearer <token>",
             "ticketId: ..., body: (empty)",
             "Status != 200, body minLength: 1 validation fails", "4xx", "Negative"},

            {"TC-API-035", "Comment", "Create comment — non-existent ticket",
             "POST /api/rest/createComment — Authorization: Bearer <token>",
             "ticketId: non-existent-ticket-xyz, body: 'Valid comment'",
             "Status != 200, ticket not found", "4xx", "Negative"},

            {"TC-API-036", "Comment", "Create comment without authentication",
             "POST /api/rest/createComment — no Authorization header",
             "ticketId: ..., body: 'Test'",
             "Status 401/403", "401", "Negative"},

            // ── PROGRESSION POSITIVE ─────────────────────────────
            {"TC-API-037", "Progression", "Create a ticket progression",
             "POST /api/rest/createProgression — Authorization: Bearer <token>",
             "ticketId: ..., title: 'Initial Investigation', description: '...'",
             "Progression ID returned, status 200", "200", "Positive"},

            {"TC-API-038", "Progression", "Get progressions by ticket ID",
             "GET /api/rest/progressionsByTicketId?ticketId=...",
             "ticketId: ...",
             "Returns progression array, status 200", "200", "Positive"},

            {"TC-API-039", "Progression", "Update a progression",
             "PUT /api/rest/updateProgression",
             "id: <progressionId>, title: 'Updated title', description: '...', attachment: ''",
             "Progression title updated, status 200", "200", "Positive"},

            {"TC-API-040", "Progression", "Delete a progression",
             "DELETE /api/rest/deleteProgression?id=...",
             "id: (ID of created progression)",
             "Progression deleted, status 200", "200", "Positive"},

            // ── PROGRESSION NEGATIVE ─────────────────────────────
            {"TC-API-041", "Progression", "Create progression — empty title (minLength violation)",
             "POST /api/rest/createProgression — Authorization: Bearer <token>",
             "ticketId: ..., title: (empty)",
             "Status != 200, title minLength: 1 validation fails", "4xx", "Negative"},

            {"TC-API-042", "Progression", "Create progression — non-existent ticket",
             "POST /api/rest/createProgression — Authorization: Bearer <token>",
             "ticketId: non-existent, title: 'Test'",
             "Status != 200", "4xx", "Negative"},

            {"TC-API-043", "Progression", "Create progression without authentication",
             "POST /api/rest/createProgression — no Authorization header",
             "ticketId: ..., title: 'Test'",
             "Status 401/403", "401", "Negative"},

            // ── VOTE POSITIVE ────────────────────────────────────
            {"TC-API-044", "Vote", "Check vote status on a ticket",
             "GET /api/rest/checkUserVote?ticketId=...",
             "ticketId: ...",
             "Returns hasVoted (boolean) and voteCount (number), status 200", "200", "Positive"},

            {"TC-API-045", "Vote", "Vote on a ticket (toggle on)",
             "POST /api/rest/voteTicket — Authorization: Bearer <token>",
             "ticketId: ...",
             "hasVoted: true, voteCount incremented, status 200", "200", "Positive"},

            {"TC-API-046", "Vote", "Unvote a ticket (toggle off)",
             "POST /api/rest/voteTicket — Authorization: Bearer <token>",
             "ticketId: ...",
             "hasVoted: false, voteCount decremented, status 200", "200", "Positive"},

            // ── VOTE NEGATIVE ─────────────────────────────────────
            {"TC-API-047", "Vote", "Vote without authentication",
             "POST /api/rest/voteTicket — no Authorization header",
             "ticketId: ...",
             "Status 401/403", "401", "Negative"},

            {"TC-API-048", "Vote", "Vote on a non-existent ticket",
             "POST /api/rest/voteTicket — Authorization: Bearer <token>",
             "ticketId: non-existent",
             "Status != 200", "4xx", "Negative"},

            {"TC-API-049", "Vote", "Check vote — missing ticketId query param",
             "GET /api/rest/checkUserVote (no ticketId)",
             "ticketId: (not provided)",
             "Status 400, ticketId is required", "400", "Negative"},

            {"TC-API-050", "Vote", "Check vote on a non-existent ticket",
             "GET /api/rest/checkUserVote?ticketId=non-existent",
             "ticketId: non-existent",
             "hasVoted: false (graceful — no error thrown)", "200", "Negative"},
        };

        int rowNum = 2;
        for (Object[] row : data) {
            Row r = sheet.createRow(rowNum++);
            for (int i = 0; i < row.length; i++) {
                Cell c = r.createCell(i);
                c.setCellValue(row[i].toString());
                c.setCellStyle("Positive".equals(row[row.length - 1].toString()) ? pos : neg);
            }
        }
    }

    // ================================================================
    //  WEB SHEET — Selenium + Cucumber/Gherkin
    // ================================================================
    private static void createWebSheet(XSSFWorkbook wb, CellStyle header, CellStyle title,
                                        CellStyle pos, CellStyle neg, CellStyle normal) {
        XSSFSheet sheet = wb.createSheet("Web Test Cases (Gherkin)");
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 7000);
        sheet.setColumnWidth(2, 14000);
        sheet.setColumnWidth(3, 18000);
        sheet.setColumnWidth(4, 20000);
        sheet.setColumnWidth(5, 18000);
        sheet.setColumnWidth(6, 5000);

        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RESONANCE — Web Test Cases (Selenium + Cucumber/Gherkin BDD)");
        titleCell.setCellStyle(title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        String[] cols = {"TC ID", "Feature", "Scenario",
                         "Given / Precondition", "When / Action", "Then / Expected Result", "Type"};
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < cols.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(header);
        }

        // Note: credentials shown as ${key} — resolved from config.properties at runtime
        Object[][] data = {
            // ── LOGIN ────────────────────────────────────────────
            {"TC-WEB-001", "Login", "Login succeeds with valid credentials",
             "I am on the login page",
             "Enter username '${usernameOrEmailResonance}', password '${passwordResonance}', click Login",
             "Redirected away from login page — URL no longer contains /login", "Positive"},

            {"TC-WEB-002", "Login", "Login fails — unregistered email",
             "I am on the login page",
             "Enter username 'unregistered_user@fake.com', password 'password', click Login",
             "Error toast visible or stay on login page", "Negative"},

            {"TC-WEB-003", "Login", "Login fails — wrong password",
             "I am on the login page",
             "Enter username '${usernameOrEmailResonance}', password 'wrongpassword123', click Login",
             "Error toast visible or stay on login page", "Negative"},

            {"TC-WEB-004", "Login", "Login fails — empty username field",
             "I am on the login page",
             "Enter username '', password 'password', click Login",
             "Stay on login page", "Negative"},

            {"TC-WEB-005", "Login", "Login fails — empty password field",
             "I am on the login page",
             "Enter username '${usernameOrEmailResonance}', password '', click Login",
             "Stay on login page", "Negative"},

            {"TC-WEB-006", "Login", "Login fails — Scenario Outline (username: unknown_user@fake.com / password: wrongpassword / password: ab)",
             "I am on the login page",
             "Each row of Examples table runs once: 3 invalid credential combinations",
             "Error visible or stay on login page for each row", "Negative"},

            {"TC-WEB-007", "Login", "Login page shows Register and Forgot Password links",
             "I am on the login page",
             "No action",
             "Register link and Forgot Password link are visible", "Positive"},

            // ── CREATE TICKET ────────────────────────────────────
            {"TC-WEB-008", "Create Ticket", "Create a public ticket with complete data",
             "Logged in as '${usernameOrEmailResonance}', on the dashboard",
             "Click Create Ticket, enter title + description, click Submit",
             "Ticket created — redirected or success toast shown", "Positive"},

            {"TC-WEB-009", "Create Ticket", "Create a private ticket with complete data",
             "Logged in as '${usernameOrEmailResonance}', on the dashboard",
             "Click Create Ticket, enter title + description, select Private, click Submit",
             "Private ticket created — redirected or success toast shown", "Positive"},

            {"TC-WEB-010", "Create Ticket", "Creating a ticket without a title should fail",
             "Logged in as '${usernameOrEmailResonance}', on the new ticket page",
             "Enter description only (title empty), click Submit",
             "Stay on /new page or validation error displayed", "Negative"},

            {"TC-WEB-011", "Create Ticket", "Accessing new ticket page without login redirects to login",
             "Not logged in (cookies cleared)",
             "Navigate directly to /new",
             "Redirected to /login", "Negative"},

            // ── VIEW TICKET ──────────────────────────────────────
            {"TC-WEB-012", "View Ticket", "Dashboard displays the ticket list",
             "Logged in as '${usernameOrEmailResonance}'",
             "Navigate to dashboard",
             "Dashboard loads, page content is not empty", "Positive"},

            {"TC-WEB-013", "View Ticket", "Filter tickets by Newest order",
             "Logged in as '${usernameOrEmailResonance}', on dashboard",
             "Filter by order 'Newest'",
             "Dashboard reloads with Newest filter applied", "Positive"},

            {"TC-WEB-014", "View Ticket", "Filter tickets by Vote count",
             "Logged in as '${usernameOrEmailResonance}', on dashboard",
             "Filter by order 'Vote'",
             "Dashboard reloads with Vote filter applied", "Positive"},

            {"TC-WEB-015", "View Ticket", "Search tickets by keyword",
             "Logged in as '${usernameOrEmailResonance}', on dashboard",
             "Search with keyword 'Test'",
             "Dashboard reloads showing search results", "Positive"},

            {"TC-WEB-016", "View Ticket", "Accessing a ticket with an invalid ID",
             "Logged in as '${usernameOrEmailResonance}'",
             "Navigate to /ticket/id-tidak-valid-xyz-123",
             "Page shows 404 / error, or redirects", "Negative"},

            // ── HISTORY ──────────────────────────────────────────
            {"TC-WEB-017", "History", "Navigate to the ticket history page",
             "Logged in as '${usernameOrEmailResonance}', on dashboard",
             "Click History in the navbar",
             "URL contains /history", "Positive"},

            {"TC-WEB-018", "History", "History page loads successfully",
             "Logged in as '${usernameOrEmailResonance}', on /history",
             "Load the history page",
             "URL contains /history, page content is not empty", "Positive"},

            {"TC-WEB-019", "History", "Navigate back to dashboard from history",
             "Logged in as '${usernameOrEmailResonance}', on /history",
             "Click the back to Dashboard button",
             "URL no longer contains /login", "Positive"},

            {"TC-WEB-020", "History", "Accessing history page without login redirects to login",
             "Not logged in (cookies cleared)",
             "Navigate directly to /history",
             "Redirected to /login", "Negative"},
        };

        int rowNum = 2;
        for (Object[] row : data) {
            Row r = sheet.createRow(rowNum++);
            for (int i = 0; i < row.length; i++) {
                Cell c = r.createCell(i);
                c.setCellValue(row[i].toString());
                c.setCellStyle("Positive".equals(row[row.length - 1].toString()) ? pos : neg);
            }
        }
    }

    // ================================================================
    //  Style helpers
    // ================================================================
    private static CellStyle createHeaderStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)31,(byte)73,(byte)125}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createTitleStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)189,(byte)215,(byte)238}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createStatusStyle(XSSFWorkbook wb, byte[] bg, byte r, byte g, byte b) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setColor(new XSSFColor(new byte[]{r, g, b}, null));
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(bg, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private static CellStyle createNormalStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }
}
