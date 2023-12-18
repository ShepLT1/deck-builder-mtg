package com.mtg.card.spell.planeswalker;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards/planeswalkers")
public class PlaneswalkerController {

    private final PlaneswalkerRepository repository;

    PlaneswalkerController(PlaneswalkerRepository repository) {
        this.repository = repository;
    }


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("")
    Page<Planeswalker> all(Pageable pageable) {
        return repository.findAll(pageable);
    }
    // end::get-aggregate-root[]

    @PostMapping("")
    Planeswalker newPlaneswalker(@RequestBody Planeswalker newPlaneswalker) {
        return repository.save(newPlaneswalker);
    }

    @PutMapping("/{id}")
    Planeswalker replacePlaneswalker(@RequestBody Planeswalker newPlaneswalker, @PathVariable Long id) {
        return repository.findById(id)
                .map(Planeswalker -> {
                    Planeswalker.setName(newPlaneswalker.getName());
                    Planeswalker.setAbilities(newPlaneswalker.getAbilities());
                    Planeswalker.setManaCost(newPlaneswalker.getManaCost());
                    Planeswalker.setLoyalty(newPlaneswalker.getLoyalty());
                    return repository.save(Planeswalker);
                })
                .orElseGet(() -> {
                    return repository.save(newPlaneswalker);
                });
    }
    
}
