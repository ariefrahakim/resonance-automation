@web @ticket
Feature: Melihat Daftar Tiket
  Sebagai pengguna yang sudah login
  Saya ingin melihat daftar tiket yang tersedia
  Supaya saya dapat memantau status dan progres tiket

  Background:
    Given saya sudah login sebagai "user1" dengan password "password"

  @positive
  Scenario: Halaman dashboard menampilkan daftar tiket
    When saya berada di halaman dashboard
    Then halaman dashboard berhasil dimuat
    And konten halaman tidak kosong

  @positive
  Scenario: Memfilter tiket berdasarkan urutan terbaru
    When saya berada di halaman dashboard
    And saya memfilter tiket dengan urutan "Newest"
    Then halaman dashboard berhasil dimuat

  @positive
  Scenario: Memfilter tiket berdasarkan jumlah vote
    When saya berada di halaman dashboard
    And saya memfilter tiket dengan urutan "Vote"
    Then halaman dashboard berhasil dimuat

  @positive
  Scenario: Mencari tiket dengan kata kunci
    When saya berada di halaman dashboard
    And saya mencari tiket dengan kata kunci "Test"
    Then halaman dashboard berhasil dimuat

  @negative
  Scenario: Mengakses halaman tiket dengan ID tidak valid
    When saya mengakses halaman tiket dengan ID "id-tidak-valid-xyz-123"
    Then halaman menampilkan error atau melakukan redirect
