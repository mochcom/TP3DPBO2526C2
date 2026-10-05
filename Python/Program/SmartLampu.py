from SmartDevice import SmartDevice  # import kelas induk


# Derived Subclass 1 (Hierarchical Inheritance dari SmartDevice)
class SmartLampu(SmartDevice):  # SmartLampu "adalah" SmartDevice
    @staticmethod  # method statis
    def warnaValid(w):  # validasi warna '#' + 6 digit heksa
        if len(w) != 7 or w[0] != "#":  # panjang 7 & diawali '#'
            return False  # tidak valid
        return all(c in "0123456789abcdefABCDEF" for c in w[1:])  # 6 karakter sisanya harus heksa

    def __init__(self, id=0, nama="", power=False, ip="", mac="", protokol="",
                 lokasi="", firmware="", sensor=None, pemilik=None,
                 kecerahan=0, warna="#FFFFFF"):  # constructor lengkap
        super().__init__(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik)  # panggil induk
        self.__tingkatKecerahan = 0  # private: nilai awal sebelum divalidasi
        self.__warnaRGB = "#FFFFFF"  # private: nilai awal sebelum divalidasi
        self.setTingkatKecerahan(kecerahan)  # isi lewat setter (divalidasi)
        self.setWarnaRGB(warna)  # isi lewat setter (divalidasi)

    # Overriding: tampilkan data lampu
    def displayInfo(self):  # menimpa method induk
        print(f"[SmartLampu] {self._namaPerangkat}")  # judul
        super().displayInfo()  # cetak data umum dari induk
        print(f"  {'Kecerahan':<15}: {self.__tingkatKecerahan}%")  # kecerahan
        print(f"  {'Warna RGB':<15}: {self.__warnaRGB}")  # warna

    def setTingkatKecerahan(self, level):  # setter dengan validasi rentang
        if level < 0 or level > 100:  # di luar 0-100
            raise ValueError(f"Tingkat kecerahan harus 0-100, bukan {level}")  # trigger error
        self.__tingkatKecerahan = level  # simpan jika valid
    def getTingkatKecerahan(self): return self.__tingkatKecerahan  # getter kecerahan

    def setWarnaRGB(self, warna):  # setter dengan validasi format
        if not SmartLampu.warnaValid(warna):  # format salah
            raise ValueError(f"Warna harus berformat #RRGGBB, bukan '{warna}'")  # trigger error
        self.__warnaRGB = warna  # simpan jika valid
    def getWarnaRGB(self): return self.__warnaRGB  # getter warna
