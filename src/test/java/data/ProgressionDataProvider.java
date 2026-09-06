package data;

import org.testng.annotations.DataProvider;

/**
 * Penyedia data untuk pengujian progres tiket.
 */
public class ProgressionDataProvider {

    /**
     * Data untuk pembuatan progres tiket.
     * Setiap baris: [judul, deskripsi, deskripsi skenario]
     */
    @DataProvider(name = "createProgressionData")
    public static Object[][] createProgressionData() {
        return new Object[][] {
            { "Investigasi awal",          "Tim sedang mengidentifikasi root cause",   "Progres fase investigasi" },
            { "Pengembangan solusi",        "Solusi sedang dikembangkan oleh tim dev",  "Progres fase pengembangan" },
            { "Testing & Verifikasi",       "QA sedang melakukan pengujian solusi",     "Progres fase QA" },
        };
    }
}
