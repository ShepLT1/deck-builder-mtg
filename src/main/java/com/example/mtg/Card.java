package com.example.mtg;

import java.util.List;

public abstract class Card {

    protected String name;
    protected List<String> abilities;

    public Card(String name, List<String> abilities) {
        this.name = name;
        this.abilities = abilities;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    public abstract List<Color> getColors();

    public abstract int getManaValue();

    @Override
    public String toString() {
        return String.format("CARD%nName = %s%nAbilities = %s%n", name, abilities);
    }
}
