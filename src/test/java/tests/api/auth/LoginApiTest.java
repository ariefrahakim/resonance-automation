package tests.api.auth;

import base.BaseApiTest;
import body.auth.LoginBody;
import data.LoginDataProvider;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.JsonFileManager;

/**
 * Kelas pengujian untuk endpoint autentikasi Resonance.
 * Mencakup skenario login sukses dan gagal.
 */
public class LoginApiTest extends BaseApiTest {

    /**
     * TC-AUTH-001: Verifikasi login berhasil dengan kredensial valid.
     * Token JWT yang dikembalikan disimpan untuk digunakan oleh test berikutnya.
     */
    @Test(priority = 1, description = "Login berhasil dengan kredensial valid")
    public void testLoginSuccess() {
        String username = ConfigReader.getProperty("usernameOrEmailResonance");
        String password = ConfigReader.getProperty("passwordResonance");

        Response response = baseRequest()
                .body(LoginBody.build(username, password).toString())
                .post("/api/rest/login");

        // Validasi status code harus 200
        Assert.assertEquals(response.getStatusCode(), 200, "Status code harus 200 untuk login sukses");

        // Validasi Bearer token tidak null
        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Bearer token harus dikembalikan setelah login berhasil");

        // Simpan token ke file — digunakan sebagai Authorization: Bearer <token> di test berikutnya
        JsonFileManager.writeValue(TOKEN_FILE, "token", token);
        System.out.println("[PASS] Bearer token disimpan: " + token.substring(0, Math.min(20, token.length())) + "...");
    }

    /**
     * TC-AUTH-002: Verifikasi login berhasil menggunakan DataProvider (data-driven).
     * Menjalankan login dengan berbagai variasi data valid.
     */
    @Test(priority = 2,
          dataProvider = "loginValidData",
          dataProviderClass = LoginDataProvider.class,
          description = "Login dengan berbagai format kredensial valid")
    public void testLoginWithValidData(String username, String password, String scenario) {
        System.out.println("[INFO] Skenario: " + scenario);

        Response response = baseRequest()
                .body(LoginBody.build(username, password).toString())
                .post("/api/rest/login");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Login harus berhasil untuk skenario: " + scenario);
        Assert.assertNotNull(response.jsonPath().getString("token"),
                "Token harus dikembalikan untuk skenario: " + scenario);

        System.out.println("[PASS] " + scenario + " - Login berhasil");
    }

    /**
     * TC-AUTH-003: Verifikasi login gagal dengan data tidak valid (data-driven).
     * Setiap kombinasi harus mengembalikan status code bukan 200.
     */
    @Test(priority = 3,
          dataProvider = "loginInvalidData",
          dataProviderClass = LoginDataProvider.class,
          description = "Login gagal dengan berbagai kombinasi data tidak valid")
    public void testLoginWithInvalidData(String username, String password, String scenario) {
        System.out.println("[INFO] Skenario negatif: " + scenario);

        Response response = baseRequest()
                .body(LoginBody.build(username, password).toString())
                .post("/api/rest/login");

        // Login dengan data tidak valid TIDAK boleh mengembalikan status 200
        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Login seharusnya gagal untuk skenario: " + scenario);

        System.out.println("[PASS] " + scenario + " - Status: " + response.getStatusCode());
    }
}
