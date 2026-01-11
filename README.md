# FocusFlow: Build Your Focus, Grow Your World 🌿

[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org/)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![UI](https://img.shields.io/badge/UI-Jetpack_Compose-orange.svg)](https://developer.android.com/jetpack/compose)

**FocusFlow** adalah aplikasi asisten produktivitas Android yang menggabungkan teknik manajemen waktu dengan mekanisme *virtual world-building*. Ubah durasi fokusmu menjadi aset digital yang indah dan bangun disiplin dengan cara yang menenangkan.

---

## ✨ Fitur Utama (Core Features)

### 🚀 The Engine (Mesin Fokus)
* **Deep Work Timer:** Teknik Pomodoro yang bisa dikustomisasi (25/5, 50/10, atau Flow State).
* **Multi-Level Blocking:**
    * *Soft Mode:* Notifikasi persuasif jika keluar aplikasi.
    * *Hard Mode:* Mengunci aplikasi distraksi menggunakan **Accessibility Service**.
* **Smart Whitelist:** Izinkan aplikasi tertentu berdasarkan kategori tugas (Contoh: Buka *ChatGPT* saat kategori "Coding").

### 🏝️ The Island (Gamifikasi)
* **Virtual Island:** Setiap sesi fokus yang berhasil akan menumbuhkan tanaman atau bangunan di pulau pribadimu.
* **Permadeath Mechanic:** Jika kamu menyerah di tengah sesi, tanaman akan layu secara permanen.
* **Zen Coins:** Mata uang hasil kerja keras untuk membeli dekorasi pulau yang langka.

### 📊 Analitik AI & Sinkronisasi
* **Productivity Heatmap:** Mengetahui kapan waktu paling produktifmu.
* **Anti-Cheating Sync:** Sinkronisasi otomatis ke ekstensi browser untuk memblokir situs hiburan di laptop saat mode fokus aktif.

---

## 🛠️ Tech Stack

| Komponen | Teknologi |
| :--- | :--- |
| **Bahasa** | Kotlin |
| **UI Framework** | Jetpack Compose |
| **Database Local** | Room Database |
| **Cloud/Sync** | Firebase Firestore |
| **Background Task** | WorkManager |
| **Arsitektur** | MVVM (Model-View-ViewModel) |

---

## 📐 Arsitektur Data

### Logika Perhitungan Reward
Kami menggunakan formula khusus untuk menghitung perolehan koin berdasarkan tingkat kesulitan:

$$Coins = \frac{Duration \times Multiplier}{Distractions + 1}$$

* **Multiplier:** Hard Mode (1.5x) | Soft Mode (1.0x).
* **Distractions:** Jumlah percobaan akses aplikasi terlarang.

### Skema Database Utama
* `Users`: Profil, total XP, Zen Coins.
* `FocusSessions`: Riwayat waktu, durasi, dan status (Success/Failed).
* `VirtualAssets`: Inventory item dekorasi pulau.
* `AppWhitelist`: Konfigurasi paket aplikasi yang diizinkan.

---

## 🎨 Desain Visual
* **Tema:** Minimalist-Zen dengan palet warna Pastel (Sage Green, Soft Blue, Warm Beige).
* **Ambience:** Integrasi suara latar seperti Binaural beats, White noise, dan Lofi hip-hop.

---

## 🛣️ Roadmap
- [x] **V1 (MVP):** Timer dasar, Hard Mode, dan 1 jenis pulau sederhana.
- [ ] **V2:** Sinkronisasi ke ekstensi Chrome dan Leaderboard global.
- [ ] **V3:** Integrasi AI Coach dan kustomisasi pulau 3D Assets.

---

## ⚙️ Cara Menjalankan Project

1. Clone repositori:
   ```bash
   git clone [https://github.com/username/FocusFlow.git](https://github.com/username/FocusFlow.git)
2. Buka di Android Studio (Versi Ladybug ke atas direkomendasikan).
3. Tambahkan file google-services.json dari Firebase Console ke folder app/.
4. Aktifkan Accessibility Service di pengaturan HP untuk mencoba fitur Hard Mode.

---
## 🖼️ Screenshot Taken:
![WhatsApp Image 2026-01-06 at 21 38 43 (2)](https://github.com/user-attachments/assets/4225d299-e451-4695-ac23-042f4978076a)
![WhatsApp Image 2026-01-07 at 21 50 52](https://github.com/user-attachments/assets/961de746-606f-4e90-83d4-db6d72f94d1b)
![WhatsApp Image 2026-01-06 at 21 38 43](https://github.com/user-attachments/assets/043a31d5-e201-471c-a13a-0d0028b629ca)
![WhatsApp Image 2026-01-06 at 21 38 43 (1)](https://github.com/user-attachments/assets/9778526e-777a-4e27-a950-3fe179dec9a3)
