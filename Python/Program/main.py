import re  # regex untuk validasi angka
from KoneksiJaringan import KoneksiJaringan  # untuk validasi IP & MAC
from Pengguna import Pengguna  # kelas pengguna (aggregation)
from SensorInternal import SensorInternal  # kelas sensor (composition)
from SmartLampu import SmartLampu  # kelas SmartLampu
from SmartCCTV import SmartCCTV  # kelas SmartCCTV
from SmartAC import SmartAC  # kelas SmartAC


# Exception khusus: dilempar saat input habis (EOF / Ctrl+D / Ctrl+Z)
class InputSelesai(Exception):  # turunan Exception
    pass  # tidak butuh isi tambahan


# ===================== DATA GLOBAL =====================
daftar_perangkat = []  # array of object: semua perangkat
daftar_pengguna = []  # array of object: semua pengguna
next_sensor_id = 7  # penghitung ID sensor otomatis (1-6 dipakai data hardcode)

PROTOKOL = ["WiFi", "Zigbee", "Ethernet", "Bluetooth"]  # pilihan protokol
SENSOR = ["Cahaya (LDR)", "Gerak (PIR)", "Suhu (NTC)", "Suhu (DHT22)"]  # pilihan sensor
RESOLUSI = ["720p", "1080p", "2K", "4K"]  # pilihan resolusi CCTV
MODE_AC = ["Cool", "Dry", "Fan", "Eco"]  # pilihan mode AC
AKSES = ["Admin", "Anggota", "Tamu"]  # pilihan tingkat akses


# ===================== HELPER INPUT & ERROR HANDLING =====================
def baca_baris(prompt):  # baca satu baris input
    try:  # tangkap EOF
        return input(prompt).strip()  # baca & buang spasi tepi
    except EOFError:  # input habis
        raise InputSelesai()  # lempar exception khusus


def baca_int(prompt):  # baca bilangan bulat (error handling huruf/simbol)
    while True:  # ulangi sampai valid
        s = baca_baris(prompt)  # baca input
        if not re.fullmatch(r"[+-]?[0-9]+", s):  # bukan angka (huruf/simbol/kosong)
            print("  [ERROR] Input harus ANGKA bulat (bukan huruf/simbol/kosong). Coba lagi.")  # pesan error
            continue  # minta ulang
        v = int(s)  # ubah string ke int
        if abs(v) > 2147483647:  # batas int 32-bit (setara C++/Java)
            print("  [ERROR] Angka terlalu besar. Coba lagi.")  # pesan error
            continue  # minta ulang
        return v  # kembalikan angka valid


def baca_int_rentang(prompt, min_v, max_v):  # baca angka dalam rentang
    while True:  # ulangi sampai valid
        v = baca_int(prompt)  # baca angka (sudah dipastikan angka)
        if v < min_v or v > max_v:  # di luar rentang
            print(f"  [ERROR] Angka harus di antara {min_v} dan {max_v}. Coba lagi.")  # pesan error
            continue  # minta ulang
        return v  # kembalikan jika valid


def baca_double(prompt):  # baca bilangan desimal
    while True:  # ulangi sampai valid
        s = baca_baris(prompt)  # baca input
        if not re.fullmatch(r"[+-]?[0-9]+([.,][0-9]+)?", s):  # bukan angka
            print("  [ERROR] Input harus ANGKA (contoh 28.5). Coba lagi.")  # pesan error
            continue  # minta ulang
        return float(s.replace(",", "."))  # koma -> titik lalu ubah ke float


def baca_teks(prompt):  # baca teks tidak kosong
    while True:  # ulangi sampai valid
        s = baca_baris(prompt)  # baca input
        if s == "":  # kosong
            print("  [ERROR] Input tidak boleh kosong. Coba lagi.")  # pesan error
            continue  # minta ulang
        return s  # kembalikan teks


def baca_ip(prompt):  # baca IP dengan validasi format
    while True:  # ulangi sampai valid
        s = baca_baris(prompt)  # baca input
        if KoneksiJaringan.ipValid(s): return s  # valid -> kembalikan
        print("  [ERROR] Format IP salah. Contoh: 192.168.1.50 (4 angka 0-255).")  # pesan error


def baca_mac(prompt):  # baca MAC dengan validasi format
    while True:  # ulangi sampai valid
        s = baca_baris(prompt)  # baca input
        if KoneksiJaringan.macValid(s): return s  # valid -> kembalikan
        print("  [ERROR] Format MAC salah. Contoh: AA:BB:CC:00:11:22.")  # pesan error


def baca_warna(prompt):  # baca warna hex dengan validasi
    while True:  # ulangi sampai valid
        s = baca_baris(prompt)  # baca input
        if SmartLampu.warnaValid(s): return s  # valid -> kembalikan
        print("  [ERROR] Format warna salah. Contoh: #FFD27F.")  # pesan error


def baca_email(prompt):  # baca email dengan validasi
    while True:  # ulangi sampai valid
        s = baca_baris(prompt)  # baca input
        if Pengguna.emailValid(s): return s  # valid -> kembalikan
        print("  [ERROR] Format email salah. Contoh: nama@domain.com.")  # pesan error


def pilih_opsi(judul, opsi):  # tampilkan pilihan bernomor
    print(f"{judul}:")  # judul pilihan
    for i, o in enumerate(opsi):  # loop tiap opsi
        print(f"  {i + 1}. {o}")  # cetak nomor & opsi
    return baca_int_rentang("Pilih nomor: ", 1, len(opsi)) - 1  # kembalikan indeks 0-based


# ===================== HELPER DATA =====================
def garis(judul):  # cetak judul bagian
    print("\n==================================================")  # garis atas
    print(f" {judul}")  # judul
    print("==================================================")  # garis bawah


def cari_index_perangkat(id):  # cari perangkat berdasarkan ID
    for i, p in enumerate(daftar_perangkat):  # loop semua perangkat
        if p.getIdPerangkat() == id: return i  # ketemu -> kembalikan indeks
    return -1  # tidak ketemu


def cari_index_pengguna(id):  # cari pengguna berdasarkan ID
    for i, p in enumerate(daftar_pengguna):  # loop semua pengguna
        if p.getIdPengguna() == id: return i  # ketemu -> kembalikan indeks
    return -1  # tidak ketemu


def tampilkan_semua():  # cetak seluruh perangkat
    if len(daftar_perangkat) == 0:  # list kosong
        print("(Belum ada perangkat yang terdaftar)")  # pesan kosong
    else:  # ada data
        for i, p in enumerate(daftar_perangkat):  # loop tiap perangkat
            print(f"\nPerangkat ke-{i + 1}")  # nomor urut
            p.displayInfo()  # polimorfisme: method sesuai jenis objek
    print(f"\nTotal perangkat: {len(daftar_perangkat)}")  # jumlah total


def tampilkan_pengguna():  # cetak seluruh pengguna
    print("Daftar pengguna:")  # judul
    for i, p in enumerate(daftar_pengguna):  # loop tiap pengguna
        print(f"  {i + 1}. [ID {p.getIdPengguna()}] {p.getNama()} "
              f"({p.getTingkatAkses()}) - {p.getEmail()}")  # nomor, ID, nama, akses, email
    print(f"Total pengguna: {len(daftar_pengguna)}")  # jumlah total


def pilih_pengguna():  # pilih pemilik dari daftar
    tampilkan_pengguna()  # tampilkan daftar bernomor
    no = baca_int_rentang("Pilih nomor pemilik: ", 1, len(daftar_pengguna))  # minta nomor valid
    return daftar_pengguna[no - 1]  # kembalikan pengguna terpilih


def pilih_perangkat():  # pilih perangkat dari daftar, 0 = batal
    if len(daftar_perangkat) == 0:  # list kosong
        raise ValueError("Belum ada perangkat.")  # trigger error
    for i, p in enumerate(daftar_perangkat):  # tampilkan ringkasan
        print(f"  {i + 1}. [{p.getIdPerangkat()}] {p.getNamaPerangkat()}")  # nomor, ID, nama
    return baca_int_rentang("Pilih nomor perangkat (0 = batal): ", 0, len(daftar_perangkat))  # 0 = batal


# ===================== DATA DUMMY HARDCODE (TAHAP 1) =====================
def isi_data_dummy():  # isi data awal secara hardcode
    daftar_pengguna.append(Pengguna(1, "Budi Santoso", "Admin", "budi@smarthome.id"))  # pengguna 1
    daftar_pengguna.append(Pengguna(2, "Siti Aminah", "Tamu", "siti@smarthome.id"))  # pengguna 2

    daftar_perangkat.append(SmartLampu(101, "Lampu Ruang Tamu", True,
        "192.168.1.11", "AA:BB:CC:00:00:11", "WiFi", "Ruang Tamu", "v2.1.0",
        SensorInternal(1, "Cahaya (LDR)", 320.5), daftar_pengguna[0], 80, "#FFD27F"))  # lampu #1
    daftar_perangkat.append(SmartCCTV(201, "CCTV Teras", True,
        "192.168.1.21", "AA:BB:CC:00:00:21", "WiFi", "Teras", "v3.4.2",
        SensorInternal(2, "Gerak (PIR)", 1.0), daftar_pengguna[0], "1080p", True, True))  # CCTV #1
    daftar_perangkat.append(SmartAC(301, "AC Ruang Tamu", True,
        "192.168.1.31", "AA:BB:CC:00:00:31", "WiFi", "Ruang Tamu", "v1.8.0",
        SensorInternal(3, "Suhu (NTC)", 28.0), daftar_pengguna[0], 24, "Cool"))  # AC #1


# ===================== TAMBAH DATA =====================
def tambah_statis():  # tambah paket data statis tambahan
    print("\n--- Tambah Perangkat (STATIS) ---")  # judul
    if cari_index_perangkat(102) != -1 or cari_index_perangkat(202) != -1 or cari_index_perangkat(302) != -1:  # cek duplikat ID
        raise ValueError("Data statis sudah pernah ditambahkan (ID 102/202/302 sudah ada).")  # trigger error
    print(f"Sebelum penambahan: {len(daftar_perangkat)} perangkat")  # cetak sebelum
    daftar_perangkat.append(SmartLampu(102, "Lampu Kamar Tidur", False,
        "192.168.1.12", "AA:BB:CC:00:00:12", "Zigbee", "Kamar Tidur", "v2.0.4",
        SensorInternal(4, "Cahaya (LDR)", 45.0), daftar_pengguna[1], 30, "#FF8C42"))  # lampu #2
    daftar_perangkat.append(SmartCCTV(202, "CCTV Garasi", True,
        "192.168.1.22", "AA:BB:CC:00:00:22", "Ethernet", "Garasi", "v3.4.0",
        SensorInternal(5, "Suhu (DHT22)", 29.5), daftar_pengguna[1], "4K", False, False))  # CCTV #2
    daftar_perangkat.append(SmartAC(302, "AC Kamar Tidur", False,
        "192.168.1.32", "AA:BB:CC:00:00:32", "Zigbee", "Kamar Tidur", "v1.7.5",
        SensorInternal(6, "Suhu (NTC)", 26.5), daftar_pengguna[1], 26, "Eco"))  # AC #2
    print("3 perangkat statis berhasil ditambahkan.")  # konfirmasi
    print(f"Sesudah penambahan: {len(daftar_perangkat)} perangkat")  # cetak sesudah
    tampilkan_semua()  # tampilkan semua data


def tambah_dinamis():  # tambah perangkat lewat input user
    global next_sensor_id  # akan mengubah penghitung sensor global
    print("\n--- Tambah Perangkat (DINAMIS) ---")  # judul
    jenis = pilih_opsi("Jenis perangkat", ["SmartLampu", "SmartCCTV", "SmartAC"])  # pilih jenis (0/1/2)
    while True:  # ulangi sampai ID valid & unik
        id = baca_int("ID perangkat (angka > 0): ")  # baca ID (pasti angka)
        if id <= 0:  # tolak ID <= 0
            print("  [ERROR] ID harus lebih dari 0.")  # pesan error
            continue  # minta ulang
        if cari_index_perangkat(id) != -1:  # tolak duplikat
            print(f"  [ERROR] ID {id} sudah dipakai perangkat lain.")  # pesan error
            continue  # minta ulang
        break  # ID valid
    nama = baca_teks("Nama perangkat: ")  # nama perangkat
    power = baca_int_rentang("Status power (1 = ON, 0 = OFF): ", 0, 1) == 1  # status power
    ip = baca_ip("IP Address (contoh 192.168.1.50): ")  # IP tervalidasi
    mac = baca_mac("MAC Address (contoh AA:BB:CC:00:11:22): ")  # MAC tervalidasi
    protokol = PROTOKOL[pilih_opsi("Protokol", PROTOKOL)]  # pilih protokol
    lokasi = baca_teks("Lokasi ruangan: ")  # lokasi
    firmware = baca_teks("Versi firmware (contoh v1.0.0): ")  # firmware
    tipe_sensor = SENSOR[pilih_opsi("Tipe sensor internal", SENSOR)]  # pilih tipe sensor
    nilai = baca_double("Nilai bacaan sensor awal: ")  # nilai sensor
    sensor = SensorInternal(next_sensor_id, tipe_sensor, nilai)  # buat sensor dengan ID otomatis
    next_sensor_id += 1  # naikkan penghitung
    pemilik = pilih_pengguna()  # pilih pemilik (aggregation)

    if jenis == 0:  # jika SmartLampu
        kec = baca_int_rentang("Tingkat kecerahan (0-100): ", 0, 100)  # kecerahan
        warna = baca_warna("Warna RGB hex (contoh #FFD27F): ")  # warna
        baru = SmartLampu(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik, kec, warna)  # buat lampu
    elif jenis == 1:  # jika SmartCCTV
        res = RESOLUSI[pilih_opsi("Resolusi", RESOLUSI)]  # resolusi
        malam = baca_int_rentang("Mode malam (1 = aktif, 0 = nonaktif): ", 0, 1) == 1  # mode malam
        rekam = baca_int_rentang("Status rekam (1 = merekam, 0 = berhenti): ", 0, 1) == 1  # status rekam
        baru = SmartCCTV(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik, res, malam, rekam)  # buat CCTV
    else:  # jika SmartAC
        suhu = baca_int_rentang("Suhu target (16-30): ", 16, 30)  # suhu target
        mode = MODE_AC[pilih_opsi("Mode pendingin", MODE_AC)]  # mode pendingin
        baru = SmartAC(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik, suhu, mode)  # buat AC
    print(f"\nSebelum penambahan: {len(daftar_perangkat)} perangkat")  # cetak sebelum
    daftar_perangkat.append(baru)  # masukkan ke array of object
    print(f'Perangkat "{nama}" berhasil ditambahkan.')  # konfirmasi
    print(f"Sesudah penambahan: {len(daftar_perangkat)} perangkat")  # cetak sesudah
    tampilkan_semua()  # tampilkan semua data


def tambah_pengguna():  # tambah pengguna lewat input user
    print("\n--- Tambah Pengguna (DINAMIS) ---")  # judul
    while True:  # ulangi sampai ID valid & unik
        id = baca_int("ID pengguna (angka > 0): ")  # baca ID
        if id <= 0:  # tolak ID <= 0
            print("  [ERROR] ID harus lebih dari 0.")  # pesan error
            continue  # minta ulang
        if cari_index_pengguna(id) != -1:  # tolak duplikat
            print(f"  [ERROR] ID {id} sudah dipakai.")  # pesan error
            continue  # minta ulang
        break  # ID valid
    nama = baca_teks("Nama pengguna: ")  # nama
    akses = AKSES[pilih_opsi("Tingkat akses", AKSES)]  # pilih akses
    email = baca_email("Email: ")  # email tervalidasi
    daftar_pengguna.append(Pengguna(id, nama, akses, email))  # simpan pengguna baru
    print(f'Pengguna "{nama}" berhasil ditambahkan.')  # konfirmasi
    tampilkan_pengguna()  # tampilkan daftar terbaru


# ===================== UBAH & HAPUS =====================
def ubah_perangkat():  # ubah pengaturan perangkat lewat setter
    print("\n--- Ubah Pengaturan Perangkat ---")  # judul
    no = pilih_perangkat()  # pilih perangkat
    if no == 0:  # 0 = batal
        print("Dibatalkan.")  # info batal
        return  # keluar fungsi
    p = daftar_perangkat[no - 1]  # ambil perangkat terpilih
    aksi = pilih_opsi("Pilih yang diubah", ["Toggle power", "Nilai sensor", "Ganti pemilik", "Pengaturan khusus jenis"])  # submenu
    if aksi == 0:  # toggle power
        p.togglePower()  # balik ON/OFF
        print("Power sekarang:", "ON" if p.getStatusPower() else "OFF")  # tampilkan status baru
    elif aksi == 1:  # ubah sensor
        p.getSensor().setNilaiBacaan(baca_double("Nilai sensor baru: "))  # ubah sensor (composition)
    elif aksi == 2:  # ganti pemilik
        p.setPemilik(pilih_pengguna())  # ganti pemilik (aggregation)
    else:  # pengaturan khusus
        if isinstance(p, SmartLampu):  # cek apakah objek ini lampu
            a = pilih_opsi("Pengaturan lampu", ["Kecerahan", "Warna"])  # submenu lampu
            if a == 0: p.setTingkatKecerahan(baca_int("Kecerahan baru (0-100): "))  # setter bisa melempar error
            else: p.setWarnaRGB(baca_teks("Warna baru (#RRGGBB): "))  # setter bisa melempar error
        elif isinstance(p, SmartCCTV):  # cek apakah objek ini CCTV
            a = pilih_opsi("Pengaturan CCTV", ["Toggle mode malam", "Toggle rekam", "Resolusi"])  # submenu CCTV
            if a == 0: p.setModeMalam(not p.getModeMalam())  # balik mode malam
            elif a == 1: p.setStatusRekam(not p.getStatusRekam())  # balik status rekam
            else: p.setResolusi(baca_teks("Resolusi baru (720p/1080p/2K/4K): "))  # setter bisa melempar error
        elif isinstance(p, SmartAC):  # cek apakah objek ini AC
            a = pilih_opsi("Pengaturan AC", ["Suhu target", "Mode pendingin"])  # submenu AC
            if a == 0: p.setSuhuTarget(baca_int("Suhu baru (16-30): "))  # setter bisa melempar error
            else: p.setModePendingin(baca_teks("Mode baru (Cool/Dry/Fan/Eco): "))  # setter bisa melempar error
    print("\nPerubahan berhasil. Data terbaru:")  # konfirmasi
    p.displayInfo()  # tampilkan perangkat setelah diubah


def hapus_perangkat():  # hapus perangkat (demo composition vs aggregation)
    print("\n--- Hapus Perangkat ---")  # judul
    no = pilih_perangkat()  # pilih perangkat
    if no == 0:  # 0 = batal
        print("Dibatalkan.")  # info batal
        return  # keluar fungsi
    if baca_int_rentang("Yakin hapus? (1 = ya, 0 = batal): ", 0, 1) == 0:  # konfirmasi
        print("Dibatalkan.")  # info batal
        return  # keluar fungsi
    target = daftar_perangkat.pop(no - 1)  # keluarkan perangkat dari list
    pemilik = target.getPemilik()  # simpan pemilik sebelum perangkat dibuang
    sensor = target.getSensor()  # ambil info sensor milik perangkat
    print(f'   [Hapus] SmartDevice "{target.getNamaPerangkat()}" dihapus -> SensorInternal '
          f'#{sensor.getIdSensor()} ({sensor.getTipeSensor()}) ikut hilang (Composition)')  # bukti composition
    del target  # lepas referensi (objek & sensornya dibuang oleh garbage collector)
    if pemilik is not None:  # jika punya pemilik
        print(f"Data pemilik masih ada (Aggregation): {pemilik.getNama()} | {pemilik.getEmail()}")  # bukti agregasi
    print(f"Sisa perangkat: {len(daftar_perangkat)}")  # jumlah sisa


def tampil_menu():  # cetak menu utama
    garis("MENU UTAMA SMART HOME")  # judul menu
    print(" 1. Tambah perangkat - STATIS (paket data contoh)")  # menu 1
    print(" 2. Tambah perangkat - DINAMIS (input manual)")  # menu 2
    print(" 3. Tambah pengguna - DINAMIS")  # menu 3
    print(" 4. Tampilkan semua perangkat")  # menu 4
    print(" 5. Tampilkan semua pengguna")  # menu 5
    print(" 6. Ubah pengaturan perangkat")  # menu 6
    print(" 7. Hapus perangkat")  # menu 7
    print(" 0. Keluar")  # menu 0


def main():  # titik masuk program
    print("SISTEM SMART HOME / IoT")  # judul program
    try:  # bungkus program utama agar EOF input ditangani
        garis("TAHAP 1: DATA DUMMY (HARDCODE)")  # judul tahap 1
        isi_data_dummy()  # isi data dummy hardcode
        tampilkan_pengguna()  # tampilkan pengguna dummy
        tampilkan_semua()  # tampilkan perangkat dummy

        garis("TAHAP 2: MENU INTERAKTIF")  # judul tahap 2
        aksi = {1: tambah_statis, 2: tambah_dinamis, 3: tambah_pengguna, 4: tampilkan_semua,
                5: tampilkan_pengguna, 6: ubah_perangkat, 7: hapus_perangkat}  # peta nomor menu -> fungsi
        while True:  # loop menu sampai user keluar
            tampil_menu()  # tampilkan menu
            pilih = baca_int_rentang("Pilih menu (0-7): ", 0, 7)  # baca pilihan (validasi angka & rentang)
            if pilih == 0: break  # 0 = keluar dari loop
            try:  # tangani error pada setiap aksi menu
                aksi[pilih]()  # jalankan aksi sesuai nomor
            except InputSelesai:  # input habis
                raise  # teruskan ke handler luar
            except Exception as e:  # error validasi dari kelas / fungsi
                print(f"\n  [ERROR] {e}")  # tampilkan pesan error
                print("  Aksi dibatalkan, kembali ke menu.")  # info kembali ke menu
    except InputSelesai:  # input berakhir (EOF)
        print("\n[INFO] Input berakhir, program ditutup.")  # info keluar

    garis("PEMBERSIHAN MEMORI")  # judul pembersihan
    daftar_perangkat.clear()  # kosongkan list perangkat
    daftar_pengguna.clear()  # kosongkan list pengguna
    print("\nProgram selesai.")  # pesan akhir


if __name__ == "__main__":  # jalankan hanya jika file ini dieksekusi langsung
    main()  # panggil fungsi utama
