package com.beobase.beospring.user.internal;

import java.time.Instant;
import java.util.Locale;

import com.beobase.beospring.shared.DbSchema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", schema = DbSchema.BEO_BASE)
@Getter
@Setter
@NoArgsConstructor
class User {

    // The id is created by the backend (NanoId), the database has no default
    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password_hashed", nullable = false)
    private String passwordHashed;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // Normalize email before storing it, extra layer of protection for DB logic
    public void setEmail(String email) {
        this.email = (email == null)
                ? null
                : email.trim().toLowerCase(Locale.ROOT);
    }
}
