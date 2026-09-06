package locators;

import org.openqa.selenium.By;

/**
 * Locator terpusat untuk halaman Home / Dashboard (/).
 * Seluruh tombol utama memiliki id eksplisit (btn-*).
 * Daftar tiket menggunakan href pattern — stabil, tidak bergantung class hash Chakra UI.
 */
public class HomePageLocators {

    // Search & create — keduanya memiliki id eksplisit
    public static final By SEARCH_INPUT         = By.id("input-search-ticket");
    public static final By CREATE_TICKET_BUTTON = By.id("btn-create-ticket");
    public static final By CREATE_TICKET_LINK   = By.cssSelector("a[href='/new']");

    /**
     * Daftar tiket: anchor yang mengarah ke /ticket/ — stabil berbasis href atribut.
     * Tidak menggunakan class hash Chakra UI.
     */
    public static final By TICKET_LIST          = By.cssSelector("a[href*='/ticket/']");

    /**
     * Judul tiket: span pertama di dalam setiap link tiket.
     */
    public static final By TICKET_TITLE         = By.xpath("//a[contains(@href,'/ticket/')]//span[1]");

    // Filter — semua memiliki id eksplisit
    public static final By FILTER_VOTE          = By.id("btn-filter-order-vote");
    public static final By FILTER_NEWEST        = By.id("btn-filter-order-newest");
    public static final By FILTER_SOLVE         = By.id("btn-filter-order-solve");

    // Navbar — id eksplisit, harus klik NAV_TOGGLE dulu sebelum aksi lain
    public static final By NAV_TOGGLE           = By.id("btn-open-navbar");
    public static final By NAV_DASHBOARD        = By.id("btn-nav-dashboard");
    public static final By NAV_HISTORY          = By.id("btn-nav-history");
    public static final By NAV_LOGOUT           = By.id("btn-logout");
    public static final By NAV_CLOSE            = By.id("btn-close-navbar");

    // Pagination — id eksplisit
    public static final By PAGINATION_PREV      = By.id("btn-pagination-prev");
    public static final By PAGINATION_NEXT      = By.id("btn-pagination-next");
}
