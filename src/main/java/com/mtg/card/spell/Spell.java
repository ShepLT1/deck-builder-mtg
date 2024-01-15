package com.mtg.card.spell;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtg.card.base.Card;
import com.mtg.mana.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "spells")
public class Spell extends Card {

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "spell_mana_symbol",
            joinColumns = @JoinColumn(name = "spell_id"),
            inverseJoinColumns = @JoinColumn(name = "mana_symbol_id"))
    private List<ManaSymbol> manaCost;
    private CardType type;

    public Spell() {
    }

    @JsonCreator
    public Spell(@JsonProperty(value = "name", required = true) String name, @JsonProperty(value = "abilities", required = true) List<String> abilities, @JsonProperty(value = "manaCost", required = true) List<ManaSymbol> manaCost, @JsonProperty(value = "type", required = true) CardType type, @JsonProperty(value = "dual", required = true) Card dual) {
        super(name, abilities, dual);
        manaCost.sort(new SortByType());
        manaCost.sort(new SortByColor());
        this.manaCost = manaCost;
        this.type = type;
    }

    @JsonIgnore
    public String getStringManaCost() {

        StringBuilder output = new StringBuilder();
        for (ManaSymbol manaSymbol : this.manaCost) {
            if (!output.toString().equals("")) {
                output.append(", ");
            }
            int i = 0;
            for (Mana mana : manaSymbol.getManaList()) {
                if (i > 0) {
                    output.append(" / ");
                }
                if (mana.getType().equals(ManaType.GENERIC)) {
                    output.append(" ").append(mana.getValue());
                }
                output.append(mana.getColor()).append(" ").append(mana.getType());
                i++;
            }
        }
        return output.toString();
    }

    public List<ManaSymbol> getManaCost() {
        return this.manaCost;
    }

    public void setManaCost(List<ManaSymbol> manaCost) {
        this.manaCost = manaCost;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        if (getClass().getSimpleName().equalsIgnoreCase("creature")) {
            this.type = CardType.CREATURE;
        } else if (getClass().getSimpleName().equalsIgnoreCase("planeswalker")) {
            this.type = CardType.PLANESWALKER;
        } else {
            this.type = type;
        }
    }

    // TODO: refactor getManaValue to account for generic mana

    @JsonIgnore
    public int getManaValue() {
        return this.manaCost.size();
    }

    @JsonIgnore
    @Override
    public List<Color> getColors() {
        List<Color> colors = new ArrayList<>();
        for (ManaSymbol manaSymbol : this.manaCost) {
            for (Mana mana : manaSymbol.getManaList()) {
                if (mana.getType().equals(ManaType.COLORED) || mana.getType().equals(ManaType.PHYREXIAN)) {
                    colors.add(mana.getColor());
                }
            }
        }
        return colors;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Card type = %s%nMana Cost = %s%n", this.type, getStringManaCost());
    }
}
