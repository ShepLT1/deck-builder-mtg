package com.mtg.card.spell.planeswalker;

import com.mtg.card.spell.SpellDto;

public class PlaneswalkerDto extends SpellDto {

    private int loyalty;

    public PlaneswalkerDto() {
    }

    public int getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(int loyalty) {
        this.loyalty = loyalty;
    }
}
