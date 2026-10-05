#pragma once // cegah include berulang
#include <iostream> // library input/output
#include <string> // library string
using namespace std; // pakai namespace std

// Member Class (Composition): sensor tertanam di dalam SmartDevice
class SensorInternal { // deklarasi kelas sensor
private: // atribut hanya bisa diakses di dalam kelas ini
    int idSensor; // ID sensor
    string tipeSensor; // jenis sensor (LDR, PIR, dll)
    double nilaiBacaan; // hasil pembacaan sensor

public: // bagian publik
    SensorInternal() : idSensor(0), tipeSensor(""), nilaiBacaan(0.0) {} // constructor default
    SensorInternal(int id, string tipe, double nilai) // constructor berparameter
        : idSensor(id), tipeSensor(tipe), nilaiBacaan(nilai) {} // isi atribut

    void setIdSensor(int id) { idSensor = id; } // setter ID sensor
    int getIdSensor() { return idSensor; } // getter ID sensor

    void setTipeSensor(string tipe) { tipeSensor = tipe; } // setter tipe sensor
    string getTipeSensor() { return tipeSensor; } // getter tipe sensor

    void setNilaiBacaan(double nilai) { nilaiBacaan = nilai; } // setter nilai bacaan
    double getNilaiBacaan() { return nilaiBacaan; } // getter nilai bacaan
}; // akhir kelas SensorInternal
