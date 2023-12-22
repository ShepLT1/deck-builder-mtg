package com.mtg.mana;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum ManaType {
    COLORED, COLORLESS, GENERIC, PHYREXIAN, SNOW;

    @JsonCreator
    public static ManaType fromText(String text) {
        for (ManaType c : ManaType.values()) {
            if (c.name().equals(text.toUpperCase())) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unaccepted ManaType value. Excepted ManaType values (case insensitive) = " + Arrays.toString(ManaType.values()));
    }

    @Override
    public String toString() {
        return name();
    }

}
