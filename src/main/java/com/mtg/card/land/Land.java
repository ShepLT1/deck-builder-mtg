package com.mtg.card.land;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtg.card.base.Card;
import com.mtg.card.base.Rarity;
import com.mtg.mana.Color;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@DiscriminatorValue("Land")
public class Land extends Card {

    private List<Color> colors;

    public Land() {}

    public Land(String name, List<Color> colors, Rarity rarity) {
        this(name, null, colors, rarity, null, null);
    }

    @JsonCreator
    public Land(@JsonProperty(value = "name", required = true) String name, String abilities, @JsonProperty(value = "colors", required = true) List<Color> colors, @JsonProperty(value = "rarity", required = true) Rarity rarity, String imageUri, Card dual) {
        super(name, abilities, rarity, imageUri, dual);
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
