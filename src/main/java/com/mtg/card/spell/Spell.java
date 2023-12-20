package com.mtg.card.spell;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtg.Color;
import com.mtg.card.base.Card;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@DiscriminatorValue("Spell")
public class Spell extends Card {

    public enum CardType {
        INSTANT, SORCERY, ARTIFACT, ENCHANTMENT, CREATURE, PLANESWALKER;

        @JsonCreator
        public static CardType fromText(String text) {
            for (CardType c : CardType.values()) {
                if (c.name().equals(text.toUpperCase())) {
                    return c;
                }
            }
            throw new IllegalArgumentException("Unaccepted CardType value. Excepted CardType values (case insensitive) = " + Arrays.toString(CardType.values()));
        }

        @Override
        public String toString() {
            return name();
        }

    }

    private List<Color> manaCost;
    private CardType type;

    public Spell() {}

    @JsonCreator
    public Spell(@JsonProperty(value = "name", required = true) String name, @JsonProperty(value = "abilities", required = true) List<String> abilities, @JsonProperty(value = "manaCost", required = true) List<Color> manaCost, @JsonProperty(value = "type", required = true) CardType type) {
        super(name, abilities);
//        manaCost.sort(new SortByType());
//        manaCost.sort(new SortByColor());
        this.manaCost = manaCost;
        this.type = type;
    }

    @JsonIgnore
    public String getStringManaCost() {

        StringBuilder output = new StringBuilder();
//        for (Mana mana : new HashSet<>(this.manaCost)) {
//            if (!output.toString().equals("")) {
//                output.append(", ");
//            }
//            if (mana instanceof Generic) {
//                output.append(" ").append(((Generic) mana).getValue());
//            }
//            output.append(mana.getColor()).append(" ").append(mana.getType());
//            if (mana instanceof Hybrid) {
//                output.append(" / ");
//                if (((Hybrid) mana).getSecondMana() instanceof Generic secondMana) {
//                    output.append(secondMana.getValue()).append(" ");
//                }
//                output.append(((Hybrid) mana).getSecondMana().getColor()).append(" ").append(((Hybrid) mana).getSecondMana().getType());
//            }
//        }
        return output.toString();
    }

    public List<Color> getManaCost() {
        return this.manaCost;
    }

    public void setManaCost(List<Color> manaCost) {
        this.manaCost = manaCost;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        if (getClass().getSimpleName().equalsIgnoreCase("creature")) {
            this.type = CardType.CREATURE;
        } else {
            this.type = type;
        }
    }

    @JsonIgnore
    public int getManaValue() {
        return this.manaCost.size();
    }

    @JsonIgnore
    @Override
    public List<Color> getColors() {
        List<Color> colors = new ArrayList<>();
//        for (Mana mana : new HashSet<>(this.manaCost)) {
//            if (mana.getType().equals(ManaType.COLORED) || mana.getType().equals(ManaType.PHYREXIAN)) {
//                colors.add(mana.getColor());
//            }
//            if (mana instanceof Hybrid) {
//                Mana secondMana = ((Hybrid) mana).getSecondMana();
//                if (secondMana.getType().equals(ManaType.COLORED) || secondMana.getType().equals(ManaType.PHYREXIAN)) {
//                    colors.add(secondMana.getColor());
//                }
//            }
//        }
        return colors;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Card type = %s%nMana Cost = %s%n", this.type, getStringManaCost());
    }
}
