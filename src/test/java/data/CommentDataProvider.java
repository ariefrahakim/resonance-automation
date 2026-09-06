package data;

import org.testng.annotations.DataProvider;

/**
 * Penyedia data untuk pengujian komentar tiket.
 * Mencakup skenario positif dan negatif sesuai API docs.
 */
public class CommentDataProvider {

    /**
     * Data positif: komentar dengan berbagai variasi isi yang valid.
     * Kolom: [isi komentar, deskripsi skenario]
     *
     * Sesuai API docs: body wajib dengan minLength: 1
     */
    @DataProvider(name = "createCommentData")
    public static Object[][] createCommentData() {
        return new Object[][] {
            { "Komentar pertama dari automation test",                    "Komentar teks biasa" },
            { "Sedang dalam proses investigasi oleh tim terkait",         "Komentar status update" },
            { "Masalah ini sudah ada tiketnya sebelumnya, mohon digabung","Komentar referensi duplikat" },
        };
    }

    /**
     * Data negatif: komentar yang seharusnya gagal dibuat.
     * Kolom: [isi komentar, ticketId, deskripsi skenario]
     */
    @DataProvider(name = "createCommentInvalidData")
    public static Object[][] createCommentInvalidData() {
        return new Object[][] {
            // Isi komentar kosong (melanggar minLength: 1)
            { "",    "valid-ticket-id", "Isi komentar kosong" },
            // ID tiket tidak valid
            { "Komentar valid", "id-tiket-tidak-ada-xyz", "ID tiket tidak ditemukan" },
        };
    }
}
