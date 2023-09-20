package com.mtg.card.base;

import com.mtg.EntityNotFoundException;

public class CardNotFoundException extends EntityNotFoundException {

    CardNotFoundException(Long id) {
        super(id, "card");
    }

    CardNotFoundException(String name) {
        super(name, "card");
    }

}
