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
public class Collectible {

    private @Id @GeneratedValue Long id;
    @ManyToOne
    @JoinColumn(name = "card_id", nullable=false)
    private Card card;
    @ManyToMany(mappedBy="collection")
    @JsonIgnore
    private List<User> users;
    @Column(unique = true, name = "IX_COLLECTOR_NUMBER")
    private String collectorNumber;
    @ManyToOne
    @JoinColumn(name = "set_id", nullable=false)
    private ReleaseSet set;
    @OneToMany(mappedBy="collectible")
    @JsonIgnore
    private Set<Listing> listings;
    private String imageUri;

    public Collectible() {
    }

    public Collectible(Card card, List<User> users, String collectorNumber, ReleaseSet set, String imageUri) {
        this(card, users, collectorNumber, set, new HashSet<Listing>(), imageUri);
    }

    @JsonCreator
    public Collectible(@JsonProperty(value = "card", required = true) Card card, List<User> users, @JsonProperty(value = "collectorNumber", required = true) String collectorNumber, @JsonProperty(value = "set", required = true) ReleaseSet set, Set<Listing> listings, @JsonProperty(value = "imageUri", required = true) String imageUri) {
        this.card = card;
        this.collectorNumber = collectorNumber;
        this.set = set;
        this.users = Objects.requireNonNullElseGet(users, ArrayList::new);
        this.listings = Objects.requireNonNullElseGet(listings, HashSet::new);
        this.imageUri = imageUri;
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

    public Set<Listing> getListings() {
        return listings;
    }

    public void setListings(Set<Listing> listings) {
        this.listings = listings;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

}
