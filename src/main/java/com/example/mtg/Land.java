package com.example.mtg;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Land extends Card {

    private @Id @GeneratedValue Long id;
    private List<Color> colors;

    public Land(String name, List<Color> colors) {
        this(name, new ArrayList<>(), colors);
    }

    public Land(String name, List<String> abilities, List<Color> colors) {
        super(name, abilities);
        this.colors = colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    @Override
    public int getManaValue() {
        return 0;
    }

    @Override
    public List<Color> getColors() {
        return this.colors;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Card Type = Land%nColors = %s%n", this.colors);
    }
}
