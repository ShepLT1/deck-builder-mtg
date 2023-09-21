package com.mtg.card.spell.creature;

import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    List<Creature> all() {
        return repository.findAll();
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