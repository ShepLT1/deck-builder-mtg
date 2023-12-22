package com.mtg.deck;

import com.mtg.admin.user.User;

import com.fasterxml.jackson.annotation.*;
import com.mtg.mana.Color;
import com.mtg.card.base.Card;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name="decks")
@JsonIgnoreProperties(value = {"cardLimit", "uniqueCardLimit"})
public class Deck {

    private @Id @GeneratedValue Long id;
    @Column(unique = true, name = "IX_NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable=false)
    private User user;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "deck_card",
            joinColumns = @JoinColumn(name = "deck_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id"))
    private List<Card> cardList;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int cardLimit;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int uniqueCardLimit;
    private List<Color> colors;

    public Deck() {
    }

    @JsonCreator
    public Deck(@JsonProperty(value = "name", required = true) String name, @JsonProperty(value = "user", required = true) User user, @JsonProperty(value = "colors", required = true) List<Color> colors) {
        this(name, user, 60, 4, colors);
    }

    public Deck(String name, User user, int cardLimit, int uniqueCardLimit, List<Color> colors) {
        this.name = name;
        this.user = user;
        this.cardList = new ArrayList<>();
        this.cardLimit = cardLimit;
        this.uniqueCardLimit = uniqueCardLimit;
        this.colors = colors;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }

    public int getCardLimit() {
        return cardLimit;
    }

    public int getUniqueCardLimit() {
        return uniqueCardLimit;
    }

    public List<Color> getColors() {
        return colors;
    }

    @PreRemove
    private void removeListsFromDeck() {
        getCardList().clear();
    }

    @JsonIgnore
    private boolean hasValidColors(Card card) {
        for (Color color : card.getColors()) {
            if (!getColors().contains(color)) {
                return false;
            }
        }
        return true;
    }

    @JsonIgnore
    public void addCard(Card card) {
        if (!hasValidColors(card)) {
            throw new IllegalArgumentException("Unable to add card " + card.getName() + " " + card.getColors() + " to deck " + name + " " + colors + ". Card contains colors not in deck.");
        }
        if (cardList.size() >= cardLimit) {
            throw new IllegalArgumentException("Unable to add card " + card.getName() + " to deck " + name + ". Deck is full.");
        }
        if (hasReachedCardLimit(card)) {
            throw new IllegalArgumentException("Unable to add card " + card.getName() + " to deck " + name + ". Deck already contains the maximum of " + uniqueCardLimit + " instances of this card.");
        }
        cardList.add(card);
    }

    @JsonIgnore
    public int getTotalMana() {
        return cardList.stream().mapToInt(Card::getManaValue).sum();
    }

    @JsonIgnore
    private boolean hasReachedCardLimit(Card card) {
        if (!(card.getClass().getSimpleName().equalsIgnoreCase("land") && card.getName().toLowerCase().contains("basic"))) {
            return Collections.frequency(cardList, card) >= uniqueCardLimit;
        }
        return false;
    }

    @JsonIgnore
    public void removeCard(Card card) {
        int lastIndex = this.cardList.lastIndexOf(card);
        if (lastIndex >= 0) {
            this.cardList.remove(lastIndex);
        }
    }

    @Override
    public String toString() {
        return String.format("DECK%nName = %s%nColors = %s%nCard List = %s%n", name, colors, cardList);
    }
}
