package com.smms.assistance.infrastructure.client;

import java.util.UUID;

public class ProjectOutput {
    public UUID id;
    public String name;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
