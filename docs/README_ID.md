# MyWallet 💸

> **Languages:**  
> [🇬🇧 English](../README.md) | [🇮🇩 Bahasa Indonesia](README_ID.md) | [🇨🇳 简体中文](README_ZH.md) | [🇯🇵 日本語](README_JA.md)

**MyWallet** adalah aplikasi pencatat keuangan pribadi modern untuk Android, dibuat sepenuhnya dengan **Kotlin**.  
Aplikasi ini menggunakan **Firebase** sebagai backend yang aman untuk sinkronisasi data secara real-time, memberikan wawasan keuangan cerdas, serta menghadirkan widget yang membantu langsung di layar utama.

Aplikasi ini dirancang untuk memberikan gambaran jelas tentang kesehatan finansialmu, membantu mencatat pengeluaran, dan memotivasimu untuk menabung sesuai tujuan.

---

## ✨ Fitur

### Fitur Utama Aplikasi
* **Pencatatan Keuangan Inti:** Catat pemasukan atau pengeluaran dengan detail seperti tanggal, deskripsi, jumlah (dalam Rupiah), dan kategori.
* **Riwayat Transaksi:** Lihat semua catatan transaksi yang dikelompokkan menjadi “Pemasukan Terbaru” dan “Pengeluaran Terbaru”.
* **Dasbor Real-time:** Menampilkan **Saldo Saat Ini** (total pemasukan dikurangi pengeluaran). Jika saldo negatif, tampilannya akan berubah menjadi merah.
* **Filter Berdasarkan Waktu:** Tampilkan data untuk "Sepanjang Waktu", "Bulan Ini", atau "Tahun Ini".
* **Grafik Visual:**
    * **Rincian Pengeluaran:** Diagram donat yang menunjukkan ke mana uangmu paling banyak digunakan.
    * **Perbandingan Pemasukan vs Pengeluaran:** Diagram batang yang menampilkan perbandingan langsung antara total pemasukan dan pengeluaran dalam periode tertentu.
* **Daftar Keinginan (Wishlist) Cerdas:**
    * Tambahkan barang yang ingin kamu beli beserta harganya.
    * **Cek Keterjangkauan:** Bandingkan harga barang dengan saldo saat ini.
    * **Perkiraan Waktu Menabung:** Jika belum cukup, aplikasi menghitung rata-rata tabungan bulanan dan memberikan saran seperti, “Dengan kecepatan menabungmu sekarang, kamu bisa beli ini dalam 5 bulan.”
    * **Checklist:** Tandai item sebagai “selesai” atau “dibeli”, lalu item tersebut akan berpindah ke bawah dengan tanda coretan.
* **Data Aman di Cloud:** Semua data disimpan aman di Firebase Firestore pribadi, terhubung dengan akun anonim unik.
* **Pengaturan Tema:** Ganti tema antara **Terang**, **Gelap**, atau **Sesuai Sistem** kapan saja.

---

## 🏠 Widget Layar Utama
* **Widget Statistik:** Menampilkan Saldo Saat Ini, Total Pemasukan, dan Total Pengeluaran langsung di layar utama.
* **Widget Wishlist:** Menampilkan item wishlist berikutnya yang belum dibeli beserta status keterjangkauannya (contoh: “Kamu bisa beli sekarang!” atau “Perkiraan 3 bulan lagi”).
* **Widget Tambah Cepat:** Tombol “+ Tambah Transaksi” yang bisa ditekan untuk langsung menambahkan pemasukan/pengeluaran tanpa membuka aplikasi utama.

---

## 🛠️ Teknologi yang Digunakan
* **Bahasa:** 100% [Kotlin](https://kotlinlang.org/)
* **Backend:** [Firebase](https://firebase.google.com/)
    * **Autentikasi:** Login anonim untuk akun pengguna unik dan aman.
    * **Database:** [Cloud Firestore](https://firebase.google.com/products/firestore) untuk penyimpanan dan sinkronisasi data secara real-time.
* **Arsitektur:**
    * [MVVM](https://developer.android.com/topic/architecture)
    * [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) (ViewModel, LiveData)
* **UI & Navigasi:**
    * [Android Navigation Component](https://developer.android.com/guide/navigation)
    * [ViewBinding](https://developer.android.com/topic/libraries/view-binding)
    * Material Components (Tombol, Kartu, Kolom Teks)
* **Grafik:** [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
* **Konkruensi:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
* **Widget:** [AppWidgets](https://developer.android.com/guide/topics/appwidgets)

---

## 🚀 Cara Build

Proyek ini dibangun menggunakan Gradle Wrapper.

1. **Klon repositori ini.**
2. **Konfigurasi Firebase:**
    * Buat proyek baru di [Firebase Console](https://console.firebase.google.com/).
    * Tambahkan aplikasi Android dengan nama paket `com.dzadafa.mywallet`.
    * Unduh file `google-services.json` dari Firebase, lalu letakkan di folder `MyWallet/app/`.
    * Di Firebase Console, buka **Authentication → Sign-in method**, lalu aktifkan metode **Anonymous**.
    * Buka **Firestore Database**, lalu buat database baru.
3. **Build Aplikasi:**
    * Hubungkan perangkat Android atau jalankan emulator.
    * Jalankan perintah `gradlew installDebug` dari direktori utama proyek.

---

Would you like me to make this into a clean `.md` file ready to drop into `docs/README_ID.md` (with proper Markdown formatting)?

