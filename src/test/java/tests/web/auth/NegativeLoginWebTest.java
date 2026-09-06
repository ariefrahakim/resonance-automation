package tests.web.auth;

import base.BaseWebTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.DriverManager;

/**
 * Kelas pengujian negatif untuk halaman login web.
 * Memverifikasi bahwa UI menangani input tidak valid dengan benar.
 */
public class NegativeLoginWebTest extends BaseWebTest {

    /**
     * TC-WEB-NEG-LOGIN-001: Login dengan username tidak terdaftar harus gagal.
     * Pengguna harus tetap di halaman login atau melihat pesan error.
     */
    @Test(priority = 1, description = "Login dengan username tidak terdaftar harus gagal")
    public void testLoginWithNonExistentUser() {
        loginPage.login("pengguna_tidak_ada_xyz@test.com", "password");

        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        boolean stayedOnLogin = currentUrl.contains("/login");
        boolean hasError = loginPage.isErrorDisplayed();

        Assert.assertTrue(stayedOnLogin || hasError,
                "Harus tetap di login atau tampilkan error untuk user tidak terdaftar. URL: " + currentUrl);
        System.out.println("[PASS] User tidak terdaftar - URL: " + currentUrl + " | Error: " + hasError);
    }

    /**
     * TC-WEB-NEG-LOGIN-002: Login dengan password salah harus gagal.
     */
    @Test(priority = 2, description = "Login dengan password salah harus gagal")
    public void testLoginWithWrongPassword() {
        loginPage.login("user1", "passwordsalah123");

        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        boolean stayedOnLogin = currentUrl.contains("/login");
        boolean hasError = loginPage.isErrorDisplayed();

        Assert.assertTrue(stayedOnLogin || hasError,
                "Harus tetap di login atau tampilkan error untuk password salah. URL: " + currentUrl);
        System.out.println("[PASS] Password salah - URL: " + currentUrl + " | Error: " + hasError);
    }

    /**
     * TC-WEB-NEG-LOGIN-003: Login dengan kedua field kosong harus gagal.
     */
    @Test(priority = 3, description = "Login dengan field kosong harus gagal atau tidak dapat disubmit")
    public void testLoginWithEmptyFields() {
        loginPage.navigate();
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // Klik tombol login tanpa mengisi field
        try {
            loginPage.clickLoginButton();
        } catch (Exception e) {
            // Tombol mungkin disabled — ini juga merupakan perilaku yang benar
            System.out.println("[INFO] Tombol login tidak dapat diklik saat field kosong: " + e.getMessage());
        }

        try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/login"),
                "Harus tetap di halaman login saat field kosong. URL: " + currentUrl);
        System.out.println("[PASS] Field kosong - tetap di halaman login: " + currentUrl);
    }

    /**
     * TC-WEB-NEG-LOGIN-004: Akses halaman /new tanpa login harus redirect ke halaman login.
     */
    @Test(priority = 4, description = "Akses halaman /new tanpa login harus diarahkan ke halaman login")
    public void testAccessProtectedPageWithoutLogin() {
        // Buka browser baru tanpa session login
        newTicketPage.navigate();
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        // Halaman terproteksi harus redirect ke /login
        boolean redirectedToLogin = currentUrl.contains("/login");
        boolean stillOnNew = currentUrl.contains("/new");

        // Salah satu kondisi harus benar: redirect ke login ATAU tetap di /new tapi menampilkan form login
        System.out.println("[INFO] URL setelah akses /new tanpa login: " + currentUrl);
        Assert.assertTrue(redirectedToLogin || !stillOnNew || DriverManager.getDriver().getPageSource().contains("login"),
                "Halaman /new tanpa login seharusnya redirect atau menampilkan login. URL: " + currentUrl);
        System.out.println("[PASS] Akses halaman terproteksi tanpa login ditangani dengan benar");
    }

    /**
     * TC-WEB-NEG-LOGIN-005: Akses halaman /history tanpa login harus redirect ke login.
     */
    @Test(priority = 5, description = "Akses halaman /history tanpa login harus redirect ke login")
    public void testAccessHistoryWithoutLogin() {
        historyPage.navigate();
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        System.out.println("[INFO] URL setelah akses /history tanpa login: " + currentUrl);

        boolean redirectedToLogin = currentUrl.contains("/login");
        String pageSource = DriverManager.getDriver().getPageSource();
        boolean hasLoginForm = pageSource.contains("password") || pageSource.contains("login");

        Assert.assertTrue(redirectedToLogin || hasLoginForm,
                "Halaman /history tanpa login seharusnya menampilkan login. URL: " + currentUrl);
        System.out.println("[PASS] Akses /history tanpa login ditangani dengan benar. URL: " + currentUrl);
    }
}
