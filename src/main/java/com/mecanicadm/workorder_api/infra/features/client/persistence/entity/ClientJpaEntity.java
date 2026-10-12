package com.mecanicadm.workorder_api.infra.features.client.persistence.entity;

import com.mecanicadm.workorder_api.infra.audit.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "clients")
@SQLRestriction("deleted_at IS NULL")
public class ClientJpaEntity extends AuditEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "document", nullable = false, unique = true)
    private String document;

    @Column(name = "phone")
    private String phone;

    protected ClientJpaEntity() {
    }

    public ClientJpaEntity(UUID id, String name, String email, String document, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.document = document;
        this.phone = phone;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
