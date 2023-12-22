package com.mtg.mana;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mtg.card.spell.Spell;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "mana_symbol")
public class ManaSymbol {

    private @Id @GeneratedValue Long id;
    private String name;
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "symbol_mana",
            joinColumns = @JoinColumn(name = "symbol_id"),
            inverseJoinColumns = @JoinColumn(name = "mana_id"))
    private List<Mana> manaList;
    @ManyToMany(mappedBy = "manaCost")
    @JsonIgnore
    private List<Spell> spellList;

    public ManaSymbol() {
    }

    @JsonCreator
    public ManaSymbol(List<Mana> manaList) {
        this.name = "";
        for (int i = 0; i < manaList.size(); i++) {
            if (i > 0) {
                this.name += " / ";
            }
            if (manaList.get(i).getColor() != Color.COLORLESS) {
                this.name += manaList.get(i).getColor() + " ";
            }
            if (manaList.get(i).getType() != ManaType.COLORED) {
                this.name += manaList.get(i).getType() + " ";
            }
            if (manaList.get(i).getType() == ManaType.GENERIC) {
                this.name += manaList.get(i).getValue();
            }
        }
        this.manaList = manaList;
        this.spellList = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Mana> getManaList() {
        return manaList;
    }

    public void setManaList(List<Mana> manaList) {
        this.manaList = manaList;
    }

    public List<Spell> getSpellList() {
        return spellList;
    }

    public void setSpellList(List<Spell> spellList) {
        this.spellList = spellList;
    }

}
