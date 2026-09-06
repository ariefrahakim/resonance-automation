@web @auth
Feature: Login Web
  Sebagai pengguna Resonance
  Saya ingin bisa login ke aplikasi
  Supaya saya bisa mengakses fitur yang tersedia

  Background:
    Given saya berada di halaman login

  @positive
  Scenario: Login berhasil dengan kredensial valid
    When saya memasukkan username "user1"
    And saya memasukkan password "password"
    And saya klik tombol Login
    Then saya harus diarahkan keluar dari halaman login
    And saya berada di halaman dashboard

  @negative
  Scenario: Login gagal dengan email tidak terdaftar
    When saya memasukkan username "email_tidak_ada@test.com"
    And saya memasukkan password "password"
    And saya klik tombol Login
    Then saya harus melihat pesan error atau tetap di halaman login

  @negative
  Scenario: Login gagal dengan password salah
    When saya memasukkan username "user1"
    And saya memasukkan password "passwordsalah123"
    And saya klik tombol Login
    Then saya harus melihat pesan error atau tetap di halaman login

  @negative
  Scenario: Login gagal dengan field username kosong
    When saya memasukkan username ""
    And saya memasukkan password "password"
    And saya klik tombol Login
    Then saya tetap berada di halaman login

  @negative
  Scenario: Login gagal dengan field password kosong
    When saya memasukkan username "user1"
    And saya memasukkan password ""
    And saya klik tombol Login
    Then saya tetap berada di halaman login

  @negative
  Scenario Outline: Login gagal dengan berbagai kombinasi data tidak valid
    When saya memasukkan username "<username>"
    And saya memasukkan password "<password>"
    And saya klik tombol Login
    Then saya harus melihat pesan error atau tetap di halaman login

    Examples:
      | username                  | password        |
      | pengguna_tidak_ada@x.com  | password        |
      | user1                     | salahpassword   |
      | user1                     | ab              |

  @positive
  Scenario: Halaman login menampilkan link Register dan Lupa Password
    Then saya melihat link menuju halaman register
    And saya melihat link lupa password
