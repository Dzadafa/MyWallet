# MyWallet 💸

> **Bahasa:** [🇬🇧 English](../README.md) | [🇮🇩 Bahasa Indonesia](README_ID.md) | [🇨🇳 简体中文](README_ZH.md) | [🇯🇵 日本語](README_JA.md)

<p align="center">
  <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="Ikon Aplikasi MyWallet" width="150">
</p>

MyWallet adalah aplikasi pelacak keuangan pribadi modern untuk Android yang dibuat seluruhnya dengan Kotlin. Aplikasi ini mengutamakan mode *offline-first* dan menggunakan database Room lokal untuk menjaga data Anda tetap cepat, pribadi, dan selalu tersedia, sekaligus menyediakan wawasan finansial dan widget layar utama yang membantu.

---

## ✨ Fitur

### 📱 Fitur Utama Aplikasi

* 📊 **Dasbor Real-time:** Lihat Saldo Saat Ini, Total Pemasukan, dan Total Pengeluaran Anda, semuanya dapat difilter berdasarkan waktu (Sepanjang Waktu, Bulan Ini, Tahun Ini).
* 📈 **Grafik Visual:**
    * **Grafik Donat** untuk melihat ke mana uang Anda paling banyak digunakan.
    * **Grafik Batang** untuk membandingkan total Pemasukan vs. Pengeluaran Anda.
* ✍️ **Catat Semuanya:** Catat pemasukan dan pengeluaran dengan mudah, lengkap dengan tanggal, deskripsi, jumlah, dan kategori.
* ⭐ **Daftar Keinginan (Wishlist) Cerdas:**
    * Tambahkan barang yang ingin Anda beli.
    * Dapatkan **pengecekan keterjangkauan** instan terhadap saldo Anda.
    * Lihat **perkiraan waktu menabung** (misal, "Est. 5 bulan lagi") berdasarkan kebiasaan menabung Anda.
* ✏️ **Edit & Hapus:**
    * Ketuk ikon pengaturan pada transaksi apa pun untuk mengedit atau menghapusnya.
    * Ketuk ikon pengaturan pada item wishlist untuk mengeditnya.
    * Tandai item wishlist sebagai "selesai", dan ikon edit akan berubah menjadi ikon hapus.
* 🔔 **Notifikasi:** Dapatkan notifikasi lokal instan setiap kali Anda berhasil menambahkan transaksi baru.
* ⏰ **Pengingat Harian:** Atur pengingat harian khusus (default jam 21:00) agar Anda tidak pernah lupa mencatat keuangan.
* 🎨 **Pengaturan Tema:** Ganti tema secara instan antara mode **Terang**, **Gelap**, atau **Sesuai Sistem**.
* 🔒 **Offline & Pribadi:** Semua data disimpan dengan aman di database Room pribadi pada perangkat Anda. Tidak perlu akun atau koneksi internet.

### 🏠 Widget Layar Utama

* 💰 **Widget Statistik:** Total saldo, pemasukan, dan pengeluaran Anda dalam sekejap. Diperbarui secara instan saat Anda membuat perubahan di aplikasi.
* 🛒 **Widget Wishlist:** Daftar interaktif yang dapat di-scroll. Anda dapat menandai barang impian Anda langsung dari layar utama.
* ➕ **Widget Tambah Cepat:** Tombol sederhana yang membuka layar khusus untuk menambahkan transaksi baru dengan cepat tanpa membuka aplikasi penuh.

---

## 🛠 Teknologi yang Digunakan

* **Bahasa:** 100% **Kotlin**
* **Arsitektur:** **MVVM** (ViewModel, LiveData)
* **Database:** **Room** (untuk penyimpanan database SQL lokal di perangkat)
* **UI:** **ViewBinding**, **Android Navigation Component**, Material Components
* **Konkurensi:** **Kotlin Coroutines**
* **Async:** `AlarmManager` & `BroadcastReceiver` (untuk pengingat harian)
* **Grafik:** **MPAndroidChart**
* **Widget:** **AppWidgets** dengan `RemoteViews` & `ListView`

---

## 🚀 Cara Build

Proyek ini menggunakan Gradle wrapper.

1.  Salin (clone) repositori ini.
2.  Hubungkan perangkat Android atau jalankan emulator.
3.  Jalankan `gradlew installDebug` dari direktori root proyek.

---

## 🔮 Rencana Masa Depan (v2.0)

* 🎨 **UI Material 3:** Memigrasikan seluruh aplikasi ke sistem desain "Material You" yang modern.
* 🔔 **Push Notification:** Menambahkan Firebase Cloud Messaging (FCM) untuk notifikasi penting.
* ⚙️ **Remote Config:** Menggunakan Firebase Remote Config untuk menampilkan modal atau pesan dinamis di dalam aplikasi.
