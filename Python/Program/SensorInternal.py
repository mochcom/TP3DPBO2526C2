# Member Class (Composition): sensor tertanam di dalam SmartDevice
class SensorInternal:  # deklarasi kelas sensor
    def __init__(self, id=0, tipe="", nilai=0.0):  # constructor
        self.__idSensor = id  # private: ID sensor
        self.__tipeSensor = tipe  # private: jenis sensor (LDR, PIR, dll)
        self.__nilaiBacaan = nilai  # private: hasil pembacaan sensor

    def setIdSensor(self, id): self.__idSensor = id  # setter ID sensor
    def getIdSensor(self): return self.__idSensor  # getter ID sensor

    def setTipeSensor(self, tipe): self.__tipeSensor = tipe  # setter tipe sensor
    def getTipeSensor(self): return self.__tipeSensor  # getter tipe sensor

    def setNilaiBacaan(self, nilai): self.__nilaiBacaan = nilai  # setter nilai bacaan
    def getNilaiBacaan(self): return self.__nilaiBacaan  # getter nilai bacaan
