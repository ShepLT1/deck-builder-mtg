package com.mtg.tasks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtg.admin.user.UserRepository;
import com.mtg.card.base.Card;
import com.mtg.card.base.CardRepository;
import com.mtg.card.base.Rarity;
import com.mtg.card.collectible.Collectible;
import com.mtg.card.collectible.CollectibleRepository;
import com.mtg.card.collectible.listing.Listing;
import com.mtg.card.collectible.listing.ListingRepository;
import com.mtg.card.land.Land;
import com.mtg.card.land.LandRepository;
import com.mtg.card.spell.CardType;
import com.mtg.card.spell.Spell;
import com.mtg.card.spell.SpellRepository;
import com.mtg.card.spell.battle.Battle;
import com.mtg.card.spell.battle.BattleRepository;
import com.mtg.card.spell.creature.Creature;
import com.mtg.card.spell.creature.CreatureRepository;
import com.mtg.card.spell.planeswalker.Planeswalker;
import com.mtg.card.spell.planeswalker.PlaneswalkerRepository;
import com.mtg.mana.*;
import com.mtg.releaseSet.ReleaseSet;
import com.mtg.releaseSet.ReleaseSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class Ingest implements ApplicationRunner {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private LandRepository landRepository;
    @Autowired
    private CreatureRepository creatureRepository;
    @Autowired
    private SpellRepository spellRepository;
    @Autowired
    private PlaneswalkerRepository planeswalkerRepository;
    @Autowired
    private BattleRepository battleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReleaseSetRepository releaseSetRepository;
    @Autowired
    private CollectibleRepository collectibleRepository;
    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    ManaRepository manaRepository;
    @Autowired
    private ManaSymbolRepository manaSymbolRepository;
    Map<String, ManaSymbol> manaMap;
    Map<String, Color> colorMap;

    public void run(final ApplicationArguments args) {
//        JSONParser parser = new JSONParser();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream jsonFile = new ClassPathResource("cards.json").getInputStream();
            JsonNode node = objectMapper.readTree(jsonFile);
//            InputStream in = getClass().getResourceAsStream("/cards.json");
//            assert in != null;
//            // TODO: use Jackson instead of JSON.simple as it can handle larger files more efficiently
//            JSONArray a = (JSONArray) parser.parse(new InputStreamReader(in));

            Set<String> cardTypesToSkip = new HashSet<>();
            cardTypesToSkip.add("Summon");
            cardTypesToSkip.add("Plane");
            cardTypesToSkip.add("Vanguard");
            cardTypesToSkip.add("Hero");
            cardTypesToSkip.add("Conspiracy");
            cardTypesToSkip.add("Phenomenon");
            cardTypesToSkip.add("Emblem");
            cardTypesToSkip.add("Stickers");
            cardTypesToSkip.add("Card");
            cardTypesToSkip.add("Token");
            cardTypesToSkip.add("Scheme");
            cardTypesToSkip.add("Dungeon");
            cardTypesToSkip.add("Eaturecray");
            cardTypesToSkip.add("Universewalker");

            manaMap = loadOrGetManaSymbols();

            colorMap = new HashMap<>();
            colorMap.put("W", Color.WHITE);
            colorMap.put("U", Color.BLUE);
            colorMap.put("B", Color.BLACK);
            colorMap.put("R", Color.RED);
            colorMap.put("G", Color.GREEN);


            for (int i = 0; i < node.size(); i++) {
                JsonNode card = (JsonNode) node.get(i);
                JsonNode cardFaces = card.get("card_faces");
                if (cardFaces == null) {
                    JsonNode cardType = card.get("type_line");
                    if (cardType == null) {
                        System.out.println(card.get("name").asText());
                    } else {
                        boolean skip = false;
                        for (String cardTypeToSkip : cardTypesToSkip) {
                            if (cardType.asText().contains(cardTypeToSkip)) {
                                skip = true;
                                break;
                            }
                        }
                        if (skip) {
                            continue;
                        }

                        Card savedCard = cardRepository.findByName(card.get("name").asText());
                        if (savedCard == null) {
                            savedCard = createNewCard(card);
                        }
                        if (savedCard == null) {
                            continue;
                        }
                        ReleaseSet releaseSet = releaseSetRepository.findByName(card.get("set_name").asText());
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        if (releaseSet == null) {
                            releaseSet = new ReleaseSet(card.get("set_name").asText(), dateFormatter.parse(card.get("released_at").asText()), null);
                            releaseSetRepository.save(releaseSet);
                        }
                        JsonNode prices = card.get("prices");
                        JsonNode finishes = card.get("finishes");
                        Set<String> finishSet = new HashSet<>();
                        for (int j = 0; j < finishes.size(); j++) {
                            finishSet.add(finishes.get(j).asText());
                        }
                        JsonNode promo_types = card.get("promo_types");
                        Collectible.PromoType promoType = null;
                        if (promo_types != null) {
                            if ((promo_types.get(0).asText()).equalsIgnoreCase("PROMOPACK") || (promo_types.get(0).asText()).equalsIgnoreCase("PRERELEASE")) {
                                promoType = Collectible.PromoType.valueOf((promo_types.get(0).asText()).toUpperCase());
                            };
                        }
                        if (finishSet.contains("nonfoil")) {
                            Collectible nonFoilCollectible = collectibleRepository.findByCardAndCollectorNumberAndFinishAndPromo(savedCard, card.get("collector_number").asText(), Collectible.Finish.NONFOIL, promoType);
                            if (nonFoilCollectible == null) {
                                nonFoilCollectible = createNewCollectible(savedCard, releaseSet, card, Collectible.Finish.NONFOIL, promoType);
                                if (!prices.get("usd").isNull()) {
                                    Listing newNonFoilListing = new Listing(nonFoilCollectible, prices.get("usd").asDouble());
                                    listingRepository.save(newNonFoilListing);
                                }
                            }
                        }
                        if (finishSet.contains("foil")) {
                            Collectible foilCollectible = collectibleRepository.findByCardAndCollectorNumberAndFinishAndPromo(savedCard, card.get("collector_number").asText(), Collectible.Finish.FOIL, promoType);
                            if (foilCollectible == null) {
                                Collectible newFoilCollectible = createNewCollectible(savedCard, releaseSet, card, Collectible.Finish.FOIL, promoType);
                                if (!prices.get("usd_foil").isNull()) {
                                    Listing newFoilListing = new Listing(newFoilCollectible, prices.get("usd_foil").asDouble());
                                    listingRepository.save(newFoilListing);
                                }
                            }
                        }
                        if (finishSet.contains("etched")) {
                            Collectible etchedCollectible = collectibleRepository.findByCardAndCollectorNumberAndFinishAndPromo(savedCard, card.get("collector_number").asText(), Collectible.Finish.ETCHED, promoType);
                            if (etchedCollectible == null) {
                                Collectible newEtchedCollectible = createNewCollectible(savedCard, releaseSet, card, Collectible.Finish.ETCHED, promoType);
                                if (!prices.get("usd_etched").isNull()) {
                                    Listing newEtchedListing = new Listing(newEtchedCollectible, prices.get("usd_etched").asDouble());
                                    listingRepository.save(newEtchedListing);
                                }
                            }
                        }
                    }
                } else {
                    boolean skip = false;
                    for (int j = 0; j < cardFaces.size(); j++) {
                        JsonNode cardFace = cardFaces.get(j);
                        JsonNode cardType = cardFace.get("type_line");
                        if (cardType == null) {
                            System.out.println(card.get("name"));
                        } else {
                            for (String cardTypeToSkip : cardTypesToSkip) {
                                if (cardType.asText().contains(cardTypeToSkip)) {
                                    skip = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (skip) {
                        continue;
                    }
                    JsonNode cardFace = cardFaces.get(0);
                    Card savedCard = cardRepository.findByName(cardFace.get("name").asText());
                    if (savedCard == null) {
                        savedCard = createNewDualCard(card);
                    }
                    if (savedCard == null) {
                        continue;
                    }
                    ReleaseSet releaseSet = releaseSetRepository.findByName(card.get("set_name").asText());
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    if (releaseSet == null) {
                        releaseSet = new ReleaseSet(card.get("set_name").asText(), dateFormatter.parse(card.get("released_at").asText()), null);
                        releaseSetRepository.save(releaseSet);
                    }
                    JsonNode prices = card.get("prices");
                    JsonNode finishes = card.get("finishes");
                    Set<String> finishSet = new HashSet<>();
                    for (int j = 0; j < finishes.size(); j++) {
                        finishSet.add(finishes.get(j).asText());
                    }
                    JsonNode promo_types = card.get("promo_types");
                    Collectible.PromoType promoType = null;
                    if (promo_types != null) {
                        if ((promo_types.get(0).asText()).equalsIgnoreCase("PROMOPACK") || (promo_types.get(0).asText()).equalsIgnoreCase("PRERELEASE")) {
                            promoType = Collectible.PromoType.valueOf((promo_types.get(0).asText()).toUpperCase());
                        };
                    }
                    if (finishSet.contains("nonfoil")) {
                        Collectible nonFoilCollectible = collectibleRepository.findByCardAndCollectorNumberAndFinishAndPromo(savedCard, card.get("collector_number").asText(), Collectible.Finish.NONFOIL, promoType);
                        if (nonFoilCollectible == null) {
                            nonFoilCollectible = createNewCollectible(savedCard, releaseSet, card, Collectible.Finish.NONFOIL, promoType);
                            if (!prices.get("usd").isNull()) {
                                Listing newNonFoilListing = new Listing(nonFoilCollectible, prices.get("usd").asDouble());
                                listingRepository.save(newNonFoilListing);
                            }
                        }
                    }
                    if (finishSet.contains("foil")) {
                        Collectible foilCollectible = collectibleRepository.findByCardAndCollectorNumberAndFinishAndPromo(savedCard, card.get("collector_number").asText(), Collectible.Finish.FOIL, promoType);
                        if (foilCollectible == null) {
                            Collectible newFoilCollectible = createNewCollectible(savedCard, releaseSet, card, Collectible.Finish.FOIL, promoType);
                            if (!prices.get("usd_foil").isNull()) {
                                Listing newFoilListing = new Listing(newFoilCollectible, prices.get("usd_foil").asDouble());
                                listingRepository.save(newFoilListing);
                            }
                        }
                    }
                    if (finishSet.contains("etched")) {
                        Collectible etchedCollectible = collectibleRepository.findByCardAndCollectorNumberAndFinishAndPromo(savedCard, card.get("collector_number").asText(), Collectible.Finish.ETCHED, promoType);
                        if (etchedCollectible == null) {
                            Collectible newEtchedCollectible = createNewCollectible(savedCard, releaseSet, card, Collectible.Finish.ETCHED, promoType);
                            if (!prices.get("usd_etched").isNull()) {
                                Listing newEtchedListing = new Listing(newEtchedCollectible, prices.get("usd_etched").asDouble());
                                listingRepository.save(newEtchedListing);
                            }
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    Collectible createNewCollectible(Card card, ReleaseSet set, JsonNode rawCard, Collectible.Finish finish, Collectible.PromoType promoType) {
        JsonNode imageUris = rawCard.get("image_uris");
        List<String> imageUrisList = new ArrayList<>();
        if (imageUris != null) {
            imageUrisList.add(imageUris.get("normal").asText());
        } else if (rawCard.get("card_faces") != null) {
            JsonNode cardFaces = rawCard.get("card_faces");
            for (int i = 0; i < cardFaces.size(); i++) {
                JsonNode cardFace = cardFaces.get(i);
                imageUris = cardFace.get("image_uris");
                if (imageUris != null) {
                    imageUrisList.add(imageUris.get("normal").asText());
                }
            }
        } else {
            System.out.printf("No image uris: %s%n", rawCard.get("name"));
        }
        Collectible collectible = new Collectible(card, new ArrayList<>(), rawCard.get("collector_number").asText(), set, finish, promoType, imageUrisList);
        return collectibleRepository.save(collectible);
    }

    Card createNewCard(JsonNode card) {
        try {
            JsonNode cardTypeNode = card.get("type_line");
            if (cardTypeNode == null) {
                System.out.printf("No card type: %s%n", card.get("name"));
            } else {
                String cardType = cardTypeNode.asText();
                if (cardType.contains("Creature")) {
                    return createNewCreature(card);
                } else if (cardType.contains("Instant")) {
                    return createNewInstant(card);
                } else if (cardType.contains("Sorcery")) {
                    return createNewSorcery(card);
                } else if (cardType.contains("Artifact")) {
                    return createNewArtifact(card);
                } else if (cardType.contains("Enchantment")) {
                    return createNewEnchantment(card);
                } else if (cardType.contains("Planeswalker")) {
                    return createNewPlaneswalker(card);
                } else if (cardType.contains("Land")) {
                    return createNewLand(card);
                } else if (cardType.contains("Battle")) {
                    return createNewBattle(card);
                } else {
                    System.out.printf("Invalid card type %s: %s%n", cardType, card.get("name"));
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Could not create card " + card.get("name"));
            System.out.println(e.getMessage());
        }
        return null;
    }

    Card createNewDualCard(JsonNode cardWithFaces) {
        JsonNode cardFaces = cardWithFaces.get("card_faces");
        Card newCard = createNewCard(cardFaces.get(0));
        if (newCard == null) {
            return null;
        }
        if (newCard.getImageUri() == null) {
            JsonNode imageUris = cardWithFaces.get("image_uris");
            if (imageUris == null) {
                System.out.printf("No image uris: %s%n", cardWithFaces.get("name"));
            } else {
                newCard.setImageUri(imageUris.get("normal").asText());
            }
        }
        Card dualCard = createNewCard(cardFaces.get(1));
        newCard.setDual(dualCard);
        if (dualCard == null) {
            return null;
        }
        newCard.setRarity(cardWithFaces.get("rarity") == null ? null : Rarity.valueOf((cardWithFaces.get("rarity").asText()).toUpperCase()));
        dualCard.setRarity(cardWithFaces.get("rarity") == null ? null : Rarity.valueOf((cardWithFaces.get("rarity").asText()).toUpperCase()));
        cardRepository.save(dualCard);
        System.out.println("dual card processed: " + newCard.getName() + " / " + dualCard.getName());
        return cardRepository.save(newCard);
    }

    Creature createNewCreature(JsonNode card) {
        Creature creature = new Creature();
        creature.setType(CardType.CREATURE);
        creature.setPower(card.get("power").asText());
        creature.setToughness(card.get("toughness").asText());
        updateSpell(card, creature);
        return creatureRepository.save(creature);
    }

    Spell createNewInstant(JsonNode card) {
        Spell instant = new Spell();
        instant.setType(CardType.INSTANT);
        updateSpell(card, instant);
        return spellRepository.save(instant);
    }

    Spell createNewSorcery(JsonNode card) {
        Spell sorcery = new Spell();
        sorcery.setType(CardType.SORCERY);
        updateSpell(card, sorcery);
        return spellRepository.save(sorcery);
    }

    Spell createNewArtifact(JsonNode card) {
        Spell artifact = new Spell();
        artifact.setType(CardType.ARTIFACT);
        updateSpell(card, artifact);
        return spellRepository.save(artifact);
    }

    Spell createNewEnchantment(JsonNode card) {
        Spell enchantment = new Spell();
        enchantment.setType(CardType.ENCHANTMENT);
        updateSpell(card, enchantment);
        return spellRepository.save(enchantment);
    }

    Planeswalker createNewPlaneswalker(JsonNode card) {
        Planeswalker planeswalker = new Planeswalker();
        planeswalker.setType(CardType.PLANESWALKER);
        planeswalker.setLoyalty(card.get("loyalty").asInt());
        updateSpell(card, planeswalker);
        return planeswalkerRepository.save(planeswalker);
    }

    Land createNewLand(JsonNode card) {
        Land land = new Land();
        land.setName(card.get("name").asText());
        land.setAbilities(card.get("oracle_text").asText());
        JsonNode rarity = card.get("rarity");
        if (rarity != null) {
            String strRarity = card.get("rarity").asText();
            if (!strRarity.equalsIgnoreCase("SPECIAL")) {
                land.setRarity(Rarity.valueOf(strRarity.toUpperCase()));
            }
        }
        JsonNode imageUris = card.get("image_uris");
        if (imageUris != null) {
            land.setImageUri(imageUris.get("normal").asText());
        }
        land.setColors(convertColors(card.get("color_identity")));
        return landRepository.save(land);
    }

    Battle createNewBattle(JsonNode card) {
        Battle battle = new Battle();
        battle.setType(CardType.BATTLE);
        battle.setDefense(card.get("defense").asInt());
        updateSpell(card, battle);
        return battleRepository.save(battle);
    }

    void updateSpell(JsonNode card, Spell spell) {
        spell.setName(card.get("name").asText());
        spell.setAbilities(card.get("oracle_text").asText());
        spell.setManaCost(convertManaCost(card.get("mana_cost").asText()));
        JsonNode rarity = card.get("rarity");
        if (rarity != null) {
            String strRarity = card.get("rarity").asText();
            if (!strRarity.equalsIgnoreCase("SPECIAL")) {
                spell.setRarity(Rarity.valueOf(strRarity.toUpperCase()));
            }
        }
        JsonNode imageUris = card.get("image_uris");
        if (imageUris != null) {
            spell.setImageUri(imageUris.get("normal").asText());
        }
    }

    // TODO: finish conversion methods and test card creation
    List<ManaSymbol> convertManaCost(String strManaCost) {
        List<ManaSymbol> manaCost = new ArrayList<>();
        int open = 0;
        int close = 0;
        for (int i = 0; i < strManaCost.length(); i++) {
            if (strManaCost.charAt(i) == '{') {
                open = i;
            } else if (strManaCost.charAt(i) == '}') {
                close = i;
                String manaString = strManaCost.substring(open + 1, close);
                if (manaMap.containsKey(manaString)) {
                    manaCost.add(manaMap.get(manaString));
                } else {
                    System.out.printf("Invalid mana symbol: %s%n", manaString);
                }
            }
        }
        return manaCost;
    }

    List<Color> convertColors(JsonNode colors) {
        List<Color> colorList = new ArrayList<>();
        if (colors != null) {
            for (int i = 0; i < colors.size(); i++) {
                colorList.add(colorMap.get(colors.get(i).asText()));
            }
        }
        return colorList;
    }

    Map<String, ManaSymbol> loadOrGetManaSymbols() {
        Map<String, ManaSymbol> manaMap = new HashMap<>();
        if (manaRepository.count() != 0) {
            ManaSymbol whiteManaSymbol = manaSymbolRepository.findByName("WHITE").orElse(null);
            manaMap.put("W", whiteManaSymbol);
            ManaSymbol blueManaSymbol = manaSymbolRepository.findByName("BLUE").orElse(null);
            manaMap.put("U", blueManaSymbol);
            ManaSymbol blackManaSymbol = manaSymbolRepository.findByName("BLACK").orElse(null);
            manaMap.put("B", blackManaSymbol);
            ManaSymbol redManaSymbol = manaSymbolRepository.findByName("RED").orElse(null);
            manaMap.put("R", redManaSymbol);
            ManaSymbol greenManaSymbol = manaSymbolRepository.findByName("GREEN").orElse(null);
            manaMap.put("G", greenManaSymbol);
            ManaSymbol colorlessManaSymbol = manaSymbolRepository.findByName("COLORLESS").orElse(null);
            manaMap.put("C", colorlessManaSymbol);
            ManaSymbol whitePhyrexianManaSymbol = manaSymbolRepository.findByName("WHITE PHYREXIAN").orElse(null);
            manaMap.put("W/P", whitePhyrexianManaSymbol);
            ManaSymbol bluePhyrexianManaSymbol = manaSymbolRepository.findByName("BLUE PHYREXIAN").orElse(null);
            manaMap.put("U/P", bluePhyrexianManaSymbol);
            ManaSymbol blackPhyrexianManaSymbol = manaSymbolRepository.findByName("BLACK PHYREXIAN").orElse(null);
            manaMap.put("B/P", blackPhyrexianManaSymbol);
            ManaSymbol redPhyrexianManaSymbol = manaSymbolRepository.findByName("RED PHYREXIAN").orElse(null);
            manaMap.put("R/P", redPhyrexianManaSymbol);
            ManaSymbol greenPhyrexianManaSymbol = manaSymbolRepository.findByName("GREEN PHYREXIAN").orElse(null);
            manaMap.put("G/P", greenPhyrexianManaSymbol);
            ManaSymbol genericManaXSymbol = manaSymbolRepository.findByName("GENERIC X").orElse(null);
            manaMap.put("X", genericManaXSymbol);
            ManaSymbol genericMana0Symbol = manaSymbolRepository.findByName("GENERIC 0").orElse(null);
            manaMap.put("0", genericMana0Symbol);
            ManaSymbol genericMana1Symbol = manaSymbolRepository.findByName("GENERIC 1").orElse(null);
            manaMap.put("1", genericMana1Symbol);
            ManaSymbol genericMana2Symbol = manaSymbolRepository.findByName("GENERIC 2").orElse(null);
            manaMap.put("2", genericMana2Symbol);
            ManaSymbol genericMana3Symbol = manaSymbolRepository.findByName("GENERIC 3").orElse(null);
            manaMap.put("3", genericMana3Symbol);
            ManaSymbol genericMana4Symbol = manaSymbolRepository.findByName("GENERIC 4").orElse(null);
            manaMap.put("4", genericMana4Symbol);
            ManaSymbol genericMana5Symbol = manaSymbolRepository.findByName("GENERIC 5").orElse(null);
            manaMap.put("5", genericMana5Symbol);
            ManaSymbol genericMana6Symbol = manaSymbolRepository.findByName("GENERIC 6").orElse(null);
            manaMap.put("6", genericMana6Symbol);
            ManaSymbol genericMana7Symbol = manaSymbolRepository.findByName("GENERIC 7").orElse(null);
            manaMap.put("7", genericMana7Symbol);
            ManaSymbol genericMana8Symbol = manaSymbolRepository.findByName("GENERIC 8").orElse(null);
            manaMap.put("8", genericMana8Symbol);
            ManaSymbol genericMana9Symbol = manaSymbolRepository.findByName("GENERIC 9").orElse(null);
            manaMap.put("9", genericMana9Symbol);
            ManaSymbol genericMana10Symbol = manaSymbolRepository.findByName("GENERIC 10").orElse(null);
            manaMap.put("10", genericMana10Symbol);
            ManaSymbol genericMana11Symbol = manaSymbolRepository.findByName("GENERIC 11").orElse(null);
            manaMap.put("11", genericMana11Symbol);
            ManaSymbol genericMana12Symbol = manaSymbolRepository.findByName("GENERIC 12").orElse(null);
            manaMap.put("12", genericMana12Symbol);
            ManaSymbol genericMana13Symbol = manaSymbolRepository.findByName("GENERIC 13").orElse(null);
            manaMap.put("13", genericMana13Symbol);
            ManaSymbol genericMana14Symbol = manaSymbolRepository.findByName("GENERIC 14").orElse(null);
            manaMap.put("14", genericMana14Symbol);
            ManaSymbol genericMana15Symbol = manaSymbolRepository.findByName("GENERIC 15").orElse(null);
            manaMap.put("15", genericMana15Symbol);
            ManaSymbol genericMana16Symbol = manaSymbolRepository.findByName("GENERIC 16").orElse(null);
            manaMap.put("16", genericMana16Symbol);
            ManaSymbol genericMana1MSymbol = manaSymbolRepository.findByName("GENERIC 1000000").orElse(null);
            manaMap.put("1000000", genericMana1MSymbol);
            ManaSymbol whiteBlueManaSymbol = manaSymbolRepository.findByName("WHITE / BLUE").orElse(null);
            manaMap.put("W/U", whiteBlueManaSymbol);
            ManaSymbol whiteBlackManaSymbol = manaSymbolRepository.findByName("WHITE / BLACK").orElse(null);
            manaMap.put("W/B", whiteBlackManaSymbol);
            ManaSymbol blueBlackManaSymbol = manaSymbolRepository.findByName("BLUE / BLACK").orElse(null);
            manaMap.put("U/B", blueBlackManaSymbol);
            ManaSymbol blueRedManaSymbol = manaSymbolRepository.findByName("BLUE / RED").orElse(null);
            manaMap.put("U/R", blueRedManaSymbol);
            ManaSymbol blackRedManaSymbol = manaSymbolRepository.findByName("BLACK / RED").orElse(null);
            manaMap.put("B/R", blackRedManaSymbol);
            ManaSymbol blackGreenManaSymbol = manaSymbolRepository.findByName("BLACK / GREEN").orElse(null);
            manaMap.put("B/G", blackGreenManaSymbol);
            ManaSymbol redGreenManaSymbol = manaSymbolRepository.findByName("RED / GREEN").orElse(null);
            manaMap.put("R/G", redGreenManaSymbol);
            ManaSymbol redWhiteManaSymbol = manaSymbolRepository.findByName("RED / WHITE").orElse(null);
            manaMap.put("R/W", redWhiteManaSymbol);
            ManaSymbol greenWhiteManaSymbol = manaSymbolRepository.findByName("GREEN / WHITE").orElse(null);
            manaMap.put("G/W", greenWhiteManaSymbol);
            ManaSymbol greenBlueManaSymbol = manaSymbolRepository.findByName("GREEN / BLUE").orElse(null);
            manaMap.put("G/U", greenBlueManaSymbol);
            ManaSymbol whitePhyrexianBlueManaSymbol = manaSymbolRepository.findByName("WHITE PHYREXIAN / BLUE PHYREXIAN").orElse(null);
            manaMap.put("W/U/P", whitePhyrexianBlueManaSymbol);
            ManaSymbol whitePhyrexianBlackManaSymbol = manaSymbolRepository.findByName("WHITE PHYREXIAN / BLACK PHYREXIAN").orElse(null);
            manaMap.put("W/B/P", whitePhyrexianBlackManaSymbol);
            ManaSymbol bluePhyrexianBlackManaSymbol = manaSymbolRepository.findByName("BLUE PHYREXIAN / BLACK PHYREXIAN").orElse(null);
            manaMap.put("U/B/P", bluePhyrexianBlackManaSymbol);
            ManaSymbol bluePhyrexianRedManaSymbol = manaSymbolRepository.findByName("BLUE PHYREXIAN / RED PHYREXIAN").orElse(null);
            manaMap.put("U/R/P", bluePhyrexianRedManaSymbol);
            ManaSymbol blackPhyrexianRedManaSymbol = manaSymbolRepository.findByName("BLACK PHYREXIAN / RED PHYREXIAN").orElse(null);
            manaMap.put("B/R/P", blackPhyrexianRedManaSymbol);
            ManaSymbol blackPhyrexianGreenManaSymbol = manaSymbolRepository.findByName("BLACK PHYREXIAN / GREEN PHYREXIAN").orElse(null);
            manaMap.put("B/G/P", blackPhyrexianGreenManaSymbol);
            ManaSymbol redPhyrexianGreenManaSymbol = manaSymbolRepository.findByName("RED PHYREXIAN / GREEN PHYREXIAN").orElse(null);
            manaMap.put("R/G/P", redPhyrexianGreenManaSymbol);
            ManaSymbol redPhyrexianWhiteManaSymbol = manaSymbolRepository.findByName("RED PHYREXIAN / WHITE PHYREXIAN").orElse(null);
            manaMap.put("R/W/P", redPhyrexianWhiteManaSymbol);
            ManaSymbol greenPhyrexianWhiteManaSymbol = manaSymbolRepository.findByName("GREEN PHYREXIAN / WHITE PHYREXIAN").orElse(null);
            manaMap.put("G/W/P", greenPhyrexianWhiteManaSymbol);
            ManaSymbol greenPhyrexianBlueManaSymbol = manaSymbolRepository.findByName("GREEN PHYREXIAN / BLUE PHYREXIAN").orElse(null);
            manaMap.put("G/U/P", greenPhyrexianBlueManaSymbol);
            ManaSymbol genericTwoWhiteManaSymbol = manaSymbolRepository.findByName("GENERIC 2 / WHITE").orElse(null);
            manaMap.put("2/W", genericTwoWhiteManaSymbol);
            ManaSymbol genericTwoBlueManaSymbol = manaSymbolRepository.findByName("GENERIC 2 / BLUE").orElse(null);
            manaMap.put("2/U", genericTwoBlueManaSymbol);
            ManaSymbol genericTwoBlackManaSymbol = manaSymbolRepository.findByName("GENERIC 2 / BLACK").orElse(null);
            manaMap.put("2/B", genericTwoBlackManaSymbol);
            ManaSymbol genericTwoRedManaSymbol = manaSymbolRepository.findByName("GENERIC 2 / RED").orElse(null);
            manaMap.put("2/R", genericTwoRedManaSymbol);
            ManaSymbol genericTwoGreenManaSymbol = manaSymbolRepository.findByName("GENERIC 2 / GREEN").orElse(null);
            manaMap.put("2/G", genericTwoGreenManaSymbol);
            ManaSymbol snowManaSymbol = manaSymbolRepository.findByName("SNOW").orElse(null);
            manaMap.put("S", snowManaSymbol);
        } else {
            Mana whiteMana = new Mana(ManaType.COLORED, Color.WHITE, "1");
            manaRepository.save(whiteMana);
            ManaSymbol whiteManaSymbol = new ManaSymbol(new ArrayList<>(List.of(whiteMana)));
            manaSymbolRepository.save(whiteManaSymbol);
            manaMap.put("W", whiteManaSymbol);
            final Mana blueMana = new Mana(ManaType.COLORED, Color.BLUE, "1");
            manaRepository.save(blueMana);
            final ManaSymbol blueManaSymbol = new ManaSymbol(new ArrayList<>(List.of(blueMana)));
            manaSymbolRepository.save(blueManaSymbol);
            manaMap.put("U", blueManaSymbol);
            final Mana blackMana = new Mana(ManaType.COLORED, Color.BLACK, "1");
            manaRepository.save(blackMana);
            final ManaSymbol blackManaSymbol = new ManaSymbol(new ArrayList<>(List.of(blackMana)));
            manaSymbolRepository.save(blackManaSymbol);
            manaMap.put("B", blackManaSymbol);
            final Mana redMana = new Mana(ManaType.COLORED, Color.RED, "1");
            manaRepository.save(redMana);
            final ManaSymbol redManaSymbol = new ManaSymbol(new ArrayList<>(List.of(redMana)));
            manaSymbolRepository.save(redManaSymbol);
            manaMap.put("R", redManaSymbol);
            final Mana greenMana = new Mana(ManaType.COLORED, Color.GREEN, "1");
            manaRepository.save(greenMana);
            final ManaSymbol greenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(greenMana)));
            manaSymbolRepository.save(greenManaSymbol);
            manaMap.put("G", greenManaSymbol);
            final Mana colorlessMana = new Mana(ManaType.COLORLESS, Color.COLORLESS,"1");
            manaRepository.save(colorlessMana);
            final ManaSymbol colorlessManaSymbol = new ManaSymbol(new ArrayList<>(List.of(colorlessMana)));
            manaSymbolRepository.save(colorlessManaSymbol);
            manaMap.put("C", colorlessManaSymbol);
            final Mana phyrexianWhiteMana = new Mana(ManaType.PHYREXIAN, Color.WHITE, "1");
            manaRepository.save(phyrexianWhiteMana);
            final ManaSymbol phyrexianWhiteManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianWhiteMana)));
            manaSymbolRepository.save(phyrexianWhiteManaSymbol);
            manaMap.put("W/P", phyrexianWhiteManaSymbol);
            final Mana phyrexianBlueMana = new Mana(ManaType.PHYREXIAN, Color.BLUE, "1");
            manaRepository.save(phyrexianBlueMana);
            final ManaSymbol phyrexianBlueManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianBlueMana)));
            manaSymbolRepository.save(phyrexianBlueManaSymbol);
            manaMap.put("U/P", phyrexianBlueManaSymbol);
            final Mana phyrexianBlackMana = new Mana(ManaType.PHYREXIAN, Color.BLACK, "1");
            manaRepository.save(phyrexianBlackMana);
            final ManaSymbol phyrexianBlackManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianBlackMana)));
            manaSymbolRepository.save(phyrexianBlackManaSymbol);
            manaMap.put("B/P", phyrexianBlackManaSymbol);
            final Mana phyrexianRedMana = new Mana(ManaType.PHYREXIAN, Color.RED, "1");
            manaRepository.save(phyrexianRedMana);
            final ManaSymbol phyrexianRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianRedMana)));
            manaSymbolRepository.save(phyrexianRedManaSymbol);
            manaMap.put("R/P", phyrexianRedManaSymbol);
            final Mana phyrexianGreenMana = new Mana(ManaType.PHYREXIAN, Color.GREEN, "1");
            manaRepository.save(phyrexianGreenMana);
            final ManaSymbol phyrexianGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianGreenMana)));
            manaSymbolRepository.save(phyrexianGreenManaSymbol);
            manaMap.put("G/P", phyrexianGreenManaSymbol);
            final Mana genericManaX = new Mana(ManaType.GENERIC, Color.COLORLESS, "X");
            manaRepository.save(genericManaX);
            final ManaSymbol genericManaXSymbol = new ManaSymbol(new ArrayList<>(List.of(genericManaX)));
            manaSymbolRepository.save(genericManaXSymbol);
            manaMap.put("X", genericManaXSymbol);
            final Mana genericMana0 = new Mana(ManaType.GENERIC, Color.COLORLESS, "0");
            manaRepository.save(genericMana0);
            final ManaSymbol genericMana0Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana0)));
            manaSymbolRepository.save(genericMana0Symbol);
            manaMap.put("0", genericMana0Symbol);
            final Mana genericMana1 = new Mana(ManaType.GENERIC, Color.COLORLESS, "1");
            manaRepository.save(genericMana1);
            final ManaSymbol genericMana1Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana1)));
            manaSymbolRepository.save(genericMana1Symbol);
            manaMap.put("1", genericMana1Symbol);
            final Mana genericMana2 = new Mana(ManaType.GENERIC, Color.COLORLESS, "2");
            manaRepository.save(genericMana2);
            final ManaSymbol genericMana2Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana2)));
            manaSymbolRepository.save(genericMana2Symbol);
            manaMap.put("2", genericMana2Symbol);
            final Mana genericMana3 = new Mana(ManaType.GENERIC, Color.COLORLESS, "3");
            manaRepository.save(genericMana3);
            final ManaSymbol genericMana3Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana3)));
            manaSymbolRepository.save(genericMana3Symbol);
            manaMap.put("3", genericMana3Symbol);
            final Mana genericMana4 = new Mana(ManaType.GENERIC, Color.COLORLESS, "4");
            manaRepository.save(genericMana4);
            final ManaSymbol genericMana4Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana4)));
            manaSymbolRepository.save(genericMana4Symbol);
            manaMap.put("4", genericMana4Symbol);
            final Mana genericMana5 = new Mana(ManaType.GENERIC, Color.COLORLESS, "5");
            manaRepository.save(genericMana5);
            final ManaSymbol genericMana5Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana5)));
            manaSymbolRepository.save(genericMana5Symbol);
            manaMap.put("5", genericMana5Symbol);
            final Mana genericMana6 = new Mana(ManaType.GENERIC, Color.COLORLESS, "6");
            manaRepository.save(genericMana6);
            final ManaSymbol genericMana6Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana6)));
            manaSymbolRepository.save(genericMana6Symbol);
            manaMap.put("6", genericMana6Symbol);
            final Mana genericMana7 = new Mana(ManaType.GENERIC, Color.COLORLESS, "7");
            manaRepository.save(genericMana7);
            final ManaSymbol genericMana7Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana7)));
            manaSymbolRepository.save(genericMana7Symbol);
            manaMap.put("7", genericMana7Symbol);
            final Mana genericMana8 = new Mana(ManaType.GENERIC, Color.COLORLESS, "8");
            manaRepository.save(genericMana8);
            final ManaSymbol genericMana8Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana8)));
            manaSymbolRepository.save(genericMana8Symbol);
            manaMap.put("8", genericMana8Symbol);
            final Mana genericMana9 = new Mana(ManaType.GENERIC, Color.COLORLESS, "9");
            manaRepository.save(genericMana9);
            final ManaSymbol genericMana9Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana9)));
            manaSymbolRepository.save(genericMana9Symbol);
            manaMap.put("9", genericMana9Symbol);
            final Mana genericMana10 = new Mana(ManaType.GENERIC, Color.COLORLESS, "10");
            manaRepository.save(genericMana10);
            final ManaSymbol genericMana10Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana10)));
            manaSymbolRepository.save(genericMana10Symbol);
            manaMap.put("10", genericMana10Symbol);
            final Mana genericMana11 = new Mana(ManaType.GENERIC, Color.COLORLESS, "11");
            manaRepository.save(genericMana11);
            final ManaSymbol genericMana11Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana11)));
            manaSymbolRepository.save(genericMana11Symbol);
            manaMap.put("11", genericMana11Symbol);
            final Mana genericMana12 = new Mana(ManaType.GENERIC, Color.COLORLESS, "12");
            manaRepository.save(genericMana12);
            final ManaSymbol genericMana12Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana12)));
            manaSymbolRepository.save(genericMana12Symbol);
            manaMap.put("12", genericMana12Symbol);
            final Mana genericMana13 = new Mana(ManaType.GENERIC, Color.COLORLESS, "13");
            manaRepository.save(genericMana13);
            final ManaSymbol genericMana13Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana13)));
            manaSymbolRepository.save(genericMana13Symbol);
            manaMap.put("13", genericMana13Symbol);
            final Mana genericMana14 = new Mana(ManaType.GENERIC, Color.COLORLESS, "14");
            manaRepository.save(genericMana14);
            final ManaSymbol genericMana14Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana14)));
            manaSymbolRepository.save(genericMana14Symbol);
            manaMap.put("14", genericMana14Symbol);
            final Mana genericMana15 = new Mana(ManaType.GENERIC, Color.COLORLESS, "15");
            manaRepository.save(genericMana15);
            final ManaSymbol genericMana15Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana15)));
            manaSymbolRepository.save(genericMana15Symbol);
            manaMap.put("15", genericMana15Symbol);
            final Mana genericMana16 = new Mana(ManaType.GENERIC, Color.COLORLESS, "16");
            manaRepository.save(genericMana16);
            final ManaSymbol genericMana16Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana16)));
            manaSymbolRepository.save(genericMana16Symbol);
            manaMap.put("16", genericMana16Symbol);
            final Mana genericMana1M = new Mana(ManaType.GENERIC, Color.COLORLESS, "1000000");
            manaRepository.save(genericMana1M);
            final ManaSymbol genericMana1MSymbol = new ManaSymbol(new ArrayList<>(List.of(genericMana1M)));
            manaSymbolRepository.save(genericMana1MSymbol);
            manaMap.put("1000000", genericMana1MSymbol);
            final ManaSymbol whiteBlueManaSymbol = new ManaSymbol(new ArrayList<>(List.of(whiteMana, blueMana)));
            manaSymbolRepository.save(whiteBlueManaSymbol);
            manaMap.put("W/U", whiteBlueManaSymbol);
            final ManaSymbol whiteBlackManaSymbol = new ManaSymbol(new ArrayList<>(List.of(whiteMana, blackMana)));
            manaSymbolRepository.save(whiteBlackManaSymbol);
            manaMap.put("W/B", whiteBlackManaSymbol);
            final ManaSymbol blueBlackManaSymbol = new ManaSymbol(new ArrayList<>(List.of(blueMana, blackMana)));
            manaSymbolRepository.save(blueBlackManaSymbol);
            manaMap.put("U/B", blueBlackManaSymbol);
            final ManaSymbol blueRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(blueMana, redMana)));
            manaSymbolRepository.save(blueRedManaSymbol);
            manaMap.put("U/R", blueRedManaSymbol);
            final ManaSymbol blackRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(blackMana, redMana)));
            manaSymbolRepository.save(blackRedManaSymbol);
            manaMap.put("B/R", blackRedManaSymbol);
            final ManaSymbol blackGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(blackMana, greenMana)));
            manaSymbolRepository.save(blackGreenManaSymbol);
            manaMap.put("B/G", blackGreenManaSymbol);
            final ManaSymbol redGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(redMana, greenMana)));
            manaSymbolRepository.save(redGreenManaSymbol);
            manaMap.put("R/G", redGreenManaSymbol);
            final ManaSymbol redWhiteManaSymbol = new ManaSymbol(new ArrayList<>(List.of(redMana, whiteMana)));
            manaSymbolRepository.save(redWhiteManaSymbol);
            manaMap.put("R/W", redWhiteManaSymbol);
            final ManaSymbol greenWhiteManaSymbol = new ManaSymbol(new ArrayList<>(List.of(greenMana, whiteMana)));
            manaSymbolRepository.save(greenWhiteManaSymbol);
            manaMap.put("G/W", greenWhiteManaSymbol);
            final ManaSymbol greenBlueManaSymbol = new ManaSymbol(new ArrayList<>(List.of(greenMana, blueMana)));
            manaSymbolRepository.save(greenBlueManaSymbol);
            manaMap.put("G/U", greenBlueManaSymbol);
            final ManaSymbol twoWhiteManaSymbol = new ManaSymbol(new ArrayList<>(List.of(genericMana2, whiteMana)));
            manaSymbolRepository.save(twoWhiteManaSymbol);
            manaMap.put("2/W", twoWhiteManaSymbol);
            final ManaSymbol twoBlueManaSymbol = new ManaSymbol(new ArrayList<>(List.of(genericMana2, blueMana)));
            manaSymbolRepository.save(twoBlueManaSymbol);
            manaMap.put("2/U", twoBlueManaSymbol);
            final ManaSymbol twoBlackManaSymbol = new ManaSymbol(new ArrayList<>(List.of(genericMana2, blackMana)));
            manaSymbolRepository.save(twoBlackManaSymbol);
            manaMap.put("2/B", twoBlackManaSymbol);
            final ManaSymbol twoRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(genericMana2, redMana)));
            manaSymbolRepository.save(twoRedManaSymbol);
            manaMap.put("2/R", twoRedManaSymbol);
            final ManaSymbol twoGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(genericMana2, greenMana)));
            manaSymbolRepository.save(twoGreenManaSymbol);
            manaMap.put("2/G", twoGreenManaSymbol);
            final ManaSymbol whitePhyrexianBlueManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianWhiteMana, phyrexianBlueMana)));
            manaSymbolRepository.save(whitePhyrexianBlueManaSymbol);
            manaMap.put("W/U/P", whitePhyrexianBlueManaSymbol);
            final ManaSymbol whitePhyrexianBlackManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianWhiteMana, phyrexianBlackMana)));
            manaSymbolRepository.save(whitePhyrexianBlackManaSymbol);
            manaMap.put("W/B/P", whitePhyrexianBlackManaSymbol);
            final ManaSymbol bluePhyrexianBlackManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianBlueMana, phyrexianBlackMana)));
            manaSymbolRepository.save(bluePhyrexianBlackManaSymbol);
            manaMap.put("U/B/P", bluePhyrexianBlackManaSymbol);
            final ManaSymbol bluePhyrexianRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianBlueMana, phyrexianRedMana)));
            manaSymbolRepository.save(bluePhyrexianRedManaSymbol);
            manaMap.put("U/R/P", bluePhyrexianRedManaSymbol);
            final ManaSymbol blackPhyrexianRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianBlackMana, phyrexianRedMana)));
            manaSymbolRepository.save(blackPhyrexianRedManaSymbol);
            manaMap.put("B/R/P", blackPhyrexianRedManaSymbol);
            final ManaSymbol blackPhyrexianGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianBlackMana, phyrexianGreenMana)));
            manaSymbolRepository.save(blackPhyrexianGreenManaSymbol);
            manaMap.put("B/G/P", blackPhyrexianGreenManaSymbol);
            final ManaSymbol redPhyrexianGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianRedMana, phyrexianGreenMana)));
            manaSymbolRepository.save(redPhyrexianGreenManaSymbol);
            manaMap.put("R/G/P", redPhyrexianGreenManaSymbol);
            final ManaSymbol whitePhyrexianRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianWhiteMana, phyrexianRedMana)));
            manaSymbolRepository.save(whitePhyrexianRedManaSymbol);
            manaMap.put("R/W/P", whitePhyrexianRedManaSymbol);
            final ManaSymbol whitePhyrexianGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianWhiteMana, phyrexianGreenMana)));
            manaSymbolRepository.save(whitePhyrexianGreenManaSymbol);
            manaMap.put("G/W/P", whitePhyrexianGreenManaSymbol);
            final ManaSymbol bluePhyrexianGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianBlueMana, phyrexianGreenMana)));
            manaSymbolRepository.save(bluePhyrexianGreenManaSymbol);
            manaMap.put("G/U/P", bluePhyrexianGreenManaSymbol);
            final Mana snowMana = new Mana(ManaType.SNOW, Color.COLORLESS, "1");
            manaRepository.save(snowMana);
            final ManaSymbol snowManaSymbol = new ManaSymbol(new ArrayList<>(List.of(snowMana)));
            manaSymbolRepository.save(snowManaSymbol);
            manaMap.put("S", snowManaSymbol);
        }
        return manaMap;
    }

}
