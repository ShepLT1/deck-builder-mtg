package com.mtg.card.land;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards/lands")
class LandController {

    private final LandRepository repository;

    LandController(LandRepository repository) {
        this.repository = repository;
    }


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("")
    Page<Land> all(Pageable pageable) {
        return repository.findAll(pageable);
    }
    // end::get-aggregate-root[]

    @PostMapping("")
    Land newLand(@RequestBody Land newLand) {
        return repository.save(newLand);
    }

    @PutMapping("/{id}")
    Land replaceLand(@RequestBody Land newLand, @PathVariable Long id) {

        return repository.findById(id)
                .map(land -> {
                    land.setName(newLand.getName());
                    land.setAbilities(newLand.getAbilities());
                    land.setColors(newLand.getColors());
                    land.setDual(newLand.getDual());
                    land.setRarity(newLand.getRarity());
                    land.setImageUri(newLand.getImageUri());
                    return repository.save(land);
                })
                .orElseGet(() -> {
                    return repository.save(newLand);
                });
    }

}