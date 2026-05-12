/*
 * Personne.java - Classe abstraite représentant toute personne dans le système.
 * Patient et Personnel héritent de cette classe (champs communs : nom, prénom, contact).
 */

package model;

import java.time.LocalDate;
import java.time.Period;

public abstract class Personne extends Entite {

    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String telephone;
    private String email;

    public Personne(String nom, String prenom, LocalDate dateNaissance) {
        super();
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
    }

    // Constructeur de rechargement CSV
    public Personne(String id, String nom, String prenom, LocalDate dateNaissance) {
        super(id);
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
    }

    // Calcule l'âge en années complètes à partir de la date de naissance
    public int getAge() {
        if (dateNaissance == null) return 0;
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

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
