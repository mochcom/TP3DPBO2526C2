# Base Class 1: identitas fisik & status daya perangkat
class PerangkatSistem:  # deklarasi kelas dasar pertama
    def __init__(self, id=0, nama="", status=False):  # constructor (nilai default = constructor kosong)
        self._idPerangkat = id  # protected: ID unik perangkat
        self._namaPerangkat = nama  # protected: nama perangkat
        self._statusPower = status  # protected: True = ON, False = OFF

    def togglePower(self):  # balik status ON <-> OFF
        self._statusPower = not self._statusPower  # negasi status

    def setIdPerangkat(self, id): self._idPerangkat = id  # setter ID
    def getIdPerangkat(self): return self._idPerangkat  # getter ID

    def setNamaPerangkat(self, nama): self._namaPerangkat = nama  # setter nama
    def getNamaPerangkat(self): return self._namaPerangkat  # getter nama

    def setStatusPower(self, status): self._statusPower = status  # setter status power
    def getStatusPower(self): return self._statusPower  # getter status power
