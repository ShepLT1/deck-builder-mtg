package com.mtg.card.collectible;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtg.admin.user.User;
import com.mtg.card.base.Card;
import com.mtg.card.collectible.listing.Listing;
import com.mtg.releaseSet.ReleaseSet;
import jakarta.persistence.*;

import java.util.*;

@Entity(name="collectibles")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"card_id", "collectorNumber", "finish", "promo"}))
public class Collectible {

    public enum Finish {
        ETCHED, FOIL, NONFOIL;

        @JsonCreator
        public static Finish fromText(String text) {
            for (Finish f : Finish.values()) {
                if (f.name().equals(text.toUpperCase())) {
                    return f;
                }
            }
            throw new IllegalArgumentException("Unaccepted Finish value. Excepted Finish values (case insensitive) = " + Arrays.toString(Finish.values()));
        }

        @Override
        public String toString() {
            return name();
        }

    }

    public enum PromoType {
        PRERELEASE, PROMOPACK;

        @JsonCreator
        public static PromoType fromText(String text) {
            for (PromoType f : PromoType.values()) {
                if (f.name().equals(text.toUpperCase())) {
                    return f;
                }
            }
            throw new IllegalArgumentException("Unaccepted PromoType value. Excepted PromoType values (case insensitive) = " + Arrays.toString(Finish.values()));
        }

        @Override
        public String toString() {
            return name();
        }

    }

    private @Id @GeneratedValue Long id;
    @ManyToOne
    @JoinColumn(name = "card_id", nullable=false)
    private Card card;
    @ManyToMany(mappedBy="collection")
    @JsonIgnore
    private List<User> users;
    private String collectorNumber;
    @ManyToOne
    @JoinColumn(name = "set_id", nullable=false)
    private ReleaseSet set;
    private Finish finish;
    private PromoType promo;
    @OneToMany(mappedBy="collectible")
    @JsonIgnore
    private Set<Listing> listings;
    private List<String> imageUris;

    public Collectible() {
    }

    public Collectible(Card card, List<User> users, String collectorNumber, ReleaseSet set, Finish finish, PromoType promo, List<String> imageUris) {
        this(card, users, collectorNumber, set, finish, promo, new HashSet<>(), imageUris);
    }

    @JsonCreator
    public Collectible(@JsonProperty(value = "card", required = true) Card card, List<User> users, @JsonProperty(value = "collectorNumber", required = true) String collectorNumber, @JsonProperty(value = "set", required = true) ReleaseSet set, @JsonProperty(value = "finish", required = true) Finish finish, PromoType promo, Set<Listing> listings, @JsonProperty(value = "imageUri", required = true) List<String> imageUris) {
        this.card = card;
        this.collectorNumber = collectorNumber;
        this.set = set;
        this.finish = finish;
        this.promo = promo;
        this.users = Objects.requireNonNullElseGet(users, ArrayList::new);
        this.listings = Objects.requireNonNullElseGet(listings, HashSet::new);
        this.imageUris = imageUris;
    }

    public Long getId() {
        return id;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getCollectorNumber() {
        return collectorNumber;
    }

    public void setCollectorNumber(String collectorNumber) {
        this.collectorNumber = collectorNumber;
    }

    public ReleaseSet getSet() {
        return set;
    }

    public void setSet(ReleaseSet set) {
        this.set = set;
    }

    public Finish getFinish() {
        return finish;
    }

    public void setFinish(Finish finish) {
        this.finish = finish;
    }

    public PromoType getPromo() {
        return promo;
    }

    public void setPromo(PromoType promo) {
        this.promo = promo;
    }

    public Set<Listing> getListings() {
        return listings;
    }

    public void setListings(Set<Listing> listings) {
        this.listings = listings;
    }

    public List<String> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<String> imageUri) {
        this.imageUris = imageUri;
    }

}
