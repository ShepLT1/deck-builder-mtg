package com.mtg.card.spell.planeswalker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtg.Color;
import com.mtg.card.spell.Spell;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Planeswalker extends Spell {

    private int loyalty;

    public Planeswalker() {}

    @JsonCreator
    public Planeswalker(@JsonProperty(value = "name", required = true) String name,@JsonProperty(value = "abilities", required = true)  List<String> abilities, @JsonProperty(value = "manaCost", required = true) List<Color> manaCost, @JsonProperty(value = "loyalty", required = true) int loyalty) {
        super(name, abilities, manaCost, CardType.PLANESWALKER);
        this.loyalty = loyalty;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Loyalty = %s%n", this.loyalty);
    }

}
