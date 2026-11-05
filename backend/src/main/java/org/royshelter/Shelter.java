package org.royshelter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Shelter {
    private final String id;
    private final String name;
    private final String city;
    private final String websiteUrl;
    private final String phone;
    private final String sourceUrl;

    @JsonCreator
    public Shelter(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("city") String city,
            @JsonProperty("websiteUrl") String websiteUrl,
            @JsonProperty("phone") String phone,
            @JsonProperty("sourceUrl") String sourceUrl) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.websiteUrl = websiteUrl;
        this.phone = phone;
        this.sourceUrl = sourceUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getWebsiteUrl() { return websiteUrl; }
    public String getPhone() { return phone; }
    public String getSourceUrl() { return sourceUrl; }
}
