package com.mtg.card.spell.battle;

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
public class Battle extends Spell {

    private int defense;

    public Battle() {}

    @JsonCreator
    public Battle(@JsonProperty(value = "name", required = true) String name, String abilities, @JsonProperty(value = "manaCost", required = true) List<ManaSymbol> manaCost, @JsonProperty(value = "defense", required = true) int defense, @JsonProperty(value = "rarity", required = true) Rarity rarity, @JsonProperty(value = "imageUri", required = true) String imageUri, @JsonProperty(value = "dual", required = true) Card dual) {
        super(name, abilities, manaCost, CardType.BATTLE, rarity, imageUri, dual);
        this.defense = defense;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

}
