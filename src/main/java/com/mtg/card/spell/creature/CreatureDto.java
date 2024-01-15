package com.mtg.card.spell.creature;

import com.mtg.card.spell.SpellDto;

import java.util.List;

public class CreatureDto extends SpellDto {

    private int power;
    private int toughness;
    private List<Creature.Attribute> attributes;

    public CreatureDto() {
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

    public List<Creature.Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Creature.Attribute> attributes) {
        this.attributes = attributes;
    }
}
