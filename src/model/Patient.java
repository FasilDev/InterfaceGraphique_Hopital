/*
 * Fichier : Patient.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Représente un patient de l'hôpital.
 *        Gère son dossier médical : admission, antécédents, chambre et sortie.
 *
 * Interfaces implémentées :
 *   - Soignable : le patient reçoit des soins, a des antécédents
 *   - Facturable : le patient génère une facturation journalière
 *
 * Interactions : Personne (parent), PatientService, PatientServlet,
 *                JSP liste-patients.jsp, detail-patient.jsp, formulaire-patient.jsp
 */

package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Patient extends Personne implements Soignable, Facturable {

    // Numéro lisible unique dans l'hôpital (ex : "P-2024-001")
    private String numeroPatient;

    private String groupeSanguin;

    // Antécédents médicaux : "Diabète type 2", "Allergie pénicilline", etc.
    private List<String> antecedents;

    // Historique des soins reçus (descriptions textuelles pour l'instant)
    // Sera lié aux objets Soin au Commit 4
    private List<String> historiqueSoins;

    private LocalDate dateAdmission;
    private LocalDate dateSortie;
    private boolean admis;
    private String notes;
    private String chambre;

    // Champs pour l'interface Facturable
    private String numeroSecuriteSociale;
    private boolean prisEnCharge;
    // Tarif journalier simplifié (en euros)
    private static final double TARIF_JOURNALIER = 350.0;

    // -----------------------------------------------------------------------
    // Constructeurs
    // -----------------------------------------------------------------------

    public Patient(String nom, String prenom, LocalDate dateNaissance, String numeroPatient) {
        super(nom, prenom, dateNaissance);
        this.numeroPatient = numeroPatient;
        this.antecedents = new ArrayList<>();
        this.historiqueSoins = new ArrayList<>();
        this.admis = false;
        this.notes = "";
        this.prisEnCharge = false;
    }

    public Patient(String id, String nom, String prenom, LocalDate dateNaissance, String numeroPatient) {
        super(id, nom, prenom, dateNaissance);
        this.numeroPatient = numeroPatient;
        this.antecedents = new ArrayList<>();
        this.historiqueSoins = new ArrayList<>();
        this.admis = false;
        this.notes = "";
        this.prisEnCharge = false;
    }

    // -----------------------------------------------------------------------
    // Méthodes métier
    // -----------------------------------------------------------------------

    public void admettre(String chambre) {
        this.admis = true;
        this.dateAdmission = LocalDate.now();
        this.chambre = chambre;
        this.dateSortie = null;
    }

    public void sortir() {
        this.admis = false;
        this.dateSortie = LocalDate.now();
        this.chambre = null;
    }

    public void ajouterAntecedent(String antecedent) {
        if (antecedent != null && !antecedent.isBlank()) {
            this.antecedents.add(antecedent);
        }
    }

    public String getAntecedentsFormates() {
        if (antecedents == null || antecedents.isEmpty()) return "Aucun antécédent connu";
        return String.join(", ", antecedents);
    }

    public String getStatutAdmission() {
        return admis ? "Hospitalisé" : "Non hospitalisé";
    }

    // -----------------------------------------------------------------------
    // Implémentation de Soignable
    // -----------------------------------------------------------------------

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

    // Un patient a des antécédents si sa liste n'est pas vide
    @Override
    public boolean aDesAntecedents() {
        return antecedents != null && !antecedents.isEmpty();
    }

    // -----------------------------------------------------------------------
    // Implémentation de Facturable
    // -----------------------------------------------------------------------

    /**
     * Calcule le montant total basé sur le nombre de jours d'hospitalisation.
     * Si le patient est encore hospitalisé, on compte jusqu'à aujourd'hui.
     * Retourne 0 si le patient n'a jamais été admis.
     */
    @Override
    public double calculerMontantTotal() {
        if (dateAdmission == null) return 0.0;
        LocalDate fin = (dateSortie != null) ? dateSortie : LocalDate.now();
        long jours = ChronoUnit.DAYS.between(dateAdmission, fin);
        // Minimum 1 jour facturé même pour une admission le jour même
        jours = Math.max(jours, 1);
        return jours * TARIF_JOURNALIER;
    }

    @Override
    public String getNumeroSecuriteSociale() { return numeroSecuriteSociale; }

    @Override
    public void setNumeroSecuriteSociale(String numero) { this.numeroSecuriteSociale = numero; }

    @Override
    public boolean estPrisEnCharge() { return prisEnCharge; }

    @Override
    public void setPrisEnCharge(boolean prise) { this.prisEnCharge = prise; }

    // -----------------------------------------------------------------------

    @Override
    public String toString() {
        return "Patient[" + numeroPatient + "] " + getNomComplet()
                + " (" + getAge() + " ans) - " + getStatutAdmission();
    }

    // -----------------------------------------------------------------------
    // Getters et Setters
    // -----------------------------------------------------------------------

    public String getNumeroPatient() { return numeroPatient; }
    public void setNumeroPatient(String numeroPatient) { this.numeroPatient = numeroPatient; }

    public String getGroupeSanguin() { return groupeSanguin; }
    public void setGroupeSanguin(String groupeSanguin) { this.groupeSanguin = groupeSanguin; }

    public List<String> getAntecedents() { return antecedents; }
    public void setAntecedents(List<String> antecedents) { this.antecedents = antecedents; }

    public void setHistoriqueSoins(List<String> historiqueSoins) { this.historiqueSoins = historiqueSoins; }

    public LocalDate getDateAdmission() { return dateAdmission; }
    public void setDateAdmission(LocalDate dateAdmission) { this.dateAdmission = dateAdmission; }

    public LocalDate getDateSortie() { return dateSortie; }
    public void setDateSortie(LocalDate dateSortie) { this.dateSortie = dateSortie; }

    public boolean isAdmis() { return admis; }
    public void setAdmis(boolean admis) { this.admis = admis; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getChambre() { return chambre; }
    public void setChambre(String chambre) { this.chambre = chambre; }
}