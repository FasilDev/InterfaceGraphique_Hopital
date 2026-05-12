/*
 * Fichier : Entite.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Classe abstraite racine de toute la hiérarchie du modèle.
 *        Elle centralise l'identifiant unique (UUID) et la date de création,
 *        communs à toutes les entités (Patient, Médecin, Soin, Chambre...).
 *
 * Interactions : toutes les classes du modèle via l'héritage (Personne, Soin, etc.)
 *
 * "abstract" signifie qu'on ne peut pas faire new Entite() directement.
 * C'est un gabarit que les autres classes vont étendre.
 * Implements Serializable = l'objet peut être converti en octets pour être sauvegardé.
 */

package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public abstract class Entite implements Serializable {

    // Identifiant de version pour la sérialisation Java.
    // Si on modifie la classe, Java peut lever une erreur à la désérialisation
    // si ce numéro ne correspond plus. On le fixe à 1L pour éviter ça.
    private static final long serialVersionUID = 1L;

    // Identifiant unique de l'entité.
    // UUID garantit qu'il n'y aura jamais deux objets avec le même id,
    // même créés au même moment sur des machines différentes.
    // Exemple : "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    private String id;

    // Date à laquelle cet enregistrement a été créé dans le système.
    // LocalDate (java.time, Java 8+) = date sans heure. Plus fiable que java.util.Date.
    private LocalDate dateCreation;


    /**
     * Constructeur par défaut.
     * Génère automatiquement un UUID et enregistre la date du jour.
     * Appelé quand on crée un nouvel objet (ex : new Patient(...)).
     */
    public Entite() {
        // UUID.randomUUID() produit une chaîne de 36 caractères unique
        this.id = UUID.randomUUID().toString();
        // LocalDate.now() = date du jour au moment de la création de l'objet
        this.dateCreation = LocalDate.now();
    }

    /**
     * Constructeur avec id fourni.
     * Utilisé lors du rechargement depuis un fichier CSV :
     * on recrée l'objet avec l'id qu'il avait déjà, pour ne pas en générer un nouveau.
     *
     * @param id L'identifiant UUID déjà existant (lu depuis le CSV)
     */
    public Entite(String id) {
        this.id = id;
        this.dateCreation = LocalDate.now();
    }


    public String getId() {
        return id;
    }

    // Le setter de l'id est utile uniquement pour le chargement CSV.
    // En usage normal, on ne devrait jamais changer l'id d'un objet déjà créé.
    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    /**
     * Chaque sous-classe doit redéfinir toString() pour s'afficher de façon lisible.
     * On impose le contrat ici, les sous-classes le remplissent à leur manière.
     */
    @Override
    public abstract String toString();
}