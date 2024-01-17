package com.mtg.deck;

import com.mtg.card.base.Card;
import com.mtg.card.base.CardRepository;
import com.mtg.error.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        if (cardIdList != null) {
            List<Long> longCardIds = new ArrayList<>();
            cardIdList.forEach(id -> longCardIds.add(Long.valueOf(id)));
            List<Card> cardsToPut = cardRepository.findByIdIn(longCardIds);
            deck.setCardList(new ArrayList<>());
            cardsToPut.forEach(card -> {
                int frequency = Collections.frequency(longCardIds, card.getId());
                for (int i = 0; i < frequency; i++) {
                    deck.addCard(card);
                }
            });
        }
    }

    public Deck addOrRemoveCard(Deck deck, Long card_id, Map<String, String> fields) {
        if (!fields.containsKey("action") || (!fields.get("action").equals("add") && !fields.get("action").equals("remove"))) {
            throw new IllegalArgumentException("request body must contain 'action' field with value equal to either 'add' or 'remove'");
        }
        Card card = cardRepository.findById(card_id).orElseThrow(() -> new EntityNotFoundException(card_id, "card"));
        if (fields.get("action").equals("add")) {
            deck.addCard(card);
        } else {
            deck.removeCard(card);
        }
        return deckRepository.save(deck);
    }

}
