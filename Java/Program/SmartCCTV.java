// Derived Subclass 2 (Hierarchical Inheritance dari SmartDevice)
public class SmartCCTV extends SmartDevice { // SmartCCTV "adalah" SmartDevice
    private String resolusi; // 720p / 1080p / 2K / 4K
    private boolean modeMalam; // mode penglihatan malam
    private boolean statusRekam; // true = sedang merekam

    // Validasi resolusi: hanya nilai yang didukung
    public static boolean resolusiValid(String r) { // method statis validasi resolusi
        return r.equals("720p") || r.equals("1080p") || r.equals("2K") || r.equals("4K"); // daftar resolusi sah
    }

    public SmartCCTV() { // constructor default
        super(); // panggil constructor SmartDevice
        this.resolusi = "720p"; // resolusi awal
        this.modeMalam = false; // mode malam mati
        this.statusRekam = false; // tidak merekam
    }

    public SmartCCTV(int id, String nama, boolean power, String ip, String mac, String protokol,
                     String lokasi, String firmware, SensorInternal sensor, Pengguna pemilik,
                     String resolusi, boolean modeMalam, boolean statusRekam) { // constructor lengkap
        super(id, nama, power, ip, mac, protokol, lokasi, firmware, sensor, pemilik); // panggil induk
        this.resolusi = "720p"; // nilai awal sebelum divalidasi
        this.modeMalam = modeMalam; // isi mode malam
        this.statusRekam = statusRekam; // isi status rekam
        setResolusi(resolusi); // isi resolusi lewat setter (divalidasi)
    }

    // Overriding: tampilkan data CCTV
    @Override
    public void displayInfo() { // menimpa method induk
        System.out.println("[SmartCCTV] " + namaPerangkat); // judul
        super.displayInfo(); // cetak data umum dari induk
        cetak("Resolusi", resolusi); // resolusi
        cetak("Mode Malam", modeMalam ? "Aktif" : "Nonaktif"); // mode malam
        cetak("Status Rekam", statusRekam ? "Merekam" : "Berhenti"); // status rekam
    }

    public void setResolusi(String resolusi) { // setter dengan validasi
        if (!resolusiValid(resolusi)) // resolusi tidak didukung
            throw new IllegalArgumentException("Resolusi '" + resolusi + "' tidak didukung (720p/1080p/2K/4K)"); // trigger error
        this.resolusi = resolusi; // simpan jika valid
    }
    public String getResolusi() { return resolusi; } // getter resolusi

    public void setModeMalam(boolean status) { this.modeMalam = status; } // setter mode malam
    public boolean getModeMalam() { return modeMalam; } // getter mode malam

    public void setStatusRekam(boolean rekam) { this.statusRekam = rekam; } // setter status rekam
    public boolean getStatusRekam() { return statusRekam; } // getter status rekam
}
