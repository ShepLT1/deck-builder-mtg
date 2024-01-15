package com.mtg.card.spell.creature;

import com.mtg.card.spell.SpellDto;

public class CreatureDto extends SpellDto {

    private int power;
    private int toughness;

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

}
