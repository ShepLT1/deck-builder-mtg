package com.mtg.card.spell.planeswalker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtg.card.base.Card;
import com.mtg.card.spell.CardType;
import com.mtg.card.spell.Spell;
import com.mtg.mana.ManaSymbol;
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
    public Planeswalker(@JsonProperty(value = "name", required = true) String name, @JsonProperty(value = "abilities", required = true)  List<String> abilities, @JsonProperty(value = "manaCost", required = true) List<ManaSymbol> manaCost, @JsonProperty(value = "loyalty", required = true) int loyalty, @JsonProperty(value = "dual", required = true) Card dual) {
        super(name, abilities, manaCost, CardType.PLANESWALKER, dual);
        this.loyalty = loyalty;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Loyalty = %s%n", this.loyalty);
    }

}
