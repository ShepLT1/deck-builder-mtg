package com.mtg.mana;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "mana")
public class Mana {

    private @Id @GeneratedValue Long id;
    @Enumerated(EnumType.STRING)
    private ManaType type;
    @Enumerated(EnumType.STRING)
    private Color color;
    @ManyToMany(mappedBy = "manaList")
    @JsonIgnore
    private List<ManaSymbol> manaSymbolList;
    private String value;

    public Mana() {}

    @JsonCreator
    public Mana(ManaType type, Color color, String value) {
        this.type = type;
        this.color = color;
        this.manaSymbolList = new ArrayList<>();
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public ManaType getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public void setType(ManaType type) {
        this.type = type;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<ManaSymbol> getManaSymbolList() {
        return manaSymbolList;
    }

    public void setManaSymbolList(List<ManaSymbol> manaSymbolList) {
        this.manaSymbolList = manaSymbolList;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

