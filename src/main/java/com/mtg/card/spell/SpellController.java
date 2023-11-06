package com.mtg.card.spell;

import com.mtg.error.InvalidCardTypeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards/spells")
class SpellController {

    private final SpellRepository repository;

    SpellController(SpellRepository repository) {
        this.repository = repository;
    }


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("")
    Page<Spell> all(Pageable pageable) {
        return repository.findAll(pageable);
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