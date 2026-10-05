#include <iostream> // library input/output
#include <vector> // vector (array of object)
#include <string> // library string
#include <regex> // regex untuk validasi angka
#include <stdexcept> // exception standar
#include "SmartLampu.cpp" // kelas SmartLampu
#include "SmartCCTV.cpp" // kelas SmartCCTV
#include "SmartAC.cpp" // kelas SmartAC
using namespace std; // pakai namespace std

// Exception khusus: dilempar saat input habis (EOF / Ctrl+D / Ctrl+Z)
class InputSelesai : public runtime_error { // turunan runtime_error
public: // bagian publik
    InputSelesai() : runtime_error("Input berakhir") {} // pesan default
}; // akhir kelas InputSelesai

// ===================== DATA GLOBAL =====================
vector<SmartDevice*> daftarPerangkat; // array of object: semua perangkat
vector<Pengguna*> daftarPengguna; // array of object: semua pengguna
int nextSensorId = 7; // penghitung ID sensor otomatis (1-6 dipakai data hardcode)

const vector<string> PROTOKOL = {"WiFi", "Zigbee", "Ethernet", "Bluetooth"}; // pilihan protokol
const vector<string> SENSOR = {"Cahaya (LDR)", "Gerak (PIR)", "Suhu (NTC)", "Suhu (DHT22)"}; // pilihan sensor
const vector<string> RESOLUSI = {"720p", "1080p", "2K", "4K"}; // pilihan resolusi CCTV
const vector<string> MODE_AC = {"Cool", "Dry", "Fan", "Eco"}; // pilihan mode AC
const vector<string> AKSES = {"Admin", "Anggota", "Tamu"}; // pilihan tingkat akses

// ===================== HELPER INPUT & ERROR HANDLING =====================
string trim(const string& s) { // buang spasi di awal & akhir
    size_t a = s.find_first_not_of(" \t\r\n"); // posisi non-spasi pertama
    if (a == string::npos) return ""; // semua spasi -> kosong
    size_t b = s.find_last_not_of(" \t\r\n"); // posisi non-spasi terakhir
    return s.substr(a, b - a + 1); // potong bagian yang berisi
}

string bacaBaris(const string& prompt) { // baca satu baris input
    cout << prompt; // tampilkan pertanyaan
    string baris; // penampung input
    if (!getline(cin, baris)) throw InputSelesai(); // EOF -> lempar exception
    return trim(baris); // kembalikan tanpa spasi tepi
}

int bacaInt(const string& prompt) { // baca bilangan bulat (error handling huruf/simbol)
    regex pola("^[+-]?[0-9]+$"); // pola bilangan bulat
    while (true) { // ulangi sampai valid
        string s = bacaBaris(prompt); // baca input
        if (!regex_match(s, pola)) { // bukan angka (huruf/simbol/kosong)
            cout << "  [ERROR] Input harus ANGKA bulat (bukan huruf/simbol/kosong). Coba lagi.\n"; // pesan error
            continue; // minta ulang
        }
        try { return stoi(s); } // ubah string ke int
        catch (const out_of_range&) { cout << "  [ERROR] Angka terlalu besar. Coba lagi.\n"; } // overflow
    }
}

int bacaIntRentang(const string& prompt, int minV, int maxV) { // baca angka dalam rentang
    while (true) { // ulangi sampai valid
        int v = bacaInt(prompt); // baca angka (sudah dipastikan angka)
        if (v < minV || v > maxV) { // di luar rentang
            cout << "  [ERROR] Angka harus di antara " << minV << " dan " << maxV << ". Coba lagi.\n"; // pesan error
            continue; // minta ulang
        }
        return v; // kembalikan jika valid
    }
}

double bacaDouble(const string& prompt) { // baca bilangan desimal
    regex pola("^[+-]?[0-9]+([.,][0-9]+)?$"); // pola desimal (titik/koma)
    while (true) { // ulangi sampai valid
        string s = bacaBaris(prompt); // baca input
        if (!regex_match(s, pola)) { // bukan angka
            cout << "  [ERROR] Input harus ANGKA (contoh 28.5). Coba lagi.\n"; // pesan error
            continue; // minta ulang
        }
        for (size_t i = 0; i < s.size(); i++) if (s[i] == ',') s[i] = '.'; // koma -> titik
        return stod(s); // ubah string ke double
    }
}

string bacaTeks(const string& prompt) { // baca teks tidak kosong
    while (true) { // ulangi sampai valid
        string s = bacaBaris(prompt); // baca input
        if (s.empty()) { cout << "  [ERROR] Input tidak boleh kosong. Coba lagi.\n"; continue; } // tolak kosong
        return s; // kembalikan teks
    }
}

string bacaIP(const string& prompt) { // baca IP dengan validasi format
    while (true) { // ulangi sampai valid
        string s = bacaBaris(prompt); // baca input
        if (KoneksiJaringan::ipValid(s)) return s; // valid -> kembalikan
        cout << "  [ERROR] Format IP salah. Contoh: 192.168.1.50 (4 angka 0-255).\n"; // pesan error
    }
}

string bacaMAC(const string& prompt) { // baca MAC dengan validasi format
    while (true) { // ulangi sampai valid
        string s = bacaBaris(prompt); // baca input
        if (KoneksiJaringan::macValid(s)) return s; // valid -> kembalikan
        cout << "  [ERROR] Format MAC salah. Contoh: AA:BB:CC:00:11:22.\n"; // pesan error
    }
}

string bacaWarna(const string& prompt) { // baca warna hex dengan validasi
    while (true) { // ulangi sampai valid
        string s = bacaBaris(prompt); // baca input
        if (SmartLampu::warnaValid(s)) return s; // valid -> kembalikan
        cout << "  [ERROR] Format warna salah. Contoh: #FFD27F.\n"; // pesan error
    }
}

string bacaEmail(const string& prompt) { // baca email dengan validasi
    while (true) { // ulangi sampai valid
        string s = bacaBaris(prompt); // baca input
        if (Pengguna::emailValid(s)) return s; // valid -> kembalikan
        cout << "  [ERROR] Format email salah. Contoh: nama@domain.com.\n"; // pesan error
    }
}

int pilihOpsi(const string& judul, const vector<string>& opsi) { // tampilkan pilihan bernomor
    cout << judul << ":" << endl; // judul pilihan
    for (size_t i = 0; i < opsi.size(); i++) cout << "  " << (i + 1) << ". " << opsi[i] << endl; // daftar pilihan
    return bacaIntRentang("Pilih nomor: ", 1, (int)opsi.size()) - 1; // kembalikan indeks 0-based
}

// ===================== HELPER DATA =====================
void garis(const string& judul) { // cetak judul bagian
    cout << endl << "==================================================" << endl; // garis atas
    cout << " " << judul << endl; // judul
    cout << "==================================================" << endl; // garis bawah
}

int cariIndexPerangkat(int id) { // cari perangkat berdasarkan ID
    for (size_t i = 0; i < daftarPerangkat.size(); i++) // loop semua perangkat
        if (daftarPerangkat[i]->getIdPerangkat() == id) return (int)i; // ketemu -> kembalikan indeks
    return -1; // tidak ketemu
}

int cariIndexPengguna(int id) { // cari pengguna berdasarkan ID
    for (size_t i = 0; i < daftarPengguna.size(); i++) // loop semua pengguna
        if (daftarPengguna[i]->getIdPengguna() == id) return (int)i; // ketemu -> kembalikan indeks
    return -1; // tidak ketemu
}

void tampilkanSemua() { // cetak seluruh perangkat
    if (daftarPerangkat.empty()) { // list kosong
        cout << "(Belum ada perangkat yang terdaftar)" << endl; // pesan kosong
    } else { // ada data
        for (size_t i = 0; i < daftarPerangkat.size(); i++) { // loop tiap perangkat
            cout << endl << "Perangkat ke-" << (i + 1) << endl; // nomor urut
            daftarPerangkat[i]->displayInfo(); // polimorfisme: method sesuai jenis objek
        }
    }
    cout << endl << "Total perangkat: " << daftarPerangkat.size() << endl; // jumlah total
}

void tampilkanPengguna() { // cetak seluruh pengguna
    cout << "Daftar pengguna:" << endl; // judul
    for (size_t i = 0; i < daftarPengguna.size(); i++) { // loop tiap pengguna
        Pengguna* p = daftarPengguna[i]; // ambil pointer pengguna
        cout << "  " << (i + 1) << ". [ID " << p->getIdPengguna() << "] " << p->getNama() // nomor, ID, nama
             << " (" << p->getTingkatAkses() << ") - " << p->getEmail() << endl; // akses & email
    }
    cout << "Total pengguna: " << daftarPengguna.size() << endl; // jumlah total
}

Pengguna* pilihPengguna() { // pilih pemilik dari daftar
    tampilkanPengguna(); // tampilkan daftar bernomor
    int no = bacaIntRentang("Pilih nomor pemilik: ", 1, (int)daftarPengguna.size()); // minta nomor valid
    return daftarPengguna[no - 1]; // kembalikan pointer pengguna terpilih
}

int pilihPerangkat() { // pilih perangkat dari daftar, 0 = batal
    if (daftarPerangkat.empty()) throw invalid_argument("Belum ada perangkat."); // trigger: list kosong
    for (size_t i = 0; i < daftarPerangkat.size(); i++) // tampilkan ringkasan
        cout << "  " << (i + 1) << ". [" << daftarPerangkat[i]->getIdPerangkat() << "] " // nomor & ID
             << daftarPerangkat[i]->getNamaPerangkat() << endl; // nama perangkat
    return bacaIntRentang("Pilih nomor perangkat (0 = batal): ", 0, (int)daftarPerangkat.size()); // 0 = batal
}

// ===================== DATA DUMMY HARDCODE (TAHAP 1) =====================
void isiDataDummy() { // isi data awal secara hardcode
    daftarPengguna.push_back(new Pengguna(1, "Budi Santoso", "Admin", "budi@smarthome.id")); // pengguna 1
    daftarPengguna.push_back(new Pengguna(2, "Siti Aminah", "Tamu", "siti@smarthome.id")); // pengguna 2

    daftarPerangkat.push_back(new SmartLampu(101, "Lampu Ruang Tamu", true, // lampu #1
        "192.168.1.11", "AA:BB:CC:00:00:11", "WiFi", "Ruang Tamu", "v2.1.0", // jaringan & lokasi
        SensorInternal(1, "Cahaya (LDR)", 320.5), daftarPengguna[0], 80, "#FFD27F")); // sensor, pemilik, kecerahan, warna
    daftarPerangkat.push_back(new SmartCCTV(201, "CCTV Teras", true, // CCTV #1
        "192.168.1.21", "AA:BB:CC:00:00:21", "WiFi", "Teras", "v3.4.2", // jaringan & lokasi
        SensorInternal(2, "Gerak (PIR)", 1.0), daftarPengguna[0], "1080p", true, true)); // sensor, pemilik, resolusi, malam, rekam
    daftarPerangkat.push_back(new SmartAC(301, "AC Ruang Tamu", true, // AC #1
        "192.168.1.31", "AA:BB:CC:00:00:31", "WiFi", "Ruang Tamu", "v1.8.0", // jaringan & lokasi
        SensorInternal(3, "Suhu (NTC)", 28.0), daftarPengguna[0], 24, "Cool")); // sensor, pemilik, suhu, mode
}

// ===================== TAMBAH DATA =====================
void tambahStatis() { // tambah paket data statis tambahan
    cout << "\n--- Tambah Perangkat (STATIS) ---\n"; // judul
    if (cariIndexPerangkat(102) != -1 || cariIndexPerangkat(202) != -1 || cariIndexPerangkat(302) != -1) // cek duplikat ID
        throw invalid_argument("Data statis sudah pernah ditambahkan (ID 102/202/302 sudah ada)."); // trigger error
    size_t sebelum = daftarPerangkat.size(); // jumlah sebelum
    cout << "Sebelum penambahan: " << sebelum << " perangkat" << endl; // cetak sebelum
    daftarPerangkat.push_back(new SmartLampu(102, "Lampu Kamar Tidur", false, // lampu #2
        "192.168.1.12", "AA:BB:CC:00:00:12", "Zigbee", "Kamar Tidur", "v2.0.4", // jaringan & lokasi
        SensorInternal(4, "Cahaya (LDR)", 45.0), daftarPengguna[1], 30, "#FF8C42")); // sensor, pemilik, kecerahan, warna
    daftarPerangkat.push_back(new SmartCCTV(202, "CCTV Garasi", true, // CCTV #2
        "192.168.1.22", "AA:BB:CC:00:00:22", "Ethernet", "Garasi", "v3.4.0", // jaringan & lokasi
        SensorInternal(5, "Suhu (DHT22)", 29.5), daftarPengguna[1], "4K", false, false)); // sensor, pemilik, resolusi, malam, rekam
    daftarPerangkat.push_back(new SmartAC(302, "AC Kamar Tidur", false, // AC #2
        "192.168.1.32", "AA:BB:CC:00:00:32", "Zigbee", "Kamar Tidur", "v1.7.5", // jaringan & lokasi
        SensorInternal(6, "Suhu (NTC)", 26.5), daftarPengguna[1], 26, "Eco")); // sensor, pemilik, suhu, mode
    cout << "3 perangkat statis berhasil ditambahkan." << endl; // konfirmasi
    cout << "Sesudah penambahan: " << daftarPerangkat.size() << " perangkat" << endl; // cetak sesudah
    tampilkanSemua(); // tampilkan semua data
}

void tambahDinamis() { // tambah perangkat lewat input user
    cout << "\n--- Tambah Perangkat (DINAMIS) ---\n"; // judul
    int jenis = pilihOpsi("Jenis perangkat", {"SmartLampu", "SmartCCTV", "SmartAC"}); // pilih jenis (0/1/2)
    int id; // penampung ID
    while (true) { // ulangi sampai ID valid & unik
        id = bacaInt("ID perangkat (angka > 0): "); // baca ID (pasti angka)
        if (id <= 0) { cout << "  [ERROR] ID harus lebih dari 0.\n"; continue; } // tolak ID <= 0
        if (cariIndexPerangkat(id) != -1) { cout << "  [ERROR] ID " << id << " sudah dipakai perangkat lain.\n"; continue; } // tolak duplikat
        break; // ID valid
    }
    string nama = bacaTeks("Nama perangkat: "); // nama perangkat
    bool power = bacaIntRentang("Status power (1 = ON, 0 = OFF): ", 0, 1) == 1; // status power
    string ip = bacaIP("IP Address (contoh 192.168.1.50): "); // IP tervalidasi
    string mac = bacaMAC("MAC Address (contoh AA:BB:CC:00:11:22): "); // MAC tervalidasi
    string protokol = PROTOKOL[pilihOpsi("Protokol", PROTOKOL)]; // pilih protokol
    string lokasi = bacaTeks("Lokasi ruangan: "); // lokasi
    string firmware = bacaTeks("Versi firmware (contoh v1.0.0): "); // firmware
    string tipeSensor = SENSOR[pilihOpsi("Tipe sensor internal", SENSOR)]; // pilih tipe sensor
    double nilai = bacaDouble("Nilai bacaan sensor awal: "); // nilai sensor
    SensorInternal sensor(nextSensorId++, tipeSensor, nilai); // buat sensor dengan ID otomatis
    Pengguna* pemilik = pilihPengguna(); // pilih pemilik (aggregation)

    SmartDevice* baru = nullptr; // pointer untuk objek baru
    if (jenis == 0) { // jika SmartLampu
        int kec = bacaIntRentang("Tingkat kecerahan (0-100): ", 0, 100); // kecerahan
        string warna = bacaWarna("Warna RGB hex (contoh #FFD27F): "); // warna
        baru = new SmartLampu(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik, kec, warna); // buat lampu
    } else if (jenis == 1) { // jika SmartCCTV
        string res = RESOLUSI[pilihOpsi("Resolusi", RESOLUSI)]; // resolusi
        bool malam = bacaIntRentang("Mode malam (1 = aktif, 0 = nonaktif): ", 0, 1) == 1; // mode malam
        bool rekam = bacaIntRentang("Status rekam (1 = merekam, 0 = berhenti): ", 0, 1) == 1; // status rekam
        baru = new SmartCCTV(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik, res, malam, rekam); // buat CCTV
    } else { // jika SmartAC
        int suhu = bacaIntRentang("Suhu target (16-30): ", 16, 30); // suhu target
        string mode = MODE_AC[pilihOpsi("Mode pendingin", MODE_AC)]; // mode pendingin
        baru = new SmartAC(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik, suhu, mode); // buat AC
    }
    cout << "\nSebelum penambahan: " << daftarPerangkat.size() << " perangkat" << endl; // cetak sebelum
    daftarPerangkat.push_back(baru); // masukkan ke array of object
    cout << "Perangkat \"" << nama << "\" berhasil ditambahkan." << endl; // konfirmasi
    cout << "Sesudah penambahan: " << daftarPerangkat.size() << " perangkat" << endl; // cetak sesudah
    tampilkanSemua(); // tampilkan semua data
}

void tambahPengguna() { // tambah pengguna lewat input user
    cout << "\n--- Tambah Pengguna (DINAMIS) ---\n"; // judul
    int id; // penampung ID
    while (true) { // ulangi sampai ID valid & unik
        id = bacaInt("ID pengguna (angka > 0): "); // baca ID
        if (id <= 0) { cout << "  [ERROR] ID harus lebih dari 0.\n"; continue; } // tolak ID <= 0
        if (cariIndexPengguna(id) != -1) { cout << "  [ERROR] ID " << id << " sudah dipakai.\n"; continue; } // tolak duplikat
        break; // ID valid
    }
    string nama = bacaTeks("Nama pengguna: "); // nama
    string akses = AKSES[pilihOpsi("Tingkat akses", AKSES)]; // pilih akses
    string email = bacaEmail("Email: "); // email tervalidasi
    daftarPengguna.push_back(new Pengguna(id, nama, akses, email)); // simpan pengguna baru
    cout << "Pengguna \"" << nama << "\" berhasil ditambahkan." << endl; // konfirmasi
    tampilkanPengguna(); // tampilkan daftar terbaru
}

// ===================== UBAH & HAPUS =====================
void ubahPerangkat() { // ubah pengaturan perangkat lewat setter
    cout << "\n--- Ubah Pengaturan Perangkat ---\n"; // judul
    int no = pilihPerangkat(); // pilih perangkat
    if (no == 0) { cout << "Dibatalkan." << endl; return; } // 0 = batal
    SmartDevice* p = daftarPerangkat[no - 1]; // ambil perangkat terpilih
    int aksi = pilihOpsi("Pilih yang diubah", {"Toggle power", "Nilai sensor", "Ganti pemilik", "Pengaturan khusus jenis"}); // submenu
    if (aksi == 0) { // toggle power
        p->togglePower(); // balik ON/OFF
        cout << "Power sekarang: " << (p->getStatusPower() ? "ON" : "OFF") << endl; // tampilkan status baru
    } else if (aksi == 1) { // ubah sensor
        double v = bacaDouble("Nilai sensor baru: "); // baca nilai baru
        p->getSensor().setNilaiBacaan(v); // ubah sensor (composition)
    } else if (aksi == 2) { // ganti pemilik
        p->setPemilik(pilihPengguna()); // ganti pemilik (aggregation)
    } else { // pengaturan khusus
        if (SmartLampu* l = dynamic_cast<SmartLampu*>(p)) { // cek apakah objek ini lampu
            int a = pilihOpsi("Pengaturan lampu", {"Kecerahan", "Warna"}); // submenu lampu
            if (a == 0) l->setTingkatKecerahan(bacaInt("Kecerahan baru (0-100): ")); // setter bisa melempar exception
            else l->setWarnaRGB(bacaTeks("Warna baru (#RRGGBB): ")); // setter bisa melempar exception
        } else if (SmartCCTV* c = dynamic_cast<SmartCCTV*>(p)) { // cek apakah objek ini CCTV
            int a = pilihOpsi("Pengaturan CCTV", {"Toggle mode malam", "Toggle rekam", "Resolusi"}); // submenu CCTV
            if (a == 0) c->setModeMalam(!c->getModeMalam()); // balik mode malam
            else if (a == 1) c->setStatusRekam(!c->getStatusRekam()); // balik status rekam
            else c->setResolusi(bacaTeks("Resolusi baru (720p/1080p/2K/4K): ")); // setter bisa melempar exception
        } else if (SmartAC* ac = dynamic_cast<SmartAC*>(p)) { // cek apakah objek ini AC
            int a = pilihOpsi("Pengaturan AC", {"Suhu target", "Mode pendingin"}); // submenu AC
            if (a == 0) ac->setSuhuTarget(bacaInt("Suhu baru (16-30): ")); // setter bisa melempar exception
            else ac->setModePendingin(bacaTeks("Mode baru (Cool/Dry/Fan/Eco): ")); // setter bisa melempar exception
        }
    }
    cout << "\nPerubahan berhasil. Data terbaru:" << endl; // konfirmasi
    p->displayInfo(); // tampilkan perangkat setelah diubah
}

void hapusPerangkat() { // hapus perangkat (demo composition vs aggregation)
    cout << "\n--- Hapus Perangkat ---\n"; // judul
    int no = pilihPerangkat(); // pilih perangkat
    if (no == 0) { cout << "Dibatalkan." << endl; return; } // 0 = batal
    int yakin = bacaIntRentang("Yakin hapus? (1 = ya, 0 = batal): ", 0, 1); // konfirmasi
    if (yakin == 0) { cout << "Dibatalkan." << endl; return; } // batal
    Pengguna* pemilik = daftarPerangkat[no - 1]->getPemilik(); // simpan pemilik sebelum dihapus
    delete daftarPerangkat[no - 1]; // hapus objek (sensor ikut hancur)
    daftarPerangkat.erase(daftarPerangkat.begin() + (no - 1)); // buang dari vector
    if (pemilik != nullptr) // jika punya pemilik
        cout << "Data pemilik masih ada (Aggregation): " << pemilik->getNama() << " | " << pemilik->getEmail() << endl; // bukti agregasi
    cout << "Sisa perangkat: " << daftarPerangkat.size() << endl; // jumlah sisa
}

void tampilMenu() { // cetak menu utama
    garis("MENU UTAMA SMART HOME"); // judul menu
    cout << " 1. Tambah perangkat - STATIS (paket data contoh)" << endl; // menu 1
    cout << " 2. Tambah perangkat - DINAMIS (input manual)" << endl; // menu 2
    cout << " 3. Tambah pengguna - DINAMIS" << endl; // menu 3
    cout << " 4. Tampilkan semua perangkat" << endl; // menu 4
    cout << " 5. Tampilkan semua pengguna" << endl; // menu 5
    cout << " 6. Ubah pengaturan perangkat" << endl; // menu 6
    cout << " 7. Hapus perangkat" << endl; // menu 7
    cout << " 0. Keluar" << endl; // menu 0
}

int main() { // titik masuk program
    cout << "SISTEM SMART HOME / IoT" << endl; // judul program
    try { // bungkus program utama agar EOF input ditangani
        garis("TAHAP 1: DATA DUMMY (HARDCODE)"); // judul tahap 1
        isiDataDummy(); // isi data dummy hardcode
        tampilkanPengguna(); // tampilkan pengguna dummy
        tampilkanSemua(); // tampilkan perangkat dummy

        garis("TAHAP 2: MENU INTERAKTIF"); // judul tahap 2
        while (true) { // loop menu sampai user keluar
            tampilMenu(); // tampilkan menu
            int pilih = bacaIntRentang("Pilih menu (0-7): ", 0, 7); // baca pilihan (validasi angka & rentang)
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
                }
            } catch (const InputSelesai&) { // input habis
                throw; // teruskan ke handler luar
            } catch (const exception& e) { // error validasi dari kelas / fungsi
                cout << "\n  [ERROR] " << e.what() << endl; // tampilkan pesan error
                cout << "  Aksi dibatalkan, kembali ke menu." << endl; // info kembali ke menu
            }
        }
    } catch (const InputSelesai&) { // input berakhir (EOF)
        cout << "\n[INFO] Input berakhir, program ditutup." << endl; // info keluar
    }

    garis("PEMBERSIHAN MEMORI"); // judul pembersihan
    for (size_t i = 0; i < daftarPerangkat.size(); i++) delete daftarPerangkat[i]; // hapus semua perangkat
    daftarPerangkat.clear(); // kosongkan vector
    for (size_t i = 0; i < daftarPengguna.size(); i++) delete daftarPengguna[i]; // hapus semua pengguna
    daftarPengguna.clear(); // kosongkan vector
    cout << "\nProgram selesai." << endl; // pesan akhir
    return 0; // kode sukses
}
