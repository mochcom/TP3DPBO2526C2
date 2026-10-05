#pragma once // cegah include berulang
#include "SmartDevice.cpp" // kelas induk

// Derived Subclass 2 (Hierarchical Inheritance dari SmartDevice)
class SmartCCTV : public SmartDevice { // SmartCCTV "adalah" SmartDevice
private: // atribut khusus CCTV
    string resolusi; // 720p / 1080p / 2K / 4K
    bool modeMalam; // mode penglihatan malam
    bool statusRekam; // true = sedang merekam

public: // bagian publik
    // Validasi resolusi: hanya nilai yang didukung
    static bool resolusiValid(const string& r) { // fungsi statis validasi resolusi
        return r == "720p" || r == "1080p" || r == "2K" || r == "4K"; // daftar resolusi sah
    }

    SmartCCTV() : SmartDevice(), resolusi("720p"), modeMalam(false), statusRekam(false) {} // constructor default

    SmartCCTV(int id, string nama, bool power, string ip, string mac, string protokol, // constructor lengkap
              string lokasi, string firmware, SensorInternal sensor, Pengguna* pemilik,
              string resolusi, bool modeMalam, bool statusRekam)
        : SmartDevice(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik), // panggil induk
          resolusi("720p"), modeMalam(modeMalam), statusRekam(statusRekam) { // nilai awal
        setResolusi(resolusi); // isi resolusi lewat setter (divalidasi)
    }

    // Overriding: tampilkan data CCTV
    void displayInfo() override { // menimpa method induk
        cout << "[SmartCCTV] " << namaPerangkat << endl; // judul
        SmartDevice::displayInfo(); // cetak data umum dari induk
        cout << "  " << setw(15) << "Resolusi" << ": " << resolusi << endl; // resolusi
        cout << "  " << setw(15) << "Mode Malam" << ": " << (modeMalam ? "Aktif" : "Nonaktif") << endl; // mode malam
        cout << "  " << setw(15) << "Status Rekam" << ": " << (statusRekam ? "Merekam" : "Berhenti") << endl; // rekam
    }

    void setResolusi(string r) { // setter dengan validasi
        if (!resolusiValid(r)) // resolusi tidak didukung
            throw invalid_argument("Resolusi '" + r + "' tidak didukung (720p/1080p/2K/4K)"); // trigger error
        resolusi = r; // simpan jika valid
    }
    string getResolusi() { return resolusi; } // getter resolusi

    void setModeMalam(bool status) { modeMalam = status; } // setter mode malam
    bool getModeMalam() { return modeMalam; } // getter mode malam

    void setStatusRekam(bool rekam) { statusRekam = rekam; } // setter status rekam
    bool getStatusRekam() { return statusRekam; } // getter status rekam
}; // akhir kelas SmartCCTV
