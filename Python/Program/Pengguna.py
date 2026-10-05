# Class (Aggregation): pemilik rumah, daur hidupnya berdiri sendiri
class Pengguna:  # deklarasi kelas pengguna
    @staticmethod  # method statis
    def emailValid(e):  # validasi sederhana email
        at = e.find("@")  # posisi '@'
        if at <= 0:  # tidak ada '@' atau di awal
            return False  # tidak valid
        titik = e.find(".", at)  # posisi '.' setelah '@'
        return titik > at + 1 and titik < len(e) - 1  # titik harus di tengah domain

    def __init__(self, id=0, nama="", akses="", email=""):  # constructor
        self.__idPengguna = id  # private: ID pengguna
        self.__nama = nama  # private: nama pengguna
        self.__tingkatAkses = akses  # private: Admin / Anggota / Tamu
        self.__email = ""  # private: email (diisi lewat setter)
        if email != "":  # jika email diberikan
            self.setEmail(email)  # validasi saat objek dibuat

    def setIdPengguna(self, id): self.__idPengguna = id  # setter ID
    def getIdPengguna(self): return self.__idPengguna  # getter ID

    def setNama(self, nama): self.__nama = nama  # setter nama
    def getNama(self): return self.__nama  # getter nama

    def setTingkatAkses(self, akses): self.__tingkatAkses = akses  # setter tingkat akses
    def getTingkatAkses(self): return self.__tingkatAkses  # getter tingkat akses

    def setEmail(self, email):  # setter email dengan validasi
        if not Pengguna.emailValid(email):  # format salah
            raise ValueError(f"Format email tidak valid: '{email}'")  # trigger error
        self.__email = email  # simpan jika valid
    def getEmail(self): return self.__email  # getter email
