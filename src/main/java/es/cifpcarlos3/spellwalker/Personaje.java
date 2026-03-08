package es.cifpcarlos3.spellwalker;

public class Personaje {
    private int id;
    private String nombrePersonaje;
    private String nombreCampana;
    private String escuela;
    private String spells;
    private String descripcion;

    public Personaje(int id, String nombrePersonaje, String nombreCampana, String escuela, String spells,
            String descripcion) {
        this.id = id;
        this.nombrePersonaje = nombrePersonaje;
        this.nombreCampana = nombreCampana;
        this.escuela = escuela;
        this.spells = spells;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public String getNombrePersonaje() {
        return nombrePersonaje;
    }

    public String getNombreCampana() {
        return nombreCampana;
    }

    public String getEscuela() {
        return escuela;
    }

    public String getSpells() {
        return spells;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
