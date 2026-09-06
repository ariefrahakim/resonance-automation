@web @history
Feature: Riwayat Tiket
  Sebagai pengguna yang sudah login
  Saya ingin melihat riwayat tiket saya
  Supaya saya dapat melacak tiket yang sudah dan belum selesai

  Background:
    Given saya sudah login sebagai "user1" dengan password "password"

  @positive
  Scenario: Navigasi ke halaman riwayat tiket
    When saya berada di halaman dashboard
    And saya klik menu History di navbar
    Then saya berada di halaman riwayat tiket

  @positive
  Scenario: Halaman riwayat tiket berhasil dimuat
    Given saya berada di halaman riwayat tiket
    Then halaman riwayat berhasil dimuat
    And konten halaman riwayat tidak kosong

  @positive
  Scenario: Kembali ke dashboard dari halaman riwayat
    Given saya berada di halaman riwayat tiket
    When saya klik tombol kembali ke Dashboard
    Then saya berada di halaman dashboard

  @negative
  Scenario: Mengakses halaman riwayat tanpa login harus diarahkan ke login
    Given saya tidak dalam keadaan login
    When saya mengakses halaman riwayat secara langsung
    Then saya diarahkan ke halaman login
