@web @ticket
Feature: Buat Tiket Baru
  Sebagai pengguna yang sudah login
  Saya ingin membuat tiket baru
  Supaya masalah atau kebutuhan saya dapat ditangani

  Background:
    Given saya sudah login sebagai "user1" dengan password "password"
    And saya berada di halaman dashboard

  @positive
  Scenario: Membuat tiket publik dengan data lengkap
    When saya klik tombol Create Ticket
    Then saya berada di halaman buat tiket baru
    When saya mengisi judul tiket dengan "Tiket Publik Automation Test"
    And saya mengisi deskripsi tiket dengan "Ini adalah deskripsi tiket yang dibuat via automation"
    And saya klik Submit Ticket
    Then tiket berhasil dibuat

  @positive
  Scenario: Membuat tiket private dengan data lengkap
    When saya klik tombol Create Ticket
    Then saya berada di halaman buat tiket baru
    When saya mengisi judul tiket dengan "Tiket Private Automation Test"
    And saya mengisi deskripsi tiket dengan "Ini adalah deskripsi tiket private"
    And saya memilih opsi Private
    And saya klik Submit Ticket
    Then tiket berhasil dibuat

  @negative
  Scenario: Membuat tiket tanpa judul harus gagal
    When saya klik tombol Create Ticket
    Then saya berada di halaman buat tiket baru
    When saya mengisi deskripsi tiket dengan "Deskripsi ada tapi judul kosong"
    And saya klik Submit Ticket
    Then tiket gagal dibuat dan saya tetap di halaman buat tiket

  @negative
  Scenario: Mengakses halaman buat tiket tanpa login harus diarahkan ke login
    Given saya tidak dalam keadaan login
    When saya mengakses halaman buat tiket secara langsung
    Then saya diarahkan ke halaman login
