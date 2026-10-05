from SmartDevice import SmartDevice  # import kelas induk


# Derived Subclass 2 (Hierarchical Inheritance dari SmartDevice)
class SmartCCTV(SmartDevice):  # SmartCCTV "adalah" SmartDevice
    @staticmethod  # method statis
    def resolusiValid(r):  # validasi resolusi yang didukung
        return r in ("720p", "1080p", "2K", "4K")  # daftar resolusi sah

    def __init__(self, id=0, nama="", power=False, ip="", mac="", protokol="",
                 lokasi="", firmware="", sensor=None, pemilik=None,
                 resolusi="720p", modeMalam=False, statusRekam=False):  # constructor lengkap
        super().__init__(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik)  # panggil induk
        self.__resolusi = "720p"  # private: nilai awal sebelum divalidasi
        self.__modeMalam = modeMalam  # private: mode penglihatan malam
        self.__statusRekam = statusRekam  # private: True = sedang merekam
        self.setResolusi(resolusi)  # isi lewat setter (divalidasi)

    # Overriding: tampilkan data CCTV
    def displayInfo(self):  # menimpa method induk
        print(f"[SmartCCTV] {self._namaPerangkat}")  # judul
        super().displayInfo()  # cetak data umum dari induk
        print(f"  {'Resolusi':<15}: {self.__resolusi}")  # resolusi
        print(f"  {'Mode Malam':<15}: {'Aktif' if self.__modeMalam else 'Nonaktif'}")  # mode malam
        print(f"  {'Status Rekam':<15}: {'Merekam' if self.__statusRekam else 'Berhenti'}")  # status rekam

    def setResolusi(self, resolusi):  # setter dengan validasi
        if not SmartCCTV.resolusiValid(resolusi):  # resolusi tidak didukung
            raise ValueError(f"Resolusi '{resolusi}' tidak didukung (720p/1080p/2K/4K)")  # trigger error
        self.__resolusi = resolusi  # simpan jika valid
    def getResolusi(self): return self.__resolusi  # getter resolusi

    def setModeMalam(self, status): self.__modeMalam = status  # setter mode malam
    def getModeMalam(self): return self.__modeMalam  # getter mode malam

    def setStatusRekam(self, rekam): self.__statusRekam = rekam  # setter status rekam
    def getStatusRekam(self): return self.__statusRekam  # getter status rekam
