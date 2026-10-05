import java.util.ArrayList; // array of object (list dinamis)
import java.util.List; // tipe antarmuka list
import java.util.NoSuchElementException; // dilempar Scanner saat input habis
import java.util.Scanner; // membaca input keyboard

public class Main { // kelas utama program

    // Exception khusus: dilempar saat input habis (EOF / Ctrl+D / Ctrl+Z)
    static class InputSelesai extends RuntimeException { // turunan RuntimeException
        InputSelesai() { super("Input berakhir"); } // pesan default
    }

    // ===================== DATA GLOBAL =====================
    static final Scanner in = new Scanner(System.in); // pembaca input keyboard
    static final List<SmartDevice> daftarPerangkat = new ArrayList<>(); // array of object: semua perangkat
    static final List<Pengguna> daftarPengguna = new ArrayList<>(); // array of object: semua pengguna
    static int nextSensorId = 7; // penghitung ID sensor otomatis (1-6 dipakai data hardcode)

    static final String[] PROTOKOL = {"WiFi", "Zigbee", "Ethernet", "Bluetooth"}; // pilihan protokol
    static final String[] SENSOR = {"Cahaya (LDR)", "Gerak (PIR)", "Suhu (NTC)", "Suhu (DHT22)"}; // pilihan sensor
    static final String[] RESOLUSI = {"720p", "1080p", "2K", "4K"}; // pilihan resolusi CCTV
    static final String[] MODE_AC = {"Cool", "Dry", "Fan", "Eco"}; // pilihan mode AC
    static final String[] AKSES = {"Admin", "Anggota", "Tamu"}; // pilihan tingkat akses

    // ===================== HELPER INPUT & ERROR HANDLING =====================
    static String bacaBaris(String prompt) { // baca satu baris input
        System.out.print(prompt); // tampilkan pertanyaan
        try { // tangkap EOF
            return in.nextLine().trim(); // baca & buang spasi tepi
        } catch (NoSuchElementException e) { // input habis
            throw new InputSelesai(); // lempar exception khusus
        }
    }

    static int bacaInt(String prompt) { // baca bilangan bulat (error handling huruf/simbol)
        while (true) { // ulangi sampai valid
            String s = bacaBaris(prompt); // baca input
            if (!s.matches("[+-]?[0-9]+")) { // bukan angka (huruf/simbol/kosong)
                System.out.println("  [ERROR] Input harus ANGKA bulat (bukan huruf/simbol/kosong). Coba lagi."); // pesan error
                continue; // minta ulang
            }
            try { return Integer.parseInt(s); } // ubah string ke int
            catch (NumberFormatException e) { System.out.println("  [ERROR] Angka terlalu besar. Coba lagi."); } // overflow
        }
    }

    static int bacaIntRentang(String prompt, int minV, int maxV) { // baca angka dalam rentang
        while (true) { // ulangi sampai valid
            int v = bacaInt(prompt); // baca angka (sudah dipastikan angka)
            if (v < minV || v > maxV) { // di luar rentang
                System.out.println("  [ERROR] Angka harus di antara " + minV + " dan " + maxV + ". Coba lagi."); // pesan error
                continue; // minta ulang
            }
            return v; // kembalikan jika valid
        }
    }

    static double bacaDouble(String prompt) { // baca bilangan desimal
        while (true) { // ulangi sampai valid
            String s = bacaBaris(prompt); // baca input
            if (!s.matches("[+-]?[0-9]+([.,][0-9]+)?")) { // bukan angka
                System.out.println("  [ERROR] Input harus ANGKA (contoh 28.5). Coba lagi."); // pesan error
                continue; // minta ulang
            }
            return Double.parseDouble(s.replace(',', '.')); // koma -> titik lalu ubah ke double
        }
    }

    static String bacaTeks(String prompt) { // baca teks tidak kosong
        while (true) { // ulangi sampai valid
            String s = bacaBaris(prompt); // baca input
            if (s.isEmpty()) { System.out.println("  [ERROR] Input tidak boleh kosong. Coba lagi."); continue; } // tolak kosong
            return s; // kembalikan teks
        }
    }

    static String bacaIP(String prompt) { // baca IP dengan validasi format
        while (true) { // ulangi sampai valid
            String s = bacaBaris(prompt); // baca input
            if (KoneksiJaringan.ipValid(s)) return s; // valid -> kembalikan
            System.out.println("  [ERROR] Format IP salah. Contoh: 192.168.1.50 (4 angka 0-255)."); // pesan error
        }
    }

    static String bacaMAC(String prompt) { // baca MAC dengan validasi format
        while (true) { // ulangi sampai valid
            String s = bacaBaris(prompt); // baca input
            if (KoneksiJaringan.macValid(s)) return s; // valid -> kembalikan
            System.out.println("  [ERROR] Format MAC salah. Contoh: AA:BB:CC:00:11:22."); // pesan error
        }
    }

    static String bacaWarna(String prompt) { // baca warna hex dengan validasi
        while (true) { // ulangi sampai valid
            String s = bacaBaris(prompt); // baca input
            if (SmartLampu.warnaValid(s)) return s; // valid -> kembalikan
            System.out.println("  [ERROR] Format warna salah. Contoh: #FFD27F."); // pesan error
        }
    }

    static String bacaEmail(String prompt) { // baca email dengan validasi
        while (true) { // ulangi sampai valid
            String s = bacaBaris(prompt); // baca input
            if (Pengguna.emailValid(s)) return s; // valid -> kembalikan
            System.out.println("  [ERROR] Format email salah. Contoh: nama@domain.com."); // pesan error
        }
    }

    static int pilihOpsi(String judul, String[] opsi) { // tampilkan pilihan bernomor
        System.out.println(judul + ":"); // judul pilihan
        for (int i = 0; i < opsi.length; i++) System.out.println("  " + (i + 1) + ". " + opsi[i]); // daftar pilihan
        return bacaIntRentang("Pilih nomor: ", 1, opsi.length) - 1; // kembalikan indeks 0-based
    }

    // ===================== HELPER DATA =====================
    static void garis(String judul) { // cetak judul bagian
        System.out.println("\n=================================================="); // garis atas
        System.out.println(" " + judul); // judul
        System.out.println("=================================================="); // garis bawah
    }

    static int cariIndexPerangkat(int id) { // cari perangkat berdasarkan ID
        for (int i = 0; i < daftarPerangkat.size(); i++) // loop semua perangkat
            if (daftarPerangkat.get(i).getIdPerangkat() == id) return i; // ketemu -> kembalikan indeks
        return -1; // tidak ketemu
    }

    static int cariIndexPengguna(int id) { // cari pengguna berdasarkan ID
        for (int i = 0; i < daftarPengguna.size(); i++) // loop semua pengguna
            if (daftarPengguna.get(i).getIdPengguna() == id) return i; // ketemu -> kembalikan indeks
        return -1; // tidak ketemu
    }

    static void tampilkanSemua() { // cetak seluruh perangkat
        if (daftarPerangkat.isEmpty()) { // list kosong
            System.out.println("(Belum ada perangkat yang terdaftar)"); // pesan kosong
        } else { // ada data
            for (int i = 0; i < daftarPerangkat.size(); i++) { // loop tiap perangkat
                System.out.println("\nPerangkat ke-" + (i + 1)); // nomor urut
                daftarPerangkat.get(i).displayInfo(); // polimorfisme: method sesuai jenis objek
            }
        }
        System.out.println("\nTotal perangkat: " + daftarPerangkat.size()); // jumlah total
    }

    static void tampilkanPengguna() { // cetak seluruh pengguna
        System.out.println("Daftar pengguna:"); // judul
        for (int i = 0; i < daftarPengguna.size(); i++) { // loop tiap pengguna
            Pengguna p = daftarPengguna.get(i); // ambil pengguna
            System.out.println("  " + (i + 1) + ". [ID " + p.getIdPengguna() + "] " + p.getNama() // nomor, ID, nama
                + " (" + p.getTingkatAkses() + ") - " + p.getEmail()); // akses & email
        }
        System.out.println("Total pengguna: " + daftarPengguna.size()); // jumlah total
    }

    static Pengguna pilihPengguna() { // pilih pemilik dari daftar
        tampilkanPengguna(); // tampilkan daftar bernomor
        int no = bacaIntRentang("Pilih nomor pemilik: ", 1, daftarPengguna.size()); // minta nomor valid
        return daftarPengguna.get(no - 1); // kembalikan pengguna terpilih
    }

    static int pilihPerangkat() { // pilih perangkat dari daftar, 0 = batal
        if (daftarPerangkat.isEmpty()) throw new IllegalArgumentException("Belum ada perangkat."); // trigger: list kosong
        for (int i = 0; i < daftarPerangkat.size(); i++) // tampilkan ringkasan
            System.out.println("  " + (i + 1) + ". [" + daftarPerangkat.get(i).getIdPerangkat() + "] " // nomor & ID
                + daftarPerangkat.get(i).getNamaPerangkat()); // nama perangkat
        return bacaIntRentang("Pilih nomor perangkat (0 = batal): ", 0, daftarPerangkat.size()); // 0 = batal
    }

    // ===================== DATA DUMMY HARDCODE (TAHAP 1) =====================
    static void isiDataDummy() { // isi data awal secara hardcode
        daftarPengguna.add(new Pengguna(1, "Budi Santoso", "Admin", "budi@smarthome.id")); // pengguna 1
        daftarPengguna.add(new Pengguna(2, "Siti Aminah", "Tamu", "siti@smarthome.id")); // pengguna 2

        daftarPerangkat.add(new SmartLampu(101, "Lampu Ruang Tamu", true, // lampu #1
            "192.168.1.11", "AA:BB:CC:00:00:11", "WiFi", "Ruang Tamu", "v2.1.0", // jaringan & lokasi
            new SensorInternal(1, "Cahaya (LDR)", 320.5), daftarPengguna.get(0), 80, "#FFD27F")); // sensor, pemilik, kecerahan, warna
        daftarPerangkat.add(new SmartCCTV(201, "CCTV Teras", true, // CCTV #1
            "192.168.1.21", "AA:BB:CC:00:00:21", "WiFi", "Teras", "v3.4.2", // jaringan & lokasi
            new SensorInternal(2, "Gerak (PIR)", 1.0), daftarPengguna.get(0), "1080p", true, true)); // sensor, pemilik, resolusi, malam, rekam
        daftarPerangkat.add(new SmartAC(301, "AC Ruang Tamu", true, // AC #1
            "192.168.1.31", "AA:BB:CC:00:00:31", "WiFi", "Ruang Tamu", "v1.8.0", // jaringan & lokasi
            new SensorInternal(3, "Suhu (NTC)", 28.0), daftarPengguna.get(0), 24, "Cool")); // sensor, pemilik, suhu, mode
    }

    // ===================== TAMBAH DATA =====================
    static void tambahStatis() { // tambah paket data statis tambahan
        System.out.println("\n--- Tambah Perangkat (STATIS) ---"); // judul
        if (cariIndexPerangkat(102) != -1 || cariIndexPerangkat(202) != -1 || cariIndexPerangkat(302) != -1) // cek duplikat ID
            throw new IllegalArgumentException("Data statis sudah pernah ditambahkan (ID 102/202/302 sudah ada)."); // trigger error
        System.out.println("Sebelum penambahan: " + daftarPerangkat.size() + " perangkat"); // cetak sebelum
        daftarPerangkat.add(new SmartLampu(102, "Lampu Kamar Tidur", false, // lampu #2
            "192.168.1.12", "AA:BB:CC:00:00:12", "Zigbee", "Kamar Tidur", "v2.0.4", // jaringan & lokasi
            new SensorInternal(4, "Cahaya (LDR)", 45.0), daftarPengguna.get(1), 30, "#FF8C42")); // sensor, pemilik, kecerahan, warna
        daftarPerangkat.add(new SmartCCTV(202, "CCTV Garasi", true, // CCTV #2
            "192.168.1.22", "AA:BB:CC:00:00:22", "Ethernet", "Garasi", "v3.4.0", // jaringan & lokasi
            new SensorInternal(5, "Suhu (DHT22)", 29.5), daftarPengguna.get(1), "4K", false, false)); // sensor, pemilik, resolusi, malam, rekam
        daftarPerangkat.add(new SmartAC(302, "AC Kamar Tidur", false, // AC #2
            "192.168.1.32", "AA:BB:CC:00:00:32", "Zigbee", "Kamar Tidur", "v1.7.5", // jaringan & lokasi
            new SensorInternal(6, "Suhu (NTC)", 26.5), daftarPengguna.get(1), 26, "Eco")); // sensor, pemilik, suhu, mode
        System.out.println("3 perangkat statis berhasil ditambahkan."); // konfirmasi
        System.out.println("Sesudah penambahan: " + daftarPerangkat.size() + " perangkat"); // cetak sesudah
        tampilkanSemua(); // tampilkan semua data
    }

    static void tambahDinamis() { // tambah perangkat lewat input user
        System.out.println("\n--- Tambah Perangkat (DINAMIS) ---"); // judul
        int jenis = pilihOpsi("Jenis perangkat", new String[]{"SmartLampu", "SmartCCTV", "SmartAC"}); // pilih jenis (0/1/2)
        int id; // penampung ID
        while (true) { // ulangi sampai ID valid & unik
            id = bacaInt("ID perangkat (angka > 0): "); // baca ID (pasti angka)
            if (id <= 0) { System.out.println("  [ERROR] ID harus lebih dari 0."); continue; } // tolak ID <= 0
            if (cariIndexPerangkat(id) != -1) { System.out.println("  [ERROR] ID " + id + " sudah dipakai perangkat lain."); continue; } // tolak duplikat
            break; // ID valid
        }
        String nama = bacaTeks("Nama perangkat: "); // nama perangkat
        boolean power = bacaIntRentang("Status power (1 = ON, 0 = OFF): ", 0, 1) == 1; // status power
        String ip = bacaIP("IP Address (contoh 192.168.1.50): "); // IP tervalidasi
        String mac = bacaMAC("MAC Address (contoh AA:BB:CC:00:11:22): "); // MAC tervalidasi
        String protokol = PROTOKOL[pilihOpsi("Protokol", PROTOKOL)]; // pilih protokol
        String lokasi = bacaTeks("Lokasi ruangan: "); // lokasi
        String firmware = bacaTeks("Versi firmware (contoh v1.0.0): "); // firmware
        String tipeSensor = SENSOR[pilihOpsi("Tipe sensor internal", SENSOR)]; // pilih tipe sensor
        double nilai = bacaDouble("Nilai bacaan sensor awal: "); // nilai sensor
        SensorInternal sensor = new SensorInternal(nextSensorId++, tipeSensor, nilai); // buat sensor dengan ID otomatis
        Pengguna pemilik = pilihPengguna(); // pilih pemilik (aggregation)

        SmartDevice baru; // referensi untuk objek baru
        if (jenis == 0) { // jika SmartLampu
            int kec = bacaIntRentang("Tingkat kecerahan (0-100): ", 0, 100); // kecerahan
            String warna = bacaWarna("Warna RGB hex (contoh #FFD27F): "); // warna
            baru = new SmartLampu(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik, kec, warna); // buat lampu
        } else if (jenis == 1) { // jika SmartCCTV
            String res = RESOLUSI[pilihOpsi("Resolusi", RESOLUSI)]; // resolusi
            boolean malam = bacaIntRentang("Mode malam (1 = aktif, 0 = nonaktif): ", 0, 1) == 1; // mode malam
            boolean rekam = bacaIntRentang("Status rekam (1 = merekam, 0 = berhenti): ", 0, 1) == 1; // status rekam
            baru = new SmartCCTV(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik, res, malam, rekam); // buat CCTV
        } else { // jika SmartAC
            int suhu = bacaIntRentang("Suhu target (16-30): ", 16, 30); // suhu target
            String mode = MODE_AC[pilihOpsi("Mode pendingin", MODE_AC)]; // mode pendingin
            baru = new SmartAC(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik, suhu, mode); // buat AC
        }
        System.out.println("\nSebelum penambahan: " + daftarPerangkat.size() + " perangkat"); // cetak sebelum
        daftarPerangkat.add(baru); // masukkan ke array of object
        System.out.println("Perangkat \"" + nama + "\" berhasil ditambahkan."); // konfirmasi
        System.out.println("Sesudah penambahan: " + daftarPerangkat.size() + " perangkat"); // cetak sesudah
        tampilkanSemua(); // tampilkan semua data
    }

    static void tambahPengguna() { // tambah pengguna lewat input user
        System.out.println("\n--- Tambah Pengguna (DINAMIS) ---"); // judul
        int id; // penampung ID
        while (true) { // ulangi sampai ID valid & unik
            id = bacaInt("ID pengguna (angka > 0): "); // baca ID
            if (id <= 0) { System.out.println("  [ERROR] ID harus lebih dari 0."); continue; } // tolak ID <= 0
            if (cariIndexPengguna(id) != -1) { System.out.println("  [ERROR] ID " + id + " sudah dipakai."); continue; } // tolak duplikat
            break; // ID valid
        }
        String nama = bacaTeks("Nama pengguna: "); // nama
        String akses = AKSES[pilihOpsi("Tingkat akses", AKSES)]; // pilih akses
        String email = bacaEmail("Email: "); // email tervalidasi
        daftarPengguna.add(new Pengguna(id, nama, akses, email)); // simpan pengguna baru
        System.out.println("Pengguna \"" + nama + "\" berhasil ditambahkan."); // konfirmasi
        tampilkanPengguna(); // tampilkan daftar terbaru
    }

    // ===================== DEMO POLIMORFISME (PENGGANTI MULTIPLE INHERITANCE) =====================
    static void cetakKoneksi(KoneksiJaringan k) { // parameter bertipe INTERFACE: menerima objek apa pun yang meng-implements-nya
        System.out.println("  Diterima sebagai KoneksiJaringan -> " + k.ringkasanJaringan()); // pakai default method interface
    }

    static void cetakPerangkat(PerangkatSistem p) { // parameter bertipe CLASS INDUK: menerima semua turunannya
        System.out.println("  Diterima sebagai PerangkatSistem -> " + p.getNamaPerangkat() // pakai method class induk
            + " (" + (p.getStatusPower() ? "ON" : "OFF") + ")"); // status power
    }

    static void demoPolimorfisme() { // tunjukkan satu objek berperan sebagai dua tipe induk
        System.out.println("\n--- Demo Polimorfisme: Multiple Inheritance di Java ---"); // judul
        System.out.println("Java tidak boleh 'extends' dua class, maka: class + interface."); // penjelasan singkat
        if (daftarPerangkat.isEmpty()) throw new IllegalArgumentException("Belum ada perangkat."); // trigger: list kosong
        for (SmartDevice d : daftarPerangkat) { // loop semua perangkat
            System.out.println("\n[" + d.getClass().getSimpleName() + "] " + d.getNamaPerangkat()); // jenis asli objek (runtime)
            cetakPerangkat(d); // upcast otomatis SmartDevice -> PerangkatSistem
            cetakKoneksi(d); // upcast otomatis SmartDevice -> KoneksiJaringan
            System.out.println("  instanceof PerangkatSistem: " + (d instanceof PerangkatSistem) // cek tipe induk class
                + " | instanceof KoneksiJaringan: " + (d instanceof KoneksiJaringan)); // cek tipe interface
        }
    }

    // ===================== UBAH & HAPUS =====================
    static void ubahPerangkat() { // ubah pengaturan perangkat lewat setter
        System.out.println("\n--- Ubah Pengaturan Perangkat ---"); // judul
        int no = pilihPerangkat(); // pilih perangkat
        if (no == 0) { System.out.println("Dibatalkan."); return; } // 0 = batal
        SmartDevice p = daftarPerangkat.get(no - 1); // ambil perangkat terpilih
        int aksi = pilihOpsi("Pilih yang diubah", new String[]{"Toggle power", "Nilai sensor", "Ganti pemilik", "Pengaturan khusus jenis"}); // submenu
        if (aksi == 0) { // toggle power
            p.togglePower(); // balik ON/OFF
            System.out.println("Power sekarang: " + (p.getStatusPower() ? "ON" : "OFF")); // tampilkan status baru
        } else if (aksi == 1) { // ubah sensor
            p.getSensor().setNilaiBacaan(bacaDouble("Nilai sensor baru: ")); // ubah sensor (composition)
        } else if (aksi == 2) { // ganti pemilik
            p.setPemilik(pilihPengguna()); // ganti pemilik (aggregation)
        } else { // pengaturan khusus
            if (p instanceof SmartLampu) { // cek apakah objek ini lampu
                SmartLampu l = (SmartLampu) p; // cast ke SmartLampu
                int a = pilihOpsi("Pengaturan lampu", new String[]{"Kecerahan", "Warna"}); // submenu lampu
                if (a == 0) l.setTingkatKecerahan(bacaInt("Kecerahan baru (0-100): ")); // setter bisa melempar exception
                else l.setWarnaRGB(bacaTeks("Warna baru (#RRGGBB): ")); // setter bisa melempar exception
            } else if (p instanceof SmartCCTV) { // cek apakah objek ini CCTV
                SmartCCTV c = (SmartCCTV) p; // cast ke SmartCCTV
                int a = pilihOpsi("Pengaturan CCTV", new String[]{"Toggle mode malam", "Toggle rekam", "Resolusi"}); // submenu CCTV
                if (a == 0) c.setModeMalam(!c.getModeMalam()); // balik mode malam
                else if (a == 1) c.setStatusRekam(!c.getStatusRekam()); // balik status rekam
                else c.setResolusi(bacaTeks("Resolusi baru (720p/1080p/2K/4K): ")); // setter bisa melempar exception
            } else if (p instanceof SmartAC) { // cek apakah objek ini AC
                SmartAC ac = (SmartAC) p; // cast ke SmartAC
                int a = pilihOpsi("Pengaturan AC", new String[]{"Suhu target", "Mode pendingin"}); // submenu AC
                if (a == 0) ac.setSuhuTarget(bacaInt("Suhu baru (16-30): ")); // setter bisa melempar exception
                else ac.setModePendingin(bacaTeks("Mode baru (Cool/Dry/Fan/Eco): ")); // setter bisa melempar exception
            }
        }
        System.out.println("\nPerubahan berhasil. Data terbaru:"); // konfirmasi
        p.displayInfo(); // tampilkan perangkat setelah diubah
    }

    static void hapusPerangkat() { // hapus perangkat (demo composition vs aggregation)
        System.out.println("\n--- Hapus Perangkat ---"); // judul
        int no = pilihPerangkat(); // pilih perangkat
        if (no == 0) { System.out.println("Dibatalkan."); return; } // 0 = batal
        if (bacaIntRentang("Yakin hapus? (1 = ya, 0 = batal): ", 0, 1) == 0) { System.out.println("Dibatalkan."); return; } // konfirmasi
        SmartDevice target = daftarPerangkat.remove(no - 1); // keluarkan perangkat dari list
        Pengguna pemilik = target.getPemilik(); // simpan pemilik sebelum perangkat dibuang
        SensorInternal sensor = target.getSensor(); // ambil info sensor milik perangkat
        System.out.println("   [Hapus] SmartDevice \"" + target.getNamaPerangkat() + "\" dihapus -> SensorInternal #"
            + sensor.getIdSensor() + " (" + sensor.getTipeSensor() + ") ikut hilang (Composition)"); // bukti composition
        target = null; // lepas referensi (objek & sensornya dibuang oleh garbage collector)
        if (pemilik != null) // jika punya pemilik
            System.out.println("Data pemilik masih ada (Aggregation): " + pemilik.getNama() + " | " + pemilik.getEmail()); // bukti agregasi
        System.out.println("Sisa perangkat: " + daftarPerangkat.size()); // jumlah sisa
    }

    static void tampilMenu() { // cetak menu utama
        garis("MENU UTAMA SMART HOME"); // judul menu
        System.out.println(" 1. Tambah perangkat - STATIS (paket data contoh)"); // menu 1
        System.out.println(" 2. Tambah perangkat - DINAMIS (input manual)"); // menu 2
        System.out.println(" 3. Tambah pengguna - DINAMIS"); // menu 3
        System.out.println(" 4. Tampilkan semua perangkat"); // menu 4
        System.out.println(" 5. Tampilkan semua pengguna"); // menu 5
        System.out.println(" 6. Ubah pengaturan perangkat"); // menu 6
        System.out.println(" 7. Hapus perangkat"); // menu 7
        System.out.println(" 8. Demo polimorfisme (multiple inheritance via interface)"); // menu 8 (khusus Java)
        System.out.println(" 0. Keluar"); // menu 0
    }

    public static void main(String[] args) { // titik masuk program
        System.out.println("SISTEM SMART HOME / IoT"); // judul program
        try { // bungkus program utama agar EOF input ditangani
            garis("TAHAP 1: DATA DUMMY (HARDCODE)"); // judul tahap 1
            isiDataDummy(); // isi data dummy hardcode
            tampilkanPengguna(); // tampilkan pengguna dummy
            tampilkanSemua(); // tampilkan perangkat dummy

            garis("TAHAP 2: MENU INTERAKTIF"); // judul tahap 2
            while (true) { // loop menu sampai user keluar
                tampilMenu(); // tampilkan menu
                int pilih = bacaIntRentang("Pilih menu (0-8): ", 0, 8); // baca pilihan (validasi angka & rentang)
                if (pilih == 0) break; // 0 = keluar dari loop
                try { // tangani error pada setiap aksi menu
                    switch (pilih) { // jalankan aksi sesuai nomor
                        case 1: tambahStatis(); break; // tambah data statis
                        case 2: tambahDinamis(); break; // tambah data dinamis
                        case 3: tambahPengguna(); break; // tambah pengguna
                        case 4: tampilkanSemua(); break; // tampilkan perangkat
                        case 5: tampilkanPengguna(); break; // tampilkan pengguna
                        case 6: ubahPerangkat(); break; // ubah perangkat
                        case 7: hapusPerangkat(); break; // hapus perangkat
                        case 8: demoPolimorfisme(); break; // demo polimorfisme
                    }
                } catch (InputSelesai e) { // input habis
                    throw e; // teruskan ke handler luar
                } catch (RuntimeException e) { // error validasi dari kelas / fungsi
                    System.out.println("\n  [ERROR] " + e.getMessage()); // tampilkan pesan error
                    System.out.println("  Aksi dibatalkan, kembali ke menu."); // info kembali ke menu
                }
            }
        } catch (InputSelesai e) { // input berakhir (EOF)
            System.out.println("\n[INFO] Input berakhir, program ditutup."); // info keluar
        }

        garis("PEMBERSIHAN MEMORI"); // judul pembersihan
        daftarPerangkat.clear(); // kosongkan list perangkat
        daftarPengguna.clear(); // kosongkan list pengguna
        System.out.println("\nProgram selesai."); // pesan akhir
    }
}
