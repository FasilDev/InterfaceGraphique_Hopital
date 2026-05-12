/*
 * Fichier : Planifiable.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Interface pour tout membre du personnel qui a un planning gérable.
 *        Implémentée par Medecin et Infirmier.
 *
 * Interactions : Medecin, Infirmier, PersonnelService
 *
 * Note : estDisponible() et setDisponible() sont déclarés ici car ils font
 *        partie du contrat "être planifiable". Personnel les implémente déjà
 *        via ses champs — les sous-classes héritent automatiquement de cette implémentation.
 */

package model;

import java.time.LocalDate;
import java.util.Map;

public interface Planifiable {

    // Indique si ce membre du personnel est disponible pour être planifié
    boolean estDisponible();

    void setDisponible(boolean disponible);

    // Ajoute un créneau au planning : date → description du créneau
    // Exemple : LocalDate.of(2024, 6, 10) → "08h00-14h00 Cardiologie"
    void ajouterCreneau(LocalDate date, String horaire);

    // Retourne le planning complet sous forme de Map triée par date
    // On utilise Map<LocalDate, String> pour associer chaque date à un créneau
    Map<LocalDate, String> getPlanning();
}
