/*
 * Medecin.java - Représente un médecin de l'hôpital.
 * Implements Soignable (historique des actes réalisés) et Planifiable (créneaux).
 * Note : estDisponible() / setDisponible() sont hérités de Personnel.
 */

package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Medecin extends Personnel implements Soignable, Planifiable {

    private String specialite;
    private String numeroOrdre;
    private List<String> historiqueSoins;
    private Map<LocalDate, String> planning;

    public Medecin(String nom, String prenom, LocalDate dateNaissance,
                   String matricule, String specialite, String numeroOrdre) {
        super(nom, prenom, dateNaissance, matricule, specialite);
        this.specialite = specialite;
        this.numeroOrdre = numeroOrdre;
        this.historiqueSoins = new ArrayList<>();
        this.planning = new HashMap<>();
    }

    // Constructeur de rechargement CSV
    public Medecin(String id, String nom, String prenom, LocalDate dateNaissance,
                   String matricule, String specialite, String numeroOrdre) {
        super(id, nom, prenom, dateNaissance, matricule, specialite);
        this.specialite = specialite;
        this.numeroOrdre = numeroOrdre;
        this.historiqueSoins = new ArrayList<>();
        this.planning = new HashMap<>();
    }

    @Override
    public String getRole() { return "Médecin"; }

    // -------------------------------------------------------------------
    // Implémentation de Soignable
    // -------------------------------------------------------------------

    @Override
    public List<String> getHistoriqueSoins() { return historiqueSoins; }

    @Override
    public void ajouterSoin(String descriptionSoin) {
        if (descriptionSoin != null && !descriptionSoin.isBlank()) {
            this.historiqueSoins.add(descriptionSoin);
        }
    }

    @Override
    public boolean aDesAntecedents() { return false; }

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
        return "Dr. " + getNomComplet() + " [" + specialite + "] - " + getMatricule();
    }

    // -------------------------------------------------------------------
    // Getters et Setters
    // -------------------------------------------------------------------

    public String getSpecialite() { return specialite; }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
        setService(specialite);
    }

    public String getNumeroOrdre() { return numeroOrdre; }
    public void setNumeroOrdre(String numeroOrdre) { this.numeroOrdre = numeroOrdre; }

    public void setHistoriqueSoins(List<String> historiqueSoins) { this.historiqueSoins = historiqueSoins; }
    public void setPlanning(Map<LocalDate, String> planning) { this.planning = planning; }
}
