package mx.edu.ittepic.miriambarajas.tpdm_u3_practica1_miriamjanethbarajaslpez;

public class Maquillajes {
    int noLote;
    String nombreFab, tipoCat;
    Boolean disponibilidad;

    public Maquillajes(int num, String nom, Boolean disp, String tipo){
        noLote = num;
        nombreFab = nom;
        disponibilidad = disp;
        tipoCat = tipo;
    }
}
