/*
 * Urgence.java - Interface pour tout acte médical ayant une priorité médicale.
 * Implémentée par ActeChirurgical. Utilisée avec PriorityQueue pour trier les urgences.
 *
 * Niveaux : 1 = Critique, 2 = Urgent, 3 = Semi-urgent, 4 = Peu urgent, 5 = Non urgent.
 * PriorityQueue Java est un tas-min : la valeur la plus basse sort en premier (niveau 1 = prioritaire).
 */

package model;

public interface Urgence {

    int getNiveauPriorite();

    void setNiveauPriorite(int niveau);

    String getDescriptionUrgence();
}
