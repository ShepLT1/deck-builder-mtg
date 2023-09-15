package com.example.mtg;

import java.util.ArrayList;
import java.util.List;

public abstract class Card {

    protected String name;
    protected List<String> abilities;

    public Card(String name) {
        this(name, new ArrayList<>());
    }

    public Card(String name, List<String> abilities) {
        this.name = name;
        this.abilities = abilities;
    }

    public String getName() {
        return this.name;
    }

    public abstract List<Color> getColors();

    @Override
    public String toString() {
        return String.format("Name = %s%nAbilities = %s%n", name, abilities);
    }
}
