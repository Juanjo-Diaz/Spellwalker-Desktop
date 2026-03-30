package com.spellwalker.spellwalker_desktop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpellItem {
    private String name;
    private String school;
    private boolean isHeader;

    public static SpellItem header(String schoolName) {
        return new SpellItem(schoolName, schoolName, true);
    }

    public static SpellItem spell(String spellName, String schoolName) {
        return new SpellItem(spellName, schoolName, false);
    }

    @Override
    public String toString() {
        return name;
    }
}
