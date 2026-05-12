/*
 * Consultation.java - Représente une consultation médicale standard.
 * Étend Soin. Tarif fixé à 25€ par consultation.
 */

package model;

import java.time.LocalDate;

public class Consultation extends Soin {

    private String motif;
    private String diagnostic;
    private String ordonnance; // null si aucune ordonnance rédigée

    public Consultation(String motif, String numeroPatient, String matriculeMedecin) {
        super("Consultation : " + motif, numeroPatient, matriculeMedecin);
        this.motif = motif;
        this.diagnostic = "";
        this.ordonnance = null;
        setCout(25.0);
    }

    // Constructeur de rechargement CSV
    public Consultation(String id, String motif, String diagnostic, String ordonnance,
                        LocalDate dateSoin, String numeroPatient, String matriculeMedecin, double cout) {
        super(id, "Consultation : " + motif, dateSoin, numeroPatient, matriculeMedecin, cout);
        this.motif = motif;
        this.diagnostic = diagnostic;
        this.ordonnance = ordonnance;
    }

    @Override
    public String getTypeSoin() { return "Consultation"; }

    public boolean aUneOrdonnance() {
        return ordonnance != null && !ordonnance.isBlank();
    }

    @Override
    public String toString() {
        return "[Consultation] " + motif + " - Patient : " + getNumeroPatient()
                + (aUneOrdonnance() ? " (ordonnance)" : "");
    }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public String getDiagnostic() { return diagnostic; }
    public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }

    public String getOrdonnance() { return ordonnance; }
    public void setOrdonnance(String ordonnance) { this.ordonnance = ordonnance; }
}
