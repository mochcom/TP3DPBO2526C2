# Base Class 2: konfigurasi jaringan & konektivitas IoT
class KoneksiJaringan:  # deklarasi kelas dasar kedua
    @staticmethod  # method statis: dipanggil tanpa objek
    def ipValid(ip):  # validasi format IPv4
        bagian = ip.split(".")  # pecah berdasarkan titik
        if len(bagian) != 4:  # harus tepat 4 segmen
            return False  # tidak valid
        for b in bagian:  # cek tiap segmen
            if not b.isdigit() or not b.isascii() or int(b) > 255:  # harus digit ASCII & <= 255
                return False  # tidak valid
        return True  # lolos semua pengecekan

    @staticmethod  # method statis
    def macValid(mac):  # validasi format MAC XX:XX:XX:XX:XX:XX
        if len(mac) != 17:  # panjang harus 17
            return False  # tidak valid
        for i, c in enumerate(mac):  # cek tiap karakter beserta posisinya
            if i % 3 == 2:  # posisi 2,5,8,11,14 harus ':'
                if c != ":":  # bukan ':'
                    return False  # tidak valid
            elif c not in "0123456789abcdefABCDEF":  # selain itu harus digit heksa
                return False  # tidak valid
        return True  # valid

    def __init__(self, ip="", mac="", protokol=""):  # constructor
        self._ipAddress = ""  # protected: IP (diisi lewat setter)
        self._macAddress = ""  # protected: MAC (diisi lewat setter)
        self._protokol = protokol  # protected: protokol komunikasi
        if ip != "":  # jika IP diberikan
            self.setIpAddress(ip)  # isi lewat setter (divalidasi)
        if mac != "":  # jika MAC diberikan
            self.setMacAddress(mac)  # isi lewat setter (divalidasi)

    def setIpAddress(self, ip):  # setter IP dengan validasi
        if not KoneksiJaringan.ipValid(ip):  # format salah
            raise ValueError(f"Format IP Address tidak valid: '{ip}'")  # trigger error
        self._ipAddress = ip  # simpan jika valid
    def getIpAddress(self): return self._ipAddress  # getter IP

    def setMacAddress(self, mac):  # setter MAC dengan validasi
        if not KoneksiJaringan.macValid(mac):  # format salah
            raise ValueError(f"Format MAC Address tidak valid: '{mac}'")  # trigger error
        self._macAddress = mac  # simpan jika valid
    def getMacAddress(self): return self._macAddress  # getter MAC

    def setProtokol(self, protokol): self._protokol = protokol  # setter protokol
    def getProtokol(self): return self._protokol  # getter protokol
