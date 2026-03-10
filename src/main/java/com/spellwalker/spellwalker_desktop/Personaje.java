package com.spellwalker.spellwalker_desktop;

public class Personaje {
    private int id;
    private String nombrePersonaje;
    private String nombreCampana;
    private String escuela;
    private String spells;
    private String descripcion;
    private String creador;

    public Personaje(int id, String nombrePersonaje, String nombreCampana, String escuela, String spells,
            String descripcion, String creador) {
        this.id = id;
        this.nombrePersonaje = nombrePersonaje;
        this.nombreCampana = nombreCampana;
        this.escuela = escuela;
        this.spells = spells;
        this.descripcion = descripcion;
        this.creador = creador;
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

    public String getCreador() {
        return creador;
    }
}
