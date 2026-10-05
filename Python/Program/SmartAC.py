from SmartDevice import SmartDevice  # import kelas induk


# Derived Subclass 3 (Hierarchical Inheritance dari SmartDevice)
class SmartAC(SmartDevice):  # SmartAC "adalah" SmartDevice
    @staticmethod  # method statis
    def modeValid(m):  # validasi mode pendingin
        return m in ("Cool", "Dry", "Fan", "Eco")  # daftar mode sah

    def __init__(self, id=0, nama="", power=False, ip="", mac="", protokol="",
                 lokasi="", firmware="", sensor=None, pemilik=None,
                 suhu=25, mode="Cool"):  # constructor lengkap
        super().__init__(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik)  # panggil induk
        self.__suhuTarget = 25  # private: nilai awal sebelum divalidasi
        self.__modePendingin = "Cool"  # private: nilai awal sebelum divalidasi
        self.setSuhuTarget(suhu)  # isi lewat setter (divalidasi)
        self.setModePendingin(mode)  # isi lewat setter (divalidasi)

    # Overriding: tampilkan data AC
    def displayInfo(self):  # menimpa method induk
        print(f"[SmartAC] {self._namaPerangkat}")  # judul
        super().displayInfo()  # cetak data umum dari induk
        print(f"  {'Suhu Target':<15}: {self.__suhuTarget} C")  # suhu target
        print(f"  {'Mode Pendingin':<15}: {self.__modePendingin}")  # mode pendingin

    def setSuhuTarget(self, suhu):  # setter dengan validasi rentang
        if suhu < 16 or suhu > 30:  # di luar 16-30
            raise ValueError(f"Suhu target harus 16-30 C, bukan {suhu}")  # trigger error
        self.__suhuTarget = suhu  # simpan jika valid
    def getSuhuTarget(self): return self.__suhuTarget  # getter suhu target

    def setModePendingin(self, mode):  # setter dengan validasi pilihan
        if not SmartAC.modeValid(mode):  # mode tidak dikenal
            raise ValueError(f"Mode '{mode}' tidak dikenal (Cool/Dry/Fan/Eco)")  # trigger error
        self.__modePendingin = mode  # simpan jika valid
    def getModePendingin(self): return self.__modePendingin  # getter mode
