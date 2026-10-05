// Member Class (Composition): sensor tertanam di dalam SmartDevice
public class SensorInternal { // deklarasi kelas sensor
    private int idSensor; // ID sensor
    private String tipeSensor; // jenis sensor (LDR, PIR, dll)
    private double nilaiBacaan; // hasil pembacaan sensor

    public SensorInternal() { // constructor default
        this.idSensor = 0; // ID awal 0
        this.tipeSensor = ""; // tipe awal kosong
        this.nilaiBacaan = 0.0; // nilai awal 0
    }

    public SensorInternal(int id, String tipe, double nilai) { // constructor berparameter
        this.idSensor = id; // isi ID
        this.tipeSensor = tipe; // isi tipe
        this.nilaiBacaan = nilai; // isi nilai
    }

    public void setIdSensor(int id) { this.idSensor = id; } // setter ID sensor
    public int getIdSensor() { return idSensor; } // getter ID sensor

    public void setTipeSensor(String tipe) { this.tipeSensor = tipe; } // setter tipe sensor
    public String getTipeSensor() { return tipeSensor; } // getter tipe sensor

    public void setNilaiBacaan(double nilai) { this.nilaiBacaan = nilai; } // setter nilai bacaan
    public double getNilaiBacaan() { return nilaiBacaan; } // getter nilai bacaan
}
