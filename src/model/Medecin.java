/*
 * Fichier : Medecin.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Représente un médecin de l'hôpital.
 *        Hérite de Personnel (et donc de Personne et Entite).
 *        Ajoute la spécialité médicale et le numéro d'ordre.
 *
 * Interactions : Personnel (parent), PersonnelService, PersonnelServlet,
 *                PatientService (association médecin-patient)
 *
 * TODO Commit 3 : ajouter "implements Soignable, Planifiable" sur la déclaration de classe
 */

package model;

import java.time.LocalDate;

public class Medecin extends Personnel {

    // Spécialité médicale : "Cardiologie", "Neurologie", "Pédiatrie", "Urgences"...
    private String specialite;

    // Numéro d'inscription à l'Ordre des Médecins.
    // C'est un identifiant réglementaire obligatoire pour exercer en France.
    private String numeroOrdre;


    //Constructeur pour créer un nouveau médecin.

    public Medecin(String nom, String prenom, LocalDate dateNaissance,
                   String matricule, String specialite, String numeroOrdre) {
        // On utilise la spécialité comme service, car pour un médecin les deux coincident
        super(nom, prenom, dateNaissance, matricule, specialite);
        this.specialite = specialite;
        this.numeroOrdre = numeroOrdre;
    }

    /**
     * Constructeur avec id existant, pour le rechargement depuis un fichier CSV.
     */
    public Medecin(String id, String nom, String prenom, LocalDate dateNaissance,
                   String matricule, String specialite, String numeroOrdre) {
        super(id, nom, prenom, dateNaissance, matricule, specialite);
        this.specialite = specialite;
        this.numeroOrdre = numeroOrdre;
    }

    // -----------------------------------------------------------------------
    // Implémentation des méthodes abstraites
    // -----------------------------------------------------------------------

    /**
     * Définit le rôle de cet objet Personnel : c'est un Médecin.
     * Imposé par la classe abstraite Personnel.
     */
    @Override
    public String getRole() {
        return "Médecin";
    }

    @Override
    public String toString() {
        return "Dr. " + getNomComplet() + " [" + specialite + "] - " + getMatricule();
    }



    public String getSpecialite() { return specialite; }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
        // On synchronise aussi le champ "service" hérité de Personnel
        // puisque pour un médecin, service == spécialité
        setService(specialite);
    }

    public String getNumeroOrdre() { return numeroOrdre; }
    public void setNumeroOrdre(String numeroOrdre) { this.numeroOrdre = numeroOrdre; }
}