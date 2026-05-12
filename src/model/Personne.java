/*
 * Fichier : Personne.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Classe abstraite représentant toute personne dans le système hospitalier.
 *        Patient et Personnel sont tous les deux des personnes, donc les deux héritent d'ici.
 *        On regroupe ici ce qu'ils ont en commun : nom, prénom, date de naissance, contact.
 *
 * Interactions : Entite (classe parente), Patient (sous-classe), Personnel (sous-classe)
 *
 * Principe appliqué : DRY (Don't Repeat Yourself).
 *        Sans cette classe, il faudrait dupliquer les champs nom/prenom dans Patient ET Medecin.
 */

package model;

import java.time.LocalDate;
import java.time.Period;

public abstract class Personne extends Entite {

    private String nom;
    private String prenom;

    // LocalDate (java.time) est préférable à java.util.Date : immuable, lisible,
    // sans problème de fuseau horaire, et bien supporté par les APIs modernes.
    private LocalDate dateNaissance;

    // Stocké en String pour accepter les formats internationaux (+33..., 06..., etc.)
    private String telephone;

    private String email;


    //Constructeur pour créer une nouvelle personne.

    public Personne(String nom, String prenom, LocalDate dateNaissance) {
        super(); // génère l'UUID dans Entite
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
    }

    /**
     * Constructeur avec id fourni, pour le rechargement depuis un fichier CSV.
     *
     * @param id            UUID déjà existant
     * @param nom           Nom de famille
     * @param prenom        Prénom
     * @param dateNaissance Date de naissance
     */
    public Personne(String id, String nom, String prenom, LocalDate dateNaissance) {
        super(id); // utilise l'id existant dans Entite
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
    }

    // -----------------------------------------------------------------------
    // Méthodes métier
    // -----------------------------------------------------------------------

    /**
     * Calcule l'âge de la personne en années complètes à partir de sa date de naissance.
     *
     * Period.between(debut, fin) calcule l'intervalle entre deux dates.
     * .getYears() en extrait le nombre d'années entières.
     *
     * @return L'âge en années, ou 0 si la date de naissance est inconnue
     */
    public int getAge() {
        if (dateNaissance == null) return 0;
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    /**
     * Retourne "Prénom NOM" (ex : "Jean DUPONT").
     * Utilisé dans les tableaux JSP et les listes déroulantes.
     */
    public String getNomComplet() {
        return prenom + " " + nom.toUpperCase();
    }



    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}