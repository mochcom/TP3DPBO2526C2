// Base Class 2 (berbentuk INTERFACE karena Java tidak mendukung multiple inheritance antar-class).
// SmartDevice meng-extends PerangkatSistem dan meng-implements interface ini, sehingga SmartDevice
// bisa diperlakukan sebagai PerangkatSistem MAUPUN KoneksiJaringan (polimorfisme, lihat Main.demoPolimorfisme).
// Catatan: interface tidak bisa menyimpan atribut (state), jadi atribut jaringan dideklarasikan di SmartDevice.
// Atribut: ipAddress, macAddress, protokol (disimpan di kelas yang mengimplementasikan)
public interface KoneksiJaringan { // deklarasi interface
    String HEKSA = "0123456789abcdefABCDEF"; // konstanta: karakter heksadesimal sah

    // Validasi format IPv4: 4 angka (0-255) dipisah titik
    static boolean ipValid(String ip) { // method statis di interface
        String[] bagian = ip.split("\\.", -1); // pecah berdasarkan titik (pertahankan segmen kosong)
        if (bagian.length != 4) return false; // harus tepat 4 segmen
        for (String b : bagian) { // cek tiap segmen
            if (b.isEmpty() || b.length() > 3) return false; // kosong / terlalu panjang -> tidak valid
            for (char c : b.toCharArray()) // cek tiap karakter segmen
                if (c < '0' || c > '9') return false; // harus digit 0-9
            if (Integer.parseInt(b) > 255) return false; // maksimal 255
        }
        return true; // lolos semua pengecekan
    }

    // Validasi format MAC: XX:XX:XX:XX:XX:XX (heksadesimal)
    static boolean macValid(String mac) { // method statis validasi MAC
        if (mac.length() != 17) return false; // panjang harus 17
        for (int i = 0; i < mac.length(); i++) { // cek tiap karakter
            char c = mac.charAt(i); // ambil karakter ke-i
            if (i % 3 == 2) { // posisi 2,5,8,11,14 harus ':'
                if (c != ':') return false; // bukan ':' -> tidak valid
            } else if (HEKSA.indexOf(c) < 0) { // selain itu harus digit heksa
                return false; // tidak valid
            }
        }
        return true; // valid
    }

    // Default method: perilaku siap pakai yang diwariskan ke semua kelas yang meng-implements interface ini
    default String ringkasanJaringan() { // tidak perlu ditulis ulang di SmartDevice
        return getIpAddress() + " | " + getMacAddress() + " | " + getProtokol(); // gabungkan IP, MAC, protokol
    }

    void setIpAddress(String ip); // wajib diimplementasikan: setter IP
    String getIpAddress(); // wajib diimplementasikan: getter IP

    void setMacAddress(String mac); // wajib diimplementasikan: setter MAC
    String getMacAddress(); // wajib diimplementasikan: getter MAC

    void setProtokol(String protokol); // wajib diimplementasikan: setter protokol
    String getProtokol(); // wajib diimplementasikan: getter protokol
}
