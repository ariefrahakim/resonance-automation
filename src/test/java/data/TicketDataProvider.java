package data;

import org.testng.annotations.DataProvider;

/**
 * Penyedia data untuk pengujian tiket.
 * Mendukung skenario positif dan negatif sesuai API docs Resonance.
 */
public class TicketDataProvider {

    /**
     * Data positif: pembuatan tiket dengan variasi input valid.
     * Kolom: [judul, deskripsi, isPublic, deskripsi skenario]
     */
    @DataProvider(name = "createTicketData")
    public static Object[][] createTicketData() {
        return new Object[][] {
            { "Tiket Publik Otomatis",    "Deskripsi tiket publik untuk testing",         true,  "Membuat tiket publik" },
            { "Tiket Private Otomatis",   "Deskripsi tiket private untuk testing",         false, "Membuat tiket private" },
            { "Bug: Halaman login error", "Saat login dengan akun valid, muncul error 500", true,  "Tiket bug publik dengan deskripsi teknis" },
        };
    }

    /**
     * Data negatif: pembuatan tiket yang seharusnya gagal.
     * Kolom: [judul, deskripsi, isPublic, deskripsi skenario]
     *
     * Sesuai API docs: title wajib dengan minLength: 1
     */
    @DataProvider(name = "createTicketInvalidData")
    public static Object[][] createTicketInvalidData() {
        return new Object[][] {
            // Judul kosong (melanggar minLength: 1)
            { "", "Deskripsi ada tapi judul kosong", true, "Judul tiket kosong" },
        };
    }

    /**
     * Data untuk pengujian pengambilan tiket aktif dengan berbagai urutan.
     * Kolom: [order, deskripsi]
     */
    @DataProvider(name = "activeTicketOrderData")
    public static Object[][] activeTicketOrderData() {
        return new Object[][] {
            { "VOTE",   "Urut berdasarkan jumlah vote" },
            { "NEWEST", "Urut berdasarkan terbaru" },
            { "SOLVE",  "Urut berdasarkan status selesai" },
        };
    }

    /**
     * Data negatif untuk pengambilan tiket berdasarkan ID.
     * Kolom: [ticketId, deskripsi skenario]
     */
    @DataProvider(name = "getTicketInvalidIdData")
    public static Object[][] getTicketInvalidIdData() {
        return new Object[][] {
            { "id-tidak-ada-12345",  "ID tiket tidak terdaftar di sistem" },
            { "",                    "ID tiket kosong" },
            { "null",                "ID tiket berisi string 'null'" },
        };
    }
}
