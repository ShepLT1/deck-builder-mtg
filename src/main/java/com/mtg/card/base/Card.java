package com.mtg.card.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mtg.card.collectible.Collectible;
import com.mtg.deck.Deck;
import com.mtg.mana.Color;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name="cards")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="basic_card_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Card {

    protected @Id @GeneratedValue Long id;
    @Column(unique = true, name = "IX_NAME")
    private String name;
    private List<String> abilities;
    @ManyToMany(mappedBy = "cardList")
    @JsonIgnore
    private List<Deck> deckList;
    @ManyToOne
    @JoinColumn(name = "dual_id", nullable=false)
    private Card dual;
    private Rarity rarity;
    private String imageUri;
    @OneToMany(mappedBy="card")
    @JsonIgnore
    private List<Collectible> collectibles = new ArrayList<>();

    public Card() {

    }

    public Card(String name, List<String> abilities, Rarity rarity, String imageUri, Card dual) {
        this.name = name;
        this.abilities = abilities;
        this.deckList = new ArrayList<>();
        this.rarity = rarity;
        this.imageUri = imageUri;
        this.dual = dual;
    }

    public Long getId() {
        return id;
    }

    public List<Deck> getDeckList() {
        return deckList;
    }

    public void setDeckList(List<Deck> deckList) {
        this.deckList = deckList;
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

    public Card getDual() {
        return dual;
    }

    public void setDual(Card dual) {
        this.dual = dual;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public List<Collectible> getCollectibles() {
        return collectibles;
    }

    public void setCollectibles(List<Collectible> collectibles) {
        this.collectibles = collectibles;
    }

    public abstract List<Color> getColors();

    public abstract int getManaValue();

    @Override
    public String toString() {
        return String.format("CARD%nName = %s%nAbilities = %s%n", name, abilities);
    }
}
