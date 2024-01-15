package com.mtg.card.base;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum Rarity {

    COMMON, UNCOMMON, RARE, MYTHIC;

    @JsonCreator
    public static Rarity fromText(String text) {
        for (Rarity r : Rarity.values()) {
            if (r.name().equals(text.toUpperCase())) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unaccepted Rarity value. Excepted Rarity values (case insensitive) = " + Arrays.toString(Rarity.values()));
    }

    @Override
    public String toString() {
        return name();
    }

}
