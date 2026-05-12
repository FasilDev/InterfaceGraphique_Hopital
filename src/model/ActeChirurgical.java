/*
 * Fichier : ActeChirurgical.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Représente un acte chirurgical ou une intervention d'urgence.
 *        Étend Soin et implémente Urgence (priorité médicale).
 *        Implémente aussi Comparable<ActeChirurgical> pour fonctionner
 *        avec PriorityQueue : les actes les plus urgents (niveau 1) sortent en premier.
 *
 * Interactions : Soin (parent), Urgence (interface), FileUrgences, SoinService, UrgenceServlet
 *
 * Pourquoi Comparable ?
 *   PriorityQueue<T> a besoin de savoir comment comparer deux éléments pour les trier.
 *   Soit T implémente Comparable, soit on passe un Comparator au constructeur.
 *   Ici on implémente Comparable directement sur la classe — plus simple.
 *
 * Niveaux de priorité (convention CCMU simplifiée) :
 *   1 = Critique — engage le pronostic vital (ex : arrêt cardiaque)
 *   2 = Urgent   — risque fort si pas traité dans l'heure
 *   3 = Semi-urgent
 *   4 = Peu urgent
 *   5 = Non urgent — peut attendre
 */

package model;

import java.time.LocalDate;

public class ActeChirurgical extends Soin implements Urgence, Comparable<ActeChirurgical> {

    // Type d'intervention (ex : "Appendicectomie", "Pose de plâtre", "Suture")
    private String typeActe;

    // Niveau de priorité médicale : 1 (critique) → 5 (non urgent)
    private int niveauPriorite;

    // Description courte de l'urgence pour l'affichage dans la file
    private String descriptionUrgence;

    // Salle d'opération ou de soins affectée (ex : "Bloc 3", "Salle A")
    private String salle;

    // false = en attente de réalisation, true = acte effectué
    private boolean realise;

    // -----------------------------------------------------------------------
    // Constructeurs
    // -----------------------------------------------------------------------

    /**
     * Constructeur principal pour créer un acte chirurgical en urgence.
     *
     * @param typeActe          Type d'intervention
     * @param niveauPriorite    Niveau 1 à 5 (1 = le plus urgent)
     * @param descriptionUrgence Description courte de la situation
     * @param numeroPatient     Numéro du patient concerné
     * @param matriculeMedecin  Matricule du chirurgien
     */
    public ActeChirurgical(String typeActe, int niveauPriorite, String descriptionUrgence,
                           String numeroPatient, String matriculeMedecin) {
        super(typeActe, numeroPatient, matriculeMedecin);
        this.typeActe = typeActe;
        // On s'assure que le niveau est entre 1 et 5
        this.niveauPriorite = Math.max(1, Math.min(5, niveauPriorite));
        this.descriptionUrgence = descriptionUrgence;
        this.realise = false;
        setCout(500.0); // coût de base d'un acte chirurgical
    }

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

    // -----------------------------------------------------------------------
    // Implémentation de Soin (méthode abstraite)
    // -----------------------------------------------------------------------

    @Override
    public String getTypeSoin() {
        return "Acte Chirurgical";
    }

    // -----------------------------------------------------------------------
    // Implémentation de Urgence
    // -----------------------------------------------------------------------

    @Override
    public int getNiveauPriorite() { return niveauPriorite; }

    @Override
    public void setNiveauPriorite(int niveau) {
        this.niveauPriorite = Math.max(1, Math.min(5, niveau));
    }

    @Override
    public String getDescriptionUrgence() { return descriptionUrgence; }

    // -----------------------------------------------------------------------
    // Implémentation de Comparable
    // -----------------------------------------------------------------------

    /**
     * Compare deux actes chirurgicaux par niveau de priorité.
     * PriorityQueue utilise cette méthode pour décider qui sort en premier.
     * Un niveau inférieur = priorité plus haute = sort en premier.
     *
     * Exemple : niveau 1 (critique) sort avant niveau 3 (semi-urgent).
     */
    @Override
    public int compareTo(ActeChirurgical autre) {
        // Integer.compare(a, b) retourne négatif si a < b, 0 si égal, positif si a > b
        // Donc niveau 1 comparé à niveau 3 → résultat négatif → 1 passe devant 3 dans la queue
        return Integer.compare(this.niveauPriorite, autre.niveauPriorite);
    }

    // -----------------------------------------------------------------------
    // Méthodes métier
    // -----------------------------------------------------------------------

    // Retourne une étiquette lisible pour l'affichage de la priorité
    public String getLabelPriorite() {
        return switch (niveauPriorite) {
            case 1 -> "Critique";
            case 2 -> "Urgent";
            case 3 -> "Semi-urgent";
            case 4 -> "Peu urgent";
            default -> "Non urgent";
        };
    }

    // Marque l'acte comme réalisé
    public void marquerRealise() {
        this.realise = true;
    }

    @Override
    public String toString() {
        return "[P" + niveauPriorite + " - " + getLabelPriorite() + "] "
                + typeActe + " - Patient : " + getNumeroPatient()
                + (realise ? " (réalisé)" : " (en attente)");
    }

    // -----------------------------------------------------------------------
    // Getters et Setters
    // -----------------------------------------------------------------------

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