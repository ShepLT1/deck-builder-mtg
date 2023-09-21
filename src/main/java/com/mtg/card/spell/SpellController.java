package com.mtg.card.spell;

import com.mtg.error.InvalidCardTypeException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards/spells")
class SpellController {

    private final SpellRepository repository;

    SpellController(SpellRepository repository) {
        this.repository = repository;
    }


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("")
    List<Spell> all() {
        return repository.findAll();
    }
    // end::get-aggregate-root[]

    @PostMapping("")
    Spell newSpell(@RequestBody Spell newSpell) {
        if (newSpell.getType().equals(Spell.CardType.CREATURE)) {
            throw new InvalidCardTypeException(Spell.CardType.CREATURE, newSpell.getClass().getSimpleName());
        }
        return repository.save(newSpell);
    }

    @PutMapping("/{id}")
    Spell replaceSpell(@RequestBody Spell newSpell, @PathVariable Long id) {
        if (newSpell.getType().equals(Spell.CardType.CREATURE)) {
            throw new InvalidCardTypeException(Spell.CardType.CREATURE, newSpell.getClass().getSimpleName());
        }
        return repository.findById(id)
                .map(spell -> {
                    spell.setName(newSpell.getName());
                    spell.setAbilities(newSpell.getAbilities());
                    spell.setManaCost(newSpell.getManaCost());
                    spell.setType(newSpell.getType());
                    return repository.save(spell);
                })
                .orElseGet(() -> {
                    return repository.save(newSpell);
                });
    }

}