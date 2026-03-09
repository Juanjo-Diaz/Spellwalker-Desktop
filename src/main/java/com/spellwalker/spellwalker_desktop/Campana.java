package com.spellwalker.spellwalker_desktop;

public class Campana {
    private int idCampana;
    private String nombre;
    private String descripcion;

    public Campana(int idCampana, String nombre, String descripcion) {
        this.idCampana = idCampana;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdCampana() {
        return idCampana;
    }

    public void setIdCampana(int idCampana) {
        this.idCampana = idCampana;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
