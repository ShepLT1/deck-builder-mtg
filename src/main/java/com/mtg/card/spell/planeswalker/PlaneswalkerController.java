package com.mtg.card.spell.planeswalker;

import com.mtg.card.spell.CardType;
import com.mtg.card.spell.SpellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards/planeswalkers")
public class PlaneswalkerController {

    @Autowired
    SpellService spellService;
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
    Planeswalker newPlaneswalker(@RequestBody PlaneswalkerDto newRawPlaneswalker) {
        Planeswalker planeswalker = new Planeswalker();
        planeswalker.setName(newRawPlaneswalker.getName());
        planeswalker.setAbilities(newRawPlaneswalker.getAbilities());
        planeswalker.setType(CardType.PLANESWALKER);
        spellService.updateManaCost(planeswalker, newRawPlaneswalker.getManaCost());
        planeswalker.setLoyalty(newRawPlaneswalker.getLoyalty());
        planeswalker.setDual(newRawPlaneswalker.getDual());
        planeswalker.setRarity(newRawPlaneswalker.getRarity());
        planeswalker.setImageUri(newRawPlaneswalker.getImageUri());
        return repository.save(planeswalker);
    }

    @PutMapping("/{id}")
    Planeswalker replacePlaneswalker(@RequestBody PlaneswalkerDto newRawPlaneswalker, @PathVariable Long id) {
        return repository.findById(id)
                .map(planeswalker -> {
                    planeswalker.setName(newRawPlaneswalker.getName());
                    planeswalker.setAbilities(newRawPlaneswalker.getAbilities());
                    planeswalker.setType(CardType.PLANESWALKER);
                    spellService.updateManaCost(planeswalker, newRawPlaneswalker.getManaCost());
                    planeswalker.setLoyalty(newRawPlaneswalker.getLoyalty());
                    planeswalker.setDual(newRawPlaneswalker.getDual());
                    planeswalker.setRarity(newRawPlaneswalker.getRarity());
                    planeswalker.setImageUri(newRawPlaneswalker.getImageUri());
                    return repository.save(planeswalker);
                })
                .orElseGet(() -> {
                    Planeswalker newPlaneswalker = new Planeswalker();
                    newPlaneswalker.setName(newRawPlaneswalker.getName());
                    newPlaneswalker.setAbilities(newRawPlaneswalker.getAbilities());
                    newPlaneswalker.setType(CardType.PLANESWALKER);
                    spellService.updateManaCost(newPlaneswalker, newRawPlaneswalker.getManaCost());
                    newPlaneswalker.setLoyalty(newRawPlaneswalker.getLoyalty());
                    newPlaneswalker.setDual(newRawPlaneswalker.getDual());
                    newPlaneswalker.setRarity(newRawPlaneswalker.getRarity());
                    newPlaneswalker.setImageUri(newRawPlaneswalker.getImageUri());
                    return repository.save(newPlaneswalker);
                });
    }
    
}
