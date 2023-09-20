package com.mtg.card.base;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
class CardBaseController {

    private final CardRepository repository;

    CardBaseController(CardRepository repository) {
        this.repository = repository;
    }


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("")
    Object all(@RequestParam(required=false) String name) {
        if (name == null) {
            return repository.findAll();
        }
        return repository.findByNameIgnoreCase(name).orElseThrow(() -> new CardNotFoundException(name));
    }
    // end::get-aggregate-root[]

    @GetMapping("/{id}")
    Card one(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    void deleteCard(@PathVariable Long id) {
        repository.delete(repository.findById(id).orElseThrow(() -> new CardNotFoundException(id)));
    }

}
