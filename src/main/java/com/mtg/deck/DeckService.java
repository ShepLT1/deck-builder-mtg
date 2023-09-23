package com.mtg.deck;

import com.mtg.card.base.Card;
import com.mtg.card.base.CardRepository;
import com.mtg.error.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeckService {

    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private CardRepository cardRepository;

    @Transactional
    public void removeCardReferences(Card card) {
        card.getDeckList().forEach(deck -> {
            List<Card> newCardList = new ArrayList<>(deck.getCardList().stream().filter(c -> !c.getId().equals(card.getId())).toList());
            Deck repoDeck = deckRepository.findById(deck.getId()).orElseThrow(() -> new EntityNotFoundException(card.getId(), "deck"));
            repoDeck.setCardList(newCardList);
            deckRepository.save(repoDeck);
        });
    }

    public void updateCardList(Deck deck, List<Integer> cardIdList) {
        List<Long> longCardIds = new ArrayList<>();
        cardIdList.forEach(id -> longCardIds.add(Long.valueOf(id)));
        List<Card> cardsToPut = cardRepository.findByIdIn(longCardIds);
        deck.setCardList(new ArrayList<>());
        cardsToPut.forEach(deck::addCard);
    }

}
