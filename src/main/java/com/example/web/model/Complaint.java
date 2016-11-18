package com.example.web.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Complaint {

    private final String id;
    private final String company;
    private final String description;

    @JsonCreator
    public Complaint(@JsonProperty("id") String id,
                     @JsonProperty("company") String company,
                     @JsonProperty("description") String description) {
        this.id = id;
        this.company = company;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getCompany() {
        return company;
    }

    public String getDescription() {
        return description;
    }
}
