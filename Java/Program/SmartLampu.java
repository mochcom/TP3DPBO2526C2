// Derived Subclass 1 (Hierarchical Inheritance dari SmartDevice)
public class SmartLampu extends SmartDevice { // SmartLampu "adalah" SmartDevice
    private int tingkatKecerahan; // 0 - 100 (%)
    private String warnaRGB; // format hex #RRGGBB

    // Validasi warna: '#' diikuti 6 digit heksadesimal
    public static boolean warnaValid(String w) { // method statis validasi warna
        if (w.length() != 7 || w.charAt(0) != '#') return false; // panjang 7 & diawali '#'
        for (int i = 1; i < w.length(); i++) // cek 6 karakter sisanya
            if (KoneksiJaringan.HEKSA.indexOf(w.charAt(i)) < 0) return false; // harus digit heksa
        return true; // valid
    }

    public SmartLampu() { // constructor default
        super(); // panggil constructor SmartDevice
        this.tingkatKecerahan = 0; // kecerahan awal 0
        this.warnaRGB = "#FFFFFF"; // warna awal putih
    }

    public SmartLampu(int id, String nama, boolean power, String ip, String mac, String protokol,
                      String lokasi, String firmware, SensorInternal sensor, Pengguna pemilik,
                      int kecerahan, String warna) { // constructor lengkap
        super(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik); // panggil induk
        this.tingkatKecerahan = 0; // nilai awal sebelum divalidasi
        this.warnaRGB = "#FFFFFF"; // nilai awal sebelum divalidasi
        setTingkatKecerahan(kecerahan); // isi lewat setter (divalidasi)
        setWarnaRGB(warna); // isi lewat setter (divalidasi)
    }

    // Overriding: tampilkan data lampu
    @Override
    public void displayInfo() { // menimpa method induk
        System.out.println("[SmartLampu] " + namaPerangkat); // judul
        super.displayInfo(); // cetak data umum dari induk
        cetak("Kecerahan", tingkatKecerahan + "%"); // kecerahan
        cetak("Warna RGB", warnaRGB); // warna
    }

    public void setTingkatKecerahan(int level) { // setter dengan validasi rentang
        if (level < 0 || level > 100) // di luar 0-100
            throw new IllegalArgumentException("Tingkat kecerahan harus 0-100, bukan " + level); // trigger error
        this.tingkatKecerahan = level; // simpan jika valid
    }
    public int getTingkatKecerahan() { return tingkatKecerahan; } // getter kecerahan

    public void setWarnaRGB(String warna) { // setter dengan validasi format
        if (!warnaValid(warna)) // format salah
            throw new IllegalArgumentException("Warna harus berformat #RRGGBB, bukan '" + warna + "'"); // trigger error
        this.warnaRGB = warna; // simpan jika valid
    }
    public String getWarnaRGB() { return warnaRGB; } // getter warna
}
