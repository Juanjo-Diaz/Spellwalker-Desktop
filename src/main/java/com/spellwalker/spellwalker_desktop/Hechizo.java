package com.spellwalker.spellwalker_desktop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hechizo {
    private int id;
    private String nombre;
    private int costeAp;
    private int costeMana;
    private String tipo;
    private String velocidad;
    private int legendario;
    private int escuelaId;
    private int esFusion;
}
