// Base Class 1: identitas fisik & status daya perangkat
public class PerangkatSistem { // deklarasi kelas dasar pertama
    protected int idPerangkat; // ID unik perangkat
    protected String namaPerangkat; // nama perangkat
    protected boolean statusPower; // true = ON, false = OFF

    public PerangkatSistem() { // constructor default
        this.idPerangkat = 0; // ID awal 0
        this.namaPerangkat = ""; // nama awal kosong
        this.statusPower = false; // awalnya OFF
    }

    public PerangkatSistem(int id, String nama, boolean status) { // constructor berparameter
        this.idPerangkat = id; // isi ID
        this.namaPerangkat = nama; // isi nama
        this.statusPower = status; // isi status power
    }

    public void togglePower() { statusPower = !statusPower; } // balik status ON <-> OFF

    public void setIdPerangkat(int id) { this.idPerangkat = id; } // setter ID
    public int getIdPerangkat() { return idPerangkat; } // getter ID

    public void setNamaPerangkat(String nama) { this.namaPerangkat = nama; } // setter nama
    public String getNamaPerangkat() { return namaPerangkat; } // getter nama

    public void setStatusPower(boolean status) { this.statusPower = status; } // setter status power
    public boolean getStatusPower() { return statusPower; } // getter status power
}
