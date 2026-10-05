// Derived Subclass 3 (Hierarchical Inheritance dari SmartDevice)
public class SmartAC extends SmartDevice { // SmartAC "adalah" SmartDevice
    private int suhuTarget; // 16 - 30 (derajat C)
    private String modePendingin; // Cool, Dry, Fan, Eco

    // Validasi mode: hanya 4 mode yang tersedia
    public static boolean modeValid(String m) { // method statis validasi mode
        return m.equals("Cool") || m.equals("Dry") || m.equals("Fan") || m.equals("Eco"); // daftar mode sah
    }

    public SmartAC() { // constructor default
        super(); // panggil constructor SmartDevice
        this.suhuTarget = 25; // suhu awal 25
        this.modePendingin = "Cool"; // mode awal Cool
    }

    public SmartAC(int id, String nama, boolean power, String ip, String mac, String protokol,
                   String lokasi, String firmware, SensorInternal sensor, Pengguna pemilik,
                   int suhu, String mode) { // constructor lengkap
        super(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik); // panggil induk
        this.suhuTarget = 25; // nilai awal sebelum divalidasi
        this.modePendingin = "Cool"; // nilai awal sebelum divalidasi
        setSuhuTarget(suhu); // isi suhu lewat setter (divalidasi)
        setModePendingin(mode); // isi mode lewat setter (divalidasi)
    }

    // Overriding: tampilkan data AC
    @Override
    public void displayInfo() { // menimpa method induk
        System.out.println("[SmartAC] " + namaPerangkat); // judul
        super.displayInfo(); // cetak data umum dari induk
        cetak("Suhu Target", suhuTarget + " C"); // suhu target
        cetak("Mode Pendingin", modePendingin); // mode pendingin
    }

    public void setSuhuTarget(int suhu) { // setter dengan validasi rentang
        if (suhu < 16 || suhu > 30) // di luar 16-30
            throw new IllegalArgumentException("Suhu target harus 16-30 C, bukan " + suhu); // trigger error
        this.suhuTarget = suhu; // simpan jika valid
    }
    public int getSuhuTarget() { return suhuTarget; } // getter suhu target

    public void setModePendingin(String mode) { // setter dengan validasi pilihan
        if (!modeValid(mode)) // mode tidak dikenal
            throw new IllegalArgumentException("Mode '" + mode + "' tidak dikenal (Cool/Dry/Fan/Eco)"); // trigger error
        this.modePendingin = mode; // simpan jika valid
    }
    public String getModePendingin() { return modePendingin; } // getter mode
}
