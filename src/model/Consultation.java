/*
 * Fichier : Consultation.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Représente une consultation médicale standard.
 *        C'est le soin le plus courant : un médecin voit un patient,
 *        pose un diagnostic et rédige éventuellement une ordonnance.
 *
 * Interactions : Soin (parent), SoinService, SoinServlet, JSP liste-soins.jsp
 */

package model;

import java.time.LocalDate;

public class Consultation extends Soin {

    // Raison de la consultation (ex : "Douleurs abdominales", "Fièvre persistante")
    private String motif;

    // Diagnostic établi par le médecin à l'issue de la consultation
    private String diagnostic;

    // Contenu de l'ordonnance (médicaments prescrits, doses, durée)
    // null si aucune ordonnance n'a été rédigée
    private String ordonnance;

    // -----------------------------------------------------------------------
    // Constructeurs
    // -----------------------------------------------------------------------

    public Consultation(String motif, String numeroPatient, String matriculeMedecin) {
        super("Consultation : " + motif, numeroPatient, matriculeMedecin);
        this.motif = motif;
        this.diagnostic = "";
        this.ordonnance = null;
        // Tarif standard d'une consultation en secteur hospitalier
        setCout(25.0);
    }

    public Consultation(String id, String motif, String diagnostic, String ordonnance,
                        LocalDate dateSoin, String numeroPatient, String matriculeMedecin, double cout) {
        super(id, "Consultation : " + motif, dateSoin, numeroPatient, matriculeMedecin, cout);
        this.motif = motif;
        this.diagnostic = diagnostic;
        this.ordonnance = ordonnance;
    }

    // -----------------------------------------------------------------------
    // Implémentation méthode abstraite de Soin
    // -----------------------------------------------------------------------

    @Override
    public String getTypeSoin() {
        return "Consultation";
    }

    // -----------------------------------------------------------------------
    // Méthodes métier
    // -----------------------------------------------------------------------

    // Indique si une ordonnance a été rédigée lors de cette consultation
    public boolean aUneOrdonnance() {
        return ordonnance != null && !ordonnance.isBlank();
    }

    @Override
    public String toString() {
        return "[Consultation] " + motif + " - Patient : " + getNumeroPatient()
                + (aUneOrdonnance() ? " (ordonnance)" : "");
    }

    // -----------------------------------------------------------------------
    // Getters et Setters
    // -----------------------------------------------------------------------

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public String getDiagnostic() { return diagnostic; }
    public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }

    public String getOrdonnance() { return ordonnance; }
    public void setOrdonnance(String ordonnance) { this.ordonnance = ordonnance; }
}
