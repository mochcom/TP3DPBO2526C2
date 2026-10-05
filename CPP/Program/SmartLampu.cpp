#pragma once // cegah include berulang
#include "SmartDevice.cpp" // kelas induk

// Derived Subclass 1 (Hierarchical Inheritance dari SmartDevice)
class SmartLampu : public SmartDevice { // SmartLampu "adalah" SmartDevice
private: // atribut khusus lampu
    int tingkatKecerahan; // 0 - 100 (%)
    string warnaRGB; // format hex #RRGGBB

public: // bagian publik
    // Validasi warna: '#' diikuti 6 digit heksadesimal
    static bool warnaValid(const string& w) { // fungsi statis validasi warna
        if (w.size() != 7 || w[0] != '#') return false; // panjang 7 & diawali '#'
        for (size_t i = 1; i < w.size(); i++) // cek 6 karakter sisanya
            if (!isxdigit((unsigned char)w[i])) return false; // harus digit heksa
        return true; // valid
    }

    SmartLampu() : SmartDevice(), tingkatKecerahan(0), warnaRGB("#FFFFFF") {} // constructor default

    SmartLampu(int id, string nama, bool power, string ip, string mac, string protokol, // constructor lengkap
               string lokasi, string firmware, SensorInternal sensor, Pengguna* pemilik,
               int kecerahan, string warna)
        : SmartDevice(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik), // panggil induk
          tingkatKecerahan(0), warnaRGB("#FFFFFF") { // nilai awal sebelum divalidasi
        setTingkatKecerahan(kecerahan); // isi lewat setter (divalidasi)
        setWarnaRGB(warna); // isi lewat setter (divalidasi)
    }

    // Overriding: tampilkan data lampu
    void displayInfo() override { // menimpa method induk
        cout << "[SmartLampu] " << namaPerangkat << endl; // judul
        SmartDevice::displayInfo(); // cetak data umum dari induk
        cout << "  " << setw(15) << "Kecerahan" << ": " << tingkatKecerahan << "%" << endl; // kecerahan
        cout << "  " << setw(15) << "Warna RGB" << ": " << warnaRGB << endl; // warna
    }

    void setTingkatKecerahan(int level) { // setter dengan validasi rentang
        if (level < 0 || level > 100) // di luar 0-100
            throw invalid_argument("Tingkat kecerahan harus 0-100, bukan " + to_string(level)); // trigger error
        tingkatKecerahan = level; // simpan jika valid
    }
    int getTingkatKecerahan() { return tingkatKecerahan; } // getter kecerahan

    void setWarnaRGB(string warna) { // setter dengan validasi format
        if (!warnaValid(warna)) // format salah
            throw invalid_argument("Warna harus berformat #RRGGBB, bukan '" + warna + "'"); // trigger error
        warnaRGB = warna; // simpan jika valid
    }
    string getWarnaRGB() { return warnaRGB; } // getter warna
}; // akhir kelas SmartLampu
