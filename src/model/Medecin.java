/*
 * Fichier : Medecin.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Représente un médecin de l'hôpital.
 *
 * Interfaces implémentées :
 *   - Soignable : le médecin effectue des soins (historique des actes réalisés)
 *   - Planifiable : le médecin a un planning de créneaux de consultation
 *
 * Note sur estDisponible() / setDisponible() :
 *   Ces méthodes sont déclarées dans Planifiable et déjà implémentées dans Personnel.
 *   Medecin hérite de Personnel, donc le contrat de l'interface est automatiquement satisfait
 *   sans qu'on ait besoin de réécrire ces méthodes ici.
 *
 * Interactions : Personnel (parent), PersonnelService, PersonnelServlet
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

    // Historique des soins effectués par ce médecin
    private List<String> historiqueSoins;

    // Planning : chaque date est associée à un créneau horaire
    // On utilise HashMap ici (pas TreeMap) car le tri par date se fera via Stream au besoin
    private Map<LocalDate, String> planning;

    // -----------------------------------------------------------------------
    // Constructeurs
    // -----------------------------------------------------------------------

    public Medecin(String nom, String prenom, LocalDate dateNaissance,
                   String matricule, String specialite, String numeroOrdre) {
        super(nom, prenom, dateNaissance, matricule, specialite);
        this.specialite = specialite;
        this.numeroOrdre = numeroOrdre;
        this.historiqueSoins = new ArrayList<>();
        this.planning = new HashMap<>();
    }

    public Medecin(String id, String nom, String prenom, LocalDate dateNaissance,
                   String matricule, String specialite, String numeroOrdre) {
        super(id, nom, prenom, dateNaissance, matricule, specialite);
        this.specialite = specialite;
        this.numeroOrdre = numeroOrdre;
        this.historiqueSoins = new ArrayList<>();
        this.planning = new HashMap<>();
    }

    // -----------------------------------------------------------------------
    // Implémentation méthode abstraite de Personnel
    // -----------------------------------------------------------------------

    @Override
    public String getRole() {
        return "Médecin";
    }

    // -----------------------------------------------------------------------
    // Implémentation de Soignable
    // -----------------------------------------------------------------------

    // Pour un médecin, l'historique contient les soins qu'il a réalisés
    @Override
    public List<String> getHistoriqueSoins() {
        return historiqueSoins;
    }

    @Override
    public void ajouterSoin(String descriptionSoin) {
        if (descriptionSoin != null && !descriptionSoin.isBlank()) {
            this.historiqueSoins.add(descriptionSoin);
        }
    }

    // Un médecin n'a pas d'antécédents médicaux dans ce contexte (il soigne, il ne reçoit pas)
    @Override
    public boolean aDesAntecedents() {
        return false;
    }

    // -----------------------------------------------------------------------
    // Implémentation de Planifiable
    // -----------------------------------------------------------------------

    // estDisponible() et setDisponible() sont hérités de Personnel — pas besoin de les réécrire

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
        return "Dr. " + getNomComplet() + " [" + specialite + "] - " + getMatricule();
    }

    // -----------------------------------------------------------------------
    // Getters et Setters
    // -----------------------------------------------------------------------

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