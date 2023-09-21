package com.mtg.deck;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtg.Color;
import com.mtg.card.base.Card;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Deck {

    private @Id @GeneratedValue Long id;
    protected String name;
    @OneToMany
    protected List<Card> cardList;
    protected int cardLimit;
    protected int uniqueCardLimit;
    protected List<Color> colors;

    @JsonCreator
    public Deck(@JsonProperty(value = "name", required = true) String name, @JsonProperty(value = "colors", required = true) List<Color> colors) {
        this(name,60, 4, colors);
    }

    public Deck(String name, int cardLimit, int uniqueCardLimit, List<Color> colors) {
        this.name = name;
        this.cardList = new ArrayList<>();
        this.cardLimit = cardLimit;
        this.uniqueCardLimit = uniqueCardLimit;
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    @JsonIgnore
    private boolean hasValidColors(Card card) {
        for (Color color : card.getColors()) {
            if (color.equals(Color.ANY)) {
                continue;
            }
            if (!getColors().contains(color)) {
                System.out.println(">>>>> Card " + card.getName() + " " + card.getColors() + " contains colors not in deck " + name + " " + colors);
                return false;
            }
        }
        return true;
    }

    @JsonIgnore
    public boolean addCard(Card card) {
        if (hasValidColors(card) && cardList.size() < cardLimit && !hasReachedCardLimit(card)) {
            cardList.add(card);
            return true;
        }
        System.out.println(">>>>> Unable to add card to deck");
        System.out.println();
        return false;
    }

    @JsonIgnore
    public int getTotalMana() {
        return cardList.stream().mapToInt(Card::getManaValue).sum();
    }

    @JsonIgnore
    private boolean hasReachedCardLimit(Card card) {
        if (!(card.getClass().getSimpleName().equalsIgnoreCase("land") && card.getName().toLowerCase().contains("basic"))) {
            if (Collections.frequency(cardList, card) >= uniqueCardLimit) {
                System.out.println(">>>>> Deck " + name + " already contains " + uniqueCardLimit + " cards named " + card.getName());
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("DECK%nName = %s%nColors = %s%nCard List = %s%n", name, colors, cardList);
    }
}
