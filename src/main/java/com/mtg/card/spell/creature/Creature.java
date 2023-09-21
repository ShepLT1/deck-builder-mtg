package com.mtg.card.spell.creature;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtg.Color;
import com.mtg.card.spell.Spell;
import jakarta.persistence.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class Creature extends Spell {

    public enum Attribute {
        DEATHTOUCH, DOUBLE_STRIKE, FIRST_STRIKE, FLYING, HASTE, LIFELINK, MENACE, REACH, TRAMPLE, VIGILANCE;

        @JsonCreator
        public static Attribute fromText(String text) {
            for (Attribute c : Attribute.values()) {
                if (c.name().equals(text.toUpperCase())) {
                    return c;
                }
            }
            throw new IllegalArgumentException("Unaccepted Attribute value. Excepted CardType values (case insensitive) = " + Arrays.toString(Attribute.values()));
        }

        @Override
        public String toString() {
            return name();
        }
    }
    private int power;
    private int toughness;
    private List<Attribute> attributes;

    public Creature() {}

    public Creature(String name, List<Color> manaCost, int power, int toughness, List<Attribute> attributes) {
        this(name, new ArrayList<>(), manaCost, power, toughness, attributes);
    }

    @JsonCreator
    public Creature(@JsonProperty(value = "name", required = true) String name, List<String> abilities, @JsonProperty(value = "manaCost", required = true) List<Color> manaCost, @JsonProperty(value = "power", required = true) int power, @JsonProperty(value = "toughness", required = true) int toughness, List<Attribute> attributes) {
        super(name, abilities, manaCost, CardType.CREATURE);
        this.power = power;
        this.toughness = toughness;
        this.attributes = attributes;
    }

    public Long getId() {
        return id;
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

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Power = %s%nToughness = %s%nAttributes = %s%n", this.power, this.toughness, this.attributes);
    }

}
