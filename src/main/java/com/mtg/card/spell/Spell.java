package com.mtg.card.spell;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtg.Color;
import com.mtg.card.base.Card;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.*;

@Entity
@DiscriminatorValue("Spell")
public class Spell extends Card {

    public enum CardType {
        INSTANT, SORCERY, ARTIFACT, ENCHANTMENT, CREATURE;

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
        Collections.sort(manaCost);
        this.manaCost = manaCost;
        this.type = type;
    }

    @JsonIgnore
    public String getStringManaCost() {

        StringBuilder output = new StringBuilder();
        for (Color color : new HashSet<>(this.manaCost)) {
            if (!output.toString().equals("")) {
                output.append(", ");
            }
            output.append(Collections.frequency(this.manaCost, color)).append(" ").append(color.toString());
        }
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
        return new ArrayList<>(new LinkedHashSet<>(this.manaCost));
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Card type = %s%nMana Cost = %s%n", this.type, getStringManaCost());
    }
}
