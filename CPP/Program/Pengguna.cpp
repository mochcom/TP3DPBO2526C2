#pragma once // cegah include berulang
#include <iostream> // library input/output
#include <string> // library string
#include <stdexcept> // untuk invalid_argument
using namespace std; // pakai namespace std

// Class (Aggregation): pemilik rumah, daur hidupnya berdiri sendiri
class Pengguna { // deklarasi kelas pengguna
private: // atribut privat
    int idPengguna; // ID pengguna
    string nama; // nama pengguna
    string tingkatAkses; // Admin / Anggota / Tamu
    string email; // alamat email

public: // bagian publik
    // Validasi sederhana email: ada '@' dan ada '.' setelahnya
    static bool emailValid(const string& e) { // fungsi statis validasi email
        size_t at = e.find('@'); // posisi '@'
        if (at == string::npos || at == 0) return false; // tidak ada '@' atau di awal
        size_t titik = e.find('.', at); // posisi '.' setelah '@'
        return titik != string::npos && titik > at + 1 && titik < e.size() - 1; // titik di tengah domain
    }

    Pengguna() : idPengguna(0), nama(""), tingkatAkses(""), email("") {} // constructor default
    Pengguna(int id, string nama, string akses, string email) // constructor berparameter
        : idPengguna(id), nama(nama), tingkatAkses(akses), email("") { // email diisi lewat setter
        setEmail(email); // validasi email saat objek dibuat
    }

    void setIdPengguna(int id) { idPengguna = id; } // setter ID
    int getIdPengguna() { return idPengguna; } // getter ID

    void setNama(string n) { nama = n; } // setter nama
    string getNama() { return nama; } // getter nama

    void setTingkatAkses(string akses) { tingkatAkses = akses; } // setter tingkat akses
    string getTingkatAkses() { return tingkatAkses; } // getter tingkat akses

    void setEmail(string e) { // setter email dengan validasi
        if (!emailValid(e)) throw invalid_argument("Format email tidak valid: '" + e + "'"); // trigger error
        email = e; // simpan jika valid
    }
    string getEmail() { return email; } // getter email
}; // akhir kelas Pengguna
