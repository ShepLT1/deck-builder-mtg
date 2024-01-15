package com.mtg.error;

import com.mtg.card.spell.CardType;

public class InvalidCardTypeException extends RuntimeException {

    public InvalidCardTypeException(CardType cardType, String className) {
        super("A " + className + " with card type " + cardType.toString() + " is not allowed. Instead use endpoint /cards/" + cardType.toString().toLowerCase() + "s for this card type.");
    }

}
