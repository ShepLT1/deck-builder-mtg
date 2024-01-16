package com.mtg.card.spell.creature;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtg.card.base.Card;
import com.mtg.card.base.Rarity;
import com.mtg.card.spell.CardType;
import com.mtg.card.spell.Spell;
import com.mtg.mana.ManaSymbol;
import jakarta.persistence.Entity;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Creature extends Spell {
    private int power;
    private int toughness;

    public Creature() {

    }

    public Creature(String name, List<ManaSymbol> manaCost, int power, int toughness, Rarity rarity, String imageUri, Card dual) {
        this(name, new ArrayList<>(), manaCost, power, toughness, rarity, imageUri, dual);
    }

    @JsonCreator
    public Creature(@JsonProperty(value = "name", required = true) String name, List<String> abilities, @JsonProperty(value = "manaCost", required = true) List<ManaSymbol> manaCost, @JsonProperty(value = "power", required = true) int power, @JsonProperty(value = "toughness", required = true) int toughness, @JsonProperty(value = "rarity", required = true) Rarity rarity, @JsonProperty(value = "imageUri", required = true) String imageUri,Card dual) {
        super(name, abilities, manaCost, CardType.CREATURE, rarity, imageUri, dual);
        this.power = power;
        this.toughness = toughness;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getToughness() {
        return toughness;
    }

    public void setToughness(int toughness) {
        this.toughness = toughness;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Power = %s%nToughness = %s%n", this.power, this.toughness);
    }

}
