package edu.uclm.esi.tysweb2023.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class Token {

    @Id
    private String id;
    private String email;
    private long date;

    public Token() {
    }

    public Token(String email) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.date = System.currentTimeMillis();
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
