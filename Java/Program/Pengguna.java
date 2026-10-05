// Class (Aggregation): pemilik rumah, daur hidupnya berdiri sendiri
public class Pengguna { // deklarasi kelas pengguna
    private int idPengguna; // ID pengguna
    private String nama; // nama pengguna
    private String tingkatAkses; // Admin / Anggota / Tamu
    private String email; // alamat email

    // Validasi sederhana email: ada '@' dan ada '.' setelahnya
    public static boolean emailValid(String e) { // method statis validasi email
        int at = e.indexOf('@'); // posisi '@'
        if (at <= 0) return false; // tidak ada '@' atau di awal
        int titik = e.indexOf('.', at); // posisi '.' setelah '@'
        return titik > at + 1 && titik < e.length() - 1; // titik harus di tengah domain
    }

    public Pengguna() { // constructor default
        this.idPengguna = 0; // ID awal 0
        this.nama = ""; // nama kosong
        this.tingkatAkses = ""; // akses kosong
        this.email = ""; // email kosong
    }

    public Pengguna(int id, String nama, String akses, String email) { // constructor berparameter
        this.idPengguna = id; // isi ID
        this.nama = nama; // isi nama
        this.tingkatAkses = akses; // isi akses
        this.email = ""; // email diisi lewat setter
        setEmail(email); // validasi email saat objek dibuat
    }

    public void setIdPengguna(int id) { this.idPengguna = id; } // setter ID
    public int getIdPengguna() { return idPengguna; } // getter ID

    public void setNama(String nama) { this.nama = nama; } // setter nama
    public String getNama() { return nama; } // getter nama

    public void setTingkatAkses(String akses) { this.tingkatAkses = akses; } // setter tingkat akses
    public String getTingkatAkses() { return tingkatAkses; } // getter tingkat akses

    public void setEmail(String email) { // setter email dengan validasi
        if (!emailValid(email)) // format salah
            throw new IllegalArgumentException("Format email tidak valid: '" + email + "'"); // trigger error
        this.email = email; // simpan jika valid
    }
    public String getEmail() { return email; } // getter email
}
