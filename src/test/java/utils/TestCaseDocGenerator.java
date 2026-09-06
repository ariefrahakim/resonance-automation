package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Generator dokumen test case dalam format Excel (.xlsx).
 * Jalankan sebagai main() untuk menghasilkan file docs/test_cases.xlsx
 */
public class TestCaseDocGenerator {

    public static void main(String[] args) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();

        // ---- Style Definitions ----
        CellStyle headerStyle  = createHeaderStyle(wb);
        CellStyle titleStyle   = createTitleStyle(wb);
        CellStyle positiveStyle = createStatusStyle(wb, new byte[]{(byte)198,(byte)239,(byte)206}, (byte)0,(byte)97,(byte)0);
        CellStyle negativeStyle = createStatusStyle(wb, new byte[]{(byte)255,(byte)199,(byte)206}, (byte)156,(byte)0,(byte)6);
        CellStyle normalStyle  = createNormalStyle(wb);

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
    //  API SHEET
    // ================================================================
    private static void createApiSheet(XSSFWorkbook wb, CellStyle header, CellStyle title,
                                        CellStyle pos, CellStyle neg, CellStyle normal) {
        XSSFSheet sheet = wb.createSheet("API Test Cases");
        sheet.setColumnWidth(0,  4000);   // TC ID
        sheet.setColumnWidth(1,  8000);   // Modul
        sheet.setColumnWidth(2,  16000);  // Nama Test Case
        sheet.setColumnWidth(3,  20000);  // Test Step / Request
        sheet.setColumnWidth(4,  14000);  // Test Data
        sheet.setColumnWidth(5,  12000);  // Expected Result
        sheet.setColumnWidth(6,  5000);   // Status Code
        sheet.setColumnWidth(7,  5000);   // Tipe

        // Judul sheet
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RESONANCE - API Test Cases");
        titleCell.setCellStyle(title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

        // Header kolom
        String[] cols = {"TC ID","Modul","Nama Test Case","Test Step / Request","Test Data","Expected Result","Exp. Status","Tipe"};
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < cols.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(header);
        }

        // Data test cases
        Object[][] data = {
            // AUTH
            {"TC-API-001","Auth","Login sukses dengan kredensial valid",
             "POST /api/rest/login","usernameOrEmail: user1, password: password",
             "Mengembalikan token JWT, status 200, field ok: true","200","Positive"},
            {"TC-API-002","Auth","Login gagal - email tidak terdaftar",
             "POST /api/rest/login","usernameOrEmail: email_tidak_ada@test.com, password: password",
             "Status bukan 200, ada pesan error","4xx","Negative"},
            {"TC-API-003","Auth","Login gagal - password salah",
             "POST /api/rest/login","usernameOrEmail: user1, password: passwordsalah",
             "Status bukan 200","4xx","Negative"},
            {"TC-API-004","Auth","Login gagal - username kosong",
             "POST /api/rest/login","usernameOrEmail: (empty), password: password",
             "Status bukan 200, validasi field wajib","4xx","Negative"},
            {"TC-API-005","Auth","Login gagal - password kosong",
             "POST /api/rest/login","usernameOrEmail: user1, password: (empty)",
             "Status bukan 200","4xx","Negative"},
            {"TC-API-006","Auth","Login gagal - password terlalu pendek (<4 char)",
             "POST /api/rest/login","usernameOrEmail: user1, password: ab",
             "Status bukan 200, validasi minLength 4","4xx","Negative"},
            // TICKET POSITIVE
            {"TC-API-007","Ticket","Membuat tiket publik",
             "POST /api/rest/createTicket + Auth Bearer token",
             "title: 'Tiket Test', description: '...', isPublic: true",
             "Tiket dibuat, mengembalikan ID, status 200","200","Positive"},
            {"TC-API-008","Ticket","Membuat tiket private",
             "POST /api/rest/createTicket + Auth Bearer token",
             "title: 'Tiket Private', isPublic: false",
             "Tiket private dibuat, isPublic: false","200","Positive"},
            {"TC-API-009","Ticket","Mengambil tiket aktif (order VOTE)",
             "GET /api/rest/activeTickets?date=...&order=VOTE + Auth",
             "order: VOTE","Mengembalikan array tiket, status 200","200","Positive"},
            {"TC-API-010","Ticket","Mengambil tiket aktif (order NEWEST)",
             "GET /api/rest/activeTickets?date=...&order=NEWEST + Auth",
             "order: NEWEST","Mengembalikan array tiket, status 200","200","Positive"},
            {"TC-API-011","Ticket","Mengambil tiket saya (My Tickets)",
             "GET /api/rest/myTickets?limit=5&page=0 + Auth",
             "limit: 5, page: 0","Array tiket milik user yang login","200","Positive"},
            {"TC-API-012","Ticket","Mengambil tiket berdasarkan ID",
             "GET /api/rest/ticketById?id=<ticketId> + Auth",
             "id: (ID dari TC-API-007)","Detail tiket dengan ID yang sesuai","200","Positive"},
            {"TC-API-013","Ticket","Menghitung jumlah tiket aktif",
             "GET /api/rest/countActiveTickets?date=... + Auth",
             "-","Mengembalikan angka (number)","200","Positive"},
            {"TC-API-014","Ticket","Update status tiket ke Solved",
             "PUT /api/rest/updateStatusTicket + Auth",
             "ticketId: ..., solvedAt: (timestamp)","solvedAt terisi, status 200","200","Positive"},
            {"TC-API-015","Ticket","Update status tiket ke Unsolved",
             "PUT /api/rest/updateStatusTicket + Auth",
             "ticketId: ..., solvedAt: null","solvedAt null, status 200","200","Positive"},
            {"TC-API-016","Ticket","Menghapus tiket (Delete)",
             "DELETE /api/rest/deleteTicket?ticketId=... + Auth",
             "ticketId: (ID tiket baru)","success: true, status 200","200","Positive"},
            // TICKET NEGATIVE
            {"TC-API-017","Ticket","Membuat tiket dengan judul kosong",
             "POST /api/rest/createTicket + Auth",
             "title: (empty), isPublic: true",
             "Status bukan 200, validasi minLength 1","4xx","Negative"},
            {"TC-API-018","Ticket","Membuat tiket tanpa autentikasi",
             "POST /api/rest/createTicket (no token)",
             "title: 'Test'",
             "Status 401 atau 403 — ditolak","401/403","Negative"},
            {"TC-API-019","Ticket","Mengambil tiket dengan ID tidak valid",
             "GET /api/rest/ticketById?id=id-palsu + Auth",
             "id: id-tidak-ada-12345",
             "Status bukan 200","4xx","Negative"},
            {"TC-API-020","Ticket","Mengambil tiket aktif tanpa parameter date",
             "GET /api/rest/activeTickets (no date) + Auth",
             "-","Status bukan 200, parameter date wajib","4xx","Negative"},
            {"TC-API-021","Ticket","Menghapus tiket tanpa autentikasi",
             "DELETE /api/rest/deleteTicket?ticketId=... (no token)",
             "ticketId: valid","Status 401 atau 403","401/403","Negative"},
            {"TC-API-022","Ticket","Menghapus tiket yang tidak ada",
             "DELETE /api/rest/deleteTicket?ticketId=id-palsu + Auth",
             "ticketId: non-existent","Status bukan 200","4xx","Negative"},
            // COMMENT POSITIVE
            {"TC-API-023","Comment","Membuat komentar pada tiket",
             "POST /api/rest/createComment + Auth",
             "ticketId: ..., body: 'Komentar test'","ID komentar dikembalikan, status 200","200","Positive"},
            {"TC-API-024","Comment","Mengambil komentar berdasarkan tiket ID",
             "GET /api/rest/commentsByTicketId?ticketId=...&limit=10 + Auth",
             "ticketId: ...","Array komentar, status 200","200","Positive"},
            {"TC-API-025","Comment","Update komentar",
             "PUT /api/rest/updateComment + Auth",
             "id: ..., body: 'Komentar diperbarui'","Body komentar berubah, status 200","200","Positive"},
            {"TC-API-026","Comment","Menghapus komentar",
             "DELETE /api/rest/deleteComment?id=... + Auth",
             "id: (ID komentar)","success: true, status 200","200","Positive"},
            // COMMENT NEGATIVE
            {"TC-API-027","Comment","Membuat komentar dengan body kosong",
             "POST /api/rest/createComment + Auth",
             "ticketId: ..., body: (empty)",
             "Status bukan 200, validasi minLength 1","4xx","Negative"},
            {"TC-API-028","Comment","Membuat komentar pada tiket tidak ada",
             "POST /api/rest/createComment + Auth",
             "ticketId: id-palsu, body: 'Test'",
             "Status bukan 200","4xx","Negative"},
            {"TC-API-029","Comment","Membuat komentar tanpa autentikasi",
             "POST /api/rest/createComment (no token)",
             "ticketId: ..., body: 'Test'",
             "Status 401 atau 403","401/403","Negative"},
            // PROGRESSION POSITIVE
            {"TC-API-030","Progression","Membuat progres tiket",
             "POST /api/rest/createProgression + Auth",
             "ticketId: ..., title: 'Investigasi'","ID progres dikembalikan, status 200","200","Positive"},
            {"TC-API-031","Progression","Mengambil progres berdasarkan tiket",
             "GET /api/rest/progressionsByTicketId?ticketId=... + Auth",
             "ticketId: ...","Array progres, status 200","200","Positive"},
            {"TC-API-032","Progression","Update progres",
             "PUT /api/rest/updateProgression + Auth",
             "id: ..., title: 'Diperbarui'","Judul progres berubah, status 200","200","Positive"},
            {"TC-API-033","Progression","Menghapus progres",
             "DELETE /api/rest/deleteProgression?id=... + Auth",
             "id: (ID progres)","success: true, status 200","200","Positive"},
            // PROGRESSION NEGATIVE
            {"TC-API-034","Progression","Membuat progres dengan judul kosong",
             "POST /api/rest/createProgression + Auth",
             "ticketId: ..., title: (empty)",
             "Status bukan 200, validasi minLength 1","4xx","Negative"},
            {"TC-API-035","Progression","Membuat progres tanpa autentikasi",
             "POST /api/rest/createProgression (no token)",
             "ticketId: ..., title: 'Test'",
             "Status 401 atau 403","401/403","Negative"},
            // VOTE POSITIVE
            {"TC-API-036","Vote","Cek status vote pada tiket",
             "GET /api/rest/checkUserVote?ticketId=... + Auth",
             "ticketId: ...","hasVoted (boolean), voteCount (number), status 200","200","Positive"},
            {"TC-API-037","Vote","Vote pada tiket (toggle)",
             "POST /api/rest/voteTicket + Auth",
             "ticketId: ...","hasVoted & voteCount dikembalikan, status 200","200","Positive"},
            {"TC-API-038","Vote","Unvote pada tiket (toggle kedua kali)",
             "POST /api/rest/voteTicket + Auth",
             "ticketId: ...","hasVoted berubah (toggle), status 200","200","Positive"},
            // VOTE NEGATIVE
            {"TC-API-039","Vote","Vote tanpa autentikasi",
             "POST /api/rest/voteTicket (no token)",
             "ticketId: ...","Status 401 atau 403","401/403","Negative"},
            {"TC-API-040","Vote","Vote pada tiket yang tidak ada",
             "POST /api/rest/voteTicket + Auth",
             "ticketId: id-tidak-ada","Status bukan 200","4xx","Negative"},
        };

        int rowNum = 2;
        for (Object[] row : data) {
            Row r = sheet.createRow(rowNum++);
            for (int i = 0; i < row.length; i++) {
                Cell c = r.createCell(i);
                c.setCellValue(row[i].toString());
                String tipe = row[row.length - 1].toString();
                c.setCellStyle("Positive".equals(tipe) ? pos : neg);
            }
        }
    }

    // ================================================================
    //  WEB SHEET
    // ================================================================
    private static void createWebSheet(XSSFWorkbook wb, CellStyle header, CellStyle title,
                                        CellStyle pos, CellStyle neg, CellStyle normal) {
        XSSFSheet sheet = wb.createSheet("Web Test Cases (Gherkin)");
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 8000);
        sheet.setColumnWidth(2, 8000);
        sheet.setColumnWidth(3, 16000);
        sheet.setColumnWidth(4, 20000);
        sheet.setColumnWidth(5, 16000);
        sheet.setColumnWidth(6, 5000);

        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RESONANCE - Web Test Cases (Gherkin BDD)");
        titleCell.setCellStyle(title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        String[] cols = {"TC ID","Feature","Scenario","Given / Precondition","When / Action","Then / Expected Result","Tipe"};
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < cols.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(header);
        }

        Object[][] data = {
            // LOGIN
            {"TC-WEB-001","Login","Login berhasil",
             "Berada di halaman /login",
             "Input username='user1', password='password', klik Login",
             "Diarahkan ke dashboard, tidak di /login","Positive"},
            {"TC-WEB-002","Login","Login gagal - email tidak terdaftar",
             "Berada di halaman /login",
             "Input username='email_tidak_ada@test.com', password='password', klik Login",
             "Tetap di /login atau muncul toast error 'Invalid Credentials'","Negative"},
            {"TC-WEB-003","Login","Login gagal - password salah",
             "Berada di halaman /login",
             "Input username='user1', password='passwordsalah123', klik Login",
             "Tetap di /login atau muncul toast error","Negative"},
            {"TC-WEB-004","Login","Login gagal - field username kosong",
             "Berada di halaman /login",
             "Input username='', password='password', klik Login",
             "Tetap di halaman /login","Negative"},
            {"TC-WEB-005","Login","Login gagal - field password kosong",
             "Berada di halaman /login",
             "Input username='user1', password='', klik Login",
             "Tetap di halaman /login","Negative"},
            {"TC-WEB-006","Login","Link Register dan Lupa Password terlihat",
             "Berada di halaman /login",
             "Tidak ada aksi",
             "Link 'Buat Akun?' dan 'Lupa Password?' terlihat","Positive"},
            // CREATE TICKET
            {"TC-WEB-007","Create Ticket","Membuat tiket publik berhasil",
             "Login sebagai user1, berada di dashboard",
             "Klik Create Ticket, isi judul & deskripsi, klik Submit",
             "Tiket berhasil dibuat (redirect atau toast sukses)","Positive"},
            {"TC-WEB-008","Create Ticket","Membuat tiket private berhasil",
             "Login sebagai user1, berada di dashboard",
             "Klik Create Ticket, isi judul & deskripsi, pilih Private, klik Submit",
             "Tiket private berhasil dibuat","Positive"},
            {"TC-WEB-009","Create Ticket","Membuat tiket dengan judul kosong gagal",
             "Login sebagai user1, berada di /new",
             "Isi deskripsi saja, judul kosong, klik Submit",
             "Tetap di /new atau muncul pesan validasi error","Negative"},
            {"TC-WEB-010","Create Ticket","Akses /new tanpa login diarahkan ke login",
             "Tidak ada sesi login aktif",
             "Akses langsung URL /new",
             "Diarahkan ke /login","Negative"},
            // VIEW TICKET
            {"TC-WEB-011","View Ticket","Dashboard berhasil dimuat setelah login",
             "Login sebagai user1",
             "Navigasi ke halaman /",
             "Halaman dashboard berhasil dimuat, konten tidak kosong","Positive"},
            {"TC-WEB-012","View Ticket","Filter tiket berdasarkan urutan Newest",
             "Login sebagai user1, di dashboard",
             "Klik btn-filter-order-newest",
             "Halaman refresh dengan filter Newest","Positive"},
            {"TC-WEB-013","View Ticket","Filter tiket berdasarkan jumlah Vote",
             "Login sebagai user1, di dashboard",
             "Klik btn-filter-order-vote",
             "Halaman refresh dengan filter Vote","Positive"},
            {"TC-WEB-014","View Ticket","Pencarian tiket dengan kata kunci",
             "Login sebagai user1, di dashboard",
             "Input kata kunci di search box",
             "Halaman refresh dengan hasil pencarian","Positive"},
            {"TC-WEB-015","View Ticket","Akses tiket dengan ID tidak valid menampilkan error",
             "Login sebagai user1",
             "Akses /ticket/id-tidak-valid-xyz",
             "Halaman menampilkan 404, error, atau redirect","Negative"},
            // HISTORY
            {"TC-WEB-016","History","Navigasi ke halaman riwayat tiket",
             "Login sebagai user1, di dashboard",
             "Klik btn-open-navbar, klik btn-nav-history",
             "Berada di halaman /history","Positive"},
            {"TC-WEB-017","History","Halaman riwayat berhasil dimuat",
             "Login sebagai user1, berada di /history",
             "Muat halaman /history",
             "Konten halaman tidak kosong","Positive"},
            {"TC-WEB-018","History","Kembali ke dashboard dari halaman riwayat",
             "Login sebagai user1, berada di /history",
             "Klik btn-dashboard",
             "Kembali ke halaman /","Positive"},
            {"TC-WEB-019","History","Akses /history tanpa login diarahkan ke login",
             "Tidak ada sesi login aktif",
             "Akses langsung URL /history",
             "Diarahkan ke /login","Negative"},
        };

        int rowNum = 2;
        for (Object[] row : data) {
            Row r = sheet.createRow(rowNum++);
            for (int i = 0; i < row.length; i++) {
                Cell c = r.createCell(i);
                c.setCellValue(row[i].toString());
                String tipe = row[row.length - 1].toString();
                c.setCellStyle("Positive".equals(tipe) ? pos : neg);
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
