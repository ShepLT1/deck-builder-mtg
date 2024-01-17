package com.mtg.card.collectible.listing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtg.card.collectible.Collectible;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"collectible_id", "date"}))
public class Listing {

    private @Id
    @GeneratedValue Long id;
    private final Date date = new Date();
    @ManyToOne
    @JoinColumn(name = "collectible_id", nullable=false)
    private Collectible collectible;
    private double usd;
    private double usdFoil;

    public Listing() {
    }

    @JsonCreator
    public Listing(@JsonProperty(value = "collectible", required = true) Collectible collectible, @JsonProperty(value = "usd", required = true) double usd, @JsonProperty(value = "usdFoil", required = true) double usdFoil) {
        this.collectible = collectible;
        this.usd = usd;
        this.usdFoil = usdFoil;
    }

    public Long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public Collectible getCollectible() {
        return collectible;
    }

    public void setCollectible(Collectible collectible) {
        this.collectible = collectible;
    }

    public double getUsd() {
        return usd;
    }

    public void setUsd(double usd) {
        this.usd = usd;
    }

    public double getUsdFoil() {
        return usdFoil;
    }

    public void setUsdFoil(double usdFoil) {
        this.usdFoil = usdFoil;
    }

}
