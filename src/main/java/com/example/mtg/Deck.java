package com.example.mtg;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Entity
public class Deck {

    private @Id @GeneratedValue Long id;
    protected String name;
    @OneToMany
    protected Card[] cardList;
    protected int uniqueCardLimit;
    protected List<Color> colors;

    public Deck(String name, List<Color> colors) {
        this(name, new Card[60], 4, colors);
    }

    public Deck(String name, Card[] cardList, int uniqueCardLimit, List<Color> colors) {
        this.name = name;
        this.cardList = cardList;
        this.uniqueCardLimit = uniqueCardLimit;
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Card[] getCardList() {
        return cardList;
    }

    public void setCardList(Card[] cardList) {
        this.cardList = cardList;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    private boolean hasValidColors(Card card) {
        for (Color color : card.getColors()) {
            if (color.equals(Color.ANY_COLOR)) {
                continue;
            }
            if (!getColors().contains(color)) {
                System.out.println(">>>>> Card " + card.getName() + " " + card.getColors() + " contains colors not in deck " + name + " " + colors);
                return false;
            }
        }
        return true;
    }

    private int getNextCardSlot() {
        List<Card> tempList = new ArrayList<>(Arrays.asList(cardList));
        int index = tempList.indexOf(null);
        if (index < 0) {
            System.out.println(">>>>> Deck " + name + " is full");
        }
        return index;
    }

    public boolean addCard(Card card) {
        int index = getNextCardSlot();
        if (hasValidColors(card) && index > -1 && !hasReachedCardLimit(card)) {
            cardList[index] = card;
            return true;
        }
        System.out.println(">>>>> Unable to add card to deck");
        System.out.println();
        return false;
    }

    public int getTotalMana() {
        return Arrays.stream(cardList).mapToInt(Card::getManaValue).sum();
    }

    private boolean hasReachedCardLimit(Card card) {
        if (!(card.getClass().getSimpleName().equalsIgnoreCase("land") && card.getName().toLowerCase().contains("basic"))) {
            List<Card> tempList = new ArrayList<>(Arrays.asList(cardList));
            if (Collections.frequency(tempList, card) >= uniqueCardLimit) {
                System.out.println(">>>>> Deck " + name + " already contains " + uniqueCardLimit + " cards named " + card.getName());
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("DECK%nName = %s%nColors = %s%nCard List = %s%n", name, colors, Arrays.toString(cardList));
    }
}
