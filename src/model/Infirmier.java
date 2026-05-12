/*
 * Infirmier.java - Représente un infirmier ou infirmière de l'hôpital.
 * Implements Planifiable (planning de gardes).
 * estDisponible() / setDisponible() sont hérités de Personnel.
 */

package model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Infirmier extends Personnel implements Planifiable {

    // Diplôme : "IDE" (Diplômé d'État), "IADE" (anesthésie), "IBODE" (bloc opératoire)
    private String qualification;
    private boolean gardeNuit;
    private Map<LocalDate, String> planning;

    public Infirmier(String nom, String prenom, LocalDate dateNaissance,
                     String matricule, String service, String qualification) {
        super(nom, prenom, dateNaissance, matricule, service);
        this.qualification = qualification;
        this.gardeNuit = false;
        this.planning = new HashMap<>();
    }

    // Constructeur de rechargement CSV
    public Infirmier(String id, String nom, String prenom, LocalDate dateNaissance,
                     String matricule, String service, String qualification) {
        super(id, nom, prenom, dateNaissance, matricule, service);
        this.qualification = qualification;
        this.gardeNuit = false;
        this.planning = new HashMap<>();
    }

    @Override
    public String getRole() { return "Infirmier"; }

    // -------------------------------------------------------------------
    // Implémentation de Planifiable
    // -------------------------------------------------------------------

    @Override
    public boolean estDisponible() { return isDisponible(); }

    @Override
    public void ajouterCreneau(LocalDate date, String horaire) {
        if (date != null && horaire != null && !horaire.isBlank()) {
            this.planning.put(date, horaire);
        }
    }

    @Override
    public Map<LocalDate, String> getPlanning() { return planning; }

    @Override
    public String toString() {
        return getNomComplet() + " [" + qualification + "] - Service : " + getService();
    }

    // -------------------------------------------------------------------
    // Getters et Setters
    // -------------------------------------------------------------------

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public boolean isGardeNuit() { return gardeNuit; }
    public void setGardeNuit(boolean gardeNuit) { this.gardeNuit = gardeNuit; }

    public void setPlanning(Map<LocalDate, String> planning) { this.planning = planning; }
}
