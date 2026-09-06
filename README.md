# Resonance Automation

Framework otomasi testing gabungan API dan Web untuk aplikasi [Resonance](https://resonance.dibimbing.id), dibangun dengan Java, TestNG, RestAssured, dan Selenium WebDriver menggunakan pola Page Object Model (POM).

## Struktur Proyek

```
resonance-automation/
├── .github/workflows/tests.yml         # CI/CD pipeline (GitHub Actions)
├── postman/
│   └── Resonance_API.postman_collection.json  # Koleksi Postman
├── src/
│   ├── main/java/
│   │   ├── body/                       # Request body builder (API)
│   │   │   ├── auth/                   # - LoginBody
│   │   │   ├── comment/                # - CreateCommentBody, UpdateCommentBody
│   │   │   ├── progression/            # - CreateProgressionBody, UpdateProgressionBody
│   │   │   ├── ticket/                 # - CreateTicketBody, UpdateTicketStatusBody
│   │   │   └── vote/                   # - VoteBody
│   │   ├── locators/                   # Locator terpusat untuk Web (POM)
│   │   │   ├── LoginPageLocators.java
│   │   │   ├── HomePageLocators.java
│   │   │   ├── NewTicketPageLocators.java
│   │   │   ├── TicketDetailPageLocators.java
│   │   │   └── HistoryPageLocators.java
│   │   ├── pages/                      # Page Object classes (Web)
│   │   │   ├── BasePage.java
│   │   │   ├── LoginPage.java
│   │   │   ├── HomePage.java
│   │   │   ├── NewTicketPage.java
│   │   │   ├── TicketDetailPage.java
│   │   │   └── HistoryPage.java
│   │   └── utils/                      # Utilitas bersama
│   │       ├── ConfigReader.java       # - Membaca config.properties
│   │       ├── DriverManager.java      # - Manajemen WebDriver
│   │       ├── JsonFileManager.java    # - Baca/tulis JSON (state sharing)
│   │       └── Utils.java              # - Generator data dinamis
│   ├── resources/
│   │   ├── config.properties           # Konfigurasi (URL, kredensial)
│   │   └── json/                       # File state antar test
│   │       ├── token.json
│   │       ├── ticket_id.json
│   │       ├── comment_id.json
│   │       └── progression_id.json
│   └── test/java/
│       ├── base/
│       │   ├── BaseApiTest.java         # Setup RestAssured + helper
│       │   └── BaseWebTest.java         # Setup WebDriver + helper
│       ├── data/                        # DataProvider untuk data-driven testing
│       │   ├── LoginDataProvider.java
│       │   ├── TicketDataProvider.java
│       │   ├── CommentDataProvider.java
│       │   └── ProgressionDataProvider.java
│       ├── runner/                      # TestNG XML suite files
│       │   ├── testng.xml              # Suite lengkap (API + Web)
│       │   ├── api-testng.xml          # Suite API saja
│       │   └── web-testng.xml          # Suite Web saja
│       └── tests/
│           ├── api/                     # Test case API
│           │   ├── auth/               # - LoginApiTest (data-driven)
│           │   ├── ticket/             # - Create, Get, UpdateStatus, Delete
│           │   ├── comment/            # - Create (data-driven), Update, Delete
│           │   ├── progression/        # - Create (data-driven), Update, Delete
│           │   └── vote/               # - CheckVote, Vote, Unvote
│           └── web/                     # Test case Web
│               ├── auth/               # - LoginWebTest
│               ├── ticket/             # - CreateTicketWebTest, ViewTicketWebTest
│               └── history/            # - HistoryWebTest
```

## Prasyarat

- Java 17+
- Gradle 8+
- Google Chrome (untuk web test)

## Konfigurasi

Edit `src/main/resources/config.properties`:

```properties
baseUrlResonance=https://resonance.dibimbing.id
usernameOrEmailResonance=user1
passwordResonance=password
webUrl=https://resonance.dibimbing.id
browser=chrome
headless=true
```

## Menjalankan Test

```bash
# Semua test (API + Web)
./gradlew clean test

# Hanya API test
./gradlew clean test -Dsuite=api-testng

# Hanya Web test
./gradlew clean test -Dsuite=web-testng
```

## Fitur Utama

### Data-Driven Testing (DataProvider)
Test utama menggunakan `@DataProvider` TestNG untuk menjalankan skenario berbeda secara otomatis:

| DataProvider | Digunakan Oleh | Skenario |
|---|---|---|
| `loginValidData` | `LoginApiTest` | Login dengan berbagai format username |
| `loginInvalidData` | `LoginApiTest` | Login gagal: email salah, password salah, kosong |
| `createTicketData` | `CreateTicketTest` | Tiket publik, private, bug report |
| `activeTicketOrderData` | `GetTicketTest` | Urutan VOTE, NEWEST, SOLVE |
| `createCommentData` | `CreateCommentTest` | Komentar biasa, status update, referensi |
| `createProgressionData` | `CreateProgressionTest` | Fase investigasi, pengembangan, QA |

### State Sharing Antar Test
Token dan ID disimpan di file JSON dan dibaca oleh test berikutnya:
- `token.json` → token JWT setelah login
- `ticket_id.json` → ID tiket yang dibuat
- `comment_id.json` → ID komentar yang dibuat
- `progression_id.json` → ID progres yang dibuat

### Locator Terpusat (POM)
Semua selector elemen web dipusatkan di package `locators/`. Jika UI berubah, cukup update satu file locator.

## Koleksi Postman

Import `postman/Resonance_API.postman_collection.json` ke Postman.

Fitur koleksi:
- Token otomatis disimpan setelah login via Test script
- ID tiket, komentar, progres tersimpan otomatis sebagai collection variable
- Setiap request memiliki test assertion
- Deskripsi dalam Bahasa Indonesia

## Coverage Test

### API
- **Auth**: Login sukses, login data tidak valid (data-driven)
- **Tiket**: Buat, ambil (dengan berbagai order), update status, hapus
- **Komentar**: Buat (data-driven), ambil, update, hapus
- **Progres**: Buat (data-driven), ambil, update, hapus
- **Vote**: Cek status, vote, unvote (toggle)

### Web
- **Login**: Sukses, gagal (kredensial salah)
- **Tiket**: Navigasi ke /new, buat tiket, lihat daftar
- **History**: Navigasi ke /history, validasi konten halaman
