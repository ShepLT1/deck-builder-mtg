package com.mtg.releaseSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtg.card.collectible.Collectible;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ReleaseSet {

    private @Id
    @GeneratedValue Long id;
    @Column(unique = true, name = "IX_SET_NAME")
    private String name;
    private Date releaseDate;
    private String iconUri;
    @OneToMany(mappedBy="set")
    @JsonIgnore
    private Set<Collectible> collectibles;
    

    public ReleaseSet() {
    }

    @JsonCreator
    public ReleaseSet(@JsonProperty(value = "name", required = true) String name, Date releaseDate, String iconUri) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.iconUri = iconUri;
        this.collectibles = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getIconUri() {
        return iconUri;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }
    
    public Set<Collectible> getCollectibles() {
        return collectibles;
    }
    
    public void setCollectibles(Set<Collectible> collectibles) {
        this.collectibles = collectibles;
    }

}
