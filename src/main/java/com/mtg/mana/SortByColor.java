package com.mtg.mana;

import java.util.Comparator;

public class SortByColor implements Comparator<ManaSymbol> {
    @Override
    public int compare(ManaSymbol a, ManaSymbol b) {
        return a.getManaList().get(0).getColor().compareTo(b.getManaList().get(0).getColor());
    }
}
