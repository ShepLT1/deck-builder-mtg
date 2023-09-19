package com.mtg.card.spell;

import com.mtg.card.spell.Spell;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpellRepository extends JpaRepository<Spell, Long> {

}