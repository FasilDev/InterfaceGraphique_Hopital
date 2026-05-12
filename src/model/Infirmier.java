/*
 * Fichier : Infirmier.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Représente un infirmier ou une infirmière de l'hôpital.
 *
 * Interface implémentée :
 *   - Planifiable : l'infirmier a un planning de gardes et de créneaux
 *
 * Note : estDisponible() et setDisponible() sont déclarés dans Planifiable
 *        et déjà implémentés dans Personnel. Infirmier hérite de cette implémentation
 *        sans avoir besoin de la réécrire.
 *
 * Interactions : Personnel (parent), PersonnelService, PersonnelServlet
 */

package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Infirmier extends Personnel implements Planifiable {

    // Diplôme : "IDE" (Infirmier Diplômé d'État), "IADE" (anesthésie), "IBODE" (bloc)
    private String qualification;

    private boolean gardeNuit;

    // Planning de gardes et créneaux : date → description
    private Map<LocalDate, String> planning;

    // -----------------------------------------------------------------------
    // Constructeurs
    // -----------------------------------------------------------------------

    public Infirmier(String nom, String prenom, LocalDate dateNaissance,
                     String matricule, String service, String qualification) {
        super(nom, prenom, dateNaissance, matricule, service);
        this.qualification = qualification;
        this.gardeNuit = false;
        this.planning = new HashMap<>();
    }

    public Infirmier(String id, String nom, String prenom, LocalDate dateNaissance,
                     String matricule, String service, String qualification) {
        super(id, nom, prenom, dateNaissance, matricule, service);
        this.qualification = qualification;
        this.gardeNuit = false;
        this.planning = new HashMap<>();
    }

    // -----------------------------------------------------------------------
    // Implémentation méthode abstraite de Personnel
    // -----------------------------------------------------------------------

    @Override
    public String getRole() {
        return "Infirmier";
    }

    // -----------------------------------------------------------------------
    // Implémentation de Planifiable
    // -----------------------------------------------------------------------

    // estDisponible() et setDisponible() sont hérités de Personnel

    @Override
    public boolean estDisponible() {
        return false;
    }

    @Override
    public void ajouterCreneau(LocalDate date, String horaire) {
        if (date != null && horaire != null && !horaire.isBlank()) {
            this.planning.put(date, horaire);
        }
    }

    @Override
    public Map<LocalDate, String> getPlanning() {
        return planning;
    }

    // -----------------------------------------------------------------------

    @Override
    public String toString() {
        return getNomComplet() + " [" + qualification + "] - Service : " + getService();
    }

    // -----------------------------------------------------------------------
    // Getters et Setters
    // -----------------------------------------------------------------------

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public boolean isGardeNuit() { return gardeNuit; }
    public void setGardeNuit(boolean gardeNuit) { this.gardeNuit = gardeNuit; }

    public void setPlanning(Map<LocalDate, String> planning) { this.planning = planning; }
}