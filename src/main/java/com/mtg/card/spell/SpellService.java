package com.mtg.card.spell;

import com.mtg.mana.ManaSymbol;
import com.mtg.mana.ManaSymbolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SpellService {
    @Autowired
    private ManaSymbolRepository manaSymbolRepository;

    public void updateManaCost(Spell spell, List<Integer> manaCostList) {
        if (manaCostList != null) {
            List<Long> longManaSymbolIds = new ArrayList<>();
            manaCostList.forEach(id -> longManaSymbolIds.add(Long.valueOf(id)));
            List<ManaSymbol> manaList = manaSymbolRepository.findByIdIn(longManaSymbolIds);
            List<ManaSymbol> newManaList = new ArrayList<>();
            manaList.forEach(symbol -> {
                int frequency = Collections.frequency(longManaSymbolIds, symbol.getId());
                for (int i = 0; i < frequency; i++) {
                    newManaList.add(symbol);
                }
            });
            spell.setManaCost(newManaList);
        }
    }

}
