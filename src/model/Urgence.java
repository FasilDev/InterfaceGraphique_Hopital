/*
 * Fichier : Urgence.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Interface pour tout acte médical ou patient ayant une priorité médicale.
 *        Implémentée par ActeChirurgical (Commit 4).
 *        Utilisée avec PriorityQueue pour trier les urgences par priorité.
 *
 * Interactions : ActeChirurgical, UrgenceService, PriorityQueue dans le registre des urgences
 *
 * Niveaux de priorité :
 *   1 = Critique (engage le pronostic vital)
 *   2 = Urgent (risque fort si pas traité rapidement)
 *   3 = Semi-urgent
 *   4 = Peu urgent
 *   5 = Non urgent (consultation standard)
 *
 * PriorityQueue en Java est un tas min (min-heap) : la valeur la plus basse sort en premier.
 * Donc niveau 1 = sorti en premier = traité en priorité. C'est cohérent.
 */

package model;

public interface Urgence {

    // Retourne le niveau de priorité médicale (1 = critique, 5 = non urgent)
    int getNiveauPriorite();

    void setNiveauPriorite(int niveau);

    // Description courte de l'urgence (ex : "Arrêt cardiaque", "Fracture ouverte")
    String getDescriptionUrgence();
}
