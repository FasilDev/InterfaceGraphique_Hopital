/*
 * ActeChirurgical.java - Représente un acte chirurgical ou une intervention d'urgence.
 * Étend Soin, implémente Urgence (priorité médicale) et Comparable.
 *
 * Pourquoi Comparable ? PriorityQueue a besoin de comparer les éléments pour les trier.
 * compareTo() trie par niveauPriorite : niveau 1 (critique) sort en premier.
 * Niveaux : 1 = Critique, 2 = Urgent, 3 = Semi-urgent, 4 = Peu urgent, 5 = Non urgent.
 */

package model;

import java.time.LocalDate;

public class ActeChirurgical extends Soin implements Urgence, Comparable<ActeChirurgical> {

    private String typeActe;
    private int niveauPriorite; // 1 = plus urgent, 5 = moins urgent
    private String descriptionUrgence;
    private String salle;
    private boolean realise;

    public ActeChirurgical(String typeActe, int niveauPriorite, String descriptionUrgence,
                           String numeroPatient, String matriculeMedecin) {
        super(typeActe, numeroPatient, matriculeMedecin);
        this.typeActe = typeActe;
        this.niveauPriorite = Math.max(1, Math.min(5, niveauPriorite));
        this.descriptionUrgence = descriptionUrgence;
        this.realise = false;
        setCout(500.0);
    }

    // Constructeur de rechargement CSV
    public ActeChirurgical(String id, String typeActe, int niveauPriorite, String descriptionUrgence,
                           String salle, boolean realise, LocalDate dateSoin,
                           String numeroPatient, String matriculeMedecin, double cout) {
        super(id, typeActe, dateSoin, numeroPatient, matriculeMedecin, cout);
        this.typeActe = typeActe;
        this.niveauPriorite = Math.max(1, Math.min(5, niveauPriorite));
        this.descriptionUrgence = descriptionUrgence;
        this.salle = salle;
        this.realise = realise;
    }

    @Override
    public String getTypeSoin() { return "Acte Chirurgical"; }

    // -------------------------------------------------------------------
    // Implémentation de Urgence
    // -------------------------------------------------------------------

    @Override
    public int getNiveauPriorite() { return niveauPriorite; }

    @Override
    public void setNiveauPriorite(int niveau) {
        this.niveauPriorite = Math.max(1, Math.min(5, niveau));
    }

    @Override
    public String getDescriptionUrgence() { return descriptionUrgence; }

    // -------------------------------------------------------------------
    // Implémentation de Comparable
    // -------------------------------------------------------------------

    // PriorityQueue utilise cette méthode pour trier : niveau 1 passe avant niveau 3
    @Override
    public int compareTo(ActeChirurgical autre) {
        return Integer.compare(this.niveauPriorite, autre.niveauPriorite);
    }

    // -------------------------------------------------------------------
    // Méthodes métier
    // -------------------------------------------------------------------

    public String getLabelPriorite() {
        return switch (niveauPriorite) {
            case 1 -> "Critique";
            case 2 -> "Urgent";
            case 3 -> "Semi-urgent";
            case 4 -> "Peu urgent";
            default -> "Non urgent";
        };
    }

    public void marquerRealise() { this.realise = true; }

    @Override
    public String toString() {
        return "[P" + niveauPriorite + " - " + getLabelPriorite() + "] "
                + typeActe + " - Patient : " + getNumeroPatient()
                + (realise ? " (réalisé)" : " (en attente)");
    }

    // -------------------------------------------------------------------
    // Getters et Setters
    // -------------------------------------------------------------------

    public String getTypeActe() { return typeActe; }
    public void setTypeActe(String typeActe) { this.typeActe = typeActe; }

    public String getSalle() { return salle; }
    public void setSalle(String salle) { this.salle = salle; }

    public boolean isRealise() { return realise; }
    public void setRealise(boolean realise) { this.realise = realise; }

    public void setDescriptionUrgence(String descriptionUrgence) {
        this.descriptionUrgence = descriptionUrgence;
    }
}
