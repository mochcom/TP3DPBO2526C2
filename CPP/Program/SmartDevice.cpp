#pragma once // cegah include berulang
#include <iostream> // library input/output
#include <iomanip> // setw, setprecision
#include <string> // library string
#include "PerangkatSistem.cpp" // base class 1
#include "KoneksiJaringan.cpp" // base class 2
#include "SensorInternal.cpp" // kelas sensor (composition)
#include "Pengguna.cpp" // kelas pengguna (aggregation)
using namespace std; // pakai namespace std

// Main Class: Multiple Inheritance (PerangkatSistem + KoneksiJaringan)
// - sensor : Composition (objek langsung, ikut hancur bersama SmartDevice)
// - pemilik: Aggregation (pointer, objek Pengguna hidup mandiri)
class SmartDevice : public PerangkatSistem, public KoneksiJaringan { // mewarisi dua kelas sekaligus
protected: // bisa diakses kelas turunan (Lampu, CCTV, AC)
    string lokasiRuangan; // lokasi pemasangan
    string versiFirmware; // versi firmware
    SensorInternal sensor; // Composition: objek langsung
    Pengguna* pemilik; // Aggregation: pointer ke pengguna

public: // bagian publik
    SmartDevice() : lokasiRuangan(""), versiFirmware(""), pemilik(nullptr) {} // constructor default

    SmartDevice(int id, string nama, bool power, string ip, string mac, string protokol, // constructor lengkap
                string lokasi, string firmware, SensorInternal sensor, Pengguna* pemilik)
        : PerangkatSistem(id, nama, power), // panggil constructor induk 1
          KoneksiJaringan(ip, mac, protokol), // panggil constructor induk 2
          lokasiRuangan(lokasi), versiFirmware(firmware), // isi atribut sendiri
          sensor(sensor), pemilik(pemilik) {} // salin sensor & simpan pointer pemilik

    virtual ~SmartDevice() { // destruktor virtual (aman dihapus lewat pointer induk)
        cout << "   [Destruktor] SmartDevice \"" << namaPerangkat << "\" dihancurkan" // info perangkat dihapus
             << " -> SensorInternal #" << sensor.getIdSensor() << " (" << sensor.getTipeSensor() // info sensor
             << ") ikut hancur (Composition)" << endl; // sensor ikut hancur
    }

    // Menampilkan data gabungan: PerangkatSistem + KoneksiJaringan + atribut sendiri
    virtual void displayInfo() { // virtual agar bisa di-override anak
        cout << left; // rata kiri
        cout << "  " << setw(15) << "ID Perangkat" << ": " << idPerangkat << endl; // cetak ID
        cout << "  " << setw(15) << "Nama" << ": " << namaPerangkat << endl; // cetak nama
        cout << "  " << setw(15) << "Status Power" << ": " << (statusPower ? "ON" : "OFF") << endl; // cetak power
        cout << "  " << setw(15) << "IP Address" << ": " << ipAddress << endl; // cetak IP
        cout << "  " << setw(15) << "MAC Address" << ": " << macAddress << endl; // cetak MAC
        cout << "  " << setw(15) << "Protokol" << ": " << protokol << endl; // cetak protokol
        cout << "  " << setw(15) << "Lokasi" << ": " << lokasiRuangan << endl; // cetak lokasi
        cout << "  " << setw(15) << "Firmware" << ": " << versiFirmware << endl; // cetak firmware
        cout << "  " << setw(15) << "Sensor" << ": #" << sensor.getIdSensor() << " " // cetak ID sensor
             << sensor.getTipeSensor() << " = " << fixed << setprecision(1) // tipe & format 1 desimal
             << sensor.getNilaiBacaan() << endl; // nilai bacaan
        cout << "  " << setw(15) << "Pemilik" << ": "; // label pemilik
        if (pemilik != nullptr) // jika pemilik ada
            cout << pemilik->getNama() << " (" << pemilik->getTingkatAkses() << ")" << endl; // nama & akses
        else // jika belum ada pemilik
            cout << "-" << endl; // tampilkan strip
    }

    void setLokasiRuangan(string lokasi) { lokasiRuangan = lokasi; } // setter lokasi
    string getLokasiRuangan() { return lokasiRuangan; } // getter lokasi

    void setVersiFirmware(string firmware) { versiFirmware = firmware; } // setter firmware
    string getVersiFirmware() { return versiFirmware; } // getter firmware

    void setSensor(SensorInternal s) { sensor = s; } // setter sensor
    SensorInternal& getSensor() { return sensor; } // getter: kembalikan referensi agar bisa diubah

    void setPemilik(Pengguna* p) { pemilik = p; } // setter pemilik (pointer)
    Pengguna* getPemilik() { return pemilik; } // getter pemilik
}; // akhir kelas SmartDevice
