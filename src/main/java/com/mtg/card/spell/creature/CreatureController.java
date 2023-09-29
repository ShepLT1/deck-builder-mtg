package com.mtg.card.spell.creature;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards/creatures")
class CreatureController {

    private final CreatureRepository repository;

    CreatureController(CreatureRepository repository) {
        this.repository = repository;
    }


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("")
    Page<Creature> all(Pageable pageable) {
        return repository.findAll(pageable);
    }
    // end::get-aggregate-root[]

    @PostMapping("")
    Creature newCreature(@RequestBody Creature newCreature) {
        return repository.save(newCreature);
    }

    @PutMapping("/{id}")
    Creature replaceCreature(@RequestBody Creature newCreature, @PathVariable Long id) {
        return repository.findById(id)
                .map(creature -> {
                    creature.setName(newCreature.getName());
                    creature.setAbilities(newCreature.getAbilities());
                    creature.setManaCost(newCreature.getManaCost());
                    creature.setPower(newCreature.getPower());
                    creature.setToughness(newCreature.getToughness());
                    creature.setAttributes(newCreature.getAttributes());
                    return repository.save(creature);
                })
                .orElseGet(() -> {
                    return repository.save(newCreature);
                });
    }

}