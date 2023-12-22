package com.mtg.mana;

import java.util.Comparator;

public class SortByType implements Comparator<ManaSymbol> {
    @Override
    public int compare(ManaSymbol a, ManaSymbol b) {
        return a.getManaList().get(0).getType().compareTo(b.getManaList().get(0).getType());
    }
}
