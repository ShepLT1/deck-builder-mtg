package com.mtg.card.spell.battle;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards/battles")
public class BattleController {

    private final BattleRepository repository;

    BattleController(BattleRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    Page<Battle> all(Pageable pageable) {
        return repository.findAll(pageable);
    }

}
