package com.mtg.card.spell;

import com.mtg.card.base.Card;
import com.mtg.card.base.Rarity;

import java.util.List;

public class SpellDto {

    private String name;
    private String abilities;
    private List<Integer> manaCost;
    private CardType type;
    private Card dual;
    private Rarity rarity;
    private String imageUri;

    public SpellDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbilities() {
        return abilities;
    }

    public void setAbilities(String abilities) {
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

    public Card getDual() {
        return dual;
    }

    public void setDual(Card dual) {
        this.dual = dual;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
