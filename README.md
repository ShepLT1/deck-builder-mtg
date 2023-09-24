# deck-builder-mtg

A simple Magic: The Gathering 60-card deck builder.

## Prerequisites

- [JDK 17](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://mkyong.com/maven/install-maven-on-mac-osx/)

## Getting Started

Navigate to the root directory of the repository and run the following command

    mvn spring-boot:run

## Endpoints

All requests must contain the following header(s)

    "X-API-KEY": "SuperSecretToken"

### Base URL

localhost:8080

### All Cards

#### GET /cards
- Returns list of all cards

#### GET /cards?name={card_name}
- Returns single card with matching name

#### GET /cards/{card_id}
- Returns single card with matching id

#### DELETE /cards/{card_id}
- Delete card with matching id

### Land Cards

#### GET /cards/lands
- Returns list of all land cards

#### POST /cards/lands
- Action
    - Creates new land card
- Request Body Fields
  - name (required)
    - String
  - colors (required)
    - Array of Colors
      - Acceptable Color values: White, Blue, Black, Red, Green, Any, Colorless
  - abilities
    - Array of Strings

Example Request Body

    {
        name: "Azorius Chancery",
        colors: ["White", "Blue"],
        abilities: ["Azorious Chancery comes into play tapped.", "When Azorius Chancery comes into play, return a land you control to its owner's hand.", "Tap to add 1 white and 1 blue mana to your mana pool."]
    }

#### PUT /cards/lands/{card_id}
- Action
  - Full update of land card with matching id, or creates new land card if no matching id found
- Request Body Fields
  - Same as POST /cards/lands, but without field requirements

### Spell Cards

#### GET /cards/spells
- Returns list of all spell cards

#### POST /cards/spells
- Action
  - Creates new spell card
- Request Body Fields
  - name (required)
    - String
  - abilities (required)
    - Array of Strings
  - manaCost (required)
    - Array of Colors
      - Acceptable Color values: White, Blue, Black, Red, Green, Any, Colorless
  - type (required)
    - Card Type (String)
      - Acceptable Card Type values: Instant, Sorcery, Artifact, Enchantment, Creature

Example Request Body

    {
        name: "Counterspell",
        abilities: ["Counter target spell."],
        manaCost: ["Blue", "Blue"],
        type: "Instant"
    }

#### PUT /cards/spells
- Action
  - Full update of spell card with matching id, or creates new spell card if no matching id found
- Request Body Fields
  - Same as POST /cards/spells, but without field requirements

### Creature Cards

#### GET /cards/creatures
- Returns list of all spell cards

#### POST /cards/creatures
- Action
  - Creates new creature card
- Request Body Fields
  - name (required)
    - String
  - manaCost (required)
    - Array of Colors
      - Acceptable Color values: White, Blue, Black, Red, Green, Any, Colorless
  - power (required)
    - Integer
  - toughness (required)
    - Integer
  - abilities
    - Array of Strings
  - attributes
    - Array of Creature Attributes
      - Acceptable Creature Attribute values: Deathtouch, Double_Strike, First_Strike, Flying, Haste, Lifelink, Menace, Reach, Trample, Vigilance

Example Request Body

    {
        name: "Atraxa, Praetor's Voice",
        manaCost: ["White", "Blue", "Black", "Green"],
        power: 4,
        toughness: 4,
        abilities: ["At the beginning of your end step, proliferate."],
        attributes: ["Flying", "Vigilance", "Deathtouch", "Lifelink"]
    }

#### PUT /cards/creatures
- Action
  - Full update of creature card with matching id, or creates new creature card if no matching id found
- Request Body Fields
  - Same as POST /cards/creatures, but without field requirements

### Decks

#### GET /decks
- Returns list of all decks

#### GET /decks?name={deck_name}
- Returns single deck with matching name

#### GET /decks/{deck_id}
- Returns single deck with matching id

#### POST /decks
- Action
  - Creates new deck
- Request Body Fields
  - name (required)
    - String
  - colors (required)
    - Array of Colors
      - Acceptable Color values: White, Blue, Black, Red, Green, Any, Colorless
  - cardList
    - Array of Card ids
    - Max length = 60
    - Note: Other than basic lands, each deck is allowed up to 4 instances of a single card

Example Request Body

    {
        name: "Azorius Control",
        colors: ["White", "Blue"],
        cardList: [1,1,1,1,2,2]
    }

#### PATCH /decks/{deck_id}
- Action
  - Partially updates deck with matching id
- Request Body fields
  - name
    - String
  - cardList
    - Array of Card ids
    - Max length = 60
    - Note: Other than basic lands, each deck is allowed up to 4 instances of a single card

#### DELETE /decks/{deck_id}
- Delete deck with matching id