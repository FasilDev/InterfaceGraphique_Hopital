/*
 * Fichier : Infirmier.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Représente un infirmier ou une infirmière de l'hôpital.
 *        Hérite de Personnel (et donc de Personne et Entite).
 *        Ajoute la qualification diplômante et l'information de garde de nuit.
 *
 * Interactions : Personnel (parent), PersonnelService, PersonnelServlet
 *
 * TODO Commit 3 : ajouter "implements Planifiable" sur la déclaration de classe
 */

package model;

import java.time.LocalDate;

public class Infirmier extends Personnel {

    // Diplôme ou qualification de l'infirmier.
    // Exemples : "IDE" (Infirmier Diplômé d'État), "IADE" (anesthésie), "IBODE" (bloc opératoire)
    private String qualification;

    // Indique si cet infirmier travaille en garde de nuit.
    // Utile pour la gestion des plannings (Commit suivants).
    private boolean gardeNuit;


    //Constructeur pour créer un nouvel infirmier.
     public Infirmier(String nom, String prenom, LocalDate dateNaissance,
                     String matricule, String service, String qualification) {
        super(nom, prenom, dateNaissance, matricule, service);
        this.qualification = qualification;
        // Par défaut, pas en garde de nuit
        this.gardeNuit = false;
    }

    // Constructeur avec id existant, pour le rechargement depuis un fichier CSV.
    public Infirmier(String id, String nom, String prenom, LocalDate dateNaissance,
                     String matricule, String service, String qualification) {
        super(id, nom, prenom, dateNaissance, matricule, service);
        this.qualification = qualification;
        this.gardeNuit = false;
    }

    // -----------------------------------------------------------------------
    // Implémentation des méthodes abstraites
    // -----------------------------------------------------------------------

    /**
     * Définit le rôle de cet objet Personnel : c'est un Infirmier.
     * Imposé par la classe abstraite Personnel.
     */
    @Override
    public String getRole() {
        return "Infirmier";
    }

    @Override
    public String toString() {
        return getNomComplet() + " [" + qualification + "] - Service : " + getService();
    }



    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public boolean isGardeNuit() { return gardeNuit; }
    public void setGardeNuit(boolean gardeNuit) { this.gardeNuit = gardeNuit; }
}