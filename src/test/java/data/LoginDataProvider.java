package data;

import org.testng.annotations.DataProvider;

/**
 * Penyedia data untuk pengujian login.
 * Menggunakan TestNG DataProvider untuk binding data dinamis.
 */
public class LoginDataProvider {

    /**
     * Data positif: login sukses dengan format username yang valid.
     * Kolom: [usernameOrEmail, password, deskripsi skenario]
     */
    @DataProvider(name = "loginValidData")
    public static Object[][] loginValidData() {
        return new Object[][] {
            { "user1", "password", "Login menggunakan username" },
        };
    }

    /**
     * Data negatif: login gagal dengan berbagai kombinasi data tidak valid.
     * Kolom: [usernameOrEmail, password, deskripsi skenario]
     *
     * Mengikuti validasi dari API docs:
     * - password minLength: 4, maxLength: 12
     * - usernameOrEmail: wajib diisi
     */
    @DataProvider(name = "loginInvalidData")
    public static Object[][] loginInvalidData() {
        return new Object[][] {
            // Email/username tidak terdaftar
            { "user_tidak_ada@test.com",  "password",      "Email tidak terdaftar di sistem" },
            // Password salah untuk user yang ada
            { "user1",                    "passwordsalah", "Password tidak sesuai" },
            // Username kosong (field wajib)
            { "",                          "password",      "Username kosong (field wajib)" },
            // Password kosong
            { "user1",                     "",              "Password kosong (field wajib)" },
            // Password terlalu pendek (< 4 karakter sesuai API docs)
            { "user1",                     "ab",            "Password terlalu pendek (< 4 karakter)" },
            // Password terlalu panjang (> 12 karakter sesuai API docs)
            { "user1",                     "passwordyang13c","Password terlalu panjang (> 12 karakter)" },
            // Keduanya kosong
            { "",                          "",              "Username dan password keduanya kosong" },
            // Format email tidak valid
            { "bukan-format-email",        "password",      "Format email tidak valid (tanpa @)" },
        };
    }
}
