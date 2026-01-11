FocusFlow: Transform Your Focus into a Flourishing World 🌿
FocusFlow adalah asisten produktivitas berbasis Android yang mengubah manajemen waktu menjadi pengalaman gamifikasi yang menenangkan. Menggunakan teknik Pomodoro yang dipadukan dengan mekanisme virtual island, aplikasi ini membantu pengguna membangun disiplin melalui ekosistem digital yang hidup.

🚀 Visi Produk
Menjadi asisten produktivitas nomor satu yang mengubah rutinitas membosankan menjadi perjalanan membangun disiplin yang estetis dan interaktif.

✨ Fitur Utama
1. The Engine (Mesin Fokus)
Deep Work Timer: Mode Pomodoro yang dapat dikustomisasi (25/5, 50/10, atau Unlimited Flow).

Multi-Level Blocking:

Soft Mode: Notifikasi persuasif jika mencoba keluar dari aplikasi.

Hard Mode: Penguncian aplikasi distraksi (Instagram, TikTok, dll) menggunakan Accessibility Service.

Smart Whitelist: Akses aplikasi tertentu berdasarkan kategori tugas (Contoh: Buka StackOverflow otomatis diizinkan saat sesi "Coding").

2. The Island (Ekosistem Gamifikasi)
Virtual Island: Mulai dengan pulau kosong dan tumbuhkan ekosistemmu setiap kali berhasil fokus.

Permadeath Mechanic: Jika menyerah di tengah jalan, tanaman akan layu atau bangunan akan runtuh.

Zen Coins: Dapatkan mata uang virtual untuk membeli dekorasi langka.

3. Analitik AI & Sinkronisasi
Productivity Heatmap: Visualisasi jam paling produktif Anda.

Distraction Insights: Analisis aplikasi mana yang paling sering mengganggu fokus Anda.

Cross-Device Sync: Sinkronisasi status fokus dengan ekstensi browser untuk memblokir situs hiburan di laptop.

🛠️ Tech Stack
Language: Kotlin

UI Framework: Jetpack Compose (Declarative UI & Smooth Animations)

Local Database: Room Persistence

Cloud Database: Firebase Firestore (Real-time Sync)

Background Processing: WorkManager

Logic: Accessibility Service API (untuk Hard Mode blocking)

📊 Arsitektur Data & Logika
Skema Database (Simplifikasi)
Aplikasi ini menggunakan struktur data relasional untuk mengelola progres pengguna:

Users: Profil, Zen Coins, dan Level.

FocusSessions: Riwayat waktu, durasi, dan status keberhasilan.

VirtualAssets: Katalog item dekorasi pulau.

AppWhitelist: Daftar aplikasi yang diizinkan per kategori.

Formula Reward
Kami menggunakan algoritma khusus untuk menghitung perolehan koin agar tetap adil dan menantang:

Coins= 
Distractions+1
Duration×Multiplier
​
 
Keterangan:

Multiplier: Hard Mode (1.5x) | Soft Mode (1.0x).

Distractions: Jumlah percobaan membuka aplikasi terlarang.

🎨 Desain UI/UX
Tema: Minimalist-Zen (Sage Green, Soft Blue, Warm Beige).

Atmosphere: Animasi pohon tertiup angin dan integrasi suara latar (Binaural beats, White noise, Lofi).

🛣️ Roadmap Pengembangan
[x] V1 (MVP): Core Timer, Hard Mode dasar, dan 1 jenis pulau.

[ ] V2: Chrome Extension Sync & Social Leaderboard.

[ ] V3: AI Productivity Coach & 3D Island Assets.

⚙️ Instalasi (Bagi Developer)
Clone repositori ini:

Bash

git clone https://github.com/username/FocusFlow.git
Buka project di Android Studio Ladybug atau versi terbaru.

Hubungkan dengan project Firebase Anda (tambahkan google-services.json).

Pastikan perangkat/emulator memiliki izin Accessibility Service untuk menguji fitur Hard Mode.

Build & Run.

FocusFlow — Build your focus, grow your world.
