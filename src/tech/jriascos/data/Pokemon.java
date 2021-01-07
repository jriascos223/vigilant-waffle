package tech.jriascos.data;

import tech.jriascos.data.Ability;

//this is dumb but it's for a test

public class Pokemon {
    private Ability[] abilities;
    private int base_experience;

    public Pokemon(Ability[] abilities, int base_experience) {
        this.abilities = abilities;
        this.base_experience = base_experience;
    }

    public Ability[] getAbility() {
        return this.abilities;
    }

    public int getExperience() {
        return this.base_experience;
    }
}
