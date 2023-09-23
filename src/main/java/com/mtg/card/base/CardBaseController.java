package com.mtg.card.base;

import com.mtg.deck.DeckRepository;
import com.mtg.deck.DeckService;
import com.mtg.error.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
class CardBaseController {

    @Autowired
    CardRepository cardRepository;

    @Autowired
    DeckRepository deckRepository;

    @Autowired
    DeckService deckService;


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("")
    Object all(@RequestParam(required=false) String name) {
        if (name == null) {
            return cardRepository.findAll();
        }
        return cardRepository.findByNameIgnoreCase(name).orElseThrow(() -> new EntityNotFoundException(name, "card"));
    }
    // end::get-aggregate-root[]

    @GetMapping("/{id}")
    Card one(@PathVariable Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "card"));
    }

    @DeleteMapping("/{id}")
    void deleteCard(@PathVariable Long id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, "card"));
        deckService.removeCardReferences(card);
        cardRepository.delete(card);
    }

}
