package com.mtg.card.spell;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

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
