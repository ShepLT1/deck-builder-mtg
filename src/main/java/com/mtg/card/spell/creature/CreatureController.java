package com.mtg.card.spell.creature;

import com.mtg.card.spell.CardType;
import com.mtg.card.spell.SpellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards/creatures")
class CreatureController {

    @Autowired
    SpellService spellService;
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
    Creature newCreature(@RequestBody CreatureDto newRawCreature) {
        Creature newCreature = new Creature();
        newCreature.setName(newRawCreature.getName());
        newCreature.setAbilities(newRawCreature.getAbilities());
        newCreature.setType(CardType.CREATURE);
        spellService.updateManaCost(newCreature, newRawCreature.getManaCost());
        newCreature.setPower(newRawCreature.getPower());
        newCreature.setToughness(newRawCreature.getToughness());
        newCreature.setAttributes(newRawCreature.getAttributes());
        newCreature.setDual(newRawCreature.getDual());
        return repository.save(newCreature);
    }

    @PutMapping("/{id}")
    Creature replaceCreature(@RequestBody CreatureDto newRawCreature, @PathVariable Long id) {
        return repository.findById(id)
                .map(creature -> {
                    creature.setName(newRawCreature.getName());
                    creature.setAbilities(newRawCreature.getAbilities());
                    creature.setType(CardType.CREATURE);
                    spellService.updateManaCost(creature, newRawCreature.getManaCost());
                    creature.setPower(newRawCreature.getPower());
                    creature.setToughness(newRawCreature.getToughness());
                    creature.setAttributes(newRawCreature.getAttributes());
                    creature.setDual(newRawCreature.getDual());
                    return repository.save(creature);
                })
                .orElseGet(() -> {
                    Creature creature = new Creature();
                    creature.setName(newRawCreature.getName());
                    creature.setAbilities(newRawCreature.getAbilities());
                    creature.setType(CardType.CREATURE);
                    spellService.updateManaCost(creature, newRawCreature.getManaCost());
                    creature.setPower(newRawCreature.getPower());
                    creature.setToughness(newRawCreature.getToughness());
                    creature.setAttributes(newRawCreature.getAttributes());
                    creature.setDual(newRawCreature.getDual());
                    return repository.save(creature);
                });
    }

}