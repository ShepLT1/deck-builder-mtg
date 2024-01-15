package com.mtg.card.spell;

import java.util.List;

public class SpellDto {

    private String name;
    private List<String> abilities;

    private List<Integer> manaCost;
    private CardType type;

    public SpellDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    public List<Integer> getManaCost() {
        return manaCost;
    }

    public void setManaCost(List<Integer> manaCost) {
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
}
