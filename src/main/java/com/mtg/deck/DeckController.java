package com.mtg.deck;

import com.mtg.error.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/decks")
class DeckController {

    @Autowired
    DeckRepository deckRepository;

    @Autowired
    DeckService deckService;


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("")
    Object all(@RequestParam(required=false) String name) {
        if (name == null) {
            return deckRepository.findAll();
        }
        return deckRepository.findByNameIgnoreCase(name).orElseThrow(() -> new EntityNotFoundException(name, "deck"));
    }
    // end::get-aggregate-root[]

    @PostMapping("")
    Deck newDeck(@RequestBody DeckDto preDeck) {
        Deck newDeck = new Deck(preDeck.getName(), preDeck.getColors());
        if (preDeck.getCardList() != null) {
            deckService.updateCardList(newDeck, preDeck.getCardList());
        }
        return deckRepository.save(newDeck);
    }

    @GetMapping("/{id}")
    Deck one(@PathVariable Long id) {
        return deckRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "deck"));
    }

    @PatchMapping("/{id}")
    Deck updateDeck(@RequestBody DeckDto partialDeck, @PathVariable Long id) {
        Deck deck = deckRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, "deck"));
        deck.setName(partialDeck.getName());
        deckService.updateCardList(deck, partialDeck.getCardList());
        return deckRepository.save(deck);
    }

    @DeleteMapping("/{id}")
    void deleteDeck(@PathVariable Long id) {
        deckRepository.delete(deckRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, "deck")));
    }

}
