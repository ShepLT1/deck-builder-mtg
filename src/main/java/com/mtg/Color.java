package com.mtg;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Color {
    ANY, COLORLESS, WHITE, BLUE, BLACK, RED, GREEN;

    @JsonCreator
    public static Color fromText(String text) {
        for (Color c : Color.values()) {
            if (c.name().equals(text.toUpperCase())) {
                return c;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return name();
    }

}
