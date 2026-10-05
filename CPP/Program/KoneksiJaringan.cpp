#pragma once // cegah file di-include berulang
#include <iostream> // library input/output
#include <string> // library string
#include <stdexcept> // untuk invalid_argument (error handling)
#include <cctype> // untuk isdigit & isxdigit
using namespace std; // pakai namespace std

// Base Class 2: konfigurasi jaringan & konektivitas IoT
class KoneksiJaringan { // deklarasi kelas dasar kedua
protected: // atribut dapat diakses kelas turunan
    string ipAddress; // alamat IP perangkat
    string macAddress; // alamat MAC perangkat
    string protokol; // protokol komunikasi (WiFi, Zigbee, dll)

public: // bagian publik
    // Validasi format IPv4: 4 angka (0-255) dipisah titik
    static bool ipValid(const string& ip) { // fungsi statis, bisa dipanggil tanpa objek
        int jumlahBagian = 0; // jumlah segmen yang sah
        int nilai = -1; // nilai segmen berjalan (-1 = belum ada digit)
        for (size_t i = 0; i <= ip.size(); i++) { // loop sampai posisi setelah karakter terakhir
            char c = (i < ip.size()) ? ip[i] : '.'; // akhir string dianggap titik penutup
            if (c == '.') { // akhir satu segmen
                if (nilai < 0) return false; // segmen kosong -> tidak valid
                jumlahBagian++; // tambah segmen sah
                nilai = -1; // reset untuk segmen berikutnya
            } else if (isdigit((unsigned char)c)) { // karakter berupa digit
                nilai = (nilai < 0 ? 0 : nilai) * 10 + (c - '0'); // susun angka segmen
                if (nilai > 255) return false; // lebih dari 255 -> tidak valid
            } else { // karakter lain (huruf/simbol)
                return false; // tidak valid
            }
        }
        return jumlahBagian == 4; // harus tepat 4 segmen
    }

    // Validasi format MAC: XX:XX:XX:XX:XX:XX (heksadesimal)
    static bool macValid(const string& mac) { // fungsi statis validasi MAC
        if (mac.size() != 17) return false; // panjang harus 17 karakter
        for (size_t i = 0; i < mac.size(); i++) { // cek tiap karakter
            if (i % 3 == 2) { // posisi 2,5,8,11,14 harus ':'
                if (mac[i] != ':') return false; // bukan ':' -> tidak valid
            } else if (!isxdigit((unsigned char)mac[i])) { // selain itu harus digit heksa
                return false; // tidak valid
            }
        }
        return true; // lolos semua pengecekan
    }

    KoneksiJaringan() : ipAddress(""), macAddress(""), protokol("") {} // constructor default
    KoneksiJaringan(string ip, string mac, string protokol) : protokol(protokol) { // constructor berparameter
        setIpAddress(ip); // isi IP lewat setter (ikut divalidasi)
        setMacAddress(mac); // isi MAC lewat setter (ikut divalidasi)
    }

    void setIpAddress(string ip) { // setter IP dengan validasi
        if (!ipValid(ip)) throw invalid_argument("Format IP Address tidak valid: '" + ip + "'"); // trigger error
        ipAddress = ip; // simpan jika valid
    }
    string getIpAddress() { return ipAddress; } // getter IP

    void setMacAddress(string mac) { // setter MAC dengan validasi
        if (!macValid(mac)) throw invalid_argument("Format MAC Address tidak valid: '" + mac + "'"); // trigger error
        macAddress = mac; // simpan jika valid
    }
    string getMacAddress() { return macAddress; } // getter MAC

    void setProtokol(string p) { protokol = p; } // setter protokol
    string getProtokol() { return protokol; } // getter protokol
}; // akhir kelas KoneksiJaringan
