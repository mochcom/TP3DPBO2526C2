#pragma once // cegah file ini di-include berulang kali
#include <iostream> // library input/output
#include <string> // library tipe string
using namespace std; // pakai namespace std agar tidak perlu menulis std::

// Base Class 1: identitas fisik & status daya perangkat
class PerangkatSistem { // deklarasi kelas dasar pertama
protected: // atribut dapat diakses oleh kelas turunan
    int idPerangkat; // ID unik perangkat
    string namaPerangkat; // nama perangkat
    bool statusPower; // true = ON, false = OFF

public: // bagian yang bisa diakses dari luar
    PerangkatSistem() : idPerangkat(0), namaPerangkat(""), statusPower(false) {} // constructor default: nilai awal kosong
    PerangkatSistem(int id, string nama, bool status) // constructor berparameter
        : idPerangkat(id), namaPerangkat(nama), statusPower(status) {} // isi atribut dari parameter

    void togglePower() { statusPower = !statusPower; } // balik status ON <-> OFF

    void setIdPerangkat(int id) { idPerangkat = id; } // setter ID
    int getIdPerangkat() { return idPerangkat; } // getter ID

    void setNamaPerangkat(string nama) { namaPerangkat = nama; } // setter nama
    string getNamaPerangkat() { return namaPerangkat; } // getter nama

    void setStatusPower(bool status) { statusPower = status; } // setter status power
    bool getStatusPower() { return statusPower; } // getter status power
}; // akhir kelas PerangkatSistem
