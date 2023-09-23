package com.mtg.deck;

import com.mtg.Color;

import java.util.List;

public class DeckDto {

    private String name;
    private List<Integer> cardList;

    private List<Color> colors;

    public DeckDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getCardList() {
        return cardList;
    }

    public void setCardList(List<Integer> cardList) {
        this.cardList = cardList;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }
}
