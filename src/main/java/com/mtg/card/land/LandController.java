package com.mtg.card.land;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards/lands")
class LandController {

    private final LandRepository repository;

    LandController(LandRepository repository) {
        this.repository = repository;
    }


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("")
    List<Land> all() {
        return repository.findAll();
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
                    return repository.save(land);
                })
                .orElseGet(() -> {
                    return repository.save(newLand);
                });
    }

}