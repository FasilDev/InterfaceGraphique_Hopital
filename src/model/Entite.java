/*
 * Entite.java - Classe abstraite racine de tout le modèle.
 * Centralise l'identifiant unique (UUID) et la date de création.
 * Toutes les classes du modèle héritent de cette classe.
 */

package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public abstract class Entite implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private LocalDate dateCreation;

    // Constructeur normal : génère un UUID unique automatiquement
    public Entite() {
        this.id = UUID.randomUUID().toString();
        this.dateCreation = LocalDate.now();
    }

    // Constructeur de rechargement CSV : réutilise l'id déjà existant
    public Entite(String id) {
        this.id = id;
        this.dateCreation = LocalDate.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    @Override
    public abstract String toString();
}
