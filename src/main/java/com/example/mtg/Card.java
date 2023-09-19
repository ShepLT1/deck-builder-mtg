package com.example.mtg;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;

import java.util.List;

@Entity
@Inheritance
public abstract class Card {

    private @Id @GeneratedValue Long id;
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
