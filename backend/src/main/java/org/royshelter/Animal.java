package org.royshelter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Animal {
    private final String id;
    private final String name;
    private final String species;
    private final String breed;
    private final int ageMonths;
    private final String description;
    private final String imageUrl;
    private final String shelterId;
    private final boolean adoptable;
    private final boolean fosterable;
    private final boolean demo;

    @JsonCreator
    public Animal(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("species") String species,
            @JsonProperty("breed") String breed,
            @JsonProperty("ageMonths") int ageMonths,
            @JsonProperty("description") String description,
            @JsonProperty("imageUrl") String imageUrl,
            @JsonProperty("shelterId") String shelterId,
            @JsonProperty("adoptable") boolean adoptable,
            @JsonProperty("fosterable") boolean fosterable,
            @JsonProperty("demo") boolean demo) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.ageMonths = ageMonths;
        this.description = description;
        this.imageUrl = imageUrl;
        this.shelterId = shelterId;
        this.adoptable = adoptable;
        this.fosterable = fosterable;
        this.demo = demo;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSpecies() { return species; }
    public String getBreed() { return breed; }
    public int getAgeMonths() { return ageMonths; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getShelterId() { return shelterId; }
    public boolean isAdoptable() { return adoptable; }
    public boolean isFosterable() { return fosterable; }
    public boolean isDemo() { return demo; }
}
