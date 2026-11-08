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

    /** Return the unique ID used as a lookup-map key. */
    public String getId() { return id; }
    /** Return the display name for text search. */
    public String getName() { return name; }
    /** Return the species code: DOG or CAT. */
    public String getSpecies() { return species; }
    /** Return the breed text for substring search. */
    public String getBreed() { return breed; }
    /** Return age in whole months, including 0 for newborns. */
    public int getAgeMonths() { return ageMonths; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    /** Return the shelter ID; several animals can share it. */
    public String getShelterId() { return shelterId; }
    /** Return this fictional animal's adoption flag, independent of its foster flag. */
    public boolean isAdoptable() { return adoptable; }
    /** Return this fictional animal's foster flag, independent of its adoption flag. */
    public boolean isFosterable() { return fosterable; }
    public boolean isDemo() { return demo; }
}
