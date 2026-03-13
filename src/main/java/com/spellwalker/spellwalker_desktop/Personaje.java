package com.spellwalker.spellwalker_desktop;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Personaje {
    private int id;
    private String nombrePersonaje;
    private String nombreCampana;
    private String escuela;
    private String spells;
    private String descripcion;
    private String creador;
}
