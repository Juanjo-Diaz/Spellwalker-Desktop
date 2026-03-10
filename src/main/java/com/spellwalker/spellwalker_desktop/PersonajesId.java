package com.spellwalker.spellwalker_desktop;

public class PersonajesId {
    private String id;
    private String nombre;
    private String idCampana;
    private String descripcion;
    private String perfil;

    public PersonajesId(String id, String nombre, String idCampana, String descripcion, String perfil) {
        this.id = id;
        this.nombre = nombre;
        this.idCampana = idCampana;
        this.descripcion = descripcion;
        this.perfil = perfil;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getIdCampana() { return idCampana; }
    public String getDescripcion() { return descripcion; }
    public String getPerfil() { return perfil; }
}

