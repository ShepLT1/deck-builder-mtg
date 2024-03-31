package com.mtg.releaseSet;

import com.mtg.error.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sets")
public class ReleaseSetController {

    @Autowired
    ReleaseSetRepository releaseSetRepository;

    @GetMapping("")
    Page<ReleaseSet> all(@RequestParam(required=false) String name, Pageable pageable) {
        if (name != null) {
            Page<ReleaseSet> setByName = releaseSetRepository.findByNameContains(name, pageable);
            if (!setByName.hasContent()) {
                throw new EntityNotFoundException(name, "set");
            }
            return setByName;
        }
        return releaseSetRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    ReleaseSet one(@PathVariable Long id) {
        return releaseSetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "set"));
    }

}
