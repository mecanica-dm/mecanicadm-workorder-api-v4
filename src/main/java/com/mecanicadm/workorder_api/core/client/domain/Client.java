package com.mecanicadm.workorder_api.core.client.domain;

import com.mecanicadm.workorder_api.core.client.exception.ClientExceptions;
import com.mecanicadm.workorder_api.shared.domain.AuditDomain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Client extends AuditDomain {

    private final UUID id;
    private String name;
    private String email;
    private String document;
    private String phone;

    private Client(UUID id, String name, String email, String document, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.document = document;
        this.phone = phone;
        validate();
    }

    public static Client create(String name, String email, String document, String phone) {
        Client client = new Client(UUID.randomUUID(), name, email, document, phone);
        client.create();
        return client;
    }

    @SuppressWarnings("java:S107")
    public static Client restore(UUID id, String name, String email, String document, String phone,
                              LocalDateTime dateCreated, LocalDateTime dateUpdated, LocalDateTime deletedAt) {
        Client client = new Client(id, name, email, document, phone);
        client.dateCreated = dateCreated;
        client.dateUpdated = dateUpdated;
        client.deletedAt = deletedAt;
        return client;
    }

    public void update(String name, String email, String document, String phone) {
        this.name = name;
        this.email = email;
        this.document = document;
        this.phone = phone;
        update();
        this.validate();
    }

    private void validate() {
        if (this.name == null || this.name.isBlank()) {
            throw new ClientExceptions.NameNotEmpty();
        }
        if (this.email == null || this.email.isBlank()) {
            throw new ClientExceptions.EmailNotEmpty();
        }
        if (this.document == null || this.document.isBlank()) {
            throw new ClientExceptions.DocumentNotEmpty();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDocument() {
        return document;
    }

    public String getPhone() {
        return phone;
    }
}
