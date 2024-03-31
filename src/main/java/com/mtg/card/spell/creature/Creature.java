package com.mtg.card.spell.creature;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtg.card.base.Card;
import com.mtg.card.base.Rarity;
import com.mtg.card.spell.CardType;
import com.mtg.card.spell.Spell;
import com.mtg.mana.ManaSymbol;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class Creature extends Spell {
    private String power;
    private String toughness;

    public Creature() {

    }

    public Creature(String name, List<ManaSymbol> manaCost, String power, String toughness, Rarity rarity, String imageUri, Card dual) {
        this(name, null, manaCost, power, toughness, rarity, imageUri, dual);
    }

    @JsonCreator
    public Creature(@JsonProperty(value = "name", required = true) String name, String abilities, @JsonProperty(value = "manaCost", required = true) List<ManaSymbol> manaCost, @JsonProperty(value = "power", required = true) String power, @JsonProperty(value = "toughness", required = true) String toughness, @JsonProperty(value = "rarity", required = true) Rarity rarity, @JsonProperty(value = "imageUri", required = true) String imageUri,Card dual) {
        super(name, abilities, manaCost, CardType.CREATURE, rarity, imageUri, dual);
        this.power = power;
        this.toughness = toughness;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getToughness() {
        return toughness;
    }

    public void setToughness(String toughness) {
        this.toughness = toughness;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Power = %s%nToughness = %s%n", this.power, this.toughness);
    }

}
