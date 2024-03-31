package com.mtg.tasks;

import com.mtg.card.base.Card;
import com.mtg.card.base.CardRepository;
import com.mtg.card.collectible.Collectible;
import com.mtg.card.collectible.CollectibleRepository;
import com.mtg.card.collectible.listing.Listing;
import com.mtg.releaseSet.ReleaseSet;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ScheduledTasks {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private CollectibleRepository collectibleRepository;
    @Autowired
    private com.mtg.releaseSet.ReleaseSetRepository releaseSetRepository;
    @Autowired
    private com.mtg.card.collectible.listing.ListingRepository listingRepository;

    private Logger logger = LoggerFactory.getLogger(ScheduledTasks.class.getName());

//    @Scheduled(cron = "0 1 1 * * ?")
//    public void runTasks() throws IOException, ParseException, InterruptedException {
//        ingestDailyListings();
//    }

//    public static void main(String[] args) {
//        logger.info("howdy");
//    }

    @Scheduled(cron = "0 0 8 * * ?")
    void downloadBulkCardData() throws IOException, InterruptedException, ParseException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.scryfall.com/bulk-data"))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONParser parser1 = new JSONParser();
        JSONObject json = (JSONObject) parser1.parse(response.body());

        JSONArray data = (JSONArray) json.get("data");
        String downloadURL = "";
        for (Object o : data) {
            JSONObject obj = (JSONObject) o;
            if (obj.get("type").equals("default_cards")) {
                downloadURL = (String) obj.get("download_uri");
                break;
            }
        }

        System.out.println(downloadURL);

        FileUtils.copyURLToFile(
                new URL(downloadURL),
                new File("./src/main/resources/cards.json"), 10000, 10000);
    }

    @Scheduled(cron = "0 30 8 * * ?")
    void ingestDailyListings() throws IOException, ParseException {

        logger.info("Ingesting daily listings");

        JSONParser parser2 = new JSONParser();
        InputStream in = getClass().getResourceAsStream("/cards.json");
        JSONArray a = (JSONArray) parser2.parse(new InputStreamReader(in));

        logger.info("Read file");

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

        for (Object o : a) {
            JSONObject card = (JSONObject) o;
            try {
                JSONArray cardFaces = (JSONArray) card.get("card_faces");
                String cardType = cardFaces == null ? (String) card.get("type_line") : (String) ((JSONObject) cardFaces.get(0)).get("type_line");
                boolean skip = false;
                for (String cardTypeToSkip : cardTypesToSkip) {
                    if (cardType.contains(cardTypeToSkip)) {
                        skip = true;
                        break;
                    }
                }
                if (skip) {
                    continue;
                }
                Card savedCard = cardFaces == null ? cardRepository.findByName((String) card.get("name")) : cardRepository.findByName((String) ((JSONObject) cardFaces.get(0)).get("name"));
//                ReleaseSet releaseSet = releaseSetRepository.findByName((String) card.get("set_name"));
                JSONObject prices = (JSONObject) card.get("prices");
                JSONArray promo_types = (JSONArray) card.get("promo_types");
                Collectible.PromoType promoType = null;
                if (promo_types != null) {
                    if (((String) promo_types.get(0)).equalsIgnoreCase("PROMOPACK") || ((String) promo_types.get(0)).equalsIgnoreCase("PRERELEASE")) {
                        promoType = Collectible.PromoType.valueOf(((String) promo_types.get(0)).toUpperCase());
                    };
                }
                if (prices.get("usd") != null) {
                    Collectible nonFoilCollectible = collectibleRepository.findByCardAndCollectorNumberAndFinishAndPromo(savedCard, (String) card.get("collector_number"), Collectible.Finish.NONFOIL, promoType);
                    if (nonFoilCollectible != null) {
                        Listing newNonFoilListing = new Listing(nonFoilCollectible, Double.parseDouble((String) prices.get("usd")));
                        listingRepository.save(newNonFoilListing);
                        System.out.println("Saved listing for " + nonFoilCollectible.getCard().getName() + " " + nonFoilCollectible.getCollectorNumber() + " " + nonFoilCollectible.getFinish());
                    }
                }
                if (prices.get("usd_foil") != null) {
                    Collectible foilCollectible = collectibleRepository.findByCardAndCollectorNumberAndFinishAndPromo(savedCard, (String) card.get("collector_number"), Collectible.Finish.FOIL, promoType);
                    if (foilCollectible != null) {
                        Listing newFoilListing = new Listing(foilCollectible, Double.parseDouble((String) prices.get("usd_foil")));
                        listingRepository.save(newFoilListing);
                        System.out.println("Saved listing for " + foilCollectible.getCard().getName() + " " + foilCollectible.getCollectorNumber() + " " + foilCollectible.getFinish());
                    }
                }
                if (prices.get("usd_etched") != null) {
                    Collectible etchedCollectible = collectibleRepository.findByCardAndCollectorNumberAndFinishAndPromo(savedCard, (String) card.get("collector_number"), Collectible.Finish.ETCHED, promoType);
                    if (etchedCollectible != null) {
                        Listing newEtchedListing = new Listing(etchedCollectible, Double.parseDouble((String) prices.get("usd_etched")));
                        listingRepository.save(newEtchedListing);
                    }
                }
            } catch (Exception e) {
                logger.error("Error ingesting daily listing for card " + card.get("name"), e);
            }
        }
    }

    Collectible createNewCollectible(Card card, ReleaseSet set, JSONObject rawCard, Collectible.Finish finish, Collectible.PromoType promoType) {
        JSONObject imageUris = (JSONObject) rawCard.get("image_uris");
        List<String> imageUrisList = new ArrayList<>();
        if (imageUris != null) {
            imageUrisList.add((String) imageUris.get("normal"));
        } else if (rawCard.get("card_faces") != null) {
            JSONArray cardFaces = (JSONArray) rawCard.get("card_faces");
            for (Object obj : cardFaces) {
                JSONObject cardFace = (JSONObject) obj;
                imageUris = (JSONObject) cardFace.get("image_uris");
                if (imageUris != null) {
                    imageUrisList.add((String) imageUris.get("normal"));
                }
            }
        } else {
            System.out.printf("No image uris: %s%n", rawCard.get("name"));
        }
        Collectible collectible = new Collectible(card, new ArrayList<>(), (String) rawCard.get("collector_number"), set, finish, promoType, imageUrisList);
        return collectibleRepository.save(collectible);
    }
}
