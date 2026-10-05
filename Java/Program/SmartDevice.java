import java.util.Locale; // untuk format angka desimal (titik)

// Main Class: "Multiple Inheritance" di Java
//  - extends PerangkatSistem (class)
//  - implements KoneksiJaringan (interface)
// - sensor : Composition (SmartDevice membuat salinan & memilikinya sendiri)
// - pemilik: Aggregation (hanya referensi ke objek Pengguna yang hidup mandiri)
public class SmartDevice extends PerangkatSistem implements KoneksiJaringan { // pewarisan class + interface
    protected String ipAddress; // atribut dari KoneksiJaringan: IP
    protected String macAddress; // atribut dari KoneksiJaringan: MAC
    protected String protokol; // atribut dari KoneksiJaringan: protokol

    protected String lokasiRuangan; // lokasi pemasangan
    protected String versiFirmware; // versi firmware
    protected SensorInternal sensor; // Composition: sensor milik perangkat
    protected Pengguna pemilik; // Aggregation: referensi ke pengguna

    public SmartDevice() { // constructor default
        super(); // panggil constructor PerangkatSistem
        this.ipAddress = ""; // IP kosong
        this.macAddress = ""; // MAC kosong
        this.protokol = ""; // protokol kosong
        this.lokasiRuangan = ""; // lokasi kosong
        this.versiFirmware = ""; // firmware kosong
        this.sensor = new SensorInternal(); // sensor kosong milik perangkat
        this.pemilik = null; // belum ada pemilik
    }

    public SmartDevice(int id, String nama, boolean power, String ip, String mac, String protokol,
                       String lokasi, String firmware, SensorInternal sensor, Pengguna pemilik) { // constructor lengkap
        super(id, nama, power); // panggil constructor PerangkatSistem
        this.ipAddress = ""; // nilai awal sebelum divalidasi
        this.macAddress = ""; // nilai awal sebelum divalidasi
        this.protokol = protokol; // isi protokol
        setIpAddress(ip); // isi IP lewat setter (divalidasi)
        setMacAddress(mac); // isi MAC lewat setter (divalidasi)
        this.lokasiRuangan = lokasi; // isi lokasi
        this.versiFirmware = firmware; // isi firmware
        this.sensor = new SensorInternal(sensor.getIdSensor(), sensor.getTipeSensor(),
                                         sensor.getNilaiBacaan()); // Composition: salin sensor jadi milik sendiri
        this.pemilik = pemilik; // Aggregation: simpan referensi
    }

    // Helper cetak baris "label : nilai"
    protected static void cetak(String label, Object nilai) { // dipakai displayInfo di semua kelas
        System.out.printf("  %-15s: %s%n", label, nilai); // label rata kiri 15 karakter
    }

    public void displayInfo() { // tampilkan data gabungan perangkat
        cetak("ID Perangkat", idPerangkat); // cetak ID
        cetak("Nama", namaPerangkat); // cetak nama
        cetak("Status Power", statusPower ? "ON" : "OFF"); // cetak power
        cetak("IP Address", ipAddress); // cetak IP
        cetak("MAC Address", macAddress); // cetak MAC
        cetak("Protokol", protokol); // cetak protokol
        cetak("Lokasi", lokasiRuangan); // cetak lokasi
        cetak("Firmware", versiFirmware); // cetak firmware
        cetak("Sensor", "#" + sensor.getIdSensor() + " " + sensor.getTipeSensor() + " = "
                + String.format(Locale.US, "%.1f", sensor.getNilaiBacaan())); // cetak sensor (1 desimal)
        if (pemilik != null) // jika pemilik ada
            cetak("Pemilik", pemilik.getNama() + " (" + pemilik.getTingkatAkses() + ")"); // nama & akses
        else // jika belum ada pemilik
            cetak("Pemilik", "-"); // tampilkan strip
    }

    // ----- implementasi interface KoneksiJaringan -----
    @Override public void setIpAddress(String ip) { // setter IP dengan validasi
        if (!KoneksiJaringan.ipValid(ip)) // format salah
            throw new IllegalArgumentException("Format IP Address tidak valid: '" + ip + "'"); // trigger error
        this.ipAddress = ip; // simpan jika valid
    }
    @Override public String getIpAddress() { return ipAddress; } // getter IP

    @Override public void setMacAddress(String mac) { // setter MAC dengan validasi
        if (!KoneksiJaringan.macValid(mac)) // format salah
            throw new IllegalArgumentException("Format MAC Address tidak valid: '" + mac + "'"); // trigger error
        this.macAddress = mac; // simpan jika valid
    }
    @Override public String getMacAddress() { return macAddress; } // getter MAC

    @Override public void setProtokol(String protokol) { this.protokol = protokol; } // setter protokol
    @Override public String getProtokol() { return protokol; } // getter protokol

    // ----- getter & setter milik SmartDevice -----
    public void setLokasiRuangan(String lokasi) { this.lokasiRuangan = lokasi; } // setter lokasi
    public String getLokasiRuangan() { return lokasiRuangan; } // getter lokasi

    public void setVersiFirmware(String firmware) { this.versiFirmware = firmware; } // setter firmware
    public String getVersiFirmware() { return versiFirmware; } // getter firmware

    public void setSensor(SensorInternal sensor) { this.sensor = sensor; } // setter sensor
    public SensorInternal getSensor() { return sensor; } // getter sensor (referensi, bisa diubah)

    public void setPemilik(Pengguna pemilik) { this.pemilik = pemilik; } // setter pemilik
    public Pengguna getPemilik() { return pemilik; } // getter pemilik
}
