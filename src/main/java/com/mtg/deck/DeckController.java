package com.mtg.deck;

import com.mtg.error.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Deck> all(@RequestParam(required=false) String name, Pageable pageable) {
        if (name != null) {
            Page<Deck> decksByName = deckRepository.findByNameIgnoreCase(name, pageable);
            if (!decksByName.hasContent()) {
                throw new EntityNotFoundException(name, "deck");
            }
            return decksByName;
        }
        return deckRepository.findAll(pageable);
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
