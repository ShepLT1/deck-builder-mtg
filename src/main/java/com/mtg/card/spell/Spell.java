package com.mtg.card.spell;

import com.mtg.Color;
import com.mtg.card.base.Card;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.*;

@Entity
@DiscriminatorValue("Spell")
public class Spell extends Card {

    public enum CardType {
        INSTANT, SORCERY, ARTIFACT, ENCHANTMENT, CREATURE
    }

    private List<Color> manaCost;
    private CardType type;

    public Spell(String name, List<String> abilities, List<Color> manaCost, CardType type) {
        super(name, abilities);
        Collections.sort(manaCost);
        this.manaCost = manaCost;
        this.type = type;
    }

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
        this.type = type;
    }

    public int getManaValue() {
        return this.manaCost.size();
    }

    @Override
    public List<Color> getColors() {
        return new ArrayList<>(new LinkedHashSet<>(this.manaCost));
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Card type = %s%nMana Cost = %s%n", this.type, getStringManaCost());
    }
}
