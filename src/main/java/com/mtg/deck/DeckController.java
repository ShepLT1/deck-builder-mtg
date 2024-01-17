package com.mtg.deck;

import com.mtg.admin.user.User;
import com.mtg.admin.user.UserDetailsServiceImpl;
import com.mtg.card.base.CardRepository;
import com.mtg.error.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/decks")
class DeckController {

    @Autowired
    DeckRepository deckRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    DeckService deckService;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("")
    Page<Deck> all(HttpServletRequest request, @RequestParam(required=false) String name, Pageable pageable) {

        User user = userDetailsService.getUserFromRequestCookies(request);

        if (name != null) {

            Page<Deck> decksByName = deckRepository.findByNameIgnoreCaseAndUser(name, user, pageable);
            if (!decksByName.hasContent()) {
                throw new EntityNotFoundException(user.getUsername(), name, "deck");
            }

            return decksByName;
        }

        return deckRepository.findAllByUser(user, pageable);
    }
    // end::get-aggregate-root[]

    @PostMapping("")
    Deck newDeck(HttpServletRequest request, @RequestBody DeckDto preDeck) {

        User user = userDetailsService.getUserFromRequestCookies(request);

        Deck newDeck = new Deck(preDeck.getName(), user, preDeck.getColors());
        if (preDeck.getCardList() != null) {
            deckService.updateCardList(newDeck, preDeck.getCardList());
        }

        return deckRepository.save(newDeck);
    }

    @GetMapping("/{id}")
    Deck one(HttpServletRequest request, @PathVariable Long id) {

        User user = userDetailsService.getUserFromRequestCookies(request);

        return deckRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException(user.getUsername(), id, "deck"));
    }

    @PatchMapping("/{id}")
    Deck updateDeck(HttpServletRequest request, @RequestBody DeckDto partialDeck, @PathVariable Long id) {

        User user = userDetailsService.getUserFromRequestCookies(request);

        Deck deck = deckRepository.findByIdAndUser(id, user).orElseThrow(() -> new EntityNotFoundException(user.getUsername(), id, "deck"));
        deck.setName(partialDeck.getName());
        deckService.updateCardList(deck, partialDeck.getCardList());

        return deckRepository.save(deck);
    }

    @PatchMapping("/{deck_id}/cards/{card_id}")
    Deck addOrRemoveCard(HttpServletRequest request, @PathVariable Long deck_id, @PathVariable Long card_id, @RequestBody Map<String, String> fields) {

        User user = userDetailsService.getUserFromRequestCookies(request);

        Deck deck = deckRepository.findByIdAndUser(deck_id, user).orElseThrow(() -> new EntityNotFoundException(user.getUsername(), deck_id, "deck"));

        return deckService.addOrRemoveCard(deck, card_id, fields);
    }

    @DeleteMapping("/{id}")
    void deleteDeck(HttpServletRequest request, @PathVariable Long id) {

        User user = userDetailsService.getUserFromRequestCookies(request);

        Deck deck = deckRepository.findByIdAndUser(id, user).orElseThrow(() -> new EntityNotFoundException(user.getUsername(), id, "deck"));

        deckRepository.delete(deck);
    }

}
