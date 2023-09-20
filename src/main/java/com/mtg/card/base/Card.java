package com.mtg.card.base;

import com.mtg.Color;
import jakarta.persistence.*;

import java.util.List;

@Entity(name="cards")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="basic_card_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Card {

    protected @Id @GeneratedValue Long id;
    @Column(unique = true)
    protected String name;
    protected List<String> abilities;

    public Card() {

    }

    public Card(String name, List<String> abilities) {
        this.name = name;
        this.abilities = abilities;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    public abstract List<Color> getColors();

    public abstract int getManaValue();

    @Override
    public String toString() {
        return String.format("CARD%nName = %s%nAbilities = %s%n", name, abilities);
    }
}
