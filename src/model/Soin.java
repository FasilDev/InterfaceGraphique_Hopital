/*
 * Fichier : Soin.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Classe abstraite représentant tout acte médical dans le système.
 *        Consultation et ActeChirurgical héritent de cette classe.
 *        Elle factorise les attributs communs à tous les soins :
 *        date, patient concerné, médecin responsable, coût.
 *
 * Interactions : Entite (parent), Consultation (sous-classe), ActeChirurgical (sous-classe),
 *                SoinService, PatientService
 *
 * On stocke le numéro du patient et le matricule du médecin comme Strings
 * plutôt que comme références d'objets. Cela évite des dépendances circulaires
 * lors du chargement CSV (on chargerait Soin avant Patient ou inversement).
 */

package model;

import java.time.LocalDate;

public abstract class Soin extends Entite {

    // Description générale de l'acte médical
    private String description;

    // Date à laquelle le soin a été effectué
    private LocalDate dateSoin;

    // Référence au patient (par son numéro lisible, ex : "P-001")
    private String numeroPatient;

    // Référence au médecin qui a réalisé le soin (par son matricule, ex : "MED-001")
    private String matriculeMedecin;

    // Coût du soin en euros
    private double cout;

    // -----------------------------------------------------------------------
    // Constructeurs
    // -----------------------------------------------------------------------

    public Soin(String description, String numeroPatient, String matriculeMedecin) {
        super();
        this.description = description;
        this.numeroPatient = numeroPatient;
        this.matriculeMedecin = matriculeMedecin;
        this.dateSoin = LocalDate.now();
        this.cout = 0.0;
    }

    public Soin(String id, String description, LocalDate dateSoin,
                String numeroPatient, String matriculeMedecin, double cout) {
        super(id);
        this.description = description;
        this.dateSoin = dateSoin;
        this.numeroPatient = numeroPatient;
        this.matriculeMedecin = matriculeMedecin;
        this.cout = cout;
    }

    // -----------------------------------------------------------------------
    // Méthode abstraite
    // -----------------------------------------------------------------------

    /**
     * Chaque sous-classe retourne son type de soin.
     * Exemples : "Consultation", "Acte Chirurgical"
     */
    public abstract String getTypeSoin();

    // -----------------------------------------------------------------------
    // Getters et Setters
    // -----------------------------------------------------------------------

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

    @Override
    public String toString() {
        return "[" + getTypeSoin() + "] " + description + " - Patient : " + numeroPatient;
    }
}
