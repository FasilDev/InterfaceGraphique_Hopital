/*
 * Patient.java - Représente un patient de l'hôpital.
 * Implements Soignable (reçoit des soins) et Facturable (facturation journalière).
 * Interactions : PatientService, PatientServlet, JSP patients/
 */

package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Patient extends Personne implements Soignable, Facturable {

    private String numeroPatient;
    private String groupeSanguin;
    private List<String> antecedents;
    private List<String> historiqueSoins;
    private LocalDate dateAdmission;
    private LocalDate dateSortie;
    private boolean admis;
    private String notes;
    private String chambre;
    private String numeroSecuriteSociale;
    private boolean prisEnCharge;
    private static final double TARIF_JOURNALIER = 350.0;

    public Patient(String nom, String prenom, LocalDate dateNaissance, String numeroPatient) {
        super(nom, prenom, dateNaissance);
        this.numeroPatient = numeroPatient;
        this.antecedents = new ArrayList<>();
        this.historiqueSoins = new ArrayList<>();
        this.admis = false;
        this.notes = "";
        this.prisEnCharge = false;
    }

    // Constructeur de rechargement CSV
    public Patient(String id, String nom, String prenom, LocalDate dateNaissance, String numeroPatient) {
        super(id, nom, prenom, dateNaissance);
        this.numeroPatient = numeroPatient;
        this.antecedents = new ArrayList<>();
        this.historiqueSoins = new ArrayList<>();
        this.admis = false;
        this.notes = "";
        this.prisEnCharge = false;
    }

    // -------------------------------------------------------------------
    // Méthodes métier
    // -------------------------------------------------------------------

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
    public boolean aDesAntecedents() {
        return antecedents != null && !antecedents.isEmpty();
    }

    // -------------------------------------------------------------------
    // Implémentation de Facturable
    // -------------------------------------------------------------------

    // Calcule le montant selon les jours d'hospitalisation (minimum 1 jour facturé)
    @Override
    public double calculerMontantTotal() {
        if (dateAdmission == null) return 0.0;
        LocalDate fin = (dateSortie != null) ? dateSortie : LocalDate.now();
        long jours = Math.max(ChronoUnit.DAYS.between(dateAdmission, fin), 1);
        return jours * TARIF_JOURNALIER;
    }

    @Override
    public String getNumeroSecuriteSociale() { return numeroSecuriteSociale; }
    @Override
    public void setNumeroSecuriteSociale(String numero) { this.numeroSecuriteSociale = numero; }
    @Override
    public boolean isPrisEnCharge() { return prisEnCharge; }
    @Override
    public void setPrisEnCharge(boolean prise) { this.prisEnCharge = prise; }

    @Override
    public String toString() {
        return "Patient[" + numeroPatient + "] " + getNomComplet()
                + " (" + getAge() + " ans) - " + getStatutAdmission();
    }

    // -------------------------------------------------------------------
    // Getters et Setters
    // -------------------------------------------------------------------

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
