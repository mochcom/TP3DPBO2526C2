#pragma once // cegah include berulang
#include "SmartDevice.cpp" // kelas induk

// Derived Subclass 3 (Hierarchical Inheritance dari SmartDevice)
class SmartAC : public SmartDevice { // SmartAC "adalah" SmartDevice
private: // atribut khusus AC
    int suhuTarget; // 16 - 30 (derajat C)
    string modePendingin; // Cool, Dry, Fan, Eco

public: // bagian publik
    // Validasi mode: hanya 4 mode yang tersedia
    static bool modeValid(const string& m) { // fungsi statis validasi mode
        return m == "Cool" || m == "Dry" || m == "Fan" || m == "Eco"; // daftar mode sah
    }

    SmartAC() : SmartDevice(), suhuTarget(25), modePendingin("Cool") {} // constructor default

    SmartAC(int id, string nama, bool power, string ip, string mac, string protokol, // constructor lengkap
            string lokasi, string firmware, SensorInternal sensor, Pengguna* pemilik,
            int suhu, string mode)
        : SmartDevice(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik), // panggil induk
          suhuTarget(25), modePendingin("Cool") { // nilai awal sebelum divalidasi
        setSuhuTarget(suhu); // isi suhu lewat setter (divalidasi)
        setModePendingin(mode); // isi mode lewat setter (divalidasi)
    }

    // Overriding: tampilkan data AC
    void displayInfo() override { // menimpa method induk
        cout << "[SmartAC] " << namaPerangkat << endl; // judul
        SmartDevice::displayInfo(); // cetak data umum dari induk
        cout << "  " << setw(15) << "Suhu Target" << ": " << suhuTarget << " C" << endl; // suhu target
        cout << "  " << setw(15) << "Mode Pendingin" << ": " << modePendingin << endl; // mode pendingin
    }

    void setSuhuTarget(int suhu) { // setter dengan validasi rentang
        if (suhu < 16 || suhu > 30) // di luar 16-30
            throw invalid_argument("Suhu target harus 16-30 C, bukan " + to_string(suhu)); // trigger error
        suhuTarget = suhu; // simpan jika valid
    }
    int getSuhuTarget() { return suhuTarget; } // getter suhu target

    void setModePendingin(string mode) { // setter dengan validasi pilihan
        if (!modeValid(mode)) // mode tidak dikenal
            throw invalid_argument("Mode '" + mode + "' tidak dikenal (Cool/Dry/Fan/Eco)"); // trigger error
        modePendingin = mode; // simpan jika valid
    }
    string getModePendingin() { return modePendingin; } // getter mode
}; // akhir kelas SmartAC
