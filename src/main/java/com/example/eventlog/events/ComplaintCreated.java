package com.example.eventlog.events;

import java.io.Serializable;

public class ComplaintCreated implements Serializable {

    public final String id;
    public final String company;
    public final String description;

    public ComplaintCreated(String id, String company, String description) {
        this.id = id;
        this.company = company;
        this.description = description;
    }
}
