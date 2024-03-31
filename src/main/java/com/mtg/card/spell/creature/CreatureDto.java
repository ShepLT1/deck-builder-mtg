package com.mtg.card.spell.creature;

import com.mtg.card.spell.SpellDto;

public class CreatureDto extends SpellDto {

    private String power;
    private String toughness;

    public CreatureDto() {
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

}
