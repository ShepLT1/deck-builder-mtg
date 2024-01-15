package com.mtg.card.spell;

import com.mtg.error.InvalidCardTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards/spells")
class SpellController {

    @Autowired
    SpellService spellService;

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
    Spell newSpell(@RequestBody SpellDto newRawSpell) {
        if (newRawSpell.getType().equals(CardType.CREATURE)) {
            throw new InvalidCardTypeException(CardType.CREATURE, newRawSpell.getClass().getSimpleName());
        }
        if (newRawSpell.getType().equals(CardType.PLANESWALKER)) {
            throw new InvalidCardTypeException(CardType.PLANESWALKER, newRawSpell.getClass().getSimpleName());
        }
        Spell newSpell = new Spell();
        newSpell.setName(newRawSpell.getName());
        newSpell.setAbilities(newRawSpell.getAbilities());
        newSpell.setType(newRawSpell.getType());
        spellService.updateManaCost(newSpell, newRawSpell.getManaCost());
        return repository.save(newSpell);
    }

    @PutMapping("/{id}")
    Spell replaceSpell(@RequestBody SpellDto newRawSpell, @PathVariable Long id) {
        if (newRawSpell.getType().equals(CardType.CREATURE)) {
            throw new InvalidCardTypeException(CardType.CREATURE, newRawSpell.getClass().getSimpleName());
        }
        if (newRawSpell.getType().equals(CardType.PLANESWALKER)) {
            throw new InvalidCardTypeException(CardType.PLANESWALKER, newRawSpell.getClass().getSimpleName());
        }
        return repository.findById(id)
                .map(spell -> {
                    spell.setName(newRawSpell.getName());
                    spell.setAbilities(newRawSpell.getAbilities());
                    spellService.updateManaCost(spell, newRawSpell.getManaCost());
                    spell.setType(newRawSpell.getType());
                    return repository.save(spell);
                })
                .orElseGet(() -> {
                    Spell newSpell = new Spell();
                    newSpell.setName(newRawSpell.getName());
                    newSpell.setAbilities(newRawSpell.getAbilities());
                    newSpell.setType(newRawSpell.getType());
                    spellService.updateManaCost(newSpell, newRawSpell.getManaCost());
                    return repository.save(newSpell);
                });
    }

}