/*
 * Soin.java - Classe abstraite représentant tout acte médical.
 * Consultation et ActeChirurgical héritent de cette classe.
 * On stocke les références au patient et au médecin par String (numéro/matricule)
 * plutôt que par objet, pour éviter des dépendances circulaires au chargement CSV.
 */

package model;

import java.time.LocalDate;

public abstract class Soin extends Entite {

    private String description;
    private LocalDate dateSoin;
    private String numeroPatient;
    private String matriculeMedecin;
    private double cout;

    public Soin(String description, String numeroPatient, String matriculeMedecin) {
        super();
        this.description = description;
        this.numeroPatient = numeroPatient;
        this.matriculeMedecin = matriculeMedecin;
        this.dateSoin = LocalDate.now();
        this.cout = 0.0;
    }

    // Constructeur de rechargement CSV
    public Soin(String id, String description, LocalDate dateSoin,
                String numeroPatient, String matriculeMedecin, double cout) {
        super(id);
        this.description = description;
        this.dateSoin = dateSoin;
        this.numeroPatient = numeroPatient;
        this.matriculeMedecin = matriculeMedecin;
        this.cout = cout;
    }

    // Chaque sous-classe retourne son type : "Consultation" ou "Acte Chirurgical"
    public abstract String getTypeSoin();

    @Override
    public String toString() {
        return "[" + getTypeSoin() + "] " + description + " - Patient : " + numeroPatient;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateSoin() { return dateSoin; }
    public void setDateSoin(LocalDate dateSoin) { this.dateSoin = dateSoin; }

    public String getNumeroPatient() { return numeroPatient; }
    public void setNumeroPatient(String numeroPatient) { this.numeroPatient = numeroPatient; }

    public String getMatriculeMedecin() { return matriculeMedecin; }
    public void setMatriculeMedecin(String matriculeMedecin) { this.matriculeMedecin = matriculeMedecin; }

    public double getCout() { return cout; }
    public void setCout(double cout) { this.cout = cout; }
}
