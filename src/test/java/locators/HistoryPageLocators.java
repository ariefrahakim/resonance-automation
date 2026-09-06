package locators;

import org.openqa.selenium.By;

/**
 * Locator terpusat untuk halaman riwayat tiket (/history).
 *
 * Prinsip pemilihan locator (dari paling stabil ke paling rapuh):
 *   1. By.id()           — paling stabil, langsung dari atribut id=""
 *   2. By.name()         — dari atribut name=""
 *   3. By.cssSelector()  — pakai href/atribut struktural, BUKAN class hash Chakra
 *   4. By.xpath()        — untuk teks atau relasi parent/child yang tidak bisa dengan CSS
 *
 * Hindari: class hash seperti css-n0wfye, css-1gu0mm2 — berubah tiap build.
 */
public class HistoryPageLocators {

    /**
     * Daftar tiket: gunakan link yang mengarah ke /ticket/ — stabil karena berbasis href.
     * Setiap tiket pasti memiliki link ke /ticket/{id}.
     */
    public static final By TICKET_LINKS     = By.cssSelector("a[href*='/ticket/']");

    /**
     * Judul tiket: span pertama di dalam link tiket.
     * XPath lebih tepat di sini karena CSS tidak bisa select anak pertama dari tipe span.
     */
    public static final By TICKET_TITLE     = By.xpath("//a[contains(@href,'/ticket/')]//span[1]");

    /**
     * Kontainer tiket: parent dari link tiket. Gunakan XPath naik dari link.
     */
    public static final By TICKET_CONTAINER = By.xpath("//a[contains(@href,'/ticket/')]/parent::*");

    // Kontrol halaman — stabil karena pakai id eksplisit
    public static final By BACK_DASHBOARD   = By.id("btn-dashboard");
    public static final By ITEMS_PER_PAGE   = By.id("select-items-per-page");
    public static final By PAGINATION_PREV  = By.id("btn-pagination-prev");
    public static final By PAGINATION_NEXT  = By.id("btn-pagination-next");
}
