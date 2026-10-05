from PerangkatSistem import PerangkatSistem  # import base class 1
from KoneksiJaringan import KoneksiJaringan  # import base class 2
from SensorInternal import SensorInternal  # import kelas sensor (composition)


# Main Class: Multiple Inheritance (PerangkatSistem + KoneksiJaringan)
# - sensor : Composition (dibuat/disalin & dimiliki oleh SmartDevice)
# - pemilik: Aggregation (hanya referensi ke objek Pengguna yang hidup mandiri)
class SmartDevice(PerangkatSistem, KoneksiJaringan):  # mewarisi dua kelas sekaligus
    def __init__(self, id=0, nama="", power=False, ip="", mac="", protokol="",
                 lokasi="", firmware="", sensor=None, pemilik=None):  # constructor lengkap
        PerangkatSistem.__init__(self, id, nama, power)  # panggil constructor induk 1
        KoneksiJaringan.__init__(self, ip, mac, protokol)  # panggil constructor induk 2
        self._lokasiRuangan = lokasi  # protected: lokasi pemasangan
        self._versiFirmware = firmware  # protected: versi firmware
        if sensor is None:  # jika sensor tidak diberikan
            self._sensor = SensorInternal()  # buat sensor kosong milik perangkat
        else:  # jika sensor diberikan
            self._sensor = SensorInternal(sensor.getIdSensor(), sensor.getTipeSensor(),
                                          sensor.getNilaiBacaan())  # Composition: salin sensor jadi milik sendiri
        self._pemilik = pemilik  # Aggregation: hanya referensi ke Pengguna

    def displayInfo(self):  # tampilkan data gabungan perangkat
        print(f"  {'ID Perangkat':<15}: {self._idPerangkat}")  # cetak ID
        print(f"  {'Nama':<15}: {self._namaPerangkat}")  # cetak nama
        print(f"  {'Status Power':<15}: {'ON' if self._statusPower else 'OFF'}")  # cetak power
        print(f"  {'IP Address':<15}: {self._ipAddress}")  # cetak IP
        print(f"  {'MAC Address':<15}: {self._macAddress}")  # cetak MAC
        print(f"  {'Protokol':<15}: {self._protokol}")  # cetak protokol
        print(f"  {'Lokasi':<15}: {self._lokasiRuangan}")  # cetak lokasi
        print(f"  {'Firmware':<15}: {self._versiFirmware}")  # cetak firmware
        print(f"  {'Sensor':<15}: #{self._sensor.getIdSensor()} "
              f"{self._sensor.getTipeSensor()} = {self._sensor.getNilaiBacaan():.1f}")  # cetak sensor (1 desimal)
        if self._pemilik is not None:  # jika pemilik ada
            print(f"  {'Pemilik':<15}: {self._pemilik.getNama()} ({self._pemilik.getTingkatAkses()})")  # nama & akses
        else:  # jika belum ada pemilik
            print(f"  {'Pemilik':<15}: -")  # tampilkan strip

    def setLokasiRuangan(self, lokasi): self._lokasiRuangan = lokasi  # setter lokasi
    def getLokasiRuangan(self): return self._lokasiRuangan  # getter lokasi

    def setVersiFirmware(self, firmware): self._versiFirmware = firmware  # setter firmware
    def getVersiFirmware(self): return self._versiFirmware  # getter firmware

    def setSensor(self, sensor): self._sensor = sensor  # setter sensor
    def getSensor(self): return self._sensor  # getter sensor (referensi, bisa diubah)

    def setPemilik(self, pemilik): self._pemilik = pemilik  # setter pemilik
    def getPemilik(self): return self._pemilik  # getter pemilik
